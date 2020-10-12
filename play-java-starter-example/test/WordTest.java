import models.word.vo.ArticleParam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;
import services.word.WordEnService;

import java.util.concurrent.ExecutionException;

public class WordTest extends WithApplication {

    WordEnService wordEnService;

    @Before
    public void create() {
        wordEnService = app.injector().instanceOf(WordEnService.class);
    }

    @After
    public void shutdown() {
        wordEnService = null;
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {
        ArticleParam articleParam = new ArticleParam();
        articleParam.articlePageLink = "/waiyu/3240558";
        articleParam.articleStartPage = 1;
        articleParam.articleEndPage = 1;
        wordEnService.loadXiaShuoArticle(articleParam);
    }
}
