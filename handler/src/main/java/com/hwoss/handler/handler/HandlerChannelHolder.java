package com.hwoss.handler.handler;

import com.hwoss.handler.handler.Handler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hwoss
 * @date 2024/05/16
 * 映射发送渠道和对应的处理器，处理器接口有发送和撤回的逻辑
 */
@Component
public class HandlerChannelHolder {
    private Map<Integer, Handler> handlers = new HashMap<>(128);

    public void putHandler(Integer channelCode, Handler handler) {
        handlers.put(channelCode, handler);
    }

    public Handler route(Integer channelCode) {
        return handlers.get(channelCode);
    }
}
