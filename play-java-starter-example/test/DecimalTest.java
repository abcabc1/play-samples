import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalTest {

    @Test
    public void round() {
//        BigDecimal bigDecimal = BigDecimal.valueOf(1.001);
        BigDecimal bigDecimal = BigDecimal.valueOf(1.009);
//        BigDecimal bigDecimal = BigDecimal.valueOf(-1.001);
//        BigDecimal bigDecimal = BigDecimal.valueOf(-1.009);
        BigDecimal roundUp = bigDecimal.setScale(2, RoundingMode.UP);
        BigDecimal roundDown = bigDecimal.setScale(2, RoundingMode.DOWN);
        BigDecimal roundHalfUp = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundHalfDown = bigDecimal.setScale(2, RoundingMode.HALF_DOWN);
        BigDecimal roundCeiling = bigDecimal.setScale(2, RoundingMode.CEILING);
        BigDecimal roundFloor = bigDecimal.setScale(2, RoundingMode.FLOOR);
//        BigDecimal roundUnnecessary = bigDecimal.setScale(2,RoundingMode.UNNECESSARY);
        BigDecimal roundHalfEven = bigDecimal.setScale(2, RoundingMode.HALF_EVEN);
        System.out.println("Round UP =" + roundDown.doubleValue());
        System.out.println("Round DOWN =" + roundFloor.doubleValue());
        System.out.println("Round HALF_UP =" + roundHalfUp.doubleValue());
        System.out.println("Round HALF_DOWN =" + roundHalfDown.doubleValue());
        System.out.println("Round CEILING =" + roundUp.doubleValue());
        System.out.println("Round FLOOR =" + roundCeiling.doubleValue());
        System.out.println("Round HALF_EVEN =" + roundHalfEven.doubleValue());
    }
}
