package models.word.vo;

import javax.validation.constraints.Positive;
import java.util.List;

public class ArticleLinkForm {

    public Long id;

    @Positive
    public Integer articlePage;

    @Positive
    public Integer articleIndex;

    public String articleLinkText;

    public String articleLinkTitle;

    public String articleLinkHref;

    public Integer articleType;

    public Integer articlePageFrom;

    public Integer articlePageTo;

    public Integer articleIndexFrom;

    public Integer articleIndexTo;

    public List<Integer> articleIndexes;

    public List<Integer> exArticleIndexes;

    public String source;
}
