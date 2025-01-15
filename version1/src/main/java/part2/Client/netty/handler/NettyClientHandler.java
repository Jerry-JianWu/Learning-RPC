package part2.Client.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import part2.common.Message.RpcResponse;

/*实现了一个NettyClientHandler
*继承自SimpleChannelInboundHandler<RpcResponse>，是Netty中用于处理服务器端响应的处理器。
* 主要功能是来自服务器RpcResponse对象，并在处理过程中管理连接的生命周期
* 继承的类是一个通用的Netty处理器基类，它为每个时间提供了默认的实现麻痹面我们每次都要去实现channelRead()方法*/
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    //该方法是SimpleChannelInboundHandler中核心方法，用于处理连接收到的消息。
    // 在这个方法中，客户端从服务器接收到RpcResponse对象，通常是RPC调用的结果
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        // 接收response，给channel设计别名，让sendRequest里读取response
        // 将服务端返回的Rpc 绑定到当前Channel属性中，以便后续逻辑能够通过Channel获取响应数据
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
        channelHandlerContext.channel().attr(key).set(rpcResponse);
        // 关闭当前 Channel（短连接模式）
        channelHandlerContext.channel().close();

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception{
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
