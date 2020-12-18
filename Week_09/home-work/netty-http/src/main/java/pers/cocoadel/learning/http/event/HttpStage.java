package pers.cocoadel.learning.http.event;

public enum  HttpStage {
    /**
     * 当前Channel正在写数据
     */
    SENDING,
    /**
     * write或者writeAndFlush已经执行完毕，但是还没有收到当前会话的返回，处于等待状态
     */
    WAIT,
    /**
     * 接收到当前会话的返回，结束状态（空闲状态）。
     * 如果一次请求等待超时或者发生异常都会被直接关闭所以不存在失败的状态。
     */
    IDLE;
}
