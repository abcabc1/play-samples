package models.word;

import io.ebean.Finder;
import models.base.BaseModel;
import models.common.Config;

import javax.persistence.*;

@Entity
@Table
public class WordEnQuestionChoice extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final Finder<Long, WordEnQuestionChoice> find = new Finder<>(WordEnQuestionChoice.class, "word");

    @Id
    public Long id;

    @Column(columnDefinition = "varchar(256) not null default '' comment '题目'")
    public String question;

    @Column(columnDefinition = "varchar(16) not null default '' comment '答案'")
    public String answer;

    @Column(columnDefinition = "varchar(256) not null default '' comment '备注'")
    public String remark;

    @Column(insertable = false, columnDefinition = "integer not null default 0 comment '错误次数'")
    public Integer errorNum;

    @ManyToOne
    @JoinColumn(name = "source")
    public Config source;

    @Column(insertable = false, columnDefinition = "tinyint unsigned not null default 0 comment '是否标记为识记[0 不识记, 1 识记]'")
    public Boolean remember_mark;
}