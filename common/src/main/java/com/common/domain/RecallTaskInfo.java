package com.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Hwoss
 * @date 2024/05/14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecallTaskInfo {
    private Long messageTemplateId;

    private List<String> recallIdList;

    private Integer sendAccount;

    private Integer sendChannel;
}
