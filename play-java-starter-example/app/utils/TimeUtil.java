package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String getFormatNow() {
        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(nowTime);
    }
}
