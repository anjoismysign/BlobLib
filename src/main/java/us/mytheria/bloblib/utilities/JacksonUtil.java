package us.mytheria.bloblib.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class JacksonUtil {
    public static String serializeMap(Map<String, String> map) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = null;
        try {
            jsonResult = mapper.writer().writeValueAsString(map);
            return jsonResult;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return jsonResult;
        }
    }

    public static HashMap<String, String> deserializeHashmap(String jsonInput) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, String>> typeRef
                = new TypeReference<>() {
        };
        try {
            return mapper.readValue(jsonInput, typeRef);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
