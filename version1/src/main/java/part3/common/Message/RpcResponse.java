package part3.common.Message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
@Data
@Builder
public class RpcResponse implements Serializable {
    // 状态信息
    private int code;
    private String message;
    // 具体数据
    private Object data;
    // 构造成功信息
    public static RpcResponse success(Object data){
        return RpcResponse.builder().code(200).data(data).build();

    }
    // 构造失败信息
    // 响应消息种有哪些信息？：状态码、状态信息、具体数据、构建成功信息、构建失败信息
    public static RpcResponse fail(){
        return RpcResponse.builder().code(500).message("服务器发生错误").build();
    }
}
