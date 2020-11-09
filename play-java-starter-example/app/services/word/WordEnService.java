package services.word;

import com.google.common.collect.Lists;
import io.ebean.annotation.Transactional;
import models.common.Config;
import models.word.*;
import models.word.vo.ArticleLink;
import models.word.vo.ArticleParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import repository.word.WordEnArticleRepository;
import repository.word.WordEnExtendRepository;
import repository.word.WordEnRepository;
import repository.word.WordEnSentenceRepository;
import services.dict.DictService;
import utils.StringUtil;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static utils.Constant.HOST_XMLY;

public class WordEnService {

    @Inject
    DictService dictService;

    @Inject
    WordEnArticleRepository wordEnArticleRepository;

    @Inject
    WordEnRepository wordEnRepository;

    @Inject
    WordEnExtendRepository wordEnExtendRepository;

    @Inject
    WordEnSentenceRepository wordEnSentenceRepository;

    private static final Logger logger = LoggerFactory.getLogger(WordEnService.class);

    @Transactional
    public void loadXiaShuoArticle(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        if (articleParam.link == null || articleParam.link.isEmpty()) {
            return;
        }
        List<Integer> pageList = listArticlePage(articleParam);
        List<WordEnArticle> wordEnArticleList = new ArrayList<>();
        for (Integer page : pageList) {
            LinkedList<String> titleList = dictService.getXMLYXiaShuoTitle(articleParam.link + "/p" + page).toCompletableFuture().get();
            for (String titleText : titleList) {
                System.out.println(titleText);
                String[] titleStr = titleText.split("#");
                Integer index = Integer.parseInt(titleStr[0]);
                String title = titleStr[1];
                if (articleParam.articleIndexList != null && !articleParam.articleIndexList.isEmpty() && !articleParam.articleIndexList.contains(index)) {
                    continue;
                }
                if (articleParam.articleTitleList != null && !articleParam.articleTitleList.isEmpty() && !articleParam.articleTitleList.contains(title)) {
                    continue;
                }
                List<String> articleList = dictService.getXMLYXiaShuoArticle(titleStr[2]).toCompletableFuture().get();
                String article = String.join(" ", articleList);
                Config config = new Config();
                config.node = "xia_shuo";
                WordEnArticle wordEnArticle = new WordEnArticle();
                wordEnArticle.titleNote = title;
                wordEnArticle.content = article;
                wordEnArticle.source = config;
                wordEnArticle.answer = "";
                wordEnArticleList.add(wordEnArticle);
            }
        }
        wordEnArticleRepository.insertAll(wordEnArticleList);
    }

    private List<Integer> listArticlePage(ArticleParam articleParam) {
        List<Integer> pageList = new ArrayList<>();
        articleParam.startPage = Math.max(articleParam.startPage, 1);
        articleParam.endPage = Math.max(articleParam.endPage, 1);
        for (int page = articleParam.startPage; page <= articleParam.endPage; page++) {
            pageList.add(page);
        }
        return pageList;
    }

