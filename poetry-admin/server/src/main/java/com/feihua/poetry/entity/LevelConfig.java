package com.feihua.poetry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("level_config")
public class LevelConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long keywordId;
    private Integer difficulty;
    private Integer poemCount;
    private String description;
    private Integer sortOrder;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String keywordChar;
}
