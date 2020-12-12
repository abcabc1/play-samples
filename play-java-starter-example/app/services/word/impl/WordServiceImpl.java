package services.word.impl;

import com.google.common.collect.Lists;
import io.ebean.annotation.Transactional;
import models.common.Config;
import models.word.ArticleLink;
import models.word.WordEnArticle;
import models.word.vo.ArticlePage;
import models.word.vo.ArticleParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import services.dict.DictService;
import services.word.WordService;
import utils.StringUtil;
import utils.exception.ExceptionEnum;
import utils.exception.InternalException;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static utils.exception.ExceptionEnum.INVALID_ARTICLE_PARAM;

public class WordServiceImpl implements WordService {
    @Inject
    DictService dictService;

    @Inject
    ArticleLinkServiceImpl articleLinkService;

    @Inject
    WordEnArticleServiceImpl wordEnArticleService;

    private static final Logger logger = LoggerFactory.getLogger(WordService.class);

    @Override
    @Transactional
    public void saveXSArticleLink(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        checkParam(articleParam);
        LinkedHashSet<ArticleLink> articleLinks = collectXSArticleLink(articleParam);
        articleLinkService.saveAll(articleLinks);
    }

    @Override
    @Transactional
    public void saveChinaDailyArticleLink(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        checkParam(articleParam);
        LinkedHashSet<ArticleLink> articleLinks = collectChinaDailyArticleLink(articleParam);
        articleLinkService.saveAll(articleLinks);
    }

    @Override
    @Transactional
    public void saveXSArticle(ArticleLink articleLink) throws ExecutionException, InterruptedException {
        List<WordEnArticle> wordEnArticleList = new ArrayList<>();
        List<ArticleLink> articleLinkList = articleLinkService.list(articleLink);
        for (ArticleLink articleLinkTemp : articleLinkList) {
            logger.info(articleLinkTemp.toString());
            String content = dictService.getXSArticle(articleLinkTemp).toCompletableFuture().get();
            if (content.isEmpty()) {
                continue;
            }
            WordEnArticle wordEnArticle = new WordEnArticle();
            wordEnArticle.content = content;
            wordEnArticle.titleNote = articleLinkTemp.articleLinkTitle;
            wordEnArticle.articleIndex = articleLinkTemp.articleIndex;
            wordEnArticle.source = articleLinkTemp.source;
            wordEnArticle.answer = "";
            wordEnArticleList.add(wordEnArticle);
        }
        Lists.partition(wordEnArticleList, 100).forEach(v -> {
            wordEnArticleService.saveAll(wordEnArticleList);
        });
    }

    public void saveChinaDailyArticleSingle(ArticleLink articleLink) throws ExecutionException, InterruptedException {
        List<WordEnArticle> wordEnArticleList = new ArrayList<>();
        List<ArticleLink> articleLinkList = articleLinkService.list(articleLink);
        for (ArticleLink articleLinkTemp : articleLinkList) {
            logger.info(articleLinkTemp.toString());
            String content = dictService.getChinaDailyArticleSingle(articleLinkTemp).toCompletableFuture().get();
            if (content.isEmpty()) {
                continue;
            }
            WordEnArticle wordEnArticle = new WordEnArticle();
            wordEnArticle.content = content;
            wordEnArticle.title = articleLinkTemp.articleLinkTitle;
            wordEnArticle.articleIndex = articleLinkTemp.articleIndex;
            wordEnArticle.source = articleLinkTemp.source;
            wordEnArticle.answer = "";
            wordEnArticleList.add(wordEnArticle);
        }
        wordEnArticleService.saveAll(wordEnArticleList);
    }

