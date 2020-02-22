package com.authguard.dal.jdbc.util;

import java.util.Map;
import java.util.function.Function;

public class FieldMapper {
    private final String fieldName;
    private final Class<?> targetType;
    private final Function<Object, Map<String, Object>> serializer;
    private final Function<Map<String, Object>, Map<String, Map<String, Object>>> normalizer;

    public FieldMapper(final String fieldName, final Class<?> targetType,
                       final Function<Object, Map<String, Object>> serializer,
                       final Function<Map<String, Object>, Map<String, Map<String, Object>>> normalizer) {
        this.fieldName = fieldName;
        this.targetType = targetType;
        this.serializer = serializer;
        this.normalizer = normalizer;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Map<String, Object> objectToMap(final Object object) {
        return serializer.apply(object);
    }

    public Map<String, Map<String, Object>> normalize(final Map<String, Object> map) {
        return normalizer.apply(map);
    }
}
