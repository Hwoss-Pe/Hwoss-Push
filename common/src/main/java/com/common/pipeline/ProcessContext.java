package com.common.pipeline;

import com.common.vo.BasicResultVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 责任链上下文
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
public class ProcessContext<T extends ProcessModel> implements Serializable {
    /**
     * 标识
     */
    private String Code;

    /**
     * 是否中断
     */
    private Boolean isBreak;

    /**
     * 标识这个是哪个模型的上下文
     */
    private T processModel;

    /**
     * 携带的数据
     */
    private BasicResultVo<Object> response;

}
