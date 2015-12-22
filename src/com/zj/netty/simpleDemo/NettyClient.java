package com.zj.netty.simpleDemo;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
	public static void main(String[] args) {
		new NettyClient().connet("127.0.0.1", 8000);
	}
	public void connet(String host,int port){
		EventLoopGroup workGroup=new NioEventLoopGroup();
		try {
			Bootstrap boot=new Bootstrap();
			boot.group(workGroup)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY,true)
			.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new TimeHandler());
				}
			});
			ChannelFuture f=boot.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			try {
				workGroup.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private class TimeHandler extends ChannelInboundHandlerAdapter{
		String query="query time";
		ByteBuf buf=Unpooled.copiedBuffer(query.getBytes());

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			System.out.println("连接成功");
			ctx.writeAndFlush(buf);
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			ByteBuf buf=(ByteBuf)msg;
			byte[] bytes=new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			String curtime=new String(bytes,"utf-8");
			System.out.println("client:"+curtime);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			ctx.close();
		}

	
		
	}

}
