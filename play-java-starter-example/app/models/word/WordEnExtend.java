package models.word;

import io.ebean.Finder;
import models.base.BaseModel;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
public class WordEnExtend extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final Finder<WordEnExtendPk, WordEnExtend> find = new Finder<>(WordEnExtend.class, "word");

    @EmbeddedId
    public WordEnExtendPk pk;

    @Column(columnDefinition = "varchar(64) not null default '' comment '单词解释'")
    public String wordNote;

    @Column(insertable = false, columnDefinition = "tinyint unsigned not null default 0 comment '是否标记为识记[0 不识记, 1 识记]'")
    public Boolean remember_mark;
}