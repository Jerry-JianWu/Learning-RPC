package part2.Server.server.impl;

import lombok.AllArgsConstructor;
import part2.Server.provider.ServiceProvider;
import part2.Server.server.RpcServer;
import part2.Server.server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/*
* 该类实现RpcServer接口，用于启动一个简单的RPC服务器，并监听客户端的请求，处理客户端请求，并通过线程并发处理每个连接
* */
@AllArgsConstructor
public class SimpleRPCRPCServer implements RpcServer {
    private ServiceProvider serviceProvider; // 本地注册中心

    @Override
    public void start(int port) {
        try {
            // 创建一个ServerSocket实例，该对象用于在指定的端口上监听客户端的连接请求。
             // 是一个TCP服务器端的类，它负责接收客户端的连接并为每个连接创建一个新的socket对象
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器启动了");
            // 服务器持续接收客户端的连接请求
            while (true) {
                // 如果没有连接，会堵塞在这里
                Socket socket = serverSocket.accept();
                // 有连接，创建一个新的线程执行处理
                new Thread(new WorkThread(socket, serviceProvider)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {
        // 停止服务端
        // 可以在未来版本中优化服务端关闭的流程
    }
}
