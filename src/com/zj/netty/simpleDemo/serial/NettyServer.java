package com.zj.netty.simpleDemo.serial;

import java.util.Date;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NettyServer {
	public void bind (int port){
		EventLoopGroup bossGroup=new NioEventLoopGroup();//reactor线程组，用于分派处理事件线程
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		ChannelGroup  groupAll=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
		try {
			ServerBootstrap boot=new ServerBootstrap();//启动服务
			boot.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG,1024)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<Channel>() {
						@Override
						protected void initChannel(Channel ch) throws Exception {
							//增加对象解析器，支持对对象的序列化
							ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
							ch.pipeline().addLast(new ObjectEncoder());
							ch.pipeline().addLast(new ServerHandler());
						}
					});//事件处理的handler
			//同步等待服务器打开成功
			ChannelFuture f=boot.bind(port).sync();
			//等待监听窗口关闭
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				bossGroup.shutdownGracefully().sync();
				workerGroup.shutdownGracefully().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
	public static void main(String[] args) {
		NettyServer server=new NettyServer();
//		server.bind(8000);
		ByteBuf buf=Unpooled.buffer(1);
		buf.writeBytes("ok".getBytes());
	}
	

	
		
	private class ServerHandler extends ChannelInboundHandlerAdapter {

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			ReqInfo req=(ReqInfo) msg;
			System.out.println(req);
			ResponInfo respon=new ResponInfo();
			respon.setReqId(req.getId());
			if(req.getId()<5){
				respon.setResStatus(0);
				respon.setDesc("好人");
			}else{
				respon.setResStatus(1);
				respon.setDesc("坏人·");
			}
			ctx.write(respon);
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx)
				throws Exception {
			ctx.flush();//为防止唤醒selector频繁处理的写，netty会将写的先放入缓存区，flush可以将缓存的数据立即写入socket
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			ctx.close();
		}
		
	}

}
