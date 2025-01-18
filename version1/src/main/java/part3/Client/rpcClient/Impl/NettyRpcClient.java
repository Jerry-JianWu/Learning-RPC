package part3.Client.rpcClient.Impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import part3.Client.netty.nettyInitializer.NettyClientInitializer;
import part3.Client.rpcClient.RpcClient;
import part3.Client.serviceCenter.ServiceCenter;
import part3.Client.serviceCenter.ZKServiceCenter;
import part3.common.Message.RpcRequest;
import part3.common.Message.RpcResponse;

import java.net.InetSocketAddress;

public class NettyRpcClient implements RpcClient {
    private String host;
    private int port;
    // bootstrap 字段是netty用于启动客户端的对象，负责设置与服务器的连接配置。
    private static final Bootstrap bootstrap;
    // eventLoopGroup是netty的线程池，用于处理i/o操作。 NioEventLoopGroup 是基于NIO实现的，适用于客户端和服务器的网络通信。
    private static final EventLoopGroup eventLoopGroup;
    private ServiceCenter serviceCenter;
    public NettyRpcClient() {
        this.serviceCenter = new ZKServiceCenter();
    }

    // netty客户端初始化
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                // NettyClientInitializer这里 配置netty对消息的处理机制
                .handler(new NettyClientInitializer());
    }

    @Override
    public RpcResponse sendRequset(RpcRequest request) {
        InetSocketAddress address = serviceCenter.serviceDiscovery(request.getInterfaceName());
        String host = address.getHostName();
        int port = address.getPort();
        try {
            // 创建一个channelFuture对象，代表这一个操作实践，sync方法表示堵塞直到connect完成
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            // channel 表示一个连接的单位，类似socket
            Channel channel = channelFuture.channel();
            // 发送数据
            channel.writeAndFlush(request);
            // sync()堵塞获取结果
            channel.closeFuture().sync();
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
            // AttributeKey是，线程隔离的，不会由线程安全问题。
            // 当前场景下选择堵塞获取结果
            // 其它场景也可以选择添加监听器的方式来异步获取结果 channelFuture.addListener...
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(key).get();
            System.out.println(response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
