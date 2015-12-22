package com.zj.netty.simpleDemo;

import java.util.Date;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
	public void bind (int port){
		EventLoopGroup bossGroup=new NioEventLoopGroup();//reactor线程组，用于分派处理事件线程
		EventLoopGroup workerGroup=new NioEventLoopGroup();
		try {
			ServerBootstrap boot=new ServerBootstrap();//启动服务
			boot.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG,1024)
					.childHandler(new ChildChannelHandler());//事件处理的handler
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
		server.bind(8000);
	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new TimeServerHandler());
			
		}

	
		
	}
	private class TimeServerHandler extends ChannelInboundHandlerAdapter {

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg)
				throws Exception {
			ByteBuf buf=(ByteBuf)msg;
			byte[] bytes=new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			String query=new String(bytes,"utf-8");
			System.out.println("receiver:"+query);
			String curtime="query time".equals(query)?new Date().toString():"bad query";
			ByteBuf responbuf=Unpooled.copiedBuffer(curtime.getBytes());
			ctx.write(responbuf);
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
