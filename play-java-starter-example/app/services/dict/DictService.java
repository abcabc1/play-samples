package services.dict;

import models.word.ArticleLink;
import models.word.vo.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import utils.HtmlUtil;

import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import static utils.Constant.*;

public class DictService {

    private static WSClient ws;

    private static final Logger logger = LoggerFactory.getLogger(DictService.class);

    @Inject
    public DictService(WSClient ws) {
        this.ws = ws;
    }

    public CompletionStage<Map<String, List<String>>> dictWordEn(String word) {
        return ws.url(HOST_DICT + "/search").addQueryParameter("q", word).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractDictEn);
    }

    public CompletionStage<Map<String, List<String>>> dictWordCn(String word) {
        return ws.url(HOST_HANS + "/" + word).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractHans);
    }

    public CompletionStage<List<String>> dictPhraseCn(String word) {
        return ws.url(HOST_HANS + "/" + word).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractHansPhrase);
    }

    public CompletionStage<Map<String, List<String>>> dictPhraseEn(String word) {
        return ws.url(HOST_DICT + "/" + word).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractDictPhraseEn);
    }

    public CompletionStage<LinkedHashSet<String>> getXiaShuoTitle(String pageLink) {
        return ws.url(HOST_XMLY + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYTitle);
    }

    public CompletionStage<LinkedHashSet<String>> getXMLYTitle(String pageLink) {
        return ws.url(HOST_XMLY + "/" + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYTitle);
    }

    public CompletionStage<String> getXSArticle(ArticleLink articleLink) {
        return ws.url(HOST_XMLY + articleLink.articleLinkHref).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXSArticle);
    }

    /*public CompletionStage<List<String>> getXMLYChinaDailyArticle(boolean isSingle, String pageLink) {
        if (isSingle) {
            return ws.url(HOST_XMLY + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYChinaDailyArticleSingle);
        } else {
            return ws.url(HOST_XMLY + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYChinaDailyArticleMulti);
        }
    }*/

    public CompletionStage<String> getChinaDailyArticleSingle(ArticleLink articleLink) {
        return ws.url(HOST_XMLY + articleLink.articleLinkHref).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractChinaDailyArticleSingle);
    }

    public CompletionStage<List<Article>> getChinaDailyArticleMulti1(ArticleLink articleLink) {
        return ws.url(HOST_XMLY + articleLink.articleLinkHref).get().thenApply(WSResponse::getBody).thenApply(v -> HtmlUtil.extractChinaDailyArticleMulti1(v, articleLink));
    }

    public CompletionStage<List<String>> getChinaDailyArticleMulti(ArticleLink articleLink) {
        return ws.url(HOST_XMLY + articleLink.articleLinkHref).get().thenApply(WSResponse::getBody).thenApply(v -> HtmlUtil.extractChinaDailyArticleMulti(v, articleLink));
    }
}
