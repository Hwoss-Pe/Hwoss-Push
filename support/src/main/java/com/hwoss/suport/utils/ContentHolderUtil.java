package com.hwoss.suport.utils;

import org.apache.logging.log4j.message.Message;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;

public class ContentHolderUtil {
    /**
     * 占位符前缀
     */
    private static final String PLACE_HOLDER_PREFIX = "{$";
    /**
     * 占位符后缀
     */
    private static final String PLACE_HOLDER_SUFFIX = "}";

    /**
     * Spring中的一个实用类，用于处理属性占位符替换
     */
    private static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER = new PropertyPlaceholderHelper(PLACE_HOLDER_PREFIX, PLACE_HOLDER_SUFFIX);

    public static String replacePlaceholder(String template, Map<String, String> variables) {
        return PROPERTY_PLACEHOLDER_HELPER.replacePlaceholders(template, new CustomPlaceholderResolver(template, variables));
    }

    private ContentHolderUtil() {
    }

    private static class CustomPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {
        private final String template;
        private final Map<String, String> variables;

        public CustomPlaceholderResolver(String template, Map<String, String> variables) {
            super();
            this.template = template;
            this.variables = variables;
        }

        @Override
        public String resolvePlaceholder(String placeholderName) {
            if (Objects.isNull(variables)) {
                String error = MessageFormat.format("template:{0} require param:{1},but not exist! paramMap:{2}", template, placeholderName, variables);
                throw new IllegalArgumentException(error);
            }
            String value = variables.get(placeholderName);
            if (value.isEmpty()) {
                String errorStr = MessageFormat.format("template:{0} require param:{1},but not exist! paramMap:{2}", template, placeholderName, variables);
                throw new IllegalArgumentException(errorStr);
            }
            return value;
        }
    }

}
