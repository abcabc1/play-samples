package utils;

import akka.actor.ActorRef;

public class Constant {

    public static String applicationPath;
    public static String authCode;
    public static final int SPACE_NUM = 3;
    public final static int INSERT = 0;
    public final static int UPDATE = 1;
    public final static String PWD = "111111";
    public static final String HOST_BAIDU = "http://api.map.baidu.com";
    public static final String AK_BAIDU = "BPkri1sw4IBu0TBoI6EE8g7N5fk7ysay";
    public static final String HOST_HANS = "https://www.zdic.net/hans";
    public static final String HOST_DICT = "https://dict.cn";
    public static final String HOST_XMLY = "https://www.ximalaya.com";
    public static final String HOST_ALI_PAY = "https://openauth.alipaydev.com/oauth2/publicAppAuthorize.htm";
    public static final String HOST_ALI_PAY_CALLBACK = "http://localhost:9000/alipay/callback";
    public static final String ALI_PAY_SCOPE = "auth_user";
    public static final String ALI_PAY_APP_ID = "2016102200736481";
    public static final String ALI_BUYER_ID = "2088102180995993";
    public static final int MAX_PAGE_SIZE = 1000;
    public static ActorRef actorRef;
    public static final String[] ABBR = {"num.","int.","n.","v.","vi.","vt.","adj.","adv.","conj.","aux.","pron.","prep.","int."};
}