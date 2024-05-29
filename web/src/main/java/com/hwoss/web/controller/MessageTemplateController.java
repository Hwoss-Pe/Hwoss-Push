package com.hwoss.web.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.common.enums.RespStatusEnum;
import com.common.vo.BasicResultVo;
import com.google.common.base.Throwables;
import com.hwoss.service.api.enums.BusinessCode;
import com.hwoss.service.api.pojo.MessageParam;
import com.hwoss.service.api.pojo.SendRequest;
import com.hwoss.service.api.pojo.SendResponse;
import com.hwoss.service.api.service.RecallService;
import com.hwoss.service.api.service.SendService;
import com.hwoss.suport.domain.MessageTemplate;
import com.hwoss.web.aspect.LogAspect;
import com.hwoss.web.aspect.ResultAspect;
import com.hwoss.web.exception.CommonException;
import com.hwoss.web.service.MessageTemplateService;
import com.hwoss.web.vo.MessageTemplateParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@LogAspect
@ResultAspect
@Api("发送消息")
@RequestMapping("/messageTemplate")
public class MessageTemplateController {
    @Autowired
    private MessageTemplateService messageTemplateService;

    @Autowired
    private SendService sendService;

    @Autowired
    private RecallService recallService;


    @Value("${austin.business.upload.crowd.path}")
    private String dataPath;



    /**
     * 上传人群文件
     */
    @PostMapping("upload")
    @ApiOperation("/上传人群文件")
    public Map<Object, Object> upload(@RequestParam("file") MultipartFile file) {
        String filePath = dataPath + IdUtil.fastSimpleUUID() + file.getOriginalFilename();
        try {
            File localFile = new File(filePath);
            if (!localFile.exists()) {
                localFile.mkdirs();
            }
            file.transferTo(localFile);
        } catch (Exception e) {
            log.error("MessageTemplateController#upload fail! e:{},params{}", Throwables.getStackTraceAsString(e), JSON.toJSONString(file));
//            throw new CommonException(RespStatusEnum.SERVICE_ERROR);
        }
        return MapUtil.of(new String[][]{{"value", filePath}});
    }

    /**
     * 如果Id存在，则修改
     * 如果Id不存在，则保存
     */
    @PostMapping("/save")
    @ApiOperation("/保存数据")
    public MessageTemplate saveOrUpdate(@RequestBody MessageTemplate messageTemplate) {
        if (CharSequenceUtil.isBlank(messageTemplate.getCreator())) {
            throw new CommonException(RespStatusEnum.NO_LOGIN.getCode(), RespStatusEnum.NO_LOGIN.getMsg());
        }
        return messageTemplateService.saveOrUpdate(messageTemplate);
    }

    /**
     * 列表数据
     */
    @GetMapping("/list")
//    前端需要传入对应的页码和数据大小
    @ApiOperation("/列表页")
    public List<MessageTemplate> queryList(@Validated MessageTemplateParam messageTemplateParam) {
        if (CollUtil.isEmpty(messageTemplateParam.getMessageTemplateList())) {
            throw new CommonException(RespStatusEnum.CLIENT_BAD_PARAMETER.getCode(), RespStatusEnum.CLIENT_BAD_PARAMETER.getMsg());
        }
        return messageTemplateService.queryList(messageTemplateParam);
    }


    /**
     * 根据Id查找
     */
    @GetMapping("query/{id}")
    @ApiOperation("/根据Id查找")
    public MessageTemplate queryById(@PathVariable("id") Long id) {
        return messageTemplateService.queryById(id);
    }


    /**
     * 根据Id删除
     * id多个用逗号分隔开
     */
    @DeleteMapping("delete/{id}")
    @ApiOperation("/根据Ids删除")
    public void deleteByIds(@PathVariable("id") String id) {
        if (CharSequenceUtil.isNotBlank(id)) {
            List<Long> idList = Arrays.stream(id.split(StrPool.COMMA)).map(Long::valueOf).collect(Collectors.toList());
            messageTemplateService.deleteByIds(idList);
        }
    }


    /**
     * 测试发送接口
     */
    @PostMapping("test")
    @ApiOperation("/测试发送接口")
    public SendResponse tests(@RequestBody Long id, String receiver) {

        MessageTemplate messageTemplate = messageTemplateService.queryById(id);
        Map<String, String> variables = JSON.parseObject(messageTemplate.getMsgContent(), Map.class);
        MessageParam messageParam = MessageParam.builder().receiver(receiver).variables(variables).build();
        SendRequest sendRequest = SendRequest.builder().code(BusinessCode.SEND.getCode()).messageTemplateId(messageTemplate.getId()).messageParam(messageParam).build();
        SendResponse response = sendService.send(sendRequest);
        if (!Objects.equals(response.getCode(), RespStatusEnum.SUCCESS.getCode())) {
            throw new CommonException(response.getMsg());
        }
        return response;
    }

    /**
     * 获取需要测试的模板占位符
     */
    @PostMapping("test/content")
    @ApiOperation("/测试发送接口")
    public String test(Long id) {
        MessageTemplate messageTemplate = messageTemplateService.queryById(id);
        return messageTemplate.getMsgContent();
    }


    /**
     * 撤回接口（根据模板id撤回）
     */
    @PostMapping("recall/{id}")
    @ApiOperation("/撤回消息接口")
    public SendResponse recall(@PathVariable("id") String id) {
        SendRequest sendRequest = SendRequest.builder().code(BusinessCode.RECALL.getCode()).messageTemplateId(Long.valueOf(id)).build();
        SendResponse response = recallService.recall(sendRequest);
        if (!Objects.equals(response.getCode(), RespStatusEnum.SUCCESS.getCode())) {
            throw new CommonException(response.getMsg());
        }
        return response;
    }


    /**
     * 启动模板的定时任务
     */
    @PostMapping("start/{id}")
    @ApiOperation("/启动模板的定时任务")
    public BasicResultVo start(@RequestBody @PathVariable("id") Long id) {
        return messageTemplateService.startCronTask(id);
    }

    /**
     * 暂停模板的定时任务
     */
    @PostMapping("stop/{id}")
    @ApiOperation("/暂停模板的定时任务")
    public BasicResultVo stop(@RequestBody @PathVariable("id") Long id) {
        return messageTemplateService.stopCronTask(id);
    }


}