    /*public void saveChinaDailyArticleMulti1(ArticleLink articleLink) throws ExecutionException, InterruptedException {
        List<WordEnArticle> wordEnArticleList = new ArrayList<>();
        List<ArticleLink> articleLinkList = articleLinkService.list(articleLink);
        for (ArticleLink articleLinkTemp : articleLinkList) {
            logger.info(articleLinkTemp.toString());
            String content = dictService.getChinaDailyArticleMulti(articleLinkTemp).toCompletableFuture().get();
            if (content.isEmpty()) {
                continue;
            }
            WordEnArticle wordEnArticle = new WordEnArticle();
            wordEnArticle.content = content;
            wordEnArticle.title = articleLinkTemp.articleLinkTitle;
            wordEnArticle.articleIndex = articleLinkTemp.articleIndex;
            wordEnArticle.source = articleLinkTemp.source;
            wordEnArticle.answer = "";
            wordEnArticleList.add(wordEnArticle);
        }
        wordEnArticleService.saveAll(wordEnArticleList);
    }*/

    public void saveChinaDailyArticleMulti(ArticleLink articleLink) {
        List<WordEnArticle> wordEnArticleList = new ArrayList<>();
        List<ArticleLink> articleLinkList = articleLinkService.list(articleLink);
        for (ArticleLink articleLinkTemp : articleLinkList) {
            logger.info(articleLinkTemp.toString());
            List<String> articleList = null;
            try {
                articleList = dictService.getChinaDailyArticleMulti(articleLinkTemp).toCompletableFuture().get();
            } catch (InterruptedException | ExecutionException e) {
                throw InternalException.build(ExceptionEnum.ARTICLE_EXCEPTION);
            }
            if (articleList.size() != 4) {
                logger.error(articleLinkTemp.toString());
            }
            for (String article : articleList) {
                String[] temp = article.split("#");
                if (temp.length != 4) {
                    logger.error(article);
                    continue;
                }
                WordEnArticle wordEnArticle = new WordEnArticle();
                wordEnArticle.title = temp[0];
                wordEnArticle.titleNote = temp[1];
                wordEnArticle.content = temp[2];
                wordEnArticle.contentNote = temp[3];
                wordEnArticle.articleIndex = articleLinkTemp.articleIndex;
                wordEnArticle.source = articleLinkTemp.source;
                wordEnArticle.answer = "";
                wordEnArticleList.add(wordEnArticle);
                logger.info(wordEnArticle.toString());
            }
        }
        wordEnArticleList.size();
//        wordEnArticleService.saveAll(wordEnArticleList);
    }

