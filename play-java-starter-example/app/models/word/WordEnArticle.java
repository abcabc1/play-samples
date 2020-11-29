package models.word;

import io.ebean.Finder;
import models.base.BaseModel;
import models.common.Config;

import javax.persistence.*;

@Entity
@Table
public class WordEnArticle extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final Finder<Long, WordEnArticle> find = new Finder<>(WordEnArticle.class, "word");

    @Id
    public Long id;

    public Integer articleIndex;

    @Column(nullable = true, columnDefinition = "varchar(128) default '' comment '标题'")
    public String title;

    @Column(nullable = true, columnDefinition = "varchar(128) default '' comment '标题翻译'")
    public String titleNote;

    @Column(nullable = true, columnDefinition = "text comment '内容'")
    public String content;

    @Column(nullable = true, columnDefinition = "varchar(1024) default '' comment '翻译'")
    public String contentNote;

    @ManyToOne
    @JoinColumn(name = "source")
    public Config source;

    @Column(insertable = false, nullable = false, columnDefinition = "tinyint unsigned default 0 comment '是否标记为识记[0 不识记, 1 识记]'")
    public Boolean remember_mark;

    @Column(columnDefinition = "varchar(64) not null default '' comment '答案'")
    public String answer;

    @Override
    public String toString() {
        return "WordEnArticle{" +
                "articleIndex=" + articleIndex +
                ", title='" + title + '\'' +
                ", titleNote='" + titleNote + '\'' +
                '}';
    }
}