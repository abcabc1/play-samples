package services.word;

import com.google.common.collect.Lists;
import io.ebean.annotation.Transactional;
import models.common.Config;
import models.word.*;
import models.word.vo.ArticleParam;
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

    public List<String> getPageLinkList(ArticleParam articleParam) {
        List<String> pageLinkList = new ArrayList<>();
        articleParam.articleStartPage = Math.max(articleParam.articleStartPage, 1);
        articleParam.articleEndPage = Math.max(articleParam.articleEndPage, 1);
        for (int p = articleParam.articleStartPage; p <= articleParam.articleEndPage; p++) {
            pageLinkList.add(articleParam.articleLink + "/p" + p);
        }
        pageLinkList.forEach(v -> System.out.println(HOST_XMLY + v));
        return pageLinkList;
    }

    @Transactional
    public void loadXiaShuoArticle(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        if (articleParam.articleLink == null || articleParam.articleLink.isEmpty()) {
            return;
        }
        List<String> pageLinkList = getPageLinkList(articleParam);
        List<WordEnArticle> wordEnArticleList = new ArrayList<>();
        for (String pageLink : pageLinkList) {
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

    @Transactional
    public void loadChinaDailyArticle(ArticleParam articleParam) throws ExecutionException, InterruptedException {
        if (articleParam.articleLink == null || articleParam.articleLink.isEmpty()) {
            return;
        }
        List<String> pageLinkList = getPageLinkList(articleParam);
        List<WordEnArticle> wordEnArticleList = new ArrayList<>();
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
                System.out.print(title);
                if (articleParam.articleIndexList != null && !articleParam.articleIndexList.isEmpty() && !articleParam.articleIndexList.contains(index)) {
                    continue;
                }
                if (articleParam.articleTitleList != null && !articleParam.articleTitleList.isEmpty() && !articleParam.articleTitleList.contains(title)) {
                    continue;
                }
                System.out.print(" [");
                List<String> articleList = dictService.getXMLYChinaDailyArticle(isSingle, link).toCompletableFuture().get();
                if (articleList != null && !articleList.isEmpty()) {
                    if (isSingle) singleArticleSize += articleList.size();
                    if (!isSingle) multiArticleSize += articleList.size();
                    articleSize += articleList.size();
                    System.out.println(articleList.size() + " single=" + singleArticleSize + " multi=" + multiArticleSize + " total=" + articleSize + "]");
                    for (String s : articleList) {
                        String[] ss = s.split("#");
                        Config config = new Config();
                        config.node = "china_daily";
                        WordEnArticle wordEnArticle = new WordEnArticle();
                        wordEnArticle.title = ss[0];
                        if (isSingle) {
                            wordEnArticle.content = ss[1];
                        } else {
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
        }
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
