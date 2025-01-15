package part2.Client.netty.nettyInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import part2.Client.netty.handler.NettyClientHandler;
/* 本类作用：实现了一个 NettyClientInitializer 类，继承自 ChannelInitializer<SocketChannel>，
用于初始化客户端的 Channel 和 ChannelPipeline。在 Netty 中，Channel 是网络通信的基本单元，
而 ChannelPipeline 是一个用于处理消息的责任链，它包含了一系列的 ChannelHandler，
每个 ChannelHandler 都负责处理不同的操作，如编码、解码、异常处理等。*/

/*ChannelInitializer 是一个抽象类，用于初始化 Channel。在这个类中，我们会配置处理网络数据的 ChannelPipeline。它是 Netty 中为每个连接提供的初始化类。
SocketChannel 是 Netty 中的一个实现，代表客户端的网络连接，通常用于 TCP 连接。*/
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 初始化，每个SocketChannel 都有一个独立的管道Pipeline， 用于定义数据的处理流程。
        ChannelPipeline pipeline = socketChannel.pipeline();
        //消息格式 【长度】【消息体】，解决沾包问题
        /*
        参数含义：
        Integer.MAX_VALUE：允许的最大帧长度。
        0, 4：表示长度字段的起始位置和长度。
        0, 4：去掉长度字段后，计算实际数据的偏移量。
         */
        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
        //计算当前待发送消息的长度，写入到前4个字节中
        pipeline.addLast(new LengthFieldPrepender(4));
        //编码器
        //使用Java序列化方式，netty的自带的解码编码支持传输这种结构
        pipeline.addLast(new ObjectEncoder());
        //解码器
        //使用了Netty中的ObjectDecoder，它用于将字节流解码为 Java 对象。
        //在ObjectDecoder的构造函数中传入了一个ClassResolver 对象，用于解析类名并加载相应的类。
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));

        pipeline.addLast(new NettyClientHandler());
    }
}

