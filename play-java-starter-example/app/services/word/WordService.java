package services.word;

import models.word.vo.ArticleLink;
import models.word.vo.ArticleParam;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface WordService {

    String listWordEnArticleTitle4XMLY(ArticleParam articleParam) throws ExecutionException, InterruptedException;

    String saveWordEnArticle4XMLY(ArticleParam articleParam);

    List<String> checkParam(ArticleParam articleParam);

    List<Integer> listPage(ArticleParam articleParam);

    LinkedList<ArticleLink> listArticleLink(ArticleParam articleParam, List<Integer> pageList) throws ExecutionException, InterruptedException;

    String replacePageTitle(String pageTitle);
}
