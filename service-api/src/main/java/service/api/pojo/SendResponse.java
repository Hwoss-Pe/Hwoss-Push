package service.api.pojo;

import com.common.domain.SimpleTaskInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Hwoss
 * @date 2024/05/10
 * 响应封装
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
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