    @Override
    @Transactional
    public void updateChinaDailyArticleType(ArticleLink articleLink) {
        List<ArticleLink> articleLinkList = articleLinkService.list(articleLink);
        List<ArticleLink> tempList = new ArrayList<>();
        for (ArticleLink articleLinkTemp : articleLinkList) {
            if (articleLinkTemp.articleLinkText.contains("：")) {
                String articleLinkTextLead = articleLinkTemp.articleLinkText.substring(0, articleLinkTemp.articleLinkText.indexOf("："));
                String articleLinkTextTrail = StringUtils.trimLeadingWhitespace(articleLinkTemp.articleLinkText.substring(articleLinkTemp.articleLinkText.indexOf("：") + 1));
                if (articleLinkTextLead.equals("早间英文播报") || articleLinkTextLead.equals("早间英文播播报")
                        || articleLinkTextLead.equals("早间英语新闻播报") || articleLinkTextLead.equals("早间英文新闻")
                        || articleLinkTextLead.equals("早间英语播报") || articleLinkTextLead.equals("早间英文")) {
                    articleLinkTemp.articleType = 1;
                } else if (articleLinkTextLead.contains("特别节目") || articleLinkTextLead.contains("新课试听") || articleLinkTextLead.contains("中国日报独家视频")
                        || articleLinkTextLead.contains("中英文双语动画") || articleLinkTextLead.contains("中英文视频")) {
                    articleLinkTemp.articleType = -1;
                } else if (articleLinkTextLead.equals("") || articleLinkTextLead.equals("精选") || articleLinkTextLead.equals("热门音频")
                        || articleLinkTextLead.equals("英语新闻精选") || articleLinkTextLead.equals("英语音频") || articleLinkTextLead.equals("英语精选")
                        || articleLinkTextLead.equals("双语热门") || articleLinkTextLead.equals("双语新闻播报") || articleLinkTextLead.equals("双语新闻精选")
                        || articleLinkTextLead.equals("午间英语新闻") || articleLinkTextLead.equals("热门双语") || articleLinkTextLead.equals("双语新闻")
                        || articleLinkTextLead.equals("午间双语播报") || articleLinkTextLead.equals("午间双语新闻") || articleLinkTextLead.equals("双语")
                        || articleLinkTextLead.equals("双语精选") || articleLinkTextLead.equals("热门") || articleLinkTextLead.equals("英语")) {
                    articleLinkTemp.articleType = 2;
                } else if (articleLinkTextLead.equals("英语新闻音频") || articleLinkTextLead.equals("英语新闻")) {
                    if (StringUtil.isChineseByScript(articleLinkTextTrail.charAt(0)) || StringUtil.isNumber(articleLinkTextTrail.charAt(0)) || articleLinkTemp.articleIndex == 631) {
                        articleLinkTemp.articleType = 2;
                    } else if (StringUtil.isAlpha(articleLinkTextTrail.charAt(0))) {
                        articleLinkTemp.articleType = 1;
                    }
                } else if (articleLinkTextLead.equals("早间新闻播报")) {
                    articleLinkTemp.articleType = 1;
                }
                if (articleLinkTemp.articleType != -1) {
                    articleLinkTemp.articleLinkTitle = articleLinkTextTrail;
                }
            } else {
                if (articleLinkTemp.articleLinkText.contains("节气英语说") || articleLinkTemp.articleLinkText.contains("特别节目") || articleLinkTemp.articleLinkText.contains("双语微视频")
                        || articleLinkTemp.articleLinkText.equals("今日立冬！话说中国节") || articleLinkTemp.articleLinkText.equals("Little New Year 小年")) {
                    articleLinkTemp.articleType = -1;
                } else if (articleLinkTemp.articleLinkText.contains("|") || articleLinkTemp.articleLinkText.contains("︱") || articleLinkTemp.articleLinkText.contains("｜")
                        || articleLinkTemp.articleLinkText.contains(":")
                        || articleLinkTemp.articleLinkText.contains("早间英文播报") || articleLinkTemp.articleLinkText.contains("早间新闻播报") || articleLinkTemp.articleLinkText.contains("早间英语播报")) {
                    String text = articleLinkTemp.articleLinkText.replaceAll("\\|", ":").replaceAll("︱", ":").replaceAll("｜", ":")
                            .replaceAll("早间英文播报", "").replaceAll("早间新闻播报", "").replaceAll("早间英语播报", "");
                    logger.info(articleLinkTemp.toString());
                    String title = StringUtils.trimLeadingWhitespace(text.substring(text.indexOf(":") + 1));
                    articleLinkTemp.articleType = 1;
                    articleLinkTemp.articleLinkTitle = title;
                }
            }
            tempList.add(articleLinkTemp);
        }
        articleLinkService.updateAll(tempList);
    }

