package com.hwoss.handler.receipt;

/**
 * @author Hwoss
 * @date 2024/05/22
 * 这里就是回执信息的拉取
 */
public interface ReceiptMessageStater {
    /**
     * 多线程的方式去启动对应的类
     */
    void start();
}
