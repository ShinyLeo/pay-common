package com.wanda.pay.common.helper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by robin on 16/12/22.
 */
public abstract class JsonHelper {
    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ObjectMapper NON_NULL_MAPPER = new ObjectMapper();

    static {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.setTimeZone(TimeZone.getDefault());

        NON_NULL_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        NON_NULL_MAPPER.setTimeZone(TimeZone.getDefault());
    }

    public static JsonNode toTree(String json) throws IOException {
        return MAPPER.readTree(json);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String json) throws IOException {
        if (StringUtils.isEmpty(json)) {
            return new HashMap<String, Object>(0);
        }
        return MAPPER.readValue(json, Map.class);
    }

    public static <T> T toBean(String json, Class<T> clazz) throws IOException {
        if (json == null || "".equals(json)) return null;
        JsonParser parser = JSON_FACTORY.createParser(json);
        parser.setCodec(MAPPER);
        T t = parser.readValueAs(clazz);
        parser.close();
        return t;
    }

    public static <T> T toBean(String json, TypeReference<T> ref) throws IOException {
        if (json == null || "".equals(json)) return null;
        return MAPPER.readValue(json, ref);
    }

    public static <T> List<T> toList(String json, Class<T> clazz) throws IOException {
        JavaType javaType = MAPPER.getTypeFactory().constructParametrizedType(List.class, clazz, clazz);
        return (List<T>) MAPPER.readValue(json, javaType);
    }

    public static String toJson(Object object) throws IOException {
        return useMapper(object, MAPPER);
    }

    public static String toJsonWithoutExcepTion(Object object) {
        try {
            return useMapper(object, MAPPER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJsonWithoutNull(Object object) throws IOException {
        return useMapper(object, NON_NULL_MAPPER);
    }


    private static String useMapper(Object object, ObjectMapper mapper) throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = JSON_FACTORY.createGenerator(writer);
        generator.setCodec(mapper);
        generator.writeObject(object);
        generator.close();
        writer.close();
        return writer.toString();
    }
}
