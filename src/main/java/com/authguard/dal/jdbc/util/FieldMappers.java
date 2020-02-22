package com.authguard.dal.jdbc.util;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FieldMappers {
    public static FieldMapper shallow(final Object object, final String fieldName) {
        try {
            return shallow(object.getClass().getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static FieldMapper shallow(final Class<?> clazz, final String fieldName) {
        try {
            return shallow(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static FieldMapper shallow(final Field field) {
        return new FieldMapper(field.getName(), field.getType(), objectToMap(field), normalizeMap(field));
    }

    private static Function<Object, Map<String, Object>> objectToMap(final Field field) {
        return target -> {
            final Field[] innerFields = field.getType().getDeclaredFields();
            final Map<String, Object> fieldsMap = new HashMap<>();

            for (final Field innerField : innerFields) {
                innerField.setAccessible(true);
                try {
                    fieldsMap.put(field.getName() + "." + innerField.getName(), innerField.get(target));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            return fieldsMap;
        };
    }

    private static Function<Map<String, Object>, Map<String, Map<String, Object>>> normalizeMap(final Field field) {
        return map -> {
            final Map<String, Object> normalized = map.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith(field.getName() + "."))
                    .collect(Collectors.toMap(entry -> nomralizeFieldName(entry.getKey()), Map.Entry::getValue));

            return ImmutableMap.of(field.getName(), normalized);
        };

    }

    private static String nomralizeFieldName(final String name) {
        final String[] parts = name.split("\\.");

        return parts.length < 2 ? name : parts[1];
    }
}
