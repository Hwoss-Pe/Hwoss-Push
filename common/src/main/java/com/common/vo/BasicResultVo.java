package com.common.vo;


import com.common.enums.RespStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Hwoss
 * @date 2024/05/09
 * 作为最终返回的结果RespResultEnum的CODE和MSG
 */
@Getter
@ToString(callSuper = true)//自动toString
@AllArgsConstructor
@NoArgsConstructor

public final class BasicResultVo<T> {
    //返回的结果状态也就是
    private String status;

    //返回的消息编码
    private String msg;

    private T data;

    public BasicResultVo(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public BasicResultVo(String status, T data) {
        this.status = status;
        this.data = data;
    }


    public BasicResultVo(RespStatusEnum status, T data) {
        this.status = status.getCode();
        this.msg = status.getMsg();
        this.data = data;
    }

    /**
     * @param status
     * @param msg
     * @param data   这个方法可以作为直接从外部传入枚举值
     */
    public BasicResultVo(RespStatusEnum status, String msg, T data) {
        this.status = status.getCode();
        this.msg = msg;
        this.data = data;
    }

    public BasicResultVo(RespStatusEnum status) {
        this(status, null);
    }

    /**
     * @return {@link BasicResultVo }<{@link T }>
     * 默认成功调用
     */
    public static <T> BasicResultVo<T> success() {
        return new BasicResultVo<>(RespStatusEnum.SUCCESS,
                                   RespStatusEnum.SUCCESS.getMsg(), null);
    }

    /**
     * @param msg
     * @return {@link BasicResultVo }<{@link T }>
     * 用于对自定义成功信息的调用
     */
    public static <T> BasicResultVo<T> success(String msg) {
        return new BasicResultVo<T>(RespStatusEnum.SUCCESS, msg, null);
    }

    /**
     * @param data
     * @return {@link BasicResultVo }<{@link T }>
     * 用于对自定义成功数据的调用
     */
    public static <T> BasicResultVo<T> success(T data) {
        return new BasicResultVo<>(RespStatusEnum.SUCCESS, data);
    }

    /**
     * @return {@link BasicResultVo }<{@link T }>
     * 默认失败调用
     */
    public static <T> BasicResultVo<T> fail() {
        return new BasicResultVo<T>(RespStatusEnum.FAIL,
                                    RespStatusEnum.FAIL.getMsg(), null);
    }

    /**
     * @param msg
     * @return {@link BasicResultVo }<{@link T }>
     * 自定义错误信息
     */
    public static <T> BasicResultVo<T> fail(String msg) {
        return fail(RespStatusEnum.FAIL, msg);
    }

    /**
     * 自定义状态和信息的失败响应
     *
     * @param status 状态
     * @param msg    信息
     * @return 自定义状态和信息的失败响应
     */
    public static <T> BasicResultVo<T> fail(RespStatusEnum status, String msg) {
        return new BasicResultVo<>(status, msg, null);
    }

    /**
     * 自定义状态的失败响应
     *
     * @param status 状态
     * @return 自定义状态的失败响应
     */
    public static <T> BasicResultVo<T> fail(RespStatusEnum status) {
        return fail(status, status.getMsg());
    }
}
