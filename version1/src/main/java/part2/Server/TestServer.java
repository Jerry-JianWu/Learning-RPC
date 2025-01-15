package part2.Server;

import part2.Server.provider.ServiceProvider;
import part2.Server.server.RpcServer;
import part2.Server.server.impl.NettyRPCRPCServer;
import part2.common.service.Impl.UserServiceImpl;
import part2.common.service.UserService;

public class TestServer {
    public static void main(String[] args) {
        // 创建服务实现类
        UserService userService = new UserServiceImpl();
        // 实例化服务注册中心，用于管理所有可供客户端调用的服务
        ServiceProvider serviceProvider = new ServiceProvider();
        // 注册服务
        serviceProvider.provideServiceInterface(userService);
        //实例化服务端
        RpcServer rpcServer = new NettyRPCRPCServer(serviceProvider);
        // 启动服务端
        rpcServer.start(9999);
    }
}
