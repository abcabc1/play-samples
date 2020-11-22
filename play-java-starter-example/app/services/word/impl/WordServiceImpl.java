package services.word.impl;

import models.word.ArticleLink;
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

    @Inject
    ArticleLinkServiceImpl articleLinkService;

    private static final Logger logger = LoggerFactory.getLogger(WordService.class);

    public void saveChinaDailyArticleLink(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        checkParam(articleParam);
        LinkedHashSet<ArticleLink> articleLinks = collectArticleLink(articleParam);
        articleLinkService.saveAll(articleLinks);
    }

    public void updateChinaDailyArticleType() {
        ArticleLink model = new ArticleLink();
        List<ArticleLink> articleLinkList = articleLinkService.list(model);
        for (ArticleLink articleLink: articleLinkList) {
            if (articleLink.articleLinkText.contains("特别节目"))
        }
    }

    private LinkedHashSet<ArticleLink> collectArticleLink(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        LinkedHashSet<ArticleLink> articleLinks = new LinkedHashSet<>();
        for (int i = articleParam.startPage; i < articleParam.endPage; i++) {
            LinkedHashSet<String> linkedHashSet = dictService.getXMLYChinaDailyTitle(articleParam.link + "/p" + i).toCompletableFuture().get();
            for (String s : linkedHashSet) {
                String[] temp = s.split("#");
                Integer index = Integer.parseInt(temp[0]);
                String linkText = temp[1].replaceAll("^\\d{1,2}\\.\\d{1,2}", "").replaceAll("^\\d{1,2}月\\d{1,2}[日月号]", "")
                        .replaceAll("Jan\\s\\d{1,2}|Feb\\s\\d{1,2}|Mar\\s\\d{1,2}|Apr\\s\\d{1,2}|May\\s\\d{1,2}|Jun\\s\\d{1,2}|July\\s\\d{1,2}|Aug\\s\\d{1,2}|Sep\\s\\d{1,2}|Oct\\s\\d{1,2}|Nov\\s\\d{1,2}|Dec\\s\\d{1,2}", "");
                linkText = StringUtils.trimTrailingWhitespace(StringUtils.trimLeadingWhitespace(linkText));
                String href = temp[2];
                ArticleLink articleLink = new ArticleLink();
                articleLink.articlePage = i;
                articleLink.articleIndex = index;
                articleLink.articleLinkText = linkText;
                articleLink.articleLinkHref = href;
                articleLinks.add(articleLink);
            }
        }
        return articleLinks;
    }

   /* @Override
    public List<ArticlePage> listWordEnArticleTitle4XMLY(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        checkParam(articleParam);
        List<ArticlePage> pageList = listPage(articleParam);
        listArticleLink(articleParam, pageList);
//        result += "total article title:" + articleLinkLinkedList.size() + " page count:" + pageList.stream().map(v -> v.page + ":" + v.articleCount).collect(Collectors.joining(","));
        return pageList;
    }

    *//**
     * list all titles for pages
     *
     * @param articleParam
     * @param pageList
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     *//*
    @Override
    public LinkedList<ArticleLink> listArticleLink(ArticleParam articleParam, List<ArticlePage> pageList) throws ExecutionException, InterruptedException {
        LinkedList<ArticleLink> articleLinkLinkedList = new LinkedList<>();
        for (ArticlePage articlePage : pageList) {
            LinkedHashSet<String> linkedHashSet = dictService.getXMLYChinaDailyTitle(articleParam.link + "/p" + articlePage.page).toCompletableFuture().get();
            for (String pageTitle : linkedHashSet) {
                String[] temp = pageTitle.split("#");
                Integer index = Integer.parseInt(temp[0]);
                String linkText = temp[1];
                String href = temp[2];
                if (articleParam.articleIndexList != null && !articleParam.articleIndexList.isEmpty() && !articleParam.articleIndexList.contains(index)) {
                    continue;
                }
                if (articleParam.articleTitleList != null && !articleParam.articleTitleList.isEmpty() && articleParam.articleTitleList.stream().noneMatch(linkText::contains)) {
                    continue;
                }
                ArticleLink articleLink = new ArticleLink();
                articleLink.articlePage = articlePage;
                articleLink.articleIndex = index;
                articleLink.articleLinkHref = href;
                linkText = linkText.replaceAll("｜", "：").replaceAll("\\|", "：");
                linkText = linkText.replaceAll(":", "：");
                if (linkText.contains("：")) {
                    linkText = linkText.replaceAll("^\\d{1,2}\\.\\d{1,2}", "").replaceAll("^\\d{1,2}月\\d{1,2}[日月号]", "")
                            .replaceAll("Jan\\s\\d{1,2}|Feb\\s\\d{1,2}|Mar\\s\\d{1,2}|Apr\\s\\d{1,2}|May\\s\\d{1,2}|Jun\\s\\d{1,2}|July\\s\\d{1,2}|Aug\\s\\d{1,2}|Sep\\s\\d{1,2}|Oct\\s\\d{1,2}|Nov\\s\\d{1,2}|Dec\\s\\d{1,2}", "")
                            .replaceAll("April\\s\\d{1,2}", "");
                    String titlePrefix = StringUtils.trimTrailingWhitespace(StringUtils.trimLeadingWhitespace(linkText.substring(0, linkText.indexOf("："))));
                    if (titlePrefix.equals("早间英文") || titlePrefix.equals("早间英文播报") || titlePrefix.equals("早间英文播播报") || titlePrefix.equals("早间英文新闻")
                            || titlePrefix.equals("早间英语播报") || titlePrefix.equals("早间英语新闻播报") || titlePrefix.equals("英语新闻音频")
                            || titlePrefix.equals("头条英文播报") || titlePrefix.equals("早间头条英文播报")*//* || titlePrefix.equals("新闻音频") || titlePrefix.equals("英语播报")*//*) {
                        articleLink.articleType = 1;
                    } else if (titlePrefix.equals("双语") || titlePrefix.equals("双语新闻") || titlePrefix.equals("午间双语播报") || titlePrefix.equals("午间双语新闻") || titlePrefix.equals("双语新闻精选")
                            || titlePrefix.equals("热门") || titlePrefix.equals("热门双语") || titlePrefix.equals("双语热门") || titlePrefix.equals("热门音频")
                            || titlePrefix.equals("午间英语新闻") || titlePrefix.equals("英语新闻精选")
                            || titlePrefix.equals("精选") || titlePrefix.equals("双语精选") || titlePrefix.equals("双语新闻播报") || titlePrefix.equals("英语音频")) {
                        articleLink.articleType = 2;
                    } else if (titlePrefix.contains("24节气英语说") || titlePrefix.equals("今日立冬！话说中国节")*//*|| titlePrefix.equals("英文视频") || titlePrefix.equals("新课试听")
                        || titlePrefix.equals("独家视频")*//*) {
                        articleLink.articleType = 4;
                    } else if (titlePrefix.contains("【新课试听】") || titlePrefix.equals("中国日报独家视频") || titlePrefix.contains("中秋特别节目") || titlePrefix.equals("中英文视频") || titlePrefix.equals("中英文双语动画")) {
                        articleLink.articleType = 4;
                    } else if (titlePrefix.equals("英语")) {
                        String title = StringUtils.trimLeadingWhitespace(linkText.substring(linkText.indexOf("：") + 1));
                        if (StringUtil.isAlpha(title.charAt(0))) {
                            articleLink.articleType = 1;
                        } else if (StringUtil.isChineseByScript(title.charAt(0))) {
                            articleLink.articleType = 2;
                        }
                    } else if (titlePrefix.equals("英语新闻")) {
                        String title = linkText.substring(linkText.indexOf("：") + 1);
                        if (StringUtil.isAlpha(title.charAt(0))) {
                            articleLink.articleType = 1;
                        } else if (StringUtil.isChineseByScript(title.charAt(0))) {
                            articleLink.articleType = 2;
                        }
                    }
                    *//* else if (titlePrefix.equals("：")) {
                    String s = StringUtils.trimLeadingWhitespace(titlePrefix).substring(titlePrefix.indexOf("：") + 1);
                    if (!s.isEmpty() && StringUtil.isChineseByScript(s.charAt(0))) {
                        articleLink.articleType = 2;
                    }
                }*//*
                } else {
                    articleLink.articleType = 4;
                }
                if (articleLink.articleType == null) {
                    articleLink.articleType = 3;
                }
                switch (articleLink.articleType) {
                    case 1:
                        articleLink.articleLinkTitle = pageTitle;
                        *//*if (titlePrefix.equals("：")) {
                            articleLink.articletitlePrefix = titlePrefix.substring(titlePrefix.indexOf("：") + 1);
                        } else if (titlePrefix.equals(":")) {
                            articleLink.articletitlePrefix = titlePrefix.substring(titlePrefix.indexOf(":") + 1);
                        }*//*
                        articlePage.singleArticleLinkList.add(articleLink);
                        articleLinkLinkedList.add(articleLink);
                        break;
                    case 2:
                        articleLink.articleLinkTitle = pageTitle;
                        *//*if (titlePrefix.equals("：")) {
                            articleLink.articletitlePrefix = titlePrefix.substring(titlePrefix.indexOf("：") + 1);
                        } else if (titlePrefix.equals(":")) {
                            articleLink.articleLinkText = titlePrefix.substring(titlePrefix.indexOf(":") + 1);
                        }*//*
                        articlePage.multiArticleLinkList.add(articleLink);
                        articleLinkLinkedList.add(articleLink);
                        break;
                    case 3:
                        articleLink.articleLinkTitle = pageTitle;
                        articlePage.todoArticleList.add(articleLink);
                        break;
                    case 4:
                        articleLink.articleLinkTitle = pageTitle;
                        articlePage.errorArticleList.add(articleLink);
                        break;
                }
                articlePage.articleLinkList.add(articleLink);
            }
        }
        return articleLinkLinkedList;
    }

    *//**
     * remove character in title page
     *
     * @param pageTitle
     * @return
     *//*
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
    }*/

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
                int startPage = articleParam.startPage.intValue();
                articleParam.startPage = Math.max(startPage, 1);
            } catch (Exception e) {
                errorList.add("article param start page:" + articleParam.startPage + "invalid");
            }
        }
        if (articleParam.endPage != null) {
            try {
                int endPage = articleParam.endPage.intValue();
                articleParam.endPage = Math.max(endPage, 1);
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
/*
    @Override
    public String saveWordEnArticle4XMLY(ArticleParam articleParam) {
        String result = "";
        // validate param
        // get all pages
        // get all title
        return result;
    }*/
}
