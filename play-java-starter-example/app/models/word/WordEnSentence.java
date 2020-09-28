package models.word;

import io.ebean.Finder;
import models.base.BaseModel;

import javax.persistence.*;

@Entity
@Table
public class WordEnSentence extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final Finder<WordEnSentencePk, WordEnSentence> find = new Finder<>(WordEnSentence.class, "word");

    @EmbeddedId
    public WordEnSentencePk pk;

    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name = "word", referencedColumnName = "word", insertable = false, updatable = false),
            @JoinColumn(name = "type", referencedColumnName = "type", insertable = false, updatable = false),
            @JoinColumn(name = "no", referencedColumnName = "no", insertable = false, updatable = false)
    })
    public WordEnExtend wordEnExtend;

    @Column(columnDefinition = "varchar(1024) not null default '' comment '例句解释'")
    public String sentenceNote;

    @Column(insertable = false, columnDefinition = "tinyint unsigned not null default 0 comment '是否标记为识记[0 不识记, 1 识记]'")
    public boolean remember_mark;
}