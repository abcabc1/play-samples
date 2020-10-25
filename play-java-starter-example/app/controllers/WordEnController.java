package controllers;

import models.word.WordEn;
import models.word.vo.ArticleParam;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.word.WordEnService;
import utils.RequestUtil;
import utils.ResultUtil;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class WordEnController extends Controller {

    @Inject
    WordEnService wordEnService;

    public Result dictWordEn(Http.Request request) throws ExecutionException, InterruptedException {
        WordEn wordEn = RequestUtil.getModel(request, WordEn.class);
        wordEnService.dictWordEn(wordEn);
        return ok(ResultUtil.success());
    }

    public Result loadXiaShuoArticle(Http.Request request) throws ExecutionException, InterruptedException {
//        String pageLink = RequestUtil.getString(request, "pageLink", "/waiyu/3240558");
        ArticleParam articleParam = RequestUtil.getModel(request, "model", ArticleParam.class);
        wordEnService.loadXiaShuoArticle(articleParam);
        return ok(ResultUtil.success());
    }

    public Result loadChinaDailyArticle(Http.Request request) throws ExecutionException, InterruptedException {
//        String pageLink = RequestUtil.getString(request, "pageLink", "/waiyu/14804689");
        ArticleParam articleParam = RequestUtil.getModel(request, "model", ArticleParam.class);
        wordEnService.loadChinaDailyArticle(articleParam);
        return ok(ResultUtil.success());
    }

    public Result listWordEn(Http.Request request) {
        WordEn model = RequestUtil.getModel(request, WordEn.class);
        List<WordEn> wordEnList = wordEnService.listWordEn(model);
        Map<String, Object> map = new HashMap<>();
        map.put("list", wordEnList);
        return ok(ResultUtil.success(map));
    }
}
