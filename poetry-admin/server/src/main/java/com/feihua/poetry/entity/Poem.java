package com.feihua.poetry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("poem")
public class Poem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String author;
    private String dynasty;
    private Integer grade;
    private String contentSummary;
    private LocalDateTime createdAt;
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private List<PoemLine> lines;
}
