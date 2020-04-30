package heartbeat_07;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatHandler extends SimpleChannelInboundHandler<String> {

    // 读空闲的次数
    int readIdleTimes = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("received msg:" + msg);
        if ("I am alive".equals(msg)) {
            ctx.channel().writeAndFlush("over");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;

        String type = "";
        switch (event.state()) {
            case READER_IDLE:
                type = "读空闲";
                readIdleTimes++;
                break;
            case WRITER_IDLE:
                type = "写空闲";
                break;
            case ALL_IDLE:
                type = "读写空闲";
                break;

        }

        System.out.println(ctx.channel().remoteAddress() + "发生了超事件："
                + type);

        // 阈值
        if (readIdleTimes > 3) {
            System.out.println("读次数超过三次，关闭连接");
            ctx.channel().writeAndFlush("u are out");
            ctx.channel().close();
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "is alive--------");
    }

}
