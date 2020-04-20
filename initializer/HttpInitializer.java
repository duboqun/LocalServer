package earthview.ne.localserver.initializer;

import earthview.ne.localserver.handler.HttpHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        // 获取通道
        ChannelPipeline p = channel.pipeline();

        // 添加http加解码器
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpHandler());
    }
}
