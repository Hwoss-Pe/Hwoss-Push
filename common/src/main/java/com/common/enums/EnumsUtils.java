package com.common.enums;

import java.util.Arrays;

public class EnumsUtils {
    private EnumsUtils() {

    }

    /**
     * @param code
     * @param enumClass
     * @return {@link String }
     * 通过传入code和对应的枚举类返回改类的描述数据
     */
    public static <T extends PowerfulEnums> String getDescription(Integer code, Class<T> enumClass) {
        //获取枚举的所有常量
        T[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants)
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .map(PowerfulEnums::getDescription)
                .orElse("");
    }

    /**
     * @param code
     * @param enumClass
     * @return {@link T }
     * 通过code获取对应的枚举
     */
    public static <T extends PowerfulEnums> T getEnumByCode(Integer code, Class<T> enumClass) {
        //获取枚举的所有常量
        T[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants)
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

}
