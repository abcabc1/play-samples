package models.word;

import io.ebean.Finder;
import models.base.BaseModel;
import models.common.Config;

import javax.persistence.*;

@Table
@Entity
@UniqueConstraint(columnNames = {"source", "article_index"})
public class ArticleLink extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final Finder<Long, ArticleLink> find = new Finder<>(ArticleLink.class, "word");

    @Id
    public Long id;
    @Column(columnDefinition = "integer not null default 0 comment '页码'")
    public Integer articlePage;
    @Column(columnDefinition = "integer not null default 0 comment '序号'")
    public Integer articleIndex;
    @Column(columnDefinition = "varchar(128) not null default '' comment '文本'")
    public String articleLinkText;
    @Column(columnDefinition = "varchar(128) not null default '' comment '标题'")
    public String articleLinkTitle;
    @Column(columnDefinition = "varchar(128) not null default '' comment '链接'")
    public String articleLinkHref;
    @Column(insertable = false, columnDefinition = "tinyint not null default 0 comment '类别[0 待处理, 1 单篇, 2 多篇,  -1 不处理]'")
    public Integer articleType;

    @ManyToOne
    @JoinColumn(name = "source")
    public Config source;

    @Override
    public String toString() {
        return "ArticleLink{" +
                "page=" + articlePage +
                ", articleIndex=" + articleIndex +
                ", articleLinkText='" + articleLinkTitle + '\'' +
                ", articleType=" + articleType +
                '}';
    }
}
