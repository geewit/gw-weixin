package io.geewit.weixin.api.common.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.geewit.core.utils.reflection.Reflections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author geewit
 * @since 2022-01-07
 */
@Slf4j
public class MapToPojoUtils {

    public static void mapToPojo(Map<String, Object> source, Object target) throws BeansException {
        Field[] fields = target.getClass().getDeclaredFields();
        if (ArrayUtils.isNotEmpty(fields)) {
            for (Field field : fields) {
                int a = field.getModifiers();
                log.info("field name {} modifier is {}", field.getName(), a);
                //当属性的修饰符为private,需要setAccessible(true);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
                if (jsonIgnore != null) {
                    continue;
                }
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                Object value;
                if (null == jsonProperty) {
                    value = source.get(field.getName());
                } else {
                    value = source.get(jsonProperty.value());
                }
                Reflections.setFieldValue(target, field.getName(), value);
            }
        }
    }
}
