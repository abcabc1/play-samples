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
        String pageLink = "/waiyu/3240558";
        int page = 1;
        wordEnService.loadXiaShuoArticle(pageLink, page);
    }
}
