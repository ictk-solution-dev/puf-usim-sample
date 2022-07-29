package com.ictkholdings.nftpufsample;

import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Util {
	private static final String LOG_TAG = MainActivity.class.getSimpleName();
	protected static final short[] CRC16TAB = { (short) 0x0000, (short) 0xC0C1, (short) 0xC181, (short) 0x0140,
			(short) 0xC301, (short) 0x03C0, (short) 0x0280, (short) 0xC241, (short) 0xC601, (short) 0x06C0,
			(short) 0x0780, (short) 0xC741, (short) 0x0500, (short) 0xC5C1, (short) 0xC481, (short) 0x0440,
			(short) 0xCC01, (short) 0x0CC0, (short) 0x0D80, (short) 0xCD41, (short) 0x0F00, (short) 0xCFC1,
			(short) 0xCE81, (short) 0x0E40, (short) 0x0A00, (short) 0xCAC1, (short) 0xCB81, (short) 0x0B40,
			(short) 0xC901, (short) 0x09C0, (short) 0x0880, (short) 0xC841, (short) 0xD801, (short) 0x18C0,
			(short) 0x1980, (short) 0xD941, (short) 0x1B00, (short) 0xDBC1, (short) 0xDA81, (short) 0x1A40,
			(short) 0x1E00, (short) 0xDEC1, (short) 0xDF81, (short) 0x1F40, (short) 0xDD01, (short) 0x1DC0,
			(short) 0x1C80, (short) 0xDC41, (short) 0x1400, (short) 0xD4C1, (short) 0xD581, (short) 0x1540,
			(short) 0xD701, (short) 0x17C0, (short) 0x1680, (short) 0xD641, (short) 0xD201, (short) 0x12C0,
			(short) 0x1380, (short) 0xD341, (short) 0x1100, (short) 0xD1C1, (short) 0xD081, (short) 0x1040,
			(short) 0xF001, (short) 0x30C0, (short) 0x3180, (short) 0xF141, (short) 0x3300, (short) 0xF3C1,
			(short) 0xF281, (short) 0x3240, (short) 0x3600, (short) 0xF6C1, (short) 0xF781, (short) 0x3740,
			(short) 0xF501, (short) 0x35C0, (short) 0x3480, (short) 0xF441, (short) 0x3C00, (short) 0xFCC1,
			(short) 0xFD81, (short) 0x3D40, (short) 0xFF01, (short) 0x3FC0, (short) 0x3E80, (short) 0xFE41,
			(short) 0xFA01, (short) 0x3AC0, (short) 0x3B80, (short) 0xFB41, (short) 0x3900, (short) 0xF9C1,
			(short) 0xF881, (short) 0x3840, (short) 0x2800, (short) 0xE8C1, (short) 0xE981, (short) 0x2940,
			(short) 0xEB01, (short) 0x2BC0, (short) 0x2A80, (short) 0xEA41, (short) 0xEE01, (short) 0x2EC0,
			(short) 0x2F80, (short) 0xEF41, (short) 0x2D00, (short) 0xEDC1, (short) 0xEC81, (short) 0x2C40,
			(short) 0xE401, (short) 0x24C0, (short) 0x2580, (short) 0xE541, (short) 0x2700, (short) 0xE7C1,
			(short) 0xE681, (short) 0x2640, (short) 0x2200, (short) 0xE2C1, (short) 0xE381, (short) 0x2340,
			(short) 0xE101, (short) 0x21C0, (short) 0x2080, (short) 0xE041, (short) 0xA001, (short) 0x60C0,
			(short) 0x6180, (short) 0xA141, (short) 0x6300, (short) 0xA3C1, (short) 0xA281, (short) 0x6240,
			(short) 0x6600, (short) 0xA6C1, (short) 0xA781, (short) 0x6740, (short) 0xA501, (short) 0x65C0,
			(short) 0x6480, (short) 0xA441, (short) 0x6C00, (short) 0xACC1, (short) 0xAD81, (short) 0x6D40,
			(short) 0xAF01, (short) 0x6FC0, (short) 0x6E80, (short) 0xAE41, (short) 0xAA01, (short) 0x6AC0,
			(short) 0x6B80, (short) 0xAB41, (short) 0x6900, (short) 0xA9C1, (short) 0xA881, (short) 0x6840,
			(short) 0x7800, (short) 0xB8C1, (short) 0xB981, (short) 0x7940, (short) 0xBB01, (short) 0x7BC0,
			(short) 0x7A80, (short) 0xBA41, (short) 0xBE01, (short) 0x7EC0, (short) 0x7F80, (short) 0xBF41,
			(short) 0x7D00, (short) 0xBDC1, (short) 0xBC81, (short) 0x7C40, (short) 0xB401, (short) 0x74C0,
			(short) 0x7580, (short) 0xB541, (short) 0x7700, (short) 0xB7C1, (short) 0xB681, (short) 0x7640,
			(short) 0x7200, (short) 0xB2C1, (short) 0xB381, (short) 0x7340, (short) 0xB101, (short) 0x71C0,
			(short) 0x7080, (short) 0xB041, (short) 0x5000, (short) 0x90C1, (short) 0x9181, (short) 0x5140,
			(short) 0x9301, (short) 0x53C0, (short) 0x5280, (short) 0x9241, (short) 0x9601, (short) 0x56C0,
			(short) 0x5780, (short) 0x9741, (short) 0x5500, (short) 0x95C1, (short) 0x9481, (short) 0x5440,
			(short) 0x9C01, (short) 0x5CC0, (short) 0x5D80, (short) 0x9D41, (short) 0x5F00, (short) 0x9FC1,
			(short) 0x9E81, (short) 0x5E40, (short) 0x5A00, (short) 0x9AC1, (short) 0x9B81, (short) 0x5B40,
			(short) 0x9901, (short) 0x59C0, (short) 0x5880, (short) 0x9841, (short) 0x8801, (short) 0x48C0,
			(short) 0x4980, (short) 0x8941, (short) 0x4B00, (short) 0x8BC1, (short) 0x8A81, (short) 0x4A40,
			(short) 0x4E00, (short) 0x8EC1, (short) 0x8F81, (short) 0x4F40, (short) 0x8D01, (short) 0x4DC0,
			(short) 0x4C80, (short) 0x8C41, (short) 0x4400, (short) 0x84C1, (short) 0x8581, (short) 0x4540,
			(short) 0x8701, (short) 0x47C0, (short) 0x4680, (short) 0x8641, (short) 0x8201, (short) 0x42C0,
			(short) 0x4380, (short) 0x8341, (short) 0x4100, (short) 0x81C1, (short) 0x8081, (short) 0x4040 };

	protected static final byte[] REVERSE_TAB = {
			(byte)0x00, (byte)0x80, (byte)0x40, (byte)0xC0, (byte)0x20, (byte)0xA0, (byte)0x60, (byte)0xE0, (byte)0x10, (byte)0x90, (byte)0x50, (byte)0xD0, (byte)0x30, (byte)0xB0, (byte)0x70, (byte)0xF0,
			(byte)0x08, (byte)0x88, (byte)0x48, (byte)0xC8, (byte)0x28, (byte)0xA8, (byte)0x68, (byte)0xE8, (byte)0x18, (byte)0x98, (byte)0x58, (byte)0xD8, (byte)0x38, (byte)0xB8, (byte)0x78, (byte)0xF8,
			(byte)0x04, (byte)0x84, (byte)0x44, (byte)0xC4, (byte)0x24, (byte)0xA4, (byte)0x64, (byte)0xE4, (byte)0x14, (byte)0x94, (byte)0x54, (byte)0xD4, (byte)0x34, (byte)0xB4, (byte)0x74, (byte)0xF4,
			(byte)0x0C, (byte)0x8C, (byte)0x4C, (byte)0xCC, (byte)0x2C, (byte)0xAC, (byte)0x6C, (byte)0xEC, (byte)0x1C, (byte)0x9C, (byte)0x5C, (byte)0xDC, (byte)0x3C, (byte)0xBC, (byte)0x7C, (byte)0xFC,
			(byte)0x02, (byte)0x82, (byte)0x42, (byte)0xC2, (byte)0x22, (byte)0xA2, (byte)0x62, (byte)0xE2, (byte)0x12, (byte)0x92, (byte)0x52, (byte)0xD2, (byte)0x32, (byte)0xB2, (byte)0x72, (byte)0xF2,
			(byte)0x0A, (byte)0x8A, (byte)0x4A, (byte)0xCA, (byte)0x2A, (byte)0xAA, (byte)0x6A, (byte)0xEA, (byte)0x1A, (byte)0x9A, (byte)0x5A, (byte)0xDA, (byte)0x3A, (byte)0xBA, (byte)0x7A, (byte)0xFA,
			(byte)0x06, (byte)0x86, (byte)0x46, (byte)0xC6, (byte)0x26, (byte)0xA6, (byte)0x66, (byte)0xE6, (byte)0x16, (byte)0x96, (byte)0x56, (byte)0xD6, (byte)0x36, (byte)0xB6, (byte)0x76, (byte)0xF6,
			(byte)0x0E, (byte)0x8E, (byte)0x4E, (byte)0xCE, (byte)0x2E, (byte)0xAE, (byte)0x6E, (byte)0xEE, (byte)0x1E, (byte)0x9E, (byte)0x5E, (byte)0xDE, (byte)0x3E, (byte)0xBE, (byte)0x7E, (byte)0xFE,
			(byte)0x01, (byte)0x81, (byte)0x41, (byte)0xC1, (byte)0x21, (byte)0xA1, (byte)0x61, (byte)0xE1, (byte)0x11, (byte)0x91, (byte)0x51, (byte)0xD1, (byte)0x31, (byte)0xB1, (byte)0x71, (byte)0xF1,
			(byte)0x09, (byte)0x89, (byte)0x49, (byte)0xC9, (byte)0x29, (byte)0xA9, (byte)0x69, (byte)0xE9, (byte)0x19, (byte)0x99, (byte)0x59, (byte)0xD9, (byte)0x39, (byte)0xB9, (byte)0x79, (byte)0xF9,
			(byte)0x05, (byte)0x85, (byte)0x45, (byte)0xC5, (byte)0x25, (byte)0xA5, (byte)0x65, (byte)0xE5, (byte)0x15, (byte)0x95, (byte)0x55, (byte)0xD5, (byte)0x35, (byte)0xB5, (byte)0x75, (byte)0xF5,
			(byte)0x0D, (byte)0x8D, (byte)0x4D, (byte)0xCD, (byte)0x2D, (byte)0xAD, (byte)0x6D, (byte)0xED, (byte)0x1D, (byte)0x9D, (byte)0x5D, (byte)0xDD, (byte)0x3D, (byte)0xBD, (byte)0x7D, (byte)0xFD,
			(byte)0x03, (byte)0x83, (byte)0x43, (byte)0xC3, (byte)0x23, (byte)0xA3, (byte)0x63, (byte)0xE3, (byte)0x13, (byte)0x93, (byte)0x53, (byte)0xD3, (byte)0x33, (byte)0xB3, (byte)0x73, (byte)0xF3,
			(byte)0x0B, (byte)0x8B, (byte)0x4B, (byte)0xCB, (byte)0x2B, (byte)0xAB, (byte)0x6B, (byte)0xEB, (byte)0x1B, (byte)0x9B, (byte)0x5B, (byte)0xDB, (byte)0x3B, (byte)0xBB, (byte)0x7B, (byte)0xFB,
			(byte)0x07, (byte)0x87, (byte)0x47, (byte)0xC7, (byte)0x27, (byte)0xA7, (byte)0x67, (byte)0xE7, (byte)0x17, (byte)0x97, (byte)0x57, (byte)0xD7, (byte)0x37, (byte)0xB7, (byte)0x77, (byte)0xF7,
			(byte)0x0F, (byte)0x8F, (byte)0x4F, (byte)0xCF, (byte)0x2F, (byte)0xAF, (byte)0x6F, (byte)0xEF, (byte)0x1F, (byte)0x9F, (byte)0x5F, (byte)0xDF, (byte)0x3F, (byte)0xBF, (byte)0x7F, (byte)0xFF,


	};//neo1seok 추가.

	/* 배열핵사 스트링으로 */
	public static String byteArrayToHexString(byte[] array) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : array) {
			int intVal = b & 0xFF;
			if (intVal < 0x10)
				hexString.append("0");
			hexString.append(Integer.toHexString(intVal));
		}
		return hexString.toString();
	}

	/* 핵사 스트링을 배열 바이트 문자열으로 */
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	/* 입력값이 헥사스티링 이나 바이트 어레이나 상관 없이 바이트어레이로 변경 한다. */
	public static byte [] toBytes(Object input){

		//Log.d(LOG_TAG,"input.getClass():"+input.getClass());
		if (input.getClass().equals(String.class)){
			//Log.d(LOG_TAG,"inputtype is string");
			return hexStringToByteArray((String)input);
		}
		else if (input.getClass().equals( byte[].class)){
			//Log.d(LOG_TAG,"inputtype is bytearray");
			return (byte[]) input;
		}

		return new byte[0];


	}
	public static byte[] joinBytes(Object  ... listbytes ) throws IOException {
		byte[] ret = new byte[0];

		ByteArrayOutputStream bstreamout = new ByteArrayOutputStream();

		for(Object tmp  : listbytes){
			bstreamout.write(toBytes(tmp));
		}


		return bstreamout.toByteArray();
	}
	public static String toHexStr(Object input){
		//Log.d(LOG_TAG,"input.getClass():"+input.getClass());
		if (input.getClass().equals(String.class)){
			//Log.d(LOG_TAG,"inputtype is string");
			return (String)input;
		}
		else if (input.getClass().equals( byte[].class)){
			//Log.d(LOG_TAG,"inputtype is bytearray");
			return byteArrayToHexString((byte[]) input) ;
		}



		return "";


	}
	public static byte[] shortToBytes(short input ){
		return  ByteBuffer.allocate(2).putShort((short)input).array();
	}
	public static short bytesToShort(byte[] input){
		return ByteBuffer.wrap(input).getShort(); // big-endian by default
	}
	public static byte[] sha256(Object ...msg) throws NoSuchAlgorithmException, IOException {

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(joinBytes(msg));

		return md.digest();
	}
	public static byte[] sha256Exp200Left16(Object ...msg) throws NoSuchAlgorithmException, IOException {
		byte[] hash_val = joinBytes(msg);
		for(int i =0 ; i < 200 ; i++){
			hash_val = sha256(hash_val);
		}


		return Arrays.copyOf(hash_val,16);
	}

	public static byte[] shareKey(Object s_chal,Object c_chal,Object system_const) throws IOException, NoSuchAlgorithmException {

		return Arrays.copyOf(sha256(s_chal,c_chal,system_const),16);

	}

	public static byte[]  encValue(Object aes_key,Object msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

		byte[] baes_key = toBytes(aes_key);
		byte[] bmsg = toBytes(msg);
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

		Key key = new SecretKeySpec(baes_key, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(new byte[16]));
		byte[] enc = cipher.doFinal(bmsg);

		return enc;

	}

	public static byte[]  decValue(Object aes_key,Object msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

		byte[] baes_key = toBytes(aes_key);
		byte[] bmsg = toBytes(msg);
		Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

		Key key = new SecretKeySpec(baes_key, "AES");
		cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(new byte[16]));
		byte[] enc = cipher.doFinal(bmsg);

		return enc;
	}
	public static byte[]  decValuePkcs7(Object aes_key,Object msg,Object iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

		byte[] baes_key = toBytes(aes_key);
		byte[] bmsg = toBytes(msg);
		byte[] biv = toBytes(iv);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		Key key = new SecretKeySpec(baes_key, "AES");
		cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(biv));
		byte[] enc = cipher.doFinal(bmsg);

		return enc;
	}
	public static byte[] getRandom(int size){
		byte [] ret = new byte[size];
		Random rand = new Random();
		rand.nextBytes(ret);
		return ret;

	}


	protected static int calcCRC(byte[] abData, int iOffset, int iLength) {
		int crc = 0;
//		String []temp_array = new String[2];
//		String []crc_array = new String[]{"",""};
//		char[][] char_buf=new char[2][8];
		int crc_idx =0;
		for (int i = 0; i < iLength; i++) {

			crc_idx = (crc ^ abData[iOffset + i]) & 255;
			//Log.d(LOG_TAG, String.format("idx:0x%x,%d", crc_idx, crc_idx));
			crc = ((crc >> 8) ^ CRC16TAB[crc_idx]) & 0xFFFF;
		}
		//Log.d(LOG_TAG,String.format("crc:0x%x",crc));

		byte[] bytes = shortToBytes((short)crc);

//		byte[] bytes = ByteBuffer.allocate(2).putShort((short)crc).array();
		byte[] newbytes =new byte[2];
		int idx =0;
		for (byte el : bytes) {
			int i2 = el & 0xFF;
			newbytes[idx++] = REVERSE_TAB[i2];
		}

		//ByteBuffer wrapped = ByteBuffer.wrap(newbytes); // big-endian by default
		short newcrc = bytesToShort(newbytes);
		//Log.d(LOG_TAG,String.format("new crc:0x%x",newcrc));
		return newcrc&0xffff; // 1

	}
	public static   byte [] getBuffFromAsset(Resources resources, String file_name) throws IOException {

		AssetManager am = resources.getAssets() ;
		InputStream is = null ;

		String text = "" ;
		byte buf[] = null;

		try {
			//AssetFileDescriptor fd = am.openFd(file_name);
			//is = fd.createInputStream();
			is = am.open(file_name);
			int size = is.available();
			//long filesize = fd.getLength();
			//long filesize = 1024;
			buf = new byte[size] ;
			is.read(buf);



		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (is != null) is.close() ;
		}


		return buf;
	}
	
		/**
	 * convert to BigInteger
	 * @param input : input value (byte [] or hexstr)
	 * @return BigInteger
	 */
	public static BigInteger toBigInteger(Object input){
		
		return  new BigInteger(1,toBytes(input));
	}
	
	/**
	 * convert to bytes from toBytesFromBigInteger
	 * @param input BigInteger value 
	 * @param length length of value 
	 * @return byte[] value
	 * @throws IOException
	 */
	public static byte [] toBytesFromBigInteger(BigInteger input,int length) throws IOException{
		byte []  ret = input.toByteArray();
		//System.out.println("ret: "+Util.toHexStr(ret));
		if (ret[0]==0x00 && ret.length == length+1){
			return subBytes(ret,1, ret.length);
		}
		if ( ret.length < length){
			return Util.joinBytes("00" ,ret);
		}
		return  ret;
	}
	/**
	 * cut from all bytes 
	 * @param in : input byte []
	 * @param st : index of start position  
	 * @param ed : index of end position
	 * @return result 
	 */
	public static byte [] subBytes(byte[]in,int st, int ed){
		byte[] ret = new byte[ed-st]; 
		System.arraycopy(in, st, ret, 0, ed-st);
		return  ret;
	}
	/**
	 * print hex string 
	 * @param title  : title 
	 * @param input : input (byte [] or hexstr)
	 */
	public static void printHexstr(String title, Object input){
		System.out.println(title+" : "+Util.toHexStr(input));
	}
	
	/**
	 * print msg 
	 * @param fmt : foramt
	 * @param input : list of objects 
	 */
	public static void printMsg(String fmt, Object ... input){
		System.out.printf(fmt, input);
	}

}
