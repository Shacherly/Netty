import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.time.LocalDateTime;

/**
 * 自定义的Hander的方式之一
 */
public class TtyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 通道刚被启用，刚刚建立连接的方法
     * 业务使用中往往发送欢迎消息
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 简便的方法， 底层：获取通道->创建缓冲区->写入数据->缓冲区写入通道
        System.out.println("Actived , someone enter!");
        ctx.writeAndFlush(String.format("[%s]: Connetced to netty server", LocalDateTime.now()));


    }

    /**
     * 数据读取的方法
     * @param ctx
     * @param msg 传入的消息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //
        ByteBuf buf = (ByteBuf) msg;
        System.out.println(String.format("[%s]: Client msg: %s", LocalDateTime.now(), buf.toString(CharsetUtil.UTF_8)));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {


        ctx.writeAndFlush(Unpooled.copiedBuffer("msg return to client:\n", CharsetUtil.UTF_8));
    }
}