    private LinkedHashSet<ArticleLink> collectChinaDailyArticleLink(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        LinkedHashSet<ArticleLink> articleLinks = new LinkedHashSet<>();
        Config config = new Config();
        config.node = "china_daily";
        for (int i = articleParam.startPage; i <= articleParam.endPage; i++) {
            LinkedHashSet<String> linkedHashSet = dictService.getXMLYTitle(articleParam.link + "/p" + i).toCompletableFuture().get();
            for (String s : linkedHashSet) {
                String[] temp = s.split("#");
                Integer index = Integer.parseInt(temp[0]);
                String linkText = temp[1].replaceAll("^\\d{1,2}\\.\\d{1,2}", "").replaceAll("^\\d{1,2}月\\d{1,2}[日月号]", "")
                        .replaceAll("Jan\\s\\d{1,2}|Feb\\s\\d{1,2}|Mar\\s\\d{1,2}|Apr\\s\\d{1,2}|May\\s\\d{1,2}|Jun\\s\\d{1,2}|July\\s\\d{1,2}|Aug\\s\\d{1,2}|Sep\\s\\d{1,2}|Oct\\s\\d{1,2}|Nov\\s\\d{1,2}|Dec\\s\\d{1,2}|April\\s\\d{1,2}", "");
                linkText = StringUtils.trimTrailingWhitespace(StringUtils.trimLeadingWhitespace(linkText));
                String href = temp[2];
                ArticleLink articleLink = new ArticleLink();
                articleLink.articlePage = i;
                articleLink.articleIndex = index;
                articleLink.articleLinkText = linkText;
                articleLink.articleLinkHref = href;
                articleLink.articleLinkTitle = "";
                articleLink.articleType = 0;
                articleLink.source = config;
                articleLinks.add(articleLink);
            }
        }
        return articleLinks;
    }

    private LinkedHashSet<ArticleLink> collectXSArticleLink(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        LinkedHashSet<ArticleLink> articleLinks = new LinkedHashSet<>();
        Config config = new Config();
        config.node = "xia_shuo";
        Set<String> titleSet = new HashSet<>();
        for (int i = articleParam.startPage; i <= articleParam.endPage; i++) {
            LinkedHashSet<String> linkedHashSet = dictService.getXMLYTitle(articleParam.link + "/p" + i).toCompletableFuture().get();
            for (String s : linkedHashSet) {
                String[] temp = s.split("#");
                Integer index = Integer.parseInt(temp[0]);
                String linkText = temp[1];
                if (linkText.contains("万娘娘") || linkText.contains("晚间一首歌") || linkText.contains("友邻优课周年纪念版")
                        || linkText.contains("春节晨读公告更新") || linkText.contains("祝祖国母亲生日快乐") || linkText.contains("直播回听")
                        || linkText.contains("十个海子在春天复活") || linkText.contains("特刊") || linkText.contains("晨读团优秀学生朗读")) {
                    continue;
                } else if (linkText.contains("|")) {
                    linkText = linkText.substring(0, linkText.indexOf("|"));
                } else if (linkText.contains("｜")) {
                    linkText = linkText.substring(0, linkText.indexOf("｜"));
                }
                linkText = StringUtils.trimTrailingWhitespace(StringUtils.trimLeadingWhitespace(
                        linkText.replaceAll("（朗读版）", "").replaceAll("（讲解版）", "")
                                .replaceAll("（慢速版）", "").replaceAll("（慢速听写版）", "")
                                .replaceAll("（暴虐朗读试听版1）", "").replaceAll("（暴虐朗读版1）", "")
                                .replaceAll("（听写版）", "").replaceAll("（暴虐跟读版）", "")
                                .replaceAll("（慢速听写训练-听写导语）", "").replaceAll("（慢速听写训练-慢速1）", "")
                                .replaceAll("（慢速听写训练-慢速2）", "").replaceAll("（慢速听写训练-慢速3）", "")
                                .replaceAll("（例句朗读版）", "").replaceAll("（credit的用法）", "")
                                .replaceAll("（虚拟语气竟然这么简单）", "").replaceAll("（慢速朗读版）", "")
                                .replaceAll("（reckon的用法）", "")));
                if (!titleSet.add(linkText)) {
                    continue;
                }
                String href = temp[2];
                ArticleLink articleLink = new ArticleLink();
                articleLink.articlePage = i;
                articleLink.articleIndex = index;
                articleLink.articleLinkText = linkText;
                articleLink.articleLinkTitle = linkText;
                articleLink.articleLinkHref = href;
                articleLink.articleType = 1;
                articleLink.source = config;
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
