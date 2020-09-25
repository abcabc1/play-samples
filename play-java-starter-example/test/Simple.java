import org.junit.Test;
import org.junit.Assert;

public class Simple {
    @Test
    public void sum() {
        int a = 1;
        int b = 2;
        Assert.assertEquals(3, a + b);
    }
}
