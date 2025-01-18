package part3.Client;

import part3.common.Message.RpcRequest;
import part3.common.Message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class IOClient {
    // 这里负责底层与服务端的通信，发送request，返回response
    // 底层通信做了哪些事情？：建立连接、发送请求、接收响应、异常处理
    public static RpcResponse sendRequest(String host, int port, RpcRequest request){
        // 服务端的主机ip地址，端口号，请求对象

        // 通过socket与服务端建立tcp连接；
        try {
            Socket socket  = new Socket(host, port);
            // 将对象序列化发送到服务端
            // ObjectOutputSteam 是一个用于将对象序列化成字节流的类，
            // 在这行代码中，socket.getOutputStream()获取到连接的输出流，接着通过OOS对请求对象进行序列化
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            // 接收并反序列化对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            // 将RpcRequest 对象序列化，并通过输出流发送到服务端
            oos.writeObject(request);
            // 刷新输出流确保数据完全发送
            oos.flush();

            // 从输入流中读取服务端返回的序列化对象，并反序列化为RpcResponse
            RpcResponse response = (RpcResponse) ois.readObject();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }


    }
}
