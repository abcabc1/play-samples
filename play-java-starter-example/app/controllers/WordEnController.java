package controllers;

import models.word.ArticleLink;
import models.word.WordEn;
import models.word.vo.ArticleLinkForm;
import models.word.vo.ArticleParam;
import org.springframework.beans.BeanUtils;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.word.WordEnService;
import services.word.WordService;
import services.word.impl.WordServiceImpl;
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

    @Inject
    WordServiceImpl wordService;

    @Inject
    FormFactory formFactory;

    public Result saveChinaDailyArticle(Http.Request request) {
        ArticleLinkForm articleLinkForm = formFactory.form(ArticleLinkForm.class).bindFromRequest(request).get();
        ArticleLink articleLink = new ArticleLink();
        BeanUtils.copyProperties(articleLinkForm, articleLink);
        wordService.saveChinaDailyArticleMulti(articleLink);
        return play.mvc.Results.ok();
    }

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
        Map<String, Object> map = new HashMap<>();
        ArticleParam articleParam = RequestUtil.getModel(request, "model", ArticleParam.class);
        String result = wordEnService.loadChinaDailyArticle(articleParam);
        map.put("result", result);
        return ok(ResultUtil.success(map));
    }

    public Result listWordEn(Http.Request request) {
        WordEn model = RequestUtil.getModel(request, WordEn.class);
        List<WordEn> wordEnList = wordEnService.listWordEn(model);
        Map<String, Object> map = new HashMap<>();
        map.put("list", wordEnList);
        return ok(ResultUtil.success(map));
    }
}
