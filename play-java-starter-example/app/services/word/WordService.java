package services.word;

import models.word.ArticleLink;
import models.word.vo.ArticlePage;
import models.word.vo.ArticleParam;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface WordService {

    void saveChinaDailyArticleLink(ArticleParam articleParam) throws ExecutionException, InterruptedException;

    boolean checkParam(ArticleParam articleParam);

    List<ArticlePage> listPage(ArticleParam articleParam);

//    List<ArticlePage> listWordEnArticleTitle4XMLY(ArticleParam articleParam) throws ExecutionException, InterruptedException;
//    LinkedList<ArticleLink> listArticleLink(ArticleParam articleParam, List<ArticlePage> pageList) throws ExecutionException, InterruptedException;

    //    String saveWordEnArticle4XMLY(ArticleParam articleParam);

//    String replacePageTitle(String pageTitle);
}
