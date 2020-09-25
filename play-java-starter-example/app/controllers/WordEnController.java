package controllers;

import models.word.WordEnArticle;
import play.mvc.Controller;
import play.mvc.Result;
import services.word.WordEnService;
import services.word.impl.WordEnArticleService;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WordEnController extends Controller {

    @Inject
    WordEnService wordEnService;

    @Inject
    WordEnArticleService wordEnArticleService;

    @SuppressWarnings("unchecked")
    public Result testDb() {
        WordEnArticle wordEnArticle = new WordEnArticle();
        List<WordEnArticle> wordEnArticleList = wordEnArticleService.list(wordEnArticle);
        return play.mvc.Results.ok(wordEnArticleList.size() + "");
    }

    public Result test() throws ExecutionException, InterruptedException {
        String pageLink = "/waiyu/3240558";
        wordEnService.dictXMLYXiaShuo(pageLink);
        return ok();
    }
}
