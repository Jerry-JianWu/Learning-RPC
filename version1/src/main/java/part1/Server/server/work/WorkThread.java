package part1.Server.server.work;

import lombok.AllArgsConstructor;
import part1.Server.provider.ServiceProvider;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

// 实现Runnable接口，用于处理客户端请求并返回响应，其核心功能是在多线程环境中接收来自客户端的请求，调用本地服务，并将服务的结果返回给客户端

/*
    实现Runnable接口的主要原因是为了使 WorkThread 类 能够在多线程环境中运行。
    Runnable接口提供了一个标准的方式来定义线程的任务，它的run()方法包含了线程执行的具体代码
    通过实现runnable接口将workthread类作为任务提交给线程池。
 */
@AllArgsConstructor
public class WorkThread implements Runnable{
    private Socket socket; // 建立网络连接
    private ServiceProvider serviceProvider ; // 本地服务注册中性

    public WorkThread() {
    }

    @Override
    public void run() {
        try{
            // 将响应数据（即服务端反会的RpcResponse）通过网络连接发送给客户端。
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            // 从客户端的网络连接中接收数据，读取序列化对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            // 读取客户端传过来的request
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            // 反射调用服务方法获取返回值
            RpcResponse rpcResponse = getResponse(rpcRequest);
            // 向客户端写入response
            oos.writeObject(rpcResponse);
            oos.flush();

        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    // 处理客户端请求，根据请求内容调用对应服务的方法，并返回执行结果
    private RpcResponse getResponse(RpcRequest rpcRequest) {
        // 得到服务名
        String interfaceName = rpcRequest.getInterfaceName();
        // 得到服务端响应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        // 反射调用方法
        Method method = null;

        try {
            // 获得方法对象
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            // 通过反射调用方法
            Object invoke = method.invoke(service, rpcRequest.getParams());
            // 封装响应对象并返回
            return RpcResponse.success(invoke);
            // 找不到请求的方法、方法无法访问、方法执行过程中抛出异常
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RpcResponse.fail();
        }
    }
}
