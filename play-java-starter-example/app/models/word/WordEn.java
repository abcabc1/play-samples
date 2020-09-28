package models.word;

import io.ebean.Finder;
import models.base.BaseModel;
import models.common.Config;

import javax.persistence.*;

@Entity
@Table
@UniqueConstraint(columnNames = {"word"})
public class WordEn extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final Finder<Long, WordEn> find = new Finder<>(WordEn.class, "word");

    @Id
    public Long id;

    @Column(columnDefinition = "varchar(128) not null default '' comment '单词'")
    public String word;

    @Column(columnDefinition = "varchar(128) not null default '' comment '单词解释'")
    public String wordNote;

    @Column(columnDefinition = "varchar(32) not null default '' comment '英音'")
    public String soundUk;

    @Column(columnDefinition = "varchar(32) not null default '' comment '美音'")
    public String soundUs;

    @Column(insertable = false, columnDefinition = "integer not null default 0 comment '错误次数'")
    public Integer errorNum;

    @Column(insertable = false, columnDefinition = "tinyint unsigned not null default 0 comment '是否标记为识记[0 不识记, 1 识记]'")
    public Boolean remember_mark;

    @ManyToOne
    @JoinColumn(name = "source")
    public Config source;
}