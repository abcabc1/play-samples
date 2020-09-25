import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class StringTest {

    @Test
    public void empty() {
        String str = "";
        Assert.assertFalse("String is empty", str.isEmpty());
    }

    @Test
    public void trimString() {
        String s1 = "a", s2 = "a    ";
        Long l1 = 1L;
        long l2 = 1;
        Assert.assertEquals(s1, s2.trim());
//        Assert.assertTrue(l1 == l2);
//        Assert.assertEquals(s1, s2);
//        Assert.assertTrue(l1.equals(l2));
    }

    @Test
    public void split() {
        String s = ",";
        if (s.trim().length() != 0) {
            TestCase.assertEquals(1, s.trim().split(",").length);
        } else {
            TestCase.assertEquals(1, s.trim().split(",").length);
        }
    }
}
