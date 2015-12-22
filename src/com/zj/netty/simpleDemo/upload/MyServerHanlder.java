package com.zj.netty.simpleDemo.upload;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;

public class MyServerHanlder extends SimpleChannelInboundHandler<HttpObject> {
	StringBuilder responseContent=new StringBuilder();
	HttpRequest request;
	HttpPostRequestDecoder postDecoder;
	HttpData  currentpartial;
	/* httpdata工厂 , 用来构建 httpostrequestdecoder */
	HttpDataFactory factory=new DefaultHttpDataFactory();
	 static{
		 DiskFileUpload.baseDirectory=null;
		 DiskFileUpload.deleteOnExitTemporaryFile=true;
		 DiskAttribute.baseDirectory=null;
		 DiskAttribute.deleteOnExitTemporaryFile=true;
	 }
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg)
			throws Exception {
		/*
		 * 属于请求（头）
		 */
		if(msg instanceof HttpRequest){
			request=(HttpRequest) msg;
			responseContent.setLength(0);
			responseContent.append("-----------welcom---------_\r\n");
			responseContent.append("uri:"+request.uri()+"\r\n");
			responseContent.append("protocol:"+request.protocolVersion()+"\r\n");
			for(Entry<String,String> entry:request.headers()){
				responseContent.append(entry.getKey()+":"+entry.getValue()+"\r\n");
			}
			QueryStringDecoder qstrd=new QueryStringDecoder(request.uri(),Charset.forName("utf8"));
			Map<String, List<String>> map=qstrd.parameters();
			for(Entry<String, List<String>> entry:map.entrySet()){
				responseContent.append("parm:"+entry.getKey()+"\r");
				for(String s:entry.getValue()){
					responseContent.append(s+"\r");
				}
				responseContent.append("\n");
			}
			if(request.method()==HttpMethod.GET){
				responseContent.append("\r\n get content end!");
				return;
			}
			try {
				postDecoder=new HttpPostRequestDecoder(factory,request);
			} catch (ErrorDataDecoderException  e) {
				e.printStackTrace();
				responseContent.append(e.toString());
				writeResponse(ctx.channel());
				ctx.channel().close();
				return;
			}
		}
		/*
		 * 属于请求(内容),postDecoder不为空则是post请求
		 */
		if(postDecoder!=null){
			if(msg instanceof HttpContent){//request如果是chuncked ,则会分为一些chunck
				HttpContent chunk=(HttpContent) msg;
				try{
					postDecoder.offer(chunk);//postdecoder从chunck中解析
				}catch (ErrorDataDecoderException  e) {
					e.printStackTrace();
					responseContent.append(e.toString());
					writeResponse(ctx.channel());
					ctx.channel().close();
					return;
				}
				readHttpdataChunk();
				//所有内容发送完成
				if(chunk instanceof LastHttpContent){
					writeResponse(ctx.channel());
					reset();
				}
				
			}
		}else{
			writeResponse(ctx.channel());
		}
		
		
	}
	
	private void reset() {
		request=null;
		postDecoder.destroy();
		postDecoder=null;
		
	}

	/**
	 * 读这一段chunk的内容
	 */
	private void readHttpdataChunk() {
		if(postDecoder.hasNext()){
			InterfaceHttpData data=postDecoder.next();//可能是attribute或upload，对于很长的内容是组装完了的，可以看下面查看进度理解
			if(data!=null){
				System.out.println(data.getName()+" ** "+data.getHttpDataType() );
				if(currentpartial==data){
					System.out.println(data.getName()+"finalized");
					currentpartial=null;
				}
				try{
					writeHttpdata(data);
				}
				finally{
					data.release();
				}
			}
		}
		/**
		 * 对于很长的内容attribute或file,是一次次接收的
		 * httpdata.definedLength 内容的总长度，详情看chunck说明,httpdata.length()是不断增长的
		 * currentPartialHttpData 当前部分内容,postDecoder会把chuncked部分内容组装起来
		 */
		InterfaceHttpData  data=postDecoder.currentPartialHttpData();
		if (data!=null) {
			if (currentpartial == null) {
				currentpartial = (HttpData) data;
				if (currentpartial.getHttpDataType().equals(
						HttpDataType.Attribute)) {
					System.err.println("Start parse attrbute");
				}
				if (currentpartial.getHttpDataType().equals(
						HttpDataType.FileUpload)) {
					System.err.println("Start parse file");
				}
				System.err.println("definelength:"
						+ currentpartial.definedLength());
			}
			if (currentpartial.definedLength() > 0) {
				System.err.println("当前进度:"
						+ (currentpartial.length() * 1.0 / currentpartial
								.definedLength()));
			} else {
				System.err.println("当前块长度:" + currentpartial.length());
			}
		}
			
	}
	private void writeHttpdata(InterfaceHttpData data) {
		/**
		 * 如果是属性
		 */
		if(data.getHttpDataType().equals(HttpDataType.Attribute)){
			Attribute attr=(Attribute) data;
			try {
				System.out.println(attr.getName()+"--"+attr.getValue());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/**
		 * 如果是文件
		 */
		if(data.getHttpDataType().equals(HttpDataType.FileUpload)){
			FileUpload file=(FileUpload)data;
			//当文件接收完成
			if(file.isCompleted()){
				try {
					System.out.println(file.getString(CharsetUtil.UTF_8));
					if(file.isInMemory()){
						File f=new File("mytest.txt");
						file.renameTo(f);//将内存中(此处没设置tempdir)文件复制到本地
						postDecoder.removeHttpDataFromClean(file);//清空文件
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				System.out.println("file shoulder be contunue but not");
			}
		}
		
	}

	/**
	 * 写HttpResponse
	 * @param ch
	 */
	public void writeResponse(Channel ch){
		ByteBuf buf=Unpooled.copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
		responseContent.setLength(0);
		boolean close=request.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE,true);
		HttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,buf);
		response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
		if(!close){
			response.headers().add(HttpHeaderNames.CONTENT_LENGTH, ""+buf.readableBytes());
		}
		ChannelFuture  f=ch.writeAndFlush(response);
		if(close){
			/*    监听chanel完成后关闭，很常用      */
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	

}
