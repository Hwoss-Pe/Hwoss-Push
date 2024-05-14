package com.hwoss.web.controller;


import com.hwoss.service.api.pojo.BatchRequest;
import com.hwoss.service.api.pojo.SendRequest;
import com.hwoss.service.api.pojo.SendResponse;
import com.hwoss.service.api.service.RecallService;
import com.hwoss.service.api.service.SendService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SendController {

    @Autowired
    private SendService sendService;

    @Autowired
    private RecallService recallService;

    /**
     * 单个文案下发相同的人
     *
     * @param sendRequest
     * @return
     */
    @ApiOperation(value = "下发接口", notes = "多渠道多类型下发消息，目前支持邮件和短信，类型支持：验证码、通知类、营销类。")
    @PostMapping("/send")
    public SendResponse send(@RequestBody SendRequest sendRequest) {
        return sendService.send(sendRequest);
    }

    /**
     * 不同文案下发到不同的人
     *
     * @param batchSendRequest
     * @return
     */
    @ApiOperation(value = "batch下发接口", notes = "多渠道多类型下发消息，目前支持邮件和短信，类型支持：验证码、通知类、营销类。")
    @PostMapping("/batchSend")
    public SendResponse batchSend(@RequestBody BatchRequest batchSendRequest) {
        return sendService.batchSend(batchSendRequest);
    }


    /**
     * 优先根据messageId撤回消息，如果messageId不存在则根据模板id撤回
     *
     * @param sendRequest
     * @return
     */
    @ApiOperation(value = "撤回消息接口", notes = "优先根据messageId撤回消息，如果messageId不存在则根据模板id撤回")
    @PostMapping("/recall")
    public SendResponse recall(@RequestBody SendRequest sendRequest) {
        return recallService.recall(sendRequest);
    }
}
