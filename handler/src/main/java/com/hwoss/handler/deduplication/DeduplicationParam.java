package com.hwoss.handler.deduplication;

import com.common.domain.AnchorInfo;
import com.common.domain.TaskInfo;
import com.common.enums.AnchorState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Hwoss
 * @date 2024/05/19
 * 标识去重的对应参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeduplicationParam {
    private TaskInfo taskInfo;

    /**
     * 去重时间，单位秒
     */
    private Long deduplicationTime;

    /**
     * 最多多少次进行去重
     */
    private Integer countNum;

    /**
     * 标识对应的哪种去重
     */
    private AnchorState anchorState;
}
