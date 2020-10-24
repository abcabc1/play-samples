package utils.exception;

import interfaces.base.ErrorCodeInterface;

import java.text.MessageFormat;

public class InternalException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private ErrorCodeInterface errorCodeInterface;

    public InternalException(ErrorCodeInterface errorCodeInterface) {
        super();
        this.errorCodeInterface = errorCodeInterface;
    }

    public String getCode() {
        return this.errorCodeInterface.getCode();
    }

    public static InternalException build(String code) {
        ErrorCodeInterface errorCodeInterface = new SysErrorEnumsInterface(code, "");
        return new InternalException(errorCodeInterface);
    }

    public static InternalException build(String code, String message) {
        ErrorCodeInterface errorCodeInterface = new SysErrorEnumsInterface(code, message);
        return new InternalException(errorCodeInterface);
    }

    public static InternalException build(ExceptionEnum exceptionEnum) {
        ErrorCodeInterface errorCodeInterface = new SysErrorEnumsInterface(exceptionEnum.getCode(), MessageFormat.format(exceptionEnum.getTemplate(), ""));
        return new InternalException(errorCodeInterface);
    }

    public static InternalException build(ExceptionEnum exceptionEnum, Object[] message) {
        ErrorCodeInterface errorCodeInterface = new SysErrorEnumsInterface(exceptionEnum.getCode(), MessageFormat.format(exceptionEnum.getTemplate(), message));
        return new InternalException(errorCodeInterface);
    }

    public String getMessage() {
        return this.errorCodeInterface.getMessage();
    }
}
