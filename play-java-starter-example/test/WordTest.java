import models.common.Config;
import models.word.ArticleLink;
import models.word.WordEn;
import models.word.vo.ArticleParam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import services.word.WordEnService;
import services.word.WordService;
import services.word.impl.WordServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WordTest extends WithApplication {

    WordEnService wordEnService;
    WordService wordService;

    @Test
    public void saveChinaDailyTitle() throws ExecutionException, InterruptedException {
        ArticleParam articleParam = new ArticleParam();
        articleParam.link = "waiyu/14804689";
        articleParam.startPage = 1;
        articleParam.endPage = 33;
        wordService.saveChinaDailyArticleLink(articleParam);
    }

    @Test
    public void saveXiaShuoTitle() throws ExecutionException, InterruptedException {
        ArticleParam articleParam = new ArticleParam();
        articleParam.link = "waiyu/3240558";
        articleParam.startPage = 1;
        articleParam.endPage = 70;
        wordService.saveXSArticleLink(articleParam);
    }

    @Test
    public void updateChinaDailyArticleType() {
        Config config = new Config();
        config.node = "china_daily";
        ArticleLink articleLink = new ArticleLink();
//        articleLink.articleIndex = 631;
        articleLink.source = config;
//        articleLink.articleType = 0;
        wordService.updateChinaDailyArticleType(articleLink);
    }

    @Test
    public void saveXSArticle() throws ExecutionException, InterruptedException {
        Config config = new Config();
        config.node = "xia_shuo";
        ArticleLink articleLink = new ArticleLink();
        articleLink.source = config;
//        articleLink.articleType = 0;
//        articleLink.articleIndex = 693;
        wordService.saveXSArticle(articleLink);
    }

    @Test
    public void saveChinaDailyArticle() throws ExecutionException, InterruptedException {
        Config config = new Config();
        config.node = "china_daily";
        ArticleLink articleLink = new ArticleLink();
        articleLink.source = config;
        articleLink.articleType = 2;
//        articleLink.exArticleIndexes = Arrays.asList(874, 876, 880, 882, 893, 887, 889, 891, 897);31-48
//        articleLink.articleIndexes = Arrays.asList(756);
//        articleLink.articlePageFrom = 10;
//        articleLink.articlePage = 10;
//        wordService.saveChinaDailyArticleSingle(articleLink);
        wordService.saveChinaDailyArticleMulti(articleLink);
    }
    /*@Test
    public void listChinaDailyTitle() throws ExecutionException, InterruptedException {
        ArticleParam articleParam = new ArticleParam();
        articleParam.link = "waiyu/14804689";
        articleParam.startPage = 1;
        articleParam.endPage = 31;
//        articleParam.articleTitleList = Arrays.asList("【新课试听】刑责年龄调低：是惩罚，也是挽救");
//        articleParam.articleIndexList = Arrays.asList(477,479);
        List<ArticlePage> articlePageList = wordService.listWordEnArticleTitle4XMLY(articleParam);
        System.out.println("error");
//        articlePageList.stream().map(v -> v.errorArticleList).forEach(System.out::println);
        System.out.println("to do");
        articlePageList.stream().map(v -> v.todoArticleList).forEach(System.out::println);
        System.out.println("single");
        articlePageList.stream().map(v -> v.singleArticleLinkList).forEach(System.out::println);
        System.out.println("multi");
//        articlePageList.stream().map(v -> v.multiArticleLinkList).forEach(System.out::println);
    }*/

    @Test
    public void xiaShuo() throws ExecutionException, InterruptedException {
        ArticleParam articleParam = new ArticleParam();
        articleParam.link = "/waiyu/3240558";
        articleParam.startPage = 31;
        articleParam.endPage = 31;
        wordEnService.loadXiaShuoArticle(articleParam);
    }

    @Test
    public void chinaDaily() throws ExecutionException, InterruptedException {
        ArticleParam articleParam = new ArticleParam();
        articleParam.link = "/waiyu/14804689";
        articleParam.startPage = 31;
        articleParam.endPage = 31;
//        articleParam.articleIndexList = Arrays.asList(871);
//        articleParam.articleTitleList = Arrays.asList("");
        wordEnService.loadChinaDailyArticle(articleParam);
    }

    @Test
    public void listWordEn() {
        WordEn wordEn = new WordEn();
        List<WordEn> wordEnList = wordEnService.listWordEn(wordEn);
        wordEnList.size();
    }

    @Before
    public void create() {
        wordEnService = app.injector().instanceOf(WordEnService.class);
        wordService = app.injector().instanceOf(WordServiceImpl.class);
    }

    @After
    public void shutdown() {
        wordEnService = null;
        wordService = null;
    }
}
