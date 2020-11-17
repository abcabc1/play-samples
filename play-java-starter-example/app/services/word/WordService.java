package services.word;

import models.word.vo.ArticleLink;
import models.word.vo.ArticlePage;
import models.word.vo.ArticleParam;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface WordService {

    List<ArticlePage> listWordEnArticleTitle4XMLY(ArticleParam articleParam) throws ExecutionException, InterruptedException;

    String saveWordEnArticle4XMLY(ArticleParam articleParam);

    boolean checkParam(ArticleParam articleParam);

    List<ArticlePage> listPage(ArticleParam articleParam);

    LinkedList<ArticleLink> listArticleLink(ArticleParam articleParam, List<ArticlePage> pageList) throws ExecutionException, InterruptedException;

    String replacePageTitle(String pageTitle);
}
