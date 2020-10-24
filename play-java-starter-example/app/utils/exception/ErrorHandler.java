package utils.exception;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.http.HttpErrorHandler;
import play.libs.Json;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import utils.exception.InternalException;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class ErrorHandler implements HttpErrorHandler {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    public CompletionStage<Result> onClientError(
            RequestHeader request, int statusCode, String message) {
        return CompletableFuture.completedFuture(
                Results.status(statusCode, "A client error occurred: " + message));
    }

    public CompletionStage<Result> onServerError(RequestHeader request, Throwable exception) {
        String message = exception.getMessage();
        String code = "500";
        if (message == null) {
            message = exception.getClass().getSimpleName() + " " + exception.getStackTrace()[0].toString();
        }
        ObjectNode objectNode = getErrorJsonNode(exception, message, code);
        objectNode.put("path", request.path());
        exception.printStackTrace();
        logger.error(message);
        return CompletableFuture.completedFuture(
                Results.internalServerError(objectNode));
    }

    @NotNull
    private ObjectNode getErrorJsonNode(Throwable exception, String message, String code) {
        ObjectNode objectNode = Json.newObject();
        if (exception instanceof InternalException) {
            code = ((InternalException) exception).getCode();
            message = exception.getMessage();
        }
        objectNode.put("code", code);
        objectNode.put("message", "A server error occurred: " + message);
        return objectNode;
    }
}