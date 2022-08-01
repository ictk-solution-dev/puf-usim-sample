package com.ictkholdings.nftpufsample;

import android.content.Context;
import android.util.Log;

import com.lguplus.usimlib.TsmClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.lguplus.usimlib.TsmClientConnectListener;
import com.lguplus.usimlib.TsmUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class UsimPufHandler {
	private static final String LOG_TAG = UsimPufHandler.class.getSimpleName();
	public boolean mServiceConnected;
	private static final int DATA_LENGTH = 64;
	public static byte[] Resp_APDU;
	static final char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	public static  String PIN = "11223344"; //외부 및 G3 인증에 필요한 패스워드 //향후 변경가능성있음.

	//http://aiotsrv.iptime.org:3000/projects/usim-plus/wiki/MobileID_Applet
	// 제휴사 코드(유플러스 발급)
	private static String ClientId = "9999";
	// 제휴사 AppKey(유플러스 발급)
	private static String AppKey = "D5E7F5334A805F3E"; //MobileID AppKey
	private static String Aid = "D4106509900098";  //MobileID Aid
	private String UiccIdEncKey ="764F964DA1DC6E35841D86585B36009F3BEC1DCE7753E8E23E35FAD891B87495";//MobileID UiccIdEncKey

	private static String system_const = "AC3DE522922F5EAA80A10BB272424F22";

	//4E54000000000000  (AES-128속성 설정)
	private static String CMD_SETUP_KEY    = "00AD000026"+"032581070000"+"4E54000000000000"+"0E54000000000000"+"0E54000000000000"+"0E54000000000000";

	private static String CMD_CHECK_KEY      = "00AF000000";
	private static String CMD_SET_PASSWORD   = "00AB00000A"+"0309"+"82000000"+"11223344";

	private static String CMD_SAVE_DATA      = "00ABFF0046"+"0345"+"880C0000"; // CMD + iv + Data
	private static String CMD_READ_DATA      = "00ABFF0146"+"0345"+"890C0000"; // CMD + iv + Data

	private static String CMD_AES_ENCRYPT    = "00AD020026"+"0345"+"880C0000";
	private static String CMD_AES_DECRYPT    = "00AD030026"+"0345"+"890C0000";
	private static String CMD_IMPORT_KEY1    = "00AD010026"+"0325"+"810C0001";
	private static String CMD_DELETE_KEY1    = "00B0010026"+"0325"+"810C0001";
	private static String DELETE_KEY         = "0000000000000000000000000000000000000000000000000000000000000000";//128bit init
	private static String CMD_G3_WAKEUP      = "00A6000004";
	private static String CMD_G3_SLEEP       = "00A8000001"+"01";//01: Sleep
	// 이 두개만 쓴다.
	private static String CHECK_MOBILE_ID    = "902B000000"; //00 : Data is none ,  01: Data exists
	private static String DELETE_MOBILE_ID   = "902C000000";
	public static final String AES_KEY       = "B2ED992186A5CB19F6668AADE821F502"; //G3내부에서 처리되지만 Applet 갱신을 하지 안기위해 넣어준다.
	public static final String AES_IV        = "BBED9921DDA5CB19F6668BBDE821F502"; //G3내부에서 처리되지만 Applet 갱신을 하지 안기위해 넣어준다.
	public static String Data                = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF0000000000000000000000000000000011111111111111111111111111111111"; //64byte //128bit
	//public static String Data                = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"; //64byte //128bit
	public static String Data_garbage        = "0107777888811-96-123456-880000000000881122-1234567888ABC20200808"; //64byte //128bit
	public TsmClient mClient;

	public static String RAW_APDU ="00B400";
	public static String READ_SN ="80000000";
	public static String GET_CHAL ="84200000";
	public static String WRITEKEY ="810C0001";
	public static String AES_SIGN ="880C0000";
	public static String RESET ="9F000000";
	public static String WRITEKEY_KEY0 ="81000001";
	public static String AES_SIGN_KEY0 ="88010000";


	public static String ECDSA_SIGN_PUF ="86100001";
	public static String ECDSA_SIGN_K1 ="86080001";
	public static String ECDSA_SIGN_R1 ="860C0001";


	public static String GET_PUK_PUF ="8C100001";
	public static String GET_PUK_R1 ="8C0C0001";
	public static String GET_PUK_K1 ="8C080001";

	public static final int EC_MODE_PRK_R1 = 0;
	public static final int EC_MODE_PRK_K1 = 1;
	public static final int EC_MODE_PUF = 2;

	public static final int PRK_R1_INDEX = 12;
	public static final int PRK_K1_INDEX = 8;

	public static final int PRK_PUF_INDEX = 16;


	byte [] _rw_session_key = new byte[16];

	public class CrcException extends Exception{

	}

	TsmClientConnectListener mCustomConnectListener;
	public UsimPufHandler(Context parent,TsmClientConnectListener connectListener){
		mCustomConnectListener = connectListener;
		init(parent);
	}
	public UsimPufHandler(Context parent){
		this(parent,null);

	}

	void init(Context parent){
		////////////////////////////////////////////////////////////////////////////////////////////////
		//ADPU Connection
		Log.d(LOG_TAG,"init");
		TsmUtil.setLogLevel(0);
		mClient = new TsmClient(parent);


		Log.d(LOG_TAG,"new TsmClient(parent)");



		mClient.setConnectListener(mConnectListener); // 아래 3.1.5섹션 참고
		Log.d(LOG_TAG,"mClient.setConnectListener(mConnectListener)");

		mClient.connectToService();
		Log.d(LOG_TAG,"mClient.connectToService()");

		mClient.setClientId(ClientId);            // 제휴사 코드(유플러스 발급)
		Log.d(LOG_TAG,"mClient.setClientId(ClientId)");

		mClient.setAppKey(AppKey);  // 제휴사 AppKey(유플러스 발급)
		Log.d(LOG_TAG,"mClient.setAppKey(AppKey)");

		mClient.setUiccIdEncKey(UiccIdEncKey); // UICCID 암호화 키(유플러스 발급)
		Log.d(LOG_TAG,"mClient.setUiccIdEncKey(UiccIdEncKey)");

		////////////////////////////////////////////////////////////////////////////////////////////////
	}
	/* transmitApdu */
	public byte[] transmitApdu(String cmd) {
		Log.d(LOG_TAG,"transmitApdu snd:"+cmd);
		try {
			Resp_APDU = mClient.transmitApdu(cmd);
			Log.d(LOG_TAG,"transmitApdu Resp_APDU:"+Util.byteArrayToHexString(Resp_APDU));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Resp_APDU;
	}

	public void wait_to_connected() throws UsimConnectionException {
		for(int i =0 ; i <100;i++)
		{
			if(mServiceConnected) return;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(!mServiceConnected) throw new UsimConnectionException();





	}

	//////////////////////////////////////////////////////////////////////////////////
	//Tsm inner function
	//g3 wake up
	//return value: 04113343 in case of success
	//Note : The Process of G3 is as follow:
	// 2 step and 3 step should runs only one time.
	// 1. G3 wake up
	// 2. Set the configuration of key
	// 3. Import any key
	// 4. Run any function
	// 5. G3 sleep
	public  boolean G3_OpenChannel()  {
		for(int i = 0 ; i <10;i++){
			try {
				return  mClient.openChannel(Aid);
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(LOG_TAG,"Open Channel Error:"+e);
				try {
					Thread.sleep(300); //1초 대기
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
				continue;
			}

		}
		return false;

	}
	public void G3_CloseChannel() {
		try {
			mClient.closeChannel();
			Log.d(LOG_TAG,"Close Channel.");
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(LOG_TAG,"Close Channel Error:"+e);
		}
	}
	public boolean G3_WakeUp() {
		String result;
		result = Util.byteArrayToHexString(transmitApdu(CMD_G3_WAKEUP));
		return result.substring(result.length()-4).equals("9000");//1235803449000
	}


	//jsplee
	public byte[] SendPacket(byte[] packet, int command) throws IOException {
		byte[] temp_apdu;
		int size = 0xFE;
		System.out.println(packet);
		List<byte[]> packets = new ArrayList<byte[]>();

		for (int i = 0; i < packet.length; i += size)
		{
			if ((packet.length - i) > size)
			{
				packets.add(Arrays.copyOfRange(packet, i, size));
			}
			else
			{
				packets.add(Arrays.copyOfRange(packet, i, packet.length - i));
			}
		}
/*
        for (int i = 0; i < (packets.size() - 1); i++)
        {
            temp_apdu = transmitApdu("00B20000"+ (byte)packets.get(i).length + packets.get(i)); //only send...
        }
 */
		String packets_len = Integer.toHexString(packets.get(packets.size() - 1).length + 0x01);
		if(packets_len.length()<2)
			packets_len = '0' + packets_len;

		/* 00B400 + {In return data length from G3} + {In transmit data length to G3} + Ins flag + packet data */
		byte [] cmd_size = {(byte) (command+1)};

		byte [] transmit = Util.joinBytes(RAW_APDU,
				new byte[]{(byte) (command + 1)},
				new byte[]{(byte) (packet.length+1)},
				"03",
				packet

				);
		//temp_apdu = transmitApdu("00B400" + Integer.toHexString(command+1) + packets_len + "03" +Util. byteArrayToHexString(packets.get(packets.size() - 1)));
		temp_apdu = transmitApdu(Util.toHexStr(transmit));

		byte[] ret = new byte[temp_apdu.length-2];
		System.arraycopy(temp_apdu,0,ret,0,ret.length);
		return ret;
	}

	public  byte[] EnterPacket(Object packet) throws IOException, CrcException {
		return EnterPacketFromBytes(Util.toBytes(packet));

	}

	public  byte[] EnterPacketFromBytes(byte[] input) throws IOException, CrcException {
		int ig3_len = input.length+3;
		byte[] g3_len = {(byte) ig3_len};

		byte[] input_data = Util.joinBytes(g3_len,input);
		/* Calculate CRC16 */
		int crc = Util.calcCRC(input_data,0,input_data.length);
		byte[] crc16 = Util.shortToBytes((short) crc);


		byte[] send_data = Util.joinBytes(input_data,crc16);

		Log.d(LOG_TAG,"send_data:"+Util.byteArrayToHexString(send_data));

		byte[] respapdu = SendPacket(send_data, 240);
		byte [] res_data = getDataFromRecv(respapdu);
		Log.d(LOG_TAG,"res_data:"+Util.toHexStr (res_data));
		return res_data;


		//return respapdu;
	}

	public byte [] GetSn() throws Exception{
		byte[] ret = GetPuk(EC_MODE_PUF);

		return Arrays.copyOf(Util.sha256(ret),16);

//		String strsn = Util.byteArrayToHexString(Util.sha256(ret));
//
//
//		return strsn.substring(0,16*2);//16자리 시리얼 넘버를 리턴한다.


	}



	public byte[] getDataFromRecv(byte[] input) throws IOException, CrcException {

		ByteArrayInputStream bstream = new ByteArrayInputStream(input);
		int size = bstream.read();
		Log.d(LOG_TAG,String.format("size : %d",size));
		byte[] data = new byte[size-3];
		byte[] crc = new byte[2];

		bstream.read(data);
		bstream.read(crc);
		int scrc = (0xffff)&Util.bytesToShort(crc);


		//Log.d(LOG_TAG,String.format("compare : %x %x",scrc,icalc_crc));
		int icalc_crc = Util.calcCRC(input,0,size-2);
		Log.d(LOG_TAG,String.format("compare : %x %x",scrc,icalc_crc));
		if(icalc_crc != scrc){
			throw new CrcException();
		}





		return data;


	}
	public byte[] GetChallenge() throws IOException, CrcException {

		return this.EnterPacket(GET_CHAL);

		//byte[] send_data = new byte[input_data.length + 2];
		//return recvdata;


	}
	public byte[]  GetPuk(int mode) throws IOException, CrcException {
		String input = null;
		switch (mode){
			case EC_MODE_PRK_R1:
				input = GET_PUK_R1;
				break;
			case EC_MODE_PRK_K1:
				input = GET_PUK_K1;
				break;
			case EC_MODE_PUF:
				input = GET_PUK_PUF;
				break;
		}

		return this.EnterPacket(input);
	}





	public byte[]  Reset() throws IOException, CrcException {

		return this.EnterPacket(RESET);
	}
	public boolean InitPassword(String master_key ) throws Exception {

		boolean ret = _verifyAccess(master_key,1);
		if(!ret){
			return false;
		}
		byte[] sn = GetSn();

		return UpdatePassword(sn);

	}
	public boolean VerifyAccessValue(Object challenge, Object sign, int keyindex ) throws Exception {

		byte [] tbs_data = MakeInputAccessValue(challenge,keyindex);

		byte [] result = EnterPacket(Util.joinBytes(String.format("87%02X1023",keyindex),tbs_data,sign));
		return Util.toHexStr(result).equals("00");

	}



	public boolean VerifyPassword(Object pw ) throws Exception {
		byte [] key_0_init = DeriveAccessKey(pw);
		//byte [] key_0_init = Util.sha256Exp200Left16(pw,system_const);
		return _verifyAccess(key_0_init,0);

	}
//	public boolean VerifyAccessBySignValue(Object tbs_data, Object sign ) throws Exception {
//		return VerifyAccessRaw(tbs_data,sign,0);
//	}


	public boolean UpdatePassword(Object pw ) throws Exception {

		//byte [] new_key = Util.sha256Exp200Left16(pw,system_const);
		byte [] new_key = DeriveAccessKey(pw);
		return WriteAcKey(new_key);

	}



	public boolean WriteAcKey(Object key ) throws IOException, CrcException {

		//byte [] paddingkey = Util.joinBytes(key,new byte[26]);
		byte[] recvdata = this.EnterPacket(Util.joinBytes(WRITEKEY_KEY0,key,new byte[16])); //16자리 키를 32 바이트로 zero padding 해준다.
		Log.d(LOG_TAG,"writeAesKey : "+Util.toHexStr(recvdata));

		//byte[] send_data = new byte[input_data.length + 2];
		return Util.toHexStr(recvdata).equals("00");
	}

	public byte [] SignEcdsa(Object tbs_data,int mode ) throws IOException, CrcException {

		String input = null;
		switch (mode){
			case EC_MODE_PRK_R1:
				input = ECDSA_SIGN_R1;
				break;
			case EC_MODE_PRK_K1:
				input = ECDSA_SIGN_K1;
				break;
			case EC_MODE_PUF:
				input = ECDSA_SIGN_PUF;
				break;
		}


		byte[] recvdata = this.EnterPacket(Util.joinBytes(input,tbs_data));

		return recvdata;

		//byte[] send_data = new byte[input_data.length + 2];
		//return recvdata;
	}


	static public byte [] MakeInputAccessValue(Object challenge , int keyindex) throws IOException {
		byte [] tbs_data = Util.joinBytes(String.format("86%02X0003000000000000000000000000",keyindex),challenge);
		return tbs_data;

	}


	static public byte [] DeriveAccessKey(Object pw ) throws Exception {

		byte [] new_key = Util.sha256Exp200Left16(pw,system_const);
		return new_key;

	}
	static public byte [] MakeAcSign(Object ac_key,Object challenge , int keyindex) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		//byte [] iv = Util.toBytes(String.format("86%02X1023000000000000000000000000",keyindex));

		byte [] tbs_data = MakeInputAccessValue(challenge,keyindex);

		byte[] enc_val = Util.aesCbc(Cipher.ENCRYPT_MODE,ac_key,tbs_data,new byte[16]);//Util.encValue(ac_key, tbs_data);

		byte [] sign = Arrays.copyOfRange(enc_val,enc_val.length-16,enc_val.length);

		return sign;

	}
	static public byte [] MakeSessionKey(Object ac_key,Object sign , int keyindex) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		byte [] iv = Util.toBytes(String.format("87%02X1023000000000000000000000000",keyindex));

		return Util.subBytes(Util.aesCbc(Cipher.ENCRYPT_MODE,ac_key,sign,iv),0,16);

	}
	public boolean WriteEncMacKey(Object new_key ,int keyindex) throws IOException, CrcException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

		//byte [] paddingkey = Util.joinBytes(key,new byte[26]);
		byte [] head = Util.toBytes(String.format("81%02X1501",keyindex));
		byte [] iv = Util.joinBytes(head,new byte[12]);

		Log.d(LOG_TAG,"_rw_session_key : "+Util.toHexStr(_rw_session_key));

		byte [] enc_data = Util.aesCbc(Cipher.ENCRYPT_MODE,_rw_session_key,new_key,iv);
		byte [] all_mac = Util.aesCbc(Cipher.ENCRYPT_MODE,_rw_session_key,enc_data,iv);

		byte [] mac = Arrays.copyOfRange(all_mac,all_mac.length-16,enc_data.length);

		byte [] packet_input = Util.joinBytes(head,enc_data,iv,mac);

		Log.d(LOG_TAG,"packet_input : "+Util.toHexStr(packet_input));

		byte[] recvdata = this.EnterPacket(packet_input); //16자리 키를 32 바이트로 zero padding 해준다.
		Log.d(LOG_TAG,"WriteEncMacKey : "+Util.toHexStr(recvdata));

		//byte[] send_data = new byte[input_data.length + 2];
		return Util.toHexStr(recvdata).equals("00");
	}



	private boolean _verifyAccess(Object ac_key, int keyindex ) throws Exception {

		Reset();
		byte []challenge = GetChallenge();

		byte [] sign = MakeAcSign(ac_key,challenge,keyindex);


		_rw_session_key = MakeSessionKey(ac_key,sign,keyindex);

		return VerifyAccessValue(challenge,sign , keyindex);
	}


	TsmClientConnectListener mConnectListener = new TsmClientConnectListener() {
		@Override
		public void onServiceConnected() {
			mServiceConnected = true;
			if(mCustomConnectListener !=null)	mCustomConnectListener.onServiceConnected();
			Log.i(LOG_TAG, "onServiceConnected.........................");
		}
		@Override
		public void onServiceConnectFail() {
			mServiceConnected = false;
			if(mCustomConnectListener !=null)  mCustomConnectListener.onServiceConnectFail();
			Log.i(LOG_TAG, "onServiceConnectFail..........................");
		}
	};
}
