import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 扔基于HTTP协议 ，所以继续使用编码解码器
        pipeline.addLast(new HttpServerCodec());

        // 块的方式写
        pipeline.addLast(new ChunkedWriteHandler());
        // 因为Http数据是分段的，因此使用聚合器
        pipeline.addLast(new HttpObjectAggregator(512 * 1024));
        // WebSocketServerProtocolHandler将http升级为websocket是指保持场链接，使用状态码101
        pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

        pipeline.addLast(new WebSocketHandler());
    }
}
