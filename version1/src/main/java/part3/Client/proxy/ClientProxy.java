package part3.Client.proxy;

import lombok.AllArgsConstructor;
import part3.Client.rpcClient.Impl.NettyRpcClient;
import part3.Client.rpcClient.Impl.SimpleSocketRpcClient;
import part3.Client.rpcClient.RpcClient;
import part3.common.Message.RpcRequest;
import part3.common.Message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    /*
    * 为什么要实现InvocationHandler接口？
    * JDK 动态代理的核心功能是通过反射生成一个代理对象，该对象可以代理一个或多个接口的方法调用。
    * 当代理对象的方法被调用时，实际的调用逻辑由 InvocationHandler 接口的 invoke 方法来处理。
    InvocationHandler 接口有一个 invoke 方法，该方法会在代理对象的方法被调用时执行。
    为了使 ClientProxy 成为一个有效的代理类，需要实现 InvocationHandler 接口，并提供 invoke 方法的实现。
    通过这种方式，所有对代理对象方法的调用都可以通过 invoke 方法进入，从而执行我们定义的增强逻辑（如远程调用、参数封装、响应处理等）。
    实现 InvocationHandler 的主要目的就是能够拦截被代理对象的方法调用，并在 invoke 方法中执行定制的处理逻辑。
    比如，在你提供的 ClientProxy 类中，invoke 方法的核心功能是：
    封装请求：将方法调用的信息封装成一个 RpcRequest 对象。
    网络通信：通过网络向远程服务发送请求，并接收服务端的响应。
    返回结果：将远程服务返回的数据返回给调用者。
通过 InvocationHandler，你可以控制代理对象方法的行为，实现远程调用的透明化，隐藏远程调用的复杂性。*/
    // 传入参数service接口的class对象，反射封装成一个request
    // 初始化代理类时传入host和port
    private RpcClient rpcClient;

    public ClientProxy(){
        rpcClient = new NettyRpcClient();
    }

    // jdk动态代理，每一次代理对象调用方法，都会经过此方法增强（反射获取request对象，socket发送到服务端）
    // 核心逻辑，用于封装请求并处理服务端响应
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构建request
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramsType(method.getParameterTypes()).build();
        // 与服务端进行通信，将请求发送出去，并接收RpcResponse 响应
        RpcResponse response = rpcClient.sendRequset(request);
        return response.getData();
    }
    // 动态生成一个实现指定接口的代理对象。
    public <T>T getProxy(Class<T> clazz){
        //
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }

}
