package utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;

import java.util.HashMap;
import java.util.Map;

public class ResultUtil {

    public final static JsonNode EMPTY_NODE = Json.newObject();

    public static JsonNode success() {
        Map<String, Object> map = new HashMap<>();
        return success(map);
    }

    public static JsonNode success(Map<String, Object> map) {
        map.put("success", true);
        return getMapJson(map);
    }

    public static JsonNode failure() {
        Map<String, Object> map = new HashMap<>();
        return failure(map);
    }

    public static JsonNode failure(String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        return failure(map);
    }

    public static JsonNode failure(String code, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        return failure(map);
    }

    public static JsonNode failure(Map<String, Object> map) {
        map.put("success", false);
        return getMapJson(map);
    }

    /*public static JsonNode failure() {
        return failure(null, null);
    }

    public static JsonNode failure(String code) {
        String key = "isSuccess,code,message";
        return ok(ResultUtil.getMJson(key, false, false, code, null));
    }

    public static JsonNode failure(String code, String message) {
        String key = "isSuccess,code,message";
        return ResultUtil.getMJson(key, false, false, code, message);
    }

    public static JsonNode success(String key, Object... o) {
        ObjectNode objectNode = Json.newObject();
        objectNode.put("isSuccess", true);
        ObjectNode dataNode = (ObjectNode) ResultUtil.getMJson(key, o);
        objectNode.set("data", dataNode);
        return objectNode;
    }

    public static JsonNode success(Boolean ignoreNull, String key, Object... o) {
        String[] keys = key.split(",");
        ObjectNode objectNode = Json.newObject();
        objectNode.put("isSuccess", true);
        ObjectNode dataNode = (ObjectNode) ResultUtil.getMJson(key, ignoreNull, o);
        objectNode.set("data", dataNode);
        return objectNode;
    }

    public static JsonNode success() {
        String key = "isSuccess";
        return ResultUtil.getMJson(key, true, true);
    }

    public static JsonNode getJson(Object o) {
        return getJson(o, false);
    }

    public static JsonNode getJson(Object o, Boolean ignoreNull) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (ignoreNull) {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        }
        Json.setObjectMapper(objectMapper);
        return (o == null) ? EMPTY_NODE : Json.toJson(o);
    }

    public static JsonNode getMJson(String key, Object... o) {
        return getMJson(key, true, o);
    }

    public static JsonNode getMJson(String key, Boolean ignoreNull, Object... o) {
        if (key == null || o == null) {
            return EMPTY_NODE;
        }
        String[] keys = key.split(",");
        if (keys.length != o.length) {
            return EMPTY_NODE;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < o.length; i++) {
            if (o[i] == null) {
                continue;
            }
            map.put(keys[i].trim(), o[i]);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        if (ignoreNull) {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        }
        String jsonString = "{}";
        try {
            jsonString = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Json.parse(jsonString);
    }*/

    private static JsonNode getMapJson(Map<String, Object> map) {
        return getMapJson(map, true);
    }

    private static JsonNode getMapJson(Map<String, Object> map, Boolean ignoreNull) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (ignoreNull) {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        }
        String jsonString = "{}";
        try {
            jsonString = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Json.parse(jsonString);
    }
}
