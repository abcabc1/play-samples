package models.word.vo;

import io.ebean.Model;

import java.util.List;

public class ArticleParam extends Model {
    public String articlePageLink;

    public Integer articleStartPage;

    public Integer articleEndPage;

    public Integer articleIndex;

    public List<String> articleTitleList;
}
