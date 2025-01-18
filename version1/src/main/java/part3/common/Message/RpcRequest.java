package part3.common.Message;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RpcRequest implements Serializable {
    // 请求消息种有哪些字段？：接口名，调用方法名，参数列表，参数类型
    // 为什么请求信息中的服务类名定为接口名？：因为使用动态代理，外部给定的信息是接口信息
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramsType;
}
