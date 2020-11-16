package services.word.impl;

import models.word.vo.ArticleLink;
import models.word.vo.ArticleParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.dict.DictService;
import services.word.WordService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class WordServiceImpl implements WordService {
    @Inject
    DictService dictService;

    private static final Logger logger = LoggerFactory.getLogger(WordService.class);

    @Override
    public String listWordEnArticleTitle4XMLY(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        String result = "";
        List<String> errorList = checkParam(articleParam);
        if (errorList.size() > 0) {
            return errorList.stream().collect(Collectors.joining());
        }
        List<Integer> pageList = listPage(articleParam);
        LinkedList<ArticleLink> articleLinkLinkedList = listArticleLink(articleParam, pageList);
        result += "total article title:" + articleLinkLinkedList.size();
        return result;
    }

    /**
     * list all titles for pages
     *
     * @param articleParam
     * @param pageList
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public LinkedList<ArticleLink> listArticleLink(ArticleParam articleParam, List<Integer> pageList) throws ExecutionException, InterruptedException {
        LinkedList<ArticleLink> articleLinkLinkedList = new LinkedList<>();
        for (Integer page : pageList) {
            LinkedHashSet<String> linkedHashSet = dictService.getXMLYChinaDailyTitle(articleParam.link + "/p" + page).toCompletableFuture().get();
            for (String pageTitle : linkedHashSet) {
                ArticleLink articleLink = new ArticleLink();
                String[] temp = pageTitle.split("#");
                articleLink.page = page;
                articleLink.articleIndex = Integer.parseInt(temp[0]);
                articleLink.articleLinkText = temp[1];
                articleLink.articleLinkHref = temp[2];
                articleLinkLinkedList.add(articleLink);
                articleLink.articleLinkText = replacePageTitle(articleLink.articleLinkText);
            }
        }
        return articleLinkLinkedList;
    }

    /**
     * remove character in title page
     * @param pageTitle
     * @return
     */
    @Override
    public String replacePageTitle(String pageTitle) {
        return pageTitle.replaceAll("'", "")
                .replaceAll("\"", "")
                .replaceAll("\\?", "")
                .replaceAll(":", "")
                .replaceAll(",", "")
                .replaceAll("-", "")
                .replaceAll("\\|", "")
                .replaceAll(">", "")
                .replaceAll("<", "");
    }

    /**
     * get all page numbers
     *
     * @param articleParam
     * @return
     */
    public List<Integer> listPage(ArticleParam articleParam) {
        List<Integer> pageList = new ArrayList<>();
        articleParam.startPage = Math.max(articleParam.startPage, 1);
        articleParam.endPage = Math.max(articleParam.endPage, 1);
        for (int page = articleParam.startPage; page <= articleParam.endPage; page++) {
            pageList.add(page);
        }
        return pageList;
    }

    /**
     * validate param
     *
     * @param articleParam
     * @return
     */
    @Override
    public List<String> checkParam(ArticleParam articleParam) {
        List<String> errorList = new ArrayList<>();
        logger.info(articleParam.toString());
        if (articleParam == null) {
            errorList.add("acticle param is missing");
        }
        if (articleParam.link == null || articleParam.link.isEmpty()) {
            errorList.add("page link is mission");
        } else {
            logger.info(articleParam.link);
        }
        if (articleParam.startPage != null) {
            try {
                articleParam.startPage.intValue();
            } catch (Exception e) {
                errorList.add("article param start page:" + articleParam.startPage + "invalid");
            }
        }
        if (articleParam.endPage != null) {
            try {
                articleParam.endPage.intValue();
            } catch (Exception e) {
                errorList.add("article param end page:" + articleParam.endPage + "invalid");
            }
        }
        if (articleParam.startPage != null && articleParam.endPage != null && articleParam.startPage < articleParam.endPage) {
            errorList.add("start page:" + articleParam.startPage + " < " + "end page:" + articleParam.endPage);
        }
        return errorList;
    }

    @Override
    public String saveWordEnArticle4XMLY(ArticleParam articleParam) {
        String result = "";
        // validate param
        // get all pages
        // get all title
        return result;
    }
}
