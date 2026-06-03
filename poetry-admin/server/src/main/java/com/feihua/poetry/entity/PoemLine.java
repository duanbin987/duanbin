package com.feihua.poetry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("poem_line")
public class PoemLine {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long poemId;
    private Integer lineNo;
    private String content;
    private String pinyin;
    private Integer sortOrder;
    @TableLogic
    private Integer deleted;
}
