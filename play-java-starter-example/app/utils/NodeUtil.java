package utils;

import com.fasterxml.jackson.databind.JsonNode;

public class NodeUtil {

    public static JsonNode resolveNode(JsonNode node, String object) {
        String[] paths = object.split("/");
        JsonNode jsonNode = null;
        for (int i = 0; i < paths.length; i++) {
            if (i == 0) {
                jsonNode = node.get(paths[i]);
            } else {
                jsonNode = jsonNode.get(paths[i]);
            }
        }
        return jsonNode;
    }
}
