package com.hwoss.service.impl.domain;

import com.common.domain.RecallTaskInfo;
import com.common.domain.TaskInfo;
import com.common.pipeline.ProcessModel;
import com.hwoss.service.api.pojo.MessageParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecallTaskModel implements ProcessModel {

    private Long messageTemplateId;

    private List<String> recallIdList;

    /**
     * 表示撤回的具体任务
     */
    private RecallTaskInfo recallTaskInfo;
}
