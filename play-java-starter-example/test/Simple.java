import org.junit.Assert;
import org.junit.Test;

public class Simple {
    @Test
    public void sum() {
        int a = 1;
        int b = 2;
        Assert.assertEquals(3, a + b);
    }

    @Test
    public void regex() {
        String s = "11月19日早间英文播报：Xi stresses unity, reason in virus fight,";
        String result = s.replaceAll("\\d{1,2}月\\d{1,2}日", "");
//        String result = s.replaceAll("\\d", "");
//        String s = "时间是10.1,";
//        String s = "10.1,";
//        String result = s.replaceAll("^\\d{1,2}.\\d{1,2}", "");
        System.out.println(s + " replace to " + result);
    }

    @Test
    public void sub() {
        String s = "今日立冬！话说中国节";
        s.substring(0, s.indexOf(":"));
    }
}
