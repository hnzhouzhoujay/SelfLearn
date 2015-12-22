package com.zj.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Securitytest {
	public static byte[] getMD5(String content) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		MessageDigest md= MessageDigest.getInstance("MD5");
		byte[] result=md.digest(content.getBytes("utf8"));
		return result;
	}
	public static byte[] getSHA(String content) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		MessageDigest md= MessageDigest.getInstance("SHA1");
		byte[] result=md.digest(content.getBytes("utf8"));
		return result;
	}
	/**
	 * 字节转换成base64字符串形式表示,每6位转成1个字符
	 * @param b
	 * @return
	 */
	public static String byte2base64 (byte[] b){
		BASE64Encoder   encoder=new BASE64Encoder();
		return encoder.encode(b);
	}
	public static byte[] base642byte (String base64) throws IOException{
		BASE64Decoder   decoder=new BASE64Decoder();
		return decoder.decodeBuffer(base64);
	}
	/**
	 * 生成des算法的key ，并转成base64的字符串
	 * @param source 原始字节内容
	 * @return base64的字符串
	 * @throws NoSuchAlgorithmException
	 */
	public static String genKeyDES (byte[] source) throws NoSuchAlgorithmException{
		KeyGenerator keygen= KeyGenerator.getInstance("DES");
		keygen.init(56);
		SecretKey key=keygen.generateKey();
		String base64=byte2base64(key.getEncoded());
		return base64;
	}
	public static SecretKey loadKeyDES(String base64) throws IOException {
		byte[] bytes=base642byte(base64);
		SecretKey key =new SecretKeySpec(bytes,"DES");
		return key;
	}
	public static byte[] encryptDES(byte[] source, SecretKey key) throws Exception{
		Cipher cipher=Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrytBytes=cipher.doFinal(source);
		return encrytBytes;
	}
	public static byte[] decryptDES(byte[] encryptSource, SecretKey key) throws Exception{
		Cipher cipher=Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrytBytes=cipher.doFinal(encryptSource);
		return decrytBytes;
	}
	/**
	 * 生成des算法的key ，并转成base64的字符串
	 * @param source 原始字节内容
	 * @return base64的字符串
	 * @throws NoSuchAlgorithmException
	 */
	public static String genKeyAES (byte[] source) throws NoSuchAlgorithmException{
		KeyGenerator keygen= KeyGenerator.getInstance("AES");
		keygen.init(128);//支持128,256
		SecretKey key=keygen.generateKey();
		String base64=byte2base64(key.getEncoded());
		return base64;
	}
	public static SecretKey loadKeyAES(String base64) throws IOException {
		byte[] bytes=base642byte(base64);
		SecretKey key =new SecretKeySpec(bytes,"AES");
		return key;
	}
	public static byte[] encryptAES(byte[] source, SecretKey key) throws Exception{
		Cipher cipher=Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrytBytes=cipher.doFinal(source);
		return encrytBytes;
	}
	public static byte[] decryptAES(byte[] encryptSource, SecretKey key) throws Exception{
		Cipher cipher=Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrytBytes=cipher.doFinal(encryptSource);
		return decrytBytes;
	}
	/**
	 * 生成公/私密匙队
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair getRSAKeyPair() throws NoSuchAlgorithmException{
		KeyPairGenerator keygen=KeyPairGenerator.getInstance("RSA");
		keygen.initialize(512);
		KeyPair keypair=keygen.generateKeyPair();
		return keypair;
	}
	public static String getRSAPublicKey(KeyPair keypair) {
		PublicKey publickey= keypair.getPublic();
		String key64=byte2base64(publickey.getEncoded());
		return key64;
	}
	public static String getRSAPrivateKey(KeyPair keypair) {
		PrivateKey privatekey=keypair.getPrivate();
		String key64=byte2base64(privatekey.getEncoded());
		return key64;
	}
	/**
	 * 公匙，需把KeyPair对中的PublicKey 转成X509EncodedKeySpec 
	 * @param sPubkey
	 * @return
	 * @throws Exception
	 */
	public static PublicKey string2PublicKey(String sPubkey) throws Exception{
		byte[] bPubkey=base642byte(sPubkey);
		//需把KeyPair对中的PublicKey 转成X509EncodedKeySpec 
		X509EncodedKeySpec keyspc=new X509EncodedKeySpec(bPubkey);
		//生成 rsa keyFactory
		KeyFactory factory=KeyFactory.getInstance("RSA");
		//生成 公匙
		PublicKey pubkey=factory.generatePublic(keyspc);
		return pubkey;
	}
	/**
	 * 私匙 ，需把KeyPair对中的PrivateKey 转成PKCS8EncodedKeySpec 
	 * @param sPrivatekey
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey string2PrivateKey(String sPrivatekey) throws Exception{
		byte[] bPubkey=base642byte(sPrivatekey);
		PKCS8EncodedKeySpec keyspc=new PKCS8EncodedKeySpec(bPubkey);
		KeyFactory factory=KeyFactory.getInstance("RSA");
		PrivateKey privatekey=factory.generatePrivate(keyspc);
		return privatekey;
	}
	public static byte[] encryptRSA(byte[] source, PublicKey key) throws Exception{
		//Cipher 密码，公匙加密，私匙解密 ，（私匙加密 ，公匙解密）
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrytBytes=cipher.doFinal(source);
		return encrytBytes;
	}
	public static byte[] decryptRSA(byte[] encryptSource, PrivateKey key) throws Exception{
		Cipher cipher=Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrytBytes=cipher.doFinal(encryptSource);
		return decrytBytes;
	}
	
	public static  String byteToStr(byte[] bytes) {
		String digestStr="";
		for(int i=0;i<bytes.length;i++){
			digestStr+=byteToHexStr(bytes[i]);
		}
		return digestStr;
	}
	private static String byteToHexStr(byte b) {
		char[] digit={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		char[] tmp=new char[2];
		tmp[0]=digit[(b>>>4) & 0x0F];
		tmp[1]=digit[b & 0x0F];
		String s=new String(tmp);
		return s;
	}

	public static void main(String[] args) {
		try {
			/*//md5 sha1 加密 ，单向加密
			byte[] md5=MD5test.getMD5("my name is zhoujie");
			System.out.println(byte2base64(md5));
			byte[] sha1=MD5test.getSHA("my name is zhoujie");
			System.out.println(byte2base64(sha1));
			*/
			
			/*DES加密解密  ,对称
			byte[] source="always go ,never stop!".getBytes();
			System.out.println("source的base64:"+byte2base64(source));
			String source64=genKeyDES(source);
			System.out.println("key的base64:"+source64);
			byte[] encryptSource=encryptDES(source,loadKeyDES(source64));
			System.out.println("DES加密:"+byte2base64(encryptSource));
			byte[] decryptSource=decryptDES(encryptSource,loadKeyDES(source64));
			System.out.println("DES解密:"+byte2base64(decryptSource));
			*/
			/*AES加密解密，比DES key更长 ，对称
			byte[] source="always go ,never stop!".getBytes();
			System.out.println("source的base64:"+byte2base64(source));
			String source64=genKeyAES(source);
			System.out.println("key的base64:"+source64);
			byte[] encryptSource=encryptAES(source,loadKeyAES(source64));
			System.out.println("AES加密:"+byte2base64(encryptSource));
			byte[] decryptSource=decryptAES(encryptSource,loadKeyAES(source64));
			System.out.println("AES解密:"+byte2base64(decryptSource));
			*/
			
			byte[] source="always go ,never stop!".getBytes();
			System.out.println("source的base64:"+byte2base64(source));
			KeyPair keypair =getRSAKeyPair();
			String s_publickey=getRSAPublicKey(keypair);
			System.out.println("公匙 :"+s_publickey);
			String s_privatekey=getRSAPrivateKey(keypair);
			System.out.println("私匙 :"+s_privatekey);
			byte[] encryptSource=encryptRSA(source,string2PublicKey(s_publickey));
			System.out.println("RSA加密:"+byte2base64(encryptSource));
			byte[] decryptSource=decryptRSA(encryptSource,string2PrivateKey(s_privatekey));
			System.out.println("RSA解密:"+byte2base64(decryptSource));
		/*	double result=10000;
			for(int i=0;i<10;i++){
				result=result*Math.pow(1.05, 12);
			}
			System.out.println(result);*/
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
