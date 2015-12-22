package com.zj.netty.simpleDemo.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

public class HttpServer {
	String defaultUrl="/src/com/zj";
	int port=9000;
	private static final Pattern INSCURE_URI=Pattern.compile(".*[<>&\"].*");
	public static void main(String[] args) {
		new HttpServer().run();
	}
	public void run(){

		EventLoopGroup bossGroup=new NioEventLoopGroup();//reactor线程组，用于分派处理事件线程
		EventLoopGroup workerGroup=new NioEventLoopGroup();
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
							ch.pipeline().addLast(new HttpRequestDecoder());
							ch.pipeline().addLast(new HttpObjectAggregator(65536));
							ch.pipeline().addLast(new HttpResponseEncoder());
							ch.pipeline().addLast(new ChunkedWriteHandler());
							ch.pipeline().addLast(new HttpFileServerHandler());
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
	class HttpFileServerHandler extends  SimpleChannelInboundHandler<FullHttpRequest>{
		@Override
		protected void channelRead0(ChannelHandlerContext ctx,
				FullHttpRequest request) throws Exception {
			if(!request.getDecoderResult().isSuccess()){
				sendError(ctx,HttpResponseStatus.BAD_REQUEST);
				return;
			}
			if(request.getMethod()!=HttpMethod .GET){
					sendError(ctx,HttpResponseStatus.METHOD_NOT_ALLOWED);
					return;
			}
			String uri=request.getUri();
			System.out.println("uri:"+uri);
			String path=saninital(uri);
			System.out.println("parsed path"+path);
			if(path==null){
				sendError(ctx,HttpResponseStatus.FORBIDDEN);
				return;
			}
			File f=new File(path);
			if(!f.exists()||f.isHidden()){
				sendError(ctx,HttpResponseStatus.NOT_FOUND);
				return;
			}
			if(f.isDirectory()){
				if(uri.endsWith("/")){
					sendList(ctx,f);
				}else{
					sendRedirect(ctx,uri+"/");
				}
				return;
			}
			if(!f.isFile()){
				sendError(ctx,HttpResponseStatus.FORBIDDEN);
				return;
			}
			RandomAccessFile rf;
			try {
				rf=new RandomAccessFile(f,"r");
			} catch (FileNotFoundException e) {
				sendError(ctx,HttpResponseStatus.NOT_FOUND);
				return;
			}
			FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			setContentLength(response,rf.length());
			setContentHeader(response,f);
			//判断是否保持连接
			if(HttpHeaders.isKeepAlive(request)){
				response.headers().set(HttpHeaders.Names.CONNECTION,HttpHeaders.Values.KEEP_ALIVE );
			}
			ctx.write(response);
			FileRegion region=new DefaultFileRegion(rf.getChannel(), 0, rf.length());
//			ChannelFuture sendFileFuture=ctx.write(
//					new HttpChunkedInput(new ChunkedFile(rf, 0,rf.length(),8192)), ctx.newProgressivePromise());
			ChannelFuture sendFileFuture=ctx.write(region, ctx.newProgressivePromise());
			sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
				
				@Override
				public void operationComplete(ChannelProgressiveFuture future)
						throws Exception {
					System.err.println("transfer:compelte");
					
				}
				
				@Override
				public void operationProgressed(ChannelProgressiveFuture future,
						long progress, long total) throws Exception {
					if(progress<0){
						System.err.println("transfer:"+progress);
					}else{
						System.err.println("transfer: "+progress+"/"+total);
					}
				}
			});
			ChannelFuture lastFuture=ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
			if(!HttpHeaders.isKeepAlive(request)){
				lastFuture.addListener(ChannelFutureListener.CLOSE);
			}
		}
	
		private void setContentHeader(FullHttpResponse response, File f) {
			MimetypesFileTypeMap mimemap=new MimetypesFileTypeMap();
			response.headers().set("CONTENT_TYPE",mimemap.getContentType(f));
			System.out.println(mimemap.getContentType(f));
		}
		private void setContentLength(FullHttpResponse response, long length) {
			response.headers().set("CONTENT_LENGTH",length);
		}
		/**
		 * 转发请求
		 * @param ctx
		 * @param newuri
		 */
		private void sendRedirect(ChannelHandlerContext ctx, String newuri) {
			HttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
			response.headers().set("LOCATION", newuri);
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
		private void sendList(ChannelHandlerContext ctx, File dir) {
			FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			response.headers().set("Content-Type","text/html;charset=UTF-8");
			StringBuffer buf=new StringBuffer();
			buf.append("<!DOCTYPE html>\r\n<html>\r\n<head><title>目录:</title></head>\r\n<body>\r\n");
			buf.append("<h3>").append(dir.getPath()).append("目录").append("</h3>\r\n");
			buf.append("<ul>");
			buf.append("<li><a href=\"../\" >..</a></li>\r\n");
			for(File f:dir.listFiles()){
				if(f.isHidden()||!f.canRead()){
					continue;
				}
				buf.append("<li><a href=\"").append(f.getName()).append("\"  >").append(f.getName()).append("</a></li>\r\n");
			}
			buf.append("</ul></body></html>\r\n");
			ByteBuf bytebuf=Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);
			response.content().writeBytes(bytebuf);
			bytebuf.release();
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
		private String saninital(String uri) {
			try {
				uri=URLDecoder.decode(uri,"utf-8");
			} catch (UnsupportedEncodingException e) {
				try {
					uri=URLDecoder.decode(uri, "ISO-8859-1");
				} catch (UnsupportedEncodingException e1) {
						throw new Error();
				}
			}
			if(!uri.startsWith(defaultUrl)){
				return null;
			}
			if(!uri.startsWith("/")){
				return null;
			}
			uri=uri.replace('/', File.separatorChar);
			if(uri.contains("."+File.separatorChar)||uri.contains(File.separatorChar+".")
					||uri.startsWith(".")||uri.endsWith(".")||INSCURE_URI.matcher(uri).matches()){
				return null;
			}
			uri=System.getProperty("user.dir")+uri;
			return uri;
		}
		protected void sendError(ChannelHandlerContext ctx,HttpResponseStatus status){
			HttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,Unpooled.copiedBuffer("Fail:"+status.toString()+"\r\n",CharsetUtil.UTF_8));
			response.headers().set("Content-Type","text/plain;charset=UTF-8");
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
		
	}
}
