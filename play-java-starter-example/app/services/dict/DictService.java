package services.dict;

import models.word.vo.Article;
import models.word.vo.ArticleLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import utils.HtmlUtil;

import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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

    public CompletionStage<LinkedList<String>> getXMLYXiaShuoTitle(String pageLink) {
        return ws.url(HOST_XMLY + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYXiaShuoTitle);
    }

    public CompletionStage<LinkedHashSet<String>> getXMLYChinaDailyTitle(String pageLink) {
        return ws.url(HOST_XMLY + "/" + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYChinaDailyTitle);
    }

    public CompletionStage<List<String>> getXMLYXiaShuoArticle(String page) {
        return ws.url(HOST_XMLY + page).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYXiaShuoArticle);
    }

    /*public CompletionStage<List<String>> getXMLYChinaDailyArticle(boolean isSingle, String pageLink) {
        if (isSingle) {
            return ws.url(HOST_XMLY + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYChinaDailyArticleSingle);
        } else {
            return ws.url(HOST_XMLY + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYChinaDailyArticleMulti);
        }
    }*/

    public CompletionStage<String> getXMLYChinaDailyArticleSingle(ArticleLink articleLink) {
        return ws.url(HOST_XMLY + articleLink.articleLinkHref).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYChinaDailyArticleSingle);
    }

    public CompletionStage<List<Article>> getXMLYChinaDailyArticleMulti(ArticleLink articleLink) {
        return ws.url(HOST_XMLY + articleLink.articleLinkHref).get().thenApply(WSResponse::getBody).thenApply(v -> HtmlUtil.extractXMLYChinaDailyArticleMulti(v, articleLink));
    }
}
