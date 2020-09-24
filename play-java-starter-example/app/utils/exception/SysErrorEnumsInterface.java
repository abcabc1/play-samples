package utils.exception;

import interfaces.base.ErrorCodeInterface;

public class SysErrorEnumsInterface implements ErrorCodeInterface {

    final static SysErrorEnumsInterface DYNAMIC_EXCEPTION = new SysErrorEnumsInterface("500","动态异常");
/*
Alt+Command+U
 */

    private String code;
    private String message;

    public SysErrorEnumsInterface(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
