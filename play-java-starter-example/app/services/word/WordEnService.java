package services.word;

import com.google.common.collect.Lists;
import io.ebean.annotation.Transactional;
import models.common.Config;
import models.word.WordEnArticle;
import repository.WordEnArticleRepository;
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

    @Transactional
    public void loadXiaShuoArticle(String pageLinkPrefix, int page) throws ExecutionException, InterruptedException {
        Config config = new Config();
        config.node = "xia_shuo";
        Map<String, WordEnArticle> wordEnArticleMap = new HashMap<>();
        List<String> pageLinkList = new ArrayList<>();
        for (int i = 1; i <= page; i++) {
            pageLinkList.add(pageLinkPrefix + "/p" + i);
        }
        pageLinkList.forEach(v -> System.out.println(HOST_XMLY + v));
        for (String pageLink : pageLinkList) {
            List<String> titleList = dictService.getXMLYXiaShuoTitle(pageLink).toCompletableFuture().get();
            for (String titleText : titleList) {
                System.out.println(titleText);
                String[] titleStr = titleText.split("#");
                String title = titleStr[0];
                List<String> articleList = dictService.getXMLYXiaShuoArticle(titleStr[1]).toCompletableFuture().get();
                String article = String.join(" ", articleList);
                WordEnArticle wordEnArticle = new WordEnArticle();
                wordEnArticle.titleNote = title;
                wordEnArticle.content = article;
                wordEnArticle.source = config;
                wordEnArticleMap.put(title, wordEnArticle);
            }
        }

//        wordEnArticleRepository.insertAll(wordEnArticleMap);
    }

    @Transactional
    public void loadChinaDailyArticle(String pageLinkPrefix, int page) throws ExecutionException, InterruptedException {
        Config config = new Config();
        config.node = "china_daily";
        List<WordEnArticle> wordEnArticleList = new ArrayList<>();
        List<String> pageLinkList = new ArrayList<>();
        for (int i = 1; i <= page; i++) {
            pageLinkList.add(pageLinkPrefix + "/p" + i);
        }
        pageLinkList.forEach(v -> System.out.println(HOST_XMLY + v));
        int articleSize = 0;
        int singleArticleSize = 0;
        int multiArticleSize = 0;
        for (String pageLink : pageLinkList) {
            Set<String> titleSet = dictService.getXMLYChinaDailyTitle(pageLink).toCompletableFuture().get();
            for (String titleText : titleSet) {
                String[] titleStr = titleText.split("#");
                String title = titleStr[0];
                String link = titleStr[1];
                boolean isSingle = !StringUtil.hasChinese(title) || title.isEmpty();
                if (!title.equals("Nation honors UN, multilateralism pledge")) continue;
                System.out.print(title);
                System.out.print(" [");
                List<String> articleList = dictService.getXMLYChinaDailyArticle(isSingle, link).toCompletableFuture().get();
                if (articleList != null && !articleList.isEmpty()) {
                    if (isSingle) singleArticleSize += articleList.size();
                    if (!isSingle) multiArticleSize += articleList.size();
                    articleSize += articleList.size();
                    System.out.println(articleList.size() + " single=" + singleArticleSize + " multi=" + multiArticleSize + " total=" + articleSize + "]");
                    for (String s : articleList) {
                        String[] ss = s.split("#");
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
                        wordEnArticleList.add(wordEnArticle);
                    }
                }
            }
        }
        for (List<WordEnArticle> subList : Lists.partition(wordEnArticleList, 100)) {
            wordEnArticleRepository.insertAll(subList);
        }
    }
}
