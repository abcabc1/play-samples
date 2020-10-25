package models.word.vo;

import io.ebean.Model;

import java.util.List;

public class ArticleParam extends Model {
    public String articleLink;

    public Integer articleStartPage;

    public Integer articleEndPage;

    public List<Integer> articleIndexList;

    public List<String> articleTitleList;
}
