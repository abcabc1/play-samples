package utils;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import utils.exception.InternalException;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static utils.exception.ExceptionEnum.MISSING_PARAM_IN_JSON_REQUEST;
import static utils.exception.ExceptionEnum.WRONG_TYPE_PARAM_IN_JSON_REQUEST;

public class RequestUtil {

    @NotNull
    private static JsonNode getJsonNode(Http.Request request, String object) {
        return request.body().asJson().findValue(object);
//        JsonNode jsonNode = request.body().asJson().findValue(object);
//        if (jsonNode == null) {
//            throw InternalException.build(MISSING_PARAM_IN_JSON_REQUEST, new String[]{object, request.body().asJson().toString()});
//        }
//        return jsonNode;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static <A> List<A> getModelList(Http.Request request, String object, Class<A> objectClass) {
        JsonNode jsonNode = getJsonNode(request, object);
        String typeName = jsonNode.getNodeType().name();
        if (typeName.equals("ARRAY")) {
            List<A> list = new ArrayList<>();
            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    list.add(Json.fromJson(node, objectClass));
                }
            }
            return list;
        } else {
            throw InternalException.build(WRONG_TYPE_PARAM_IN_JSON_REQUEST, new String[]{"ARRAY", typeName});
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static <A> A getModel(Http.Request request, Class<A> objectClass) {
        return getModel(request, "model", objectClass);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static <A> A getModel(Http.Request request, String object, Class<A> objectClass) {
        JsonNode jsonNode = getJsonNode(request, object);
        String typeName = jsonNode.getNodeType().name();
        if (typeName.equals("OBJECT")) {
            return Json.fromJson(jsonNode, objectClass);
        } else {
            throw InternalException.build(WRONG_TYPE_PARAM_IN_JSON_REQUEST, new String[]{"OBJECT", typeName});
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Integer getInt(Http.Request request, String object) {
        return getInt(request, object, null);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Integer getInt(Http.Request request, String object, Integer o) {
        JsonNode jsonNode = getJsonNode(request, object);
        if (jsonNode == null && o == null) {
            throw InternalException.build(MISSING_PARAM_IN_JSON_REQUEST, new String[]{object, request.body().asJson().toString()});
        } else if (jsonNode == null) {
            return o;
        }
        String typeName = jsonNode.getNodeType().name();
        if (typeName.equals("NUMBER")) {
            return jsonNode.asInt();
        } else {
            throw InternalException.build(WRONG_TYPE_PARAM_IN_JSON_REQUEST, new String[]{"NUMBER", typeName});
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static String getString(Http.Request request, String object) {
        return getString(request, object, null);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static String getString(Http.Request request, String object, String o) {
        JsonNode jsonNode = getJsonNode(request, object);
        if (jsonNode == null && o == null) {
            throw InternalException.build(MISSING_PARAM_IN_JSON_REQUEST, new String[]{object, request.body().asJson().toString()});
        } else if (jsonNode == null) {
            return o;
        }
        String typeName = jsonNode.getNodeType().name();
        if (typeName.equals("STRING")) {
            return jsonNode.asText();
        } else {
            throw InternalException.build(WRONG_TYPE_PARAM_IN_JSON_REQUEST, new String[]{"STRING", typeName});
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Boolean getBoolean(Http.Request request, String object) {
        return getBoolean(request, object, null);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Boolean getBoolean(Http.Request request, String object, Boolean o) {
        JsonNode jsonNode = getJsonNode(request, object);
        if (jsonNode == null && o == null) {
            throw InternalException.build(MISSING_PARAM_IN_JSON_REQUEST, new String[]{object, request.body().asJson().toString()});
        } else if (jsonNode == null) {
            return o;
        }
        String typeName = jsonNode.getNodeType().name();
        if (typeName.equals("BOOLEAN")) {
            return Boolean.valueOf(jsonNode.asBoolean());
        } else {
            throw InternalException.build(WRONG_TYPE_PARAM_IN_JSON_REQUEST, new String[]{"BOOLEAN", typeName});
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Long getLong(Http.Request request, String object) {
        return getLong(request, object, null);
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Long getLong(Http.Request request, String object, Long o) {
        JsonNode jsonNode = getJsonNode(request, object);
        if (jsonNode == null && o == null) {
            throw InternalException.build(MISSING_PARAM_IN_JSON_REQUEST, new String[]{object, request.body().asJson().toString()});
        } else if (jsonNode == null) {
            return o;
        }
        String typeName = jsonNode.getNodeType().name();
        if (typeName.equals("NUMBER")) {
            return Long.valueOf(jsonNode.asLong());
        } else {
            throw InternalException.build(WRONG_TYPE_PARAM_IN_JSON_REQUEST, new String[]{"NUMBER", typeName});
        }
    }
}
