package models.word;

import io.ebean.Finder;
import models.base.BaseModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity
public class ArticleLink extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final Finder<Long, ArticleLink> find = new Finder<>(ArticleLink.class, "word");

    @Id
    public Long id;

    public Integer articlePage;
    public Integer articleIndex;
    public String articleLinkText;
    public String articleLinkTitle;
    public String articleLinkHref;
    public Integer articleType;

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
