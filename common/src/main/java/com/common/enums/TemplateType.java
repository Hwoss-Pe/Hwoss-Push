package com.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum TemplateType implements PowerfulEnums {

    /**
     * 定时类的模板(后台定时调用)
     */
    CLOCKING(10, "定时类的模板(后台定时调用)"),
    /**
     * 实时类的模板(接口实时调用)
     */
    REALTIME(20, "实时类的模板(接口实时调用)"),
    ;

    private final Integer code;
    private final String description;

}