    /**
     * @startuml (*)  --> "获取参数 ArticleParam"
     * If "参数链接是否存在" then
     * --> [Yes] "获取页码地址"
     * --> "run command"
     * else
     * --> (*)
     * Endif
     * -->(*)
     * @enduml
     */
    @Transactional
    public void loadChinaDailyArticle(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        if (articleParam.link == null || articleParam.link.isEmpty()) {
            logger.error("link is mission");
            return;
        } else {
            logger.info(articleParam.link);
        }
        // list all pages of link
        List<Integer> pageList = new ArrayList<>();
        articleParam.startPage = Math.max(articleParam.startPage, 1);
        articleParam.endPage = Math.max(articleParam.endPage, 1);
        for (int page = articleParam.startPage; page <= articleParam.endPage; page++) {
            pageList.add(page);
        }
        List<ArticleLink> articleLinkList = new ArrayList<>();
        int sum = 0, successSum = 0, failSum = 0, singleSum = 0;
        for (Integer page : pageList) {
            // list article links of current page
            LinkedHashSet<String> pageTitleSet = dictService.getXMLYChinaDailyTitle(articleParam.link + "/p" + page).toCompletableFuture().get();
            for (String pageTitle : pageTitleSet) {
                ArticleLink articleLink = new ArticleLink();
                String[] temp = pageTitle.split("#");
                articleLink.page = page;
                articleLink.articleIndex = Integer.parseInt(temp[0]);
                articleLink.articleLinkText = temp[1];
                articleLink.articleLinkHref = temp[2];
                String articleLinkText = articleLink.articleLinkText;
                String articleLinkTextTemp = articleLinkText
                        .replaceAll("'", "")
                        .replaceAll("\"", "")
                        .replaceAll("\\?", "")
                        .replaceAll("\\d", "")
                        .replaceAll(":", "")
                        .replaceAll(",", "");
                if (StringUtil.isAlpha(StringUtils.trimAllWhitespace(articleLinkTextTemp))) {
                    articleLink.articleType = 1;
                } else {
                    articleLink.articleType = 2;
                }
                if (articleParam.articleIndexList != null && !articleParam.articleIndexList.isEmpty() && !articleParam.articleIndexList.contains(articleLink.articleIndex)) {
                    continue;
                }
                if (articleParam.articleTitleList != null && !articleParam.articleTitleList.isEmpty() && !articleParam.articleTitleList.contains(articleLink.articleLinkText)) {
                    continue;
                }
                articleLinkList.add(articleLink);
            }
        }
        // collect articles
        int total = 0, success = 0, fail = 0, single = 0;
        List<WordEnArticle> wordEnArticleList = new ArrayList<>();
        for (ArticleLink articleLink : articleLinkList) {
            Config config = new Config();
            config.node = "china_daily";
            if (1 == articleLink.articleType) {
                String articleContent = dictService.getXMLYChinaDailyArticleSingle(articleLink).toCompletableFuture().get();
                if (articleContent == null || articleContent.isEmpty()) {
                    System.out.println("---------------------------fail " + articleLink);
                    fail++;
                    continue;
                }
                WordEnArticle wordEnArticle = new WordEnArticle();
                wordEnArticle.source = config;
                wordEnArticle.answer = "";
                wordEnArticle.linkTitle = articleLink.articleLinkText;
                wordEnArticle.articleIndex = articleLink.articleIndex;
                wordEnArticle.title = articleLink.articleLinkText;
                wordEnArticle.content = articleContent;
                wordEnArticleList.add(wordEnArticle);
                single++;
                total++;
                logger.info(articleLink.toString());
                System.out.println(articleLink);
            } else if (2 == articleLink.articleType) {/*
                    List<Article> articleList = dictService.getXMLYChinaDailyArticleMulti(articleLink).toCompletableFuture().get();
                    if (articleList.size() == 4) {
                        for (Article article : articleList) {
                            WordEnArticle wordEnArticle = new WordEnArticle();
                            wordEnArticle.source = config;
                            wordEnArticle.articleIndex = article.articleLink.articleIndex;
                            wordEnArticle.linkTitle = article.articleLink.articleLinkText;
                            wordEnArticle.linkTitle = article.articleLink.articleLinkText;
                            wordEnArticle.answer = "";
                            wordEnArticle.title = article.title;
                            wordEnArticle.titleNote = article.titleNote;
                            wordEnArticle.content = article.content;
                            wordEnArticle.contentNote = article.contentNote;
                            wordEnArticleList.add(wordEnArticle);
                        }
                        success++;
                        logger.info(articleLink.toString());
                        System.out.println(articleLink);
                    } else {
                        System.out.println("------------------fail " + articleLink.toString());
                        fail++;
                    }
                    total++;*/
            }
            sum += total;
            successSum += success;
            failSum += fail;
            singleSum += single;
            System.out.println(String.format("                page:{%d}, index:{%d}, total:{%d}, success:{%d}, fail:{%d}", articleLink.page, articleLink.articleIndex, total, success, fail));
        }
        for (List<WordEnArticle> subList : Lists.partition(wordEnArticleList, 100)) {
//                wordEnArticleRepository.insertAll(subList);
        }
        System.out.println(String.format("total:{%d}, success:{%d}, fail:{%d}, single:{%d}", sum, successSum, failSum, singleSum));
    }

    public void dictWordEn(WordEn model) throws ExecutionException, InterruptedException {
        List<WordEn> wordEnList = wordEnRepository.list(model, "id asc");
        List<WordEnExtend> wordEnExtendList = new ArrayList<>();
        List<WordEnSentence> wordEnSentenceList = new ArrayList<>();
        for (WordEn wordEn : wordEnList) {
            Map<String, List<String>> map = dictService.dictWordEn(wordEn.word).toCompletableFuture().get();
            List<String> soundList = map.get("sound");
            if (soundList != null && soundList.size() == 2) {
                wordEn.soundUk = map.get("sound").get(0);
                wordEn.soundUs = map.get("sound").get(1);
            }
            int no = 0;
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                {
                    String key = entry.getKey();
                    if (key.equals("sound")) {
                        continue;
                    }
                    no = no + 1;

                    WordEnExtendPk wordEnExtendPk = new WordEnExtendPk();
                    wordEnExtendPk.word = wordEn.word;
                    wordEnExtendPk.type = (key.contains(".")) ? key.substring(0, key.indexOf(".")) : "";
                    wordEnExtendPk.no = no;

                    WordEnExtend wordEnExtend = new WordEnExtend();
                    wordEnExtend.pk = wordEnExtendPk;
                    wordEnExtend.wordNote = (key.contains(".")) ? key.substring(key.indexOf(".") + 1) : "";
                    wordEnExtendList.add(wordEnExtend);

                    List<String> valueList = entry.getValue();
                    Set<String> sentenceSet = new HashSet<>();
                    for (String s : valueList) {
                        WordEnSentencePk wordEnSentencePk = new WordEnSentencePk();
                        wordEnSentencePk.word = wordEnExtendPk.word;
                        wordEnSentencePk.type = wordEnExtendPk.type;
                        wordEnSentencePk.no = wordEnExtendPk.no;
                        wordEnSentencePk.sentence = (s.contains("<br>")) ? s.substring(0, s.indexOf("<br>")) : "";

                        WordEnSentence wordEnSentence = new WordEnSentence();
                        wordEnSentence.pk = wordEnSentencePk;
                        wordEnSentence.sentenceNote = (s.contains("<br>")) ? s.substring(s.indexOf("<br>") + 4) : "";
                        if (!sentenceSet.contains(wordEnSentencePk.sentence)) {
                            sentenceSet.add(wordEnSentencePk.sentence);
                            wordEnSentenceList.add(wordEnSentence);
                        }
                    }
                }
            }
        }
        wordEnRepository.updateAll(wordEnList);
        wordEnExtendRepository.saveAll(wordEnExtendList);
        wordEnSentenceRepository.saveAll(wordEnSentenceList);
    }

    public List<WordEn> listWordEn(WordEn model) {
        return wordEnRepository.list(model);
    }
}
