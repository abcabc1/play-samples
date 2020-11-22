package services.word;

import com.google.common.collect.Lists;
import io.ebean.annotation.Transactional;
import models.common.Config;
import models.word.*;
import models.word.vo.Article;
import models.word.ArticleLink;
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
    public String loadChinaDailyArticle(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        String result;
        if (articleParam.link == null || articleParam.link.isEmpty()) {
            result = "link is mission";
            logger.error(result);
            return result;
        } else {
            logger.info(articleParam.link);
        }
        // list all pages of link
        List<ArticleLink> articleLinkList = new ArrayList<>();
        List<Integer> pageList = listArticlePage(articleParam);
        for (Integer page : pageList) {
            // list article links of current page
            LinkedHashSet<String> pageTitleSet = dictService.getXMLYChinaDailyTitle(articleParam.link + "/p" + page).toCompletableFuture().get();
            for (String pageTitle : pageTitleSet) {
                ArticleLink articleLink = new ArticleLink();
                String[] temp = pageTitle.split("#");
//                articleLink.articlePage.page = page;
                articleLink.articleIndex = Integer.parseInt(temp[0]);
                articleLink.articleLinkTitle = temp[1];
                articleLink.articleLinkHref = temp[2];
                String articleLinkText = articleLink.articleLinkTitle;
                String articleLinkTextTemp = articleLinkText
                        .replaceAll("'", "")
                        .replaceAll("\"", "")
                        .replaceAll("\\?", "")
                        .replaceAll("\\d", "")
                        .replaceAll(":", "")
                        .replaceAll(",", "")
                        .replaceAll("-", "")
                        .replaceAll("\\|", "");
                if (StringUtil.isAlpha(StringUtils.trimAllWhitespace(articleLinkTextTemp)) || articleLinkTextTemp.contains("英文播报")) {
                    articleLink.articleType = 1;
                    if (articleLinkText.contains("英文播报")) {
                        articleLink.articleLinkTitle = StringUtils.trimLeadingWhitespace(articleLinkTextTemp.substring(articleLinkTextTemp.indexOf("英文播报") + 4));
                    }
                } else {
                    articleLink.articleType = 2;
                }
                if (articleParam.articleIndexList != null && !articleParam.articleIndexList.isEmpty() && !articleParam.articleIndexList.contains(articleLink.articleIndex)) {
                    continue;
                }
                if (articleParam.articleTitleList != null && !articleParam.articleTitleList.isEmpty() && !articleParam.articleTitleList.contains(articleLink.articleLinkTitle)) {
                    continue;
                }
                articleLinkList.add(articleLink);
            }
        }
        // collect articles
        int total = 0, success = 0, fail = 0, single = 0, page = 1;
        List<WordEnArticle> wordEnArticleList = new ArrayList<>();
        for (ArticleLink articleLink : articleLinkList) {
//            if (articleLink.articlePage.page != page) {
//                logger.info(String.format("                page:{%d}, pageTotal:{%d}, pageSuccess:{%d}, pageFail:{%d}, pageSingle:{%d}", articleLink.articlePage.page - 1, total, success, fail, single));
//                page = articleLink.articlePage.page;
//            }
            Config config = new Config();
            config.node = "china_daily";
            if (articleLink.articleType == 1) {
                String articleContent = dictService.getXMLYChinaDailyArticleSingle(articleLink).toCompletableFuture().get();
                if (articleContent == null || articleContent.isEmpty()) {
                    logger.error("---------------------------fail " + articleLink);
                    fail++;
                } else {
                    WordEnArticle wordEnArticle = new WordEnArticle();
                    wordEnArticle.source = config;
                    wordEnArticle.answer = "";
                    wordEnArticle.linkTitle = articleLink.articleLinkTitle;
                    wordEnArticle.articleIndex = articleLink.articleIndex;
                    wordEnArticle.title = articleLink.articleLinkTitle;
                    wordEnArticle.content = articleContent;
                    wordEnArticleList.add(wordEnArticle);
                    logger.info(articleLink.toString());
                }
                single++;
            } else if (articleLink.articleType == 2) {
                List<Article> articleList = dictService.getXMLYChinaDailyArticleMulti(articleLink).toCompletableFuture().get();
                if (articleList.size() == 4) {
                    for (Article article : articleList) {
                        WordEnArticle wordEnArticle = new WordEnArticle();
                        wordEnArticle.source = config;
                        wordEnArticle.articleIndex = article.articleLink.articleIndex;
                        wordEnArticle.linkTitle = article.articleLink.articleLinkTitle;
                        wordEnArticle.linkTitle = article.articleLink.articleLinkTitle;
                        wordEnArticle.answer = "";
                        wordEnArticle.title = article.title;
                        wordEnArticle.titleNote = article.titleNote;
                        wordEnArticle.content = article.content;
                        wordEnArticle.contentNote = article.contentNote;
                        wordEnArticleList.add(wordEnArticle);
                    }
                    success++;
                    logger.info(articleLink.toString());
                } else {
                    logger.error("------------------fail " + articleLink.toString());
                    fail++;
                }
            }
            total++;
//            System.out.println(String.format("                page:{%d}, index:{%d}, total:{%d}, success:{%d}, fail:{%d}, single:{%d}", articleLink.page, articleLink.articleIndex, total, success, fail, single));
        }
        for (List<WordEnArticle> subList : Lists.partition(wordEnArticleList, 100)) {
                wordEnArticleRepository.insertAll(subList);
        }
        result = String.format("total:{%d}, success:{%d}, fail:{%d}, single:{%d}", total, success, fail, single);
        logger.info(result);
        return result;
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
