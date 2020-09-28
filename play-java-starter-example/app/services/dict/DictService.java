package services.dict;

import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import utils.HtmlUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import static utils.Constant.*;

public class DictService {

    private static WSClient ws;

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

    public CompletionStage<List<String>> getXMLYXiaShuoTitle(String pageLink) {
        return ws.url(HOST_XMLY + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYXiaShuoTitle);
    }

    public CompletionStage<Set<String>> getXMLYChinaDailyTitle(String pageLink) {
        return ws.url(HOST_XMLY + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYChinaDailyTitle);
    }

    public CompletionStage<List<String>> getXMLYXiaShuoArticle(String page) {
        return ws.url(HOST_XMLY + page).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYXiaShuoArticle);
    }

    public CompletionStage<List<String>> getXMLYChinaDailyArticle(boolean isSingle, String pageLink) {
        if (isSingle) {
            return ws.url(HOST_XMLY + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYChinaDailyArticleSingle);
        } else {
            return ws.url(HOST_XMLY + pageLink).get().thenApply(WSResponse::getBody).thenApply(HtmlUtil::extractXMLYChinaDailyArticleMulti);
        }
    }
}
