package com.zj.netty.simpleDemo.protocol_define;

import java.io.IOException;

import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
/**
 * jboss的序列化工具
 * @author Administrator
 *
 */
public class MarshallingCodeCFactory {
	/**
	 * 反序列化类工厂
	 * @return
	 */
	public static MarshallingDecoder  buildMarshallingDecoder(){
		MarshallerFactory factory=Marshalling.getProvidedMarshallerFactory("serial");
		MarshallingConfiguration config=new MarshallingConfiguration();
		config.setVersion(5);
		UnmarshallerProvider provider=new DefaultUnmarshallerProvider(factory, config);
		MarshallingDecoder decoder=new MarshallingDecoder(provider);
		return decoder;
	}
	/**
	 * 序列化类工厂
	 * @return
	 */
	public static MarshallingEncoder  buildMarshallingEncoder(){
		MarshallerFactory factory=Marshalling.getProvidedMarshallerFactory("serial");
		MarshallingConfiguration config=new MarshallingConfiguration();
		config.setVersion(5);
		MarshallerProvider provider=new DefaultMarshallerProvider(factory, config);
		MarshallingEncoder encoder=new MarshallingEncoder(provider);
		return encoder;
	}
	/**
	 * 反序列化类工厂
	 * @return
	 */
	public static Marshaller  buildMarshalling(){
		MarshallerFactory factory=Marshalling.getProvidedMarshallerFactory("serial");
		MarshallingConfiguration config=new MarshallingConfiguration();
		config.setVersion(5);
		Marshaller marshaller=null;
		try {
			marshaller = factory.createMarshaller(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return marshaller;
	}
	/**
	 * 反序列化类工厂
	 * @return
	 */
	public static Unmarshaller  buildUnmarshalling(){
		MarshallerFactory factory=Marshalling.getProvidedMarshallerFactory("serial");
		MarshallingConfiguration config=new MarshallingConfiguration();
		config.setVersion(5);
		Unmarshaller unmarshaller=null;
		try {
			unmarshaller = factory.createUnmarshaller(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return unmarshaller;
	}
}
