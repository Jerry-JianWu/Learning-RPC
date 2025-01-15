package part2.Server.server.impl;

import part2.Server.provider.ServiceProvider;
import part2.Server.server.RpcServer;
import part2.Server.server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/*
* 该类作用实现了一个RpcServer接口的Rpc服务器，它通过线程池来管理和执行请求处理任务，以提高并发处理能力*/
public class ThreadPoolRPCRPCServer implements RpcServer {
    private final ThreadPoolExecutor threadPoolExecutor; // 定义一个线程池对象threadpool，用于管理和执行线程任务
    private ServiceProvider serviceProvider;

    // 默认构造方法：创建一个线程池，核心线程数等于CPU核心数，
    // 最大线程数1000，
    // 非核心线程空闲存活60s，
    // 队列大小为100
    public ThreadPoolRPCRPCServer(ServiceProvider serviceProvider) {
        threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                1000,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(100));
        this.serviceProvider = serviceProvider;
    }

    // 自定义构造方法：允许用户传入线程池参数，自定义线程池配置。
    public ThreadPoolRPCRPCServer(ServiceProvider serviceProvider,
                                  int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  BlockingDeque<Runnable> workQueue) {
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue);
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start(int port) {
        System.out.println("服务端启动了");
        try {
            // 初始化ServerSocket， 监听端口
            ServerSocket serverSocket = new ServerSocket();
            while(true){
                // 接收连接：通过accept()阻塞等待客户端连接，返回Socket对象。
                Socket socket = serverSocket.accept();
                // 使用线程池分发任务，每个客户端请求交给线程池管理
                threadPoolExecutor.execute(new WorkThread(socket, serviceProvider));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
