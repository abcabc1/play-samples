package utils.exception;

public enum ExceptionEnum {

    MODEL_NOT_FOUND_IN_DB("501", "Model {0} key {1} is not found in DB"),
    MOVE_NODE_TO_CHILD("502", "Move Model {0} node {1} to child {2}"),
    MISSING_PARAM_IN_JSON_REQUEST("503", "Missing param {0} in json request {1}"),
    WRONG_TYPE_PARAM_IN_JSON_REQUEST("504", "Expect {0} param but found {1} in json request"),
    DICT_EN_FAILURE("505", "Found {0} En dict failure"),
    DICT_CN_FAILURE("506", "Found {0} Cn dict failure"),
    RESET_TREE_CHILD_NODE_FAILURE_1("507", "Can not relocate child with NULL parent"),
    RESET_TREE_CHILD_NODE_FAILURE_2("508", "Missing parent's level or seq when relocate child"),
    RESET_TREE_CHILD_NODE_FAILURE_3("509", "No need handle child if parent doesn't shift position"),
    ALI_PAY_TRADE_CREATE_FAILURE("101", "Ali pay trade create fail"),
    ALI_PAY_TRADE_QUERY_FAILURE("102", "Ali pay trade query fail"),
    ALI_PAY_PASS_TEMPLATE_ADD_FAILURE("104", "Ali pass template add fail"),
    ALI_PAY_PASS_INSTANCE_ADD_FAILURE("105", "Ali pass instance add fail"),
    ALI_PAY_SYSTEM_OAUTH_TOKEN_FAILURE("103", "Ali pay system oauth token fail"),
    EXCEL_PROCESS_FAILURE("601", "Excel processing, Waiting for stop"),
    MISSING_ARTICLE_TITLE_FAILURE("word", "Missing article title"),
    FINAL_ARTICLE_FAILURE("word", "Final article extract fail"),
    MATCH_ARTICLE_TITLE_4_CONTENT_FAILURE("word", "Match article title fail");

    // 成员变量
    private String code;
    private String template;

    ExceptionEnum(String code, String template) {
        this.code = code;
        this.template = template;
    }

    // 普通方法
    public static String getTemplateByCode(String code) {
        for (ExceptionEnum e : ExceptionEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.template;
            }
        }
        return null;
    }

    // get set 方法
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
