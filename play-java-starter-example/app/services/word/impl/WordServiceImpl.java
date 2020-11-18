package services.word.impl;

import models.word.vo.ArticleLink;
import models.word.vo.ArticlePage;
import models.word.vo.ArticleParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import services.dict.DictService;
import services.word.WordService;
import utils.StringUtil;
import utils.exception.InternalException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static utils.exception.ExceptionEnum.INVALID_ARTICLE_PARAM;

public class WordServiceImpl implements WordService {
    @Inject
    DictService dictService;

    private static final Logger logger = LoggerFactory.getLogger(WordService.class);

    @Override
    public List<ArticlePage> listWordEnArticleTitle4XMLY(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        checkParam(articleParam);
        List<ArticlePage> pageList = listPage(articleParam);
        listArticleLink(articleParam, pageList);
//        result += "total article title:" + articleLinkLinkedList.size() + " page count:" + pageList.stream().map(v -> v.page + ":" + v.articleCount).collect(Collectors.joining(","));
        return pageList;
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
    public LinkedList<ArticleLink> listArticleLink(ArticleParam articleParam, List<ArticlePage> pageList) throws ExecutionException, InterruptedException {
        LinkedList<ArticleLink> articleLinkLinkedList = new LinkedList<>();
        for (ArticlePage articlePage : pageList) {
            LinkedHashSet<String> linkedHashSet = dictService.getXMLYChinaDailyTitle(articleParam.link + "/p" + articlePage.page).toCompletableFuture().get();
            for (String pageTitle : linkedHashSet) {
                ArticleLink articleLink = new ArticleLink();
                String[] temp = pageTitle.split("#");
                articleLink.articlePage = articlePage;
                articleLink.articleIndex = Integer.parseInt(temp[0]);
                articleLink.articleLinkHref = temp[2];
                if (temp[1].contains("早间英文播报") || temp[1].contains("早间英文播播报") || temp[1].contains("头条英文播报") || temp[1].contains("英语新闻音频")
                        || temp[1].contains("早间英文新闻") || temp[1].contains("早间英语新闻播报") || temp[1].contains("早间英语播报") || temp[1].contains("早间英文")/* || temp[1].contains("新闻音频") || temp[1].contains("英语播报")*/) {
                    articleLink.articleType = 1;
                } else if (temp[1].contains("午间双语播报") || temp[1].contains("午间双语新闻") || temp[1].contains("双语") || temp[1].contains("双语精选") || temp[1].contains("热门")/*|| temp[1].contains("英语新闻")  || temp[1].contains("英文新闻")*/) {
                    articleLink.articleType = 2;
                } else if (temp[1].contains("24节气英语说") || temp[1].contains("今日立冬！话说中国节")/*|| temp[1].contains("英文视频") || temp[1].contains("新课试听")
                        || temp[1].contains("独家视频")*/) {
                    articleLink.articleType = 3;
                } else if (temp[1].contains("独家视频") || temp[1].contains("【新课试听】") || temp[1].contains("中国日报独家视频：深圳，四十而已")) {
                    articleLink.articleType = 4;
                }/* else if (temp[1].contains("：")) {
                    String s = StringUtils.trimLeadingWhitespace(temp[1]).substring(temp[1].indexOf("：") + 1);
                    if (!s.isEmpty() && StringUtil.isChineseByScript(s.charAt(0))) {
                        articleLink.articleType = 2;
                    }
                }*/
                if (articleLink.articleType == null) {
                    articleLink.articleType = 4;
                }
                switch (articleLink.articleType) {
                    case 1:
                        if (temp[1].contains("：")) {
                            articleLink.articleLinkText = temp[1].substring(temp[1].indexOf("：") + 1);
                        } else if (temp[1].contains(":")) {
                            articleLink.articleLinkText = temp[1].substring(temp[1].indexOf(":") + 1);
                        }
//                        articleLink.articleLinkText = replacePageTitle(articleLink.articleLinkText);
                        articlePage.singleArticleLinkList.add(articleLink);
                        articleLinkLinkedList.add(articleLink);
                        break;
                    case 2:
                        if (temp[1].contains("：")) {
                            articleLink.articleLinkText = temp[1].substring(temp[1].indexOf("：") + 1);
                        } else if (temp[1].contains(":")) {
                            articleLink.articleLinkText = temp[1].substring(temp[1].indexOf(":") + 1);
                        }
//                        articleLink.articleLinkText = replacePageTitle(articleLink.articleLinkText);
                        articlePage.multiArticleLinkList.add(articleLink);
                        articleLinkLinkedList.add(articleLink);
                        break;
                    case 3:
                        articleLink.articleLinkText = temp[1];
                        articlePage.todoArticleList.add(pageTitle);
                        break;
                    case 4:
                        articleLink.articleLinkText = temp[1];
                        articlePage.errorArticleList.add(pageTitle);
                        break;
                }
                articlePage.articleLinkList.add(articleLink);
            }
        }
        return articleLinkLinkedList;
    }

    /**
     * remove character in title page
     *
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
    public List<ArticlePage> listPage(ArticleParam articleParam) {
        List<ArticlePage> articlePageList = new ArrayList<>();
        articleParam.startPage = Math.max(articleParam.startPage, 1);
        articleParam.endPage = Math.max(articleParam.endPage, 1);
        for (int page = articleParam.startPage; page <= articleParam.endPage; page++) {
            ArticlePage articlePage = new ArticlePage();
            articlePage.page = page;
            articlePageList.add(articlePage);
        }
        return articlePageList;
    }

    /**
     * validate param
     *
     * @param articleParam
     * @return
     */
    @Override
    public boolean checkParam(ArticleParam articleParam) {
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
        if (articleParam.startPage != null && articleParam.endPage != null && articleParam.startPage > articleParam.endPage) {
            errorList.add("start page:" + articleParam.startPage + " < " + "end page:" + articleParam.endPage);
        }
        if (errorList.size() > 0) {
            throw InternalException.build(INVALID_ARTICLE_PARAM, new String[]{errorList.stream().collect(Collectors.joining())});
        }
        return true;
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
