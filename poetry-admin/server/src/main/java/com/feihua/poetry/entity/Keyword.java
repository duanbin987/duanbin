package com.feihua.poetry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("keyword")
public class Keyword {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String charValue;
    private String description;
    private LocalDateTime createdAt;
    @TableLogic
    private Integer deleted;
}
