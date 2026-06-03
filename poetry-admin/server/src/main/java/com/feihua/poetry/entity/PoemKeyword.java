package com.feihua.poetry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("poem_keyword")
public class PoemKeyword {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long poemId;
    private Long keywordId;
    private Long lineId;
    @TableLogic
    private Integer deleted;
}
