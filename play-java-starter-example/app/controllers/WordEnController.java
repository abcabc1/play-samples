package controllers;

import models.word.WordEn;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.word.WordEnService;
import utils.RequestUtil;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class WordEnController extends Controller {

    @Inject
    WordEnService wordEnService;

    public Result dictWordEn(Http.Request request) throws ExecutionException, InterruptedException {
        WordEn wordEn = RequestUtil.getModel(request, WordEn.class);
        wordEnService.dictWordEn(wordEn);
        return play.mvc.Results.ok();
    }

    public Result loadXiaShuoArticle(Http.Request request) throws ExecutionException, InterruptedException {
        String pageLink = RequestUtil.getString(request, "pageLink", "/waiyu/3240558");
        Integer page = RequestUtil.getInt(request, "page", 1);
        wordEnService.loadXiaShuoArticle(pageLink, page);
        return play.mvc.Results.ok();
    }

    public Result loadChinaDailyArticle(Http.Request request) throws ExecutionException, InterruptedException {
        String pageLink = RequestUtil.getString(request, "pageLink", "/waiyu/14804689");
        Integer page = RequestUtil.getInt(request, "page", 1);
        wordEnService.loadChinaDailyArticle(pageLink, page);
        return play.mvc.Results.ok();
    }
}
