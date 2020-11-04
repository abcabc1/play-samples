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
        if (articleParam.articleLink == null || articleParam.articleLink.isEmpty()) {
            return;
        }
        List<String> pageList = listArticlePage(articleParam);
        List<WordEnArticle> wordEnArticleList = new ArrayList<>();
        for (String pageLink : pageList) {
            LinkedList<String> titleList = dictService.getXMLYXiaShuoTitle(pageLink).toCompletableFuture().get();
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

    public List<String> listArticlePage(ArticleParam articleParam) {
        List<String> articlePageList = new ArrayList<>();
        articleParam.articleStartPage = Math.max(articleParam.articleStartPage, 1);
        articleParam.articleEndPage = Math.max(articleParam.articleEndPage, 1);
        for (int p = articleParam.articleStartPage; p <= articleParam.articleEndPage; p++) {
            articlePageList.add(articleParam.articleLink + "/p" + p);
        }
        articlePageList.forEach(v -> System.out.println(HOST_XMLY + v));
        return articlePageList;
    }

    @Transactional
    public void loadChinaDailyArticle(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        if (articleParam.articleLink == null || articleParam.articleLink.isEmpty()) {
            logger.error("article link is mission");
            return;
        }
        List<String> pageList = listArticlePage(articleParam);
        List<ArticleLink> articleLinkList = new ArrayList<>();
        for (String page : pageList) {
            if (page == null || page.isEmpty()) {
                logger.error("page is missing");
                continue;
            }
            LinkedHashSet<String> pageTitleSet = dictService.getXMLYChinaDailyTitle(page).toCompletableFuture().get();
            for (String pageTitle : pageTitleSet) {
                ArticleLink articleLink = new ArticleLink();
                String[] temp = pageTitle.split("#");
                articleLink.page = page;
                articleLink.articleIndex = Integer.parseInt(temp[0]);
                articleLink.articleLinkText = temp[1];
                articleLink.articleLinkHref = temp[2];
                if (StringUtil.isAlpha(articleLink.articleLinkText.charAt(0))) {
                    articleLink.articleType = 1;
                } else if (StringUtil.isChineseByScript(articleLink.articleLinkText.charAt(0))) {
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
            List<WordEnArticle> wordEnArticleList = new ArrayList<>();
            for (ArticleLink articleLink : articleLinkList) {
                Config config = new Config();
                config.node = "china_daily";
                if (1 == articleLink.articleType) {
                    logger.info(articleLink.articleLinkText);
                    System.out.println(articleLink.articleIndex + ":" + articleLink.articleLinkText);
                    WordEnArticle wordEnArticle = new WordEnArticle();
                    wordEnArticle.source = config;
                    wordEnArticle.answer = "";
                    wordEnArticle.linkTitle = articleLink.articleLinkText;
                    String articleContent = dictService.getXMLYChinaDailyArticleSingle(articleLink).toCompletableFuture().get();
                    wordEnArticle.articleIndex = articleLink.articleIndex;
                    wordEnArticle.title = articleLink.articleLinkText;
                    wordEnArticle.content = articleContent;
                    wordEnArticleList.add(wordEnArticle);
                } else if (2 == articleLink.articleType) {
                    logger.info(articleLink.articleLinkText);
                    System.out.println(articleLink.articleIndex + ":" + articleLink.articleLinkText);
                    List<String> articleList = dictService.getXMLYChinaDailyArticleMulti(articleLink).toCompletableFuture().get();
                    if (articleList.size() == 4) {
                        for (String article : articleList) {
                            String[] temp = article.split("#");
                            WordEnArticle wordEnArticle = new WordEnArticle();
                            wordEnArticle.source = config;
                            wordEnArticle.answer = "";
                            wordEnArticle.linkTitle = articleLink.articleLinkText;
                            wordEnArticle.title = temp[0];
                            wordEnArticle.titleNote = temp[1];
                            wordEnArticle.content = temp[2];
                            wordEnArticle.contentNote = temp[3];
                            wordEnArticleList.add(wordEnArticle);
                        }
                    }
                }
            }
            for (List<WordEnArticle> subList : Lists.partition(wordEnArticleList, 100)) {
//                wordEnArticleRepository.insertAll(subList);
            }
        }

        /*List<WordEnArticle> wordEnArticleList = new ArrayList<>();
        int articleSize = 0;
        int singleArticleSize = 0;
        int multiArticleSize = 0;
        for (String pageLink : pageLinkList) {
            LinkedHashSet<String> titleSet = dictService.getXMLYChinaDailyTitle(pageLink).toCompletableFuture().get();
            for (String titleText : titleSet) {
                String[] titleStr = titleText.split("#");
                Integer index = Integer.parseInt(titleStr[0]);
                String title = titleStr[1];
                String link = titleStr[2];
                boolean isSingle = !StringUtil.hasChinese(title) || title.isEmpty();
                if (articleParam.articleIndexList != null && !articleParam.articleIndexList.isEmpty() && !articleParam.articleIndexList.contains(index)) {
                    continue;
                }
                if (articleParam.articleTitleList != null && !articleParam.articleTitleList.isEmpty() && !articleParam.articleTitleList.contains(title)) {
                    continue;
                }
                System.out.print(title);
                System.out.print(" [");
                List<String> articleList = dictService.getXMLYChinaDailyArticle(isSingle, link).toCompletableFuture().get();
                if (articleList != null && !articleList.isEmpty()) {
                    if (isSingle) singleArticleSize += articleList.size();
                    if (!isSingle) multiArticleSize += articleList.size();
                    articleSize += articleList.size();
                    System.out.println(articleList.size() + " single=" + singleArticleSize + " multi=" + multiArticleSize + " total=" + articleSize + "]");
                    for (String s : articleList) {
                        Config config = new Config();
                        config.node = "china_daily";
                        WordEnArticle wordEnArticle = new WordEnArticle();
                        wordEnArticle.articleIndex = index;
                        if (isSingle) {
                            wordEnArticle.title = title;
                            wordEnArticle.content = s;
                        } else {
                            String[] ss = s.split("#");
                            wordEnArticle.title = ss[0];
                            wordEnArticle.titleNote = ss[1];
                            wordEnArticle.content = ss[2];
                            wordEnArticle.contentNote = ss[3];
                        }
                        wordEnArticle.source = config;
                        wordEnArticle.answer = "";
                        wordEnArticleList.add(wordEnArticle);
                    }
                }
            }
        }
        for (List<WordEnArticle> subList : Lists.partition(wordEnArticleList, 100)) {
            wordEnArticleRepository.insertAll(subList);
        }*/
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
