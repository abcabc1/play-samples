package models.word.vo;

import io.ebean.Model;

import java.util.List;

public class ArticleParam extends Model {
    public String link;

    public Integer startPage;

    public Integer endPage;

    public List<Integer> articleIndexList;

    public List<String> articleTitleList;
}
