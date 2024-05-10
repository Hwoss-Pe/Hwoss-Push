package service.api.pojo;

import com.common.domain.SimpleTaskInfo;

import java.util.List;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 响应封装
 */
public class SendResponse {

    /**
     * 返回的状态
     */
    private String code;

    /**
     * 返回的编码
     */
    private String msg;


    public SendResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 返回发送信息的任务列表
     */
    private List<SimpleTaskInfo> data;


}
