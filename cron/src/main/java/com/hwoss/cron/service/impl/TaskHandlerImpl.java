package com.hwoss.cron.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvRowHandler;
import com.hwoss.cron.csv.CountFileRowHandler;
import com.hwoss.cron.handler.CrowdBatchTaskPending;
import com.hwoss.cron.service.TaskHandler;
import com.hwoss.cron.utils.ReadFileUtils;
import com.hwoss.cron.vo.CrowdInfoVo;
import com.hwoss.suport.dao.MessageTemplateDao;
import com.hwoss.suport.domain.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class TaskHandlerImpl implements TaskHandler {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    private ApplicationContext context;


    @Override
    public void handle(long messageTemplateId) {
        MessageTemplate messageTemplate = messageTemplateDao.findById(messageTemplateId).orElse(null);
        if (Objects.isNull(messageTemplate)) {
            return;
        }
        if (messageTemplate.getCronCrowdPath().isEmpty()) {
            log.error("TaskHandler#handle crowdPath empty! messageTemplateId:{}", messageTemplateId);
            return;
        }
//        获取文件行数大小
        long countCsvRow = ReadFileUtils.countCsvRow(messageTemplate.getCronCrowdPath(), new CountFileRowHandler());

//  读取文件得到每一行记录给到队列做lazy batch处理，因此是多例模式
        CrowdBatchTaskPending crowdBatchTaskPending = context.getBean(CrowdBatchTaskPending.class);
        ReadFileUtils.getCsvRow(//读取每一行后执行csvRowHandler
                                messageTemplate.getCronCrowdPath(), csvRow -> {
                    if (CollUtil.isEmpty(csvRow.getFieldMap())
                            || CharSequenceUtil.isBlank(csvRow.getFieldMap().get(ReadFileUtils.RECEIVER_KEY))) {
                        return;
                    }
                    Map<String, String> params = ReadFileUtils.getParamFromLine(csvRow.getFieldMap());
                    CrowdInfoVo vo = CrowdInfoVo.builder()
                            .messageTemplateId(messageTemplateId)
                            .receiver(csvRow.getFieldMap().get(ReadFileUtils.RECEIVER_KEY))
                            .params(params)
                            .build();
//                    把每行数据加入阻塞队列
                    crowdBatchTaskPending.pending(vo);

//                    如果当前行号遍历完了就结束循环
                    if (csvRow.getOriginalLineNumber() == countCsvRow) {
                        crowdBatchTaskPending.setStop(true);
                        log.info("messageTemplate:[{}] read csv file complete!", messageTemplateId);
                    }
                }
        );

    }
}
