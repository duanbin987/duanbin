package com.feihua.poetry.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feihua.poetry.dto.CheckAnswerResponse;
import com.feihua.poetry.dto.QuestionDto;
import com.feihua.poetry.entity.*;
import com.feihua.poetry.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class QuestionEngine {

    public static final String FIND_LINE = "FIND_LINE";
    public static final String FILL_CHAR = "FILL_CHAR";
    public static final String MATCH_LINE = "MATCH_LINE";

    private final ConcurrentHashMap<String, String> answerStore = new ConcurrentHashMap<>();

    @Autowired
    private LevelConfigMapper levelConfigMapper;
    @Autowired
    private KeywordMapper keywordMapper;
    @Autowired
    private PoemKeywordMapper poemKeywordMapper;
    @Autowired
    private PoemMapper poemMapper;
    @Autowired
    private PoemLineMapper poemLineMapper;

    public List<QuestionDto> generateQuestions(Long levelId, int count) {
        LevelConfig level = levelConfigMapper.selectById(levelId);
        if (level == null) {
            throw new IllegalArgumentException("关卡不存在");
        }
        Keyword keyword = keywordMapper.selectById(level.getKeywordId());
        if (keyword == null) {
            throw new IllegalArgumentException("关键字不存在");
        }
        String charValue = keyword.getCharValue();

        List<PoemKeyword> bindings = poemKeywordMapper.selectList(
                new LambdaQueryWrapper<PoemKeyword>().eq(PoemKeyword::getKeywordId, level.getKeywordId()));
        if (bindings.isEmpty()) {
            throw new IllegalArgumentException("该关卡暂无关联诗词");
        }

        List<QuestionContext> contexts = buildContexts(bindings, charValue);
        if (contexts.isEmpty()) {
            throw new IllegalArgumentException("无法生成题目");
        }

        Random random = new Random();
        List<QuestionDto> questions = new ArrayList<>();
        String[] types = {FIND_LINE, FILL_CHAR, MATCH_LINE};

        for (int i = 0; i < count; i++) {
            QuestionContext ctx = contexts.get(random.nextInt(contexts.size()));
            String type = types[i % types.length];
            QuestionDto q = buildQuestion(type, ctx, contexts, random);
            if (q != null) {
                questions.add(q);
            }
        }

        while (questions.size() < count && !contexts.isEmpty()) {
            QuestionContext ctx = contexts.get(random.nextInt(contexts.size()));
            QuestionDto q = buildQuestion(FIND_LINE, ctx, contexts, random);
            if (q != null && questions.stream().noneMatch(x -> x.getQuestionId().equals(q.getQuestionId()))) {
                questions.add(q);
            } else {
                break;
            }
        }
        return questions;
    }

    public CheckAnswerResponse checkAnswer(String questionId, String answer) {
        String correct = answerStore.get(questionId);
        if (correct == null) {
            throw new IllegalArgumentException("题目不存在或已过期");
        }
        boolean ok = correct.equalsIgnoreCase(answer != null ? answer.trim() : "");
        answerStore.remove(questionId);
        return new CheckAnswerResponse(ok, correct);
    }

    private List<QuestionContext> buildContexts(List<PoemKeyword> bindings, String charValue) {
        List<QuestionContext> list = new ArrayList<>();
        for (PoemKeyword pk : bindings) {
            Poem poem = poemMapper.selectById(pk.getPoemId());
            PoemLine line = poemLineMapper.selectById(pk.getLineId());
            if (poem == null || line == null || !line.getContent().contains(charValue)) {
                continue;
            }
            QuestionContext ctx = new QuestionContext();
            ctx.poem = poem;
            ctx.targetLine = line;
            ctx.charValue = charValue;
            ctx.keywordId = pk.getKeywordId();
            list.add(ctx);
        }
        return list;
    }

    private QuestionDto buildQuestion(String type, QuestionContext ctx, List<QuestionContext> all, Random random) {
        switch (type) {
            case FIND_LINE:
                return buildFindLine(ctx, all, random);
            case FILL_CHAR:
                return buildFillChar(ctx);
            case MATCH_LINE:
                return buildMatchLine(ctx, all, random);
            default:
                return null;
        }
    }

    private QuestionDto buildFindLine(QuestionContext ctx, List<QuestionContext> all, Random random) {
        List<PoemLine> poemLines = poemLineMapper.selectList(
                new LambdaQueryWrapper<PoemLine>().eq(PoemLine::getPoemId, ctx.poem.getId())
                        .orderByAsc(PoemLine::getSortOrder));
        if (poemLines.size() < 2) {
            return null;
        }

        List<QuestionDto.LineOption> options = poemLines.stream().map(l -> toLineOption(l, ctx.poem)).collect(Collectors.toList());
        Collections.shuffle(options, random);

        QuestionDto q = new QuestionDto();
        q.setQuestionId(UUID.randomUUID().toString());
        q.setType(FIND_LINE);
        q.setKeyword(ctx.charValue);
        q.setPoemTitle(ctx.poem.getTitle());
        q.setAuthor(ctx.poem.getAuthor());
        q.setLines(options);
        answerStore.put(q.getQuestionId(), String.valueOf(ctx.targetLine.getId()));
        return q;
    }

    private QuestionDto buildFillChar(QuestionContext ctx) {
        String content = ctx.targetLine.getContent();
        int idx = content.indexOf(ctx.charValue);
        if (idx < 0) {
            return null;
        }
        String display = content.substring(0, idx) + "___" + content.substring(idx + 1);
        String[] pinyinParts = ctx.targetLine.getPinyin().split("\\s+");
        String displayPinyin = ctx.targetLine.getPinyin();
        if (pinyinParts.length == content.replace("，", "").replace("。", "").length()) {
            displayPinyin = String.join(" ", pinyinParts);
        }

        QuestionDto q = new QuestionDto();
        q.setQuestionId(UUID.randomUUID().toString());
        q.setType(FILL_CHAR);
        q.setKeyword(ctx.charValue);
        q.setPoemTitle(ctx.poem.getTitle());
        q.setAuthor(ctx.poem.getAuthor());
        q.setDisplayLine(display);
        q.setDisplayPinyin(displayPinyin);
        q.setBlankIndex(idx);
        answerStore.put(q.getQuestionId(), ctx.charValue);
        return q;
    }

    private QuestionDto buildMatchLine(QuestionContext ctx, List<QuestionContext> all, Random random) {
        List<QuestionContext> others = all.stream()
                .filter(c -> !c.targetLine.getId().equals(ctx.targetLine.getId()))
                .filter(c -> c.targetLine.getContent().contains(ctx.charValue))
                .collect(Collectors.toList());
        if (others.isEmpty()) {
            return buildFindLine(ctx, all, random);
        }

        QuestionContext correct = others.get(random.nextInt(others.size()));
        List<QuestionContext> candidates = new ArrayList<>();
        candidates.add(correct);
        List<QuestionContext> distractors = others.stream()
                .filter(c -> !c.targetLine.getId().equals(correct.targetLine.getId()))
                .collect(Collectors.toList());
        Collections.shuffle(distractors, random);
        for (QuestionContext d : distractors) {
            if (candidates.size() >= 4) break;
            candidates.add(d);
        }
        while (candidates.size() < 2 && distractors.size() > candidates.size() - 1) {
            candidates.add(distractors.get(candidates.size() - 1));
        }
        Collections.shuffle(candidates, random);

        List<QuestionDto.LineOption> options = candidates.stream()
                .map(c -> toLineOption(c.targetLine, c.poem))
                .collect(Collectors.toList());

        QuestionDto q = new QuestionDto();
        q.setQuestionId(UUID.randomUUID().toString());
        q.setType(MATCH_LINE);
        q.setKeyword(ctx.charValue);
        q.setPoemTitle(ctx.poem.getTitle());
        q.setAuthor(ctx.poem.getAuthor());
        q.setDisplayLine(ctx.targetLine.getContent());
        q.setDisplayPinyin(ctx.targetLine.getPinyin());
        q.setLines(options);
        answerStore.put(q.getQuestionId(), String.valueOf(correct.targetLine.getId()));
        return q;
    }

    private QuestionDto.LineOption toLineOption(PoemLine line, Poem poem) {
        QuestionDto.LineOption opt = new QuestionDto.LineOption();
        opt.setId(line.getId());
        opt.setContent(line.getContent());
        opt.setPinyin(line.getPinyin());
        opt.setPoemId(poem.getId());
        opt.setPoemTitle(poem.getTitle());
        return opt;
    }

    private static class QuestionContext {
        Poem poem;
        PoemLine targetLine;
        String charValue;
        Long keywordId;
    }
}
