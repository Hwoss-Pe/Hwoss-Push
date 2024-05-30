package com.hwoss.web.vo;


import com.hwoss.suport.domain.MessageTemplate;
import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.internal.build.AllowPrintStacktrace;

import java.util.List;

@Data
@NoArgsConstructor
@AllowPrintStacktrace
public class MessageTemplateParam {

    /**
     * 当前页码
     */
    @NotNull
    private Integer page = 1;

    /**
     * 当前页大小
     */
    @NotNull
    private Integer perPage = 10;

    //    最终返回的信息在这里
    private List<MessageTemplate> messageTemplateList;


    //这些都是筛选信息的条件
    private String creator;

    /**
     * 模版名称
     */
    private String keywords;
}
