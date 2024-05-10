package com.common.pipeline;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 把责任链用集合存储
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTemplate<T extends ProcessModel> {
    private List<BusinessProcess<T>> businessProcessList;
}
