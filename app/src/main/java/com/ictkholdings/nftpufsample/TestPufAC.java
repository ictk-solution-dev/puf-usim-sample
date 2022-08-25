package com.ictkholdings.nftpufsample;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ictk.pufusim.CryptoUtil;
import com.lguplus.usimlib.*;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import com.ictk.pufusim.UsimPufHandler;
import com.ictk.pufusim.Util;
import com.ictk.pufusim.UsimConnectionException;


//import static com.ictkholdings.pqc2nd_sample.App.mClient;

public class TestPufAC extends AppCompatActivity {
    private static final String LOG_TAG = TestPufAC.class.getSimpleName();

    private TextView sample_result;

    String new_password ="ictk_test";

    private static final String url = "https://kkqjwjrde9.execute-api.ap-southeast-2.amazonaws.com/usim-puf/auth"; // URL 설정.
    private static final String system_const = "D9DADFDED657F87C1B7C6D46493877B4";
    private static final String master_key	= "";

    Context mContext;
    UsimPufHandler usimpufhandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_puf_ac);


        sample_result = findViewById(R.id.sample_result);;



        mContext = this;
        usimpufhandler = new UsimPufHandler(mContext,mConnectListener);

        ((Button) findViewById(R.id.base_sample_button))
         .setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    baseSample(sample_result);
                } catch (UsimConnectionException e) {
                    sample_result.setText(e.toString());
                    e.printStackTrace();
                }


            }
        });

        ((Button) findViewById(R.id.uid_sample_button))
            .setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uidSample(sample_result);
                } catch (UsimConnectionException e) {
                    sample_result.setText(e.toString());

                    e.printStackTrace();
                }


            }
        });

        ((Button) findViewById(R.id.verify_pwd_sample_button))
            .setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    verifyPwdSample(sample_result);
                } catch (UsimConnectionException e) {
                    sample_result.setText(e.toString());
                    e.printStackTrace();
                }


            }
        });

        ((Button) findViewById(R.id.update_pwd_sample_button))
            .setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updatePwdSample(sample_result);
                } catch (UsimConnectionException e) {
                    sample_result.setText(e.toString());
                    e.printStackTrace();
                }


            }
        });
        ((Button) findViewById(R.id.verify_new_pwd_sample_button))
                .setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            verifyNewPwdSample(sample_result);
                        } catch (UsimConnectionException e) {
                            sample_result.setText(e.toString());
                            e.printStackTrace();
                        }


                    }
                });




        ((Button) findViewById(R.id.write_r1_key_sample_button))
                .setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            writeKeySample(sample_result,UsimPufHandler.EC_MODE_PRK_R1);
                            //verifyPwdScureStoreSample(sample_result);
                        } catch (UsimConnectionException e) {
                            sample_result.setText(e.toString());
                            e.printStackTrace();
                        }


                    }
                });
        ((Button) findViewById(R.id.write_k1_key_sample_button))
                .setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            writeKeySample(sample_result,UsimPufHandler.EC_MODE_PRK_K1);
                        } catch (UsimConnectionException e) {
                            sample_result.setText(e.toString());
                            e.printStackTrace();
                        }


                    }
                });

        ((Button) findViewById(R.id.init_pw_sample_button))
            .setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initPwdSample(sample_result);
                Thread thread = new Thread(new InitPwdSamplClass(sample_result) {

                });
                thread.start();
                //InitPwdSampl2
                //initPwdSampl2(sample_result);


            }
        });


//
//        ((Button) findViewById(R.id.get_puk_sample_button))
//            .setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    getPukSample(sample_result);
//                } catch (UsimConnectionException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        });
//
//        ((Button) findViewById(R.id.decrypt_test_button)).setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    decryptTest(findViewById(R.id.sample_result));
//                } catch (UsimConnectionException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        });












    }//onCreate



    public void baseSample(TextView result) throws UsimConnectionException {
        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();
        byte[] respapdu = new byte[0];
        try {
            respapdu = usimpufhandler.GetChallenge();
            result.setText(Util.toHexStr(respapdu));
        } catch (Exception e) {
            result.setText("FAIL!!!");
        }
//
//
//
        usimpufhandler.G3_CloseChannel();



    }

    public void uidSample(TextView result) throws UsimConnectionException {
        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();

        try {
            byte[] sn = usimpufhandler.GetSn();
            result.setText(Util.toHexStr(sn));
        } catch (Exception e) {
            result.setText("FAIL!!!");
        }
//
//
//
        usimpufhandler.G3_CloseChannel();



    }


//    public void verifyPwdKe1Sample(TextView result)
//    {
//        usimpufhandler.wait_to_connected();
//        usimpufhandler.G3_OpenChannel();
//        usimpufhandler.G3_WakeUp();
// /*
//    S_TEST_LNI_0_SIDX_0_RPI_0	PASS	reset	RESET	0	0000H		00
//S_TEST_LNI_1_SIDX_0_RPI_0	PASS	GET CHALENGE 	GET_CHAL	32	0000H		639634A1032AF19AB16A2DE8ECDF06967C39E06A200E59B7F2E8ED90130377F6
//S_TEST_LNI_2_SIDX_0_RPI_0	PASS	GET KEY 1 AC	VERIFY	1	1023H	86010003000000000000000000000000639634A1032AF19AB16A2DE8ECDF06967C39E06A200E59B7F2E8ED90130377F6586E2EB7A030B89B186253F03A55109E	00
//S_TEST_LNI_3_SIDX_0_RPI_0	PASS	reset	RESET	0	0000H		00
//S_TEST_LNI_4_SIDX_0_RPI_0	NO_CHCEK	GET SN	GET_PUB_KEY	16	0001H		0322E080042C61F8E09CD0F9F482B49DB22AB9AC93CF42EC5A7CD72D73825A7C63FAFC76630A4216CD8F9FEE2B9B8D9340282175CBC0340820E765AB7641F17F
//S_TEST_LNI_5_SIDX_0_RPI_0	PASS	GET CHALENGE 	GET_CHAL	32	0000H		60C736E765082D98100D460747823751EA901B6E34D2597F7240AA657FB5CD28
//S_TEST_LNI_6_SIDX_0_RPI_0	PASS	GET KEY 0 AC	VERIFY	0	1023H	8600000300000000000000000000000060C736E765082D98100D460747823751EA901B6E34D2597F7240AA657FB5CD28563FF32470FFBA6080505CCAA027AE0C	00
//
//    * */
//        byte[] respapdu = new byte[0];
//        try {
//            respapdu = usimpufhandler.EnterPacket("8200000003ac674216f3e15c761ee1a5e255f067");
//
//
//            byte []challenge = usimpufhandler.GetChallenge();
//
//            Log.d(LOG_TAG,String.format("challenge key 0 : %s",Util.toHexStr(challenge)));
//
//            String aes_key = "0B0CF1D734DE39C34DC827F4DCDE6624";
//            String iv =  "86010003000000000000000000000000";
//            String org_data = iv+Util.toHexStr(challenge);
//            String msg = "1C10D282025D5E8D91D1EBB264706DF364810010C4313124CD077CECCF437F7D";
//            byte[] enc_val = Util.encValue(aes_key, org_data);
//
//            byte [] sign = Arrays.copyOfRange(enc_val,enc_val.length-16,enc_val.length);
//            Log.d(LOG_TAG,String.format("enc_val  : %s",Util.toHexStr(enc_val)));
//            Log.d(LOG_TAG,String.format("sign  : %s",Util.toHexStr(sign)));
//
//
//
//            respapdu = usimpufhandler.EnterPacket("87011023"+org_data+Util.toHexStr(sign));
//
//
//
//
//            result.setText(Util.toHexStr(respapdu));
//        } catch (Exception e) {
//            result.setText("FAIL!!!:"+e.getMessage());
//        }
//        usimpufhandler.G3_CloseChannel();
//
//
//
//    }
    public void verifyPwdScureStoreSample(TextView result) throws UsimConnectionException {
        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();
        byte[] respapdu = new byte[0];
        try {
            byte[] init_pw = usimpufhandler.GetSn();//초기 pw 는 sn 이다.
            boolean ret = usimpufhandler.VerifyPassword(init_pw);
            //최초 실행시 시리얼 값으로 verify를 해서 access 를 얻는다.

            byte []  new_password ="1234".getBytes();
            //사용자에게 4자리 핀번호를 입력 받는다.

            usimpufhandler.UpdatePassword(new_password);
            //사용자에게 받은 pw 를 puf에 저장 한다.

            byte []  new_key = usimpufhandler.DeriveAccessKey(new_password);
            //사용자에게 받은 pw 로 puf내 저장된 키 형태로 secure store 에 저장 한다.


            byte []challenge = usimpufhandler.GetChallenge();
            //challenge 값을 얻는다.


            /*######################################################################*/
            // 이 과정은 시큐어 스토어에서 키를 저장후 연산하는 과정을 가상으로 구현한 것이다.


            // pw 를 통해 실제 puf안에 저장된 키를 유도 한다.
            // 지문 인증을 통해 허가를 얻은 SecureStore에 저장한다고 가정한다.


            byte [] input_access_value = usimpufhandler.MakeInputAccessValue(challenge,0);

            byte [] enc_data = Util.encValue(new_key,input_access_value);
            // 실제로 secure store에서 계산 한다고 가정 한다. ( no padding)

            byte [] sign = Arrays.copyOfRange(enc_data,enc_data.length-16,enc_data.length);
            //암호화 된 데이타의 뒤에 16바이트를 서명값으로 취한다.
            /*######################################################################*/


            //0을 넣어 준다.
            //서명 대상 파일을 만든다.

            ret = usimpufhandler.VerifyAccessValue(challenge,sign,0);




            result.setText(ret ? "VERIFY SUCCESS":"VERIFY FAIL");

            usimpufhandler.UpdatePassword(init_pw);


        } catch (Exception e) {
            result.setText("FAIL!!!:"+e.getMessage());
        }
        usimpufhandler.G3_CloseChannel();



    }
    public void verifyPwdSample(TextView result) throws UsimConnectionException {
        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();
        byte[] respapdu = new byte[0];
        try {
            byte[] init_pw = usimpufhandler.GetSn();//초기 pw 는 sn 이다.
            boolean ret = usimpufhandler.VerifyPassword(init_pw);



            result.setText(ret ? "VERIFY SUCCESS":"VERIFY FAIL");
        } catch (Exception e) {
            result.setText("FAIL!!!:"+e.getMessage());
        }
        usimpufhandler.G3_CloseChannel();



    }



    public void updatePwdSample(TextView result) throws UsimConnectionException {
        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();

        byte[] respapdu = new byte[0];
        try {

            boolean ret = usimpufhandler.UpdatePassword(new_password.getBytes());


            result.setText(ret ? "UPDATE SUCCESS":"UPDATE FAIL");
        } catch (Exception e) {
            result.setText("FAIL!!!");
        }


        usimpufhandler.G3_CloseChannel();



    }

    private void verifyNewPwdSample(TextView result) throws UsimConnectionException {

        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();
        byte[] respapdu = new byte[0];
        try {

            boolean ret = usimpufhandler.VerifyPassword(new_password.getBytes());



            result.setText(ret ? "VERIFY SUCCESS":"VERIFY FAIL");
        } catch (Exception e) {
            result.setText("FAIL!!!:"+e.getMessage());
        }
        usimpufhandler.G3_CloseChannel();
    }
    public void writeKeySample(TextView result,int mode) throws UsimConnectionException {
        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();
        int key_index =0;
        String csr_pem ="NOT R1 KEY";
        switch(mode){
            case UsimPufHandler.EC_MODE_PRK_R1:
                key_index = UsimPufHandler.PRK_R1_INDEX;
                break;
            case UsimPufHandler.EC_MODE_PRK_K1:
                key_index = UsimPufHandler.PRK_K1_INDEX;
                break;
            default:
                throw new RuntimeException("");
        }

        try {
            byte[] prk = Util.getRandom(32);


            boolean isok = usimpufhandler.WriteEncMacKey(prk,key_index);
            if(!isok){
                throw new Exception("WRITE ERROR");
            }
            byte [] puk = usimpufhandler.GetPuk(mode);

            Log.d(LOG_TAG,"prk:"+Util.toHexStr(prk));
            Log.d(LOG_TAG,"puk:"+Util.toHexStr(puk));
            Log.d(LOG_TAG,"isok:"+isok);

            if(mode==UsimPufHandler.EC_MODE_PRK_R1)

            csr_pem = CryptoUtil.generateCSR(prk, puk, "CCTK TEST CSR");



            String restext = String.format
                    ("OK!!\n\nprk:\n%s\n\npuk:\n%s\n\ncsr_pem:\n%s\n\n",
                            Util.toHexStr(prk),
                            Util.toHexStr(puk),
                            csr_pem
                    );


            sample_result.setText(restext);

            Log.d(LOG_TAG,"csr_pem:"+csr_pem);

           // result.setText(isok? "OK!!!":"FAIL!!!" );
        } catch (Exception e) {
            result.setText("FAIL!!!");
        }
//
//
//
        usimpufhandler.G3_CloseChannel();



    }

    public void initPwdSample(TextView result) throws UsimConnectionException {
        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();

        byte[] respapdu = new byte[0];
        try {
            byte []challenge = usimpufhandler.GetChallenge();

            byte [] input_access_value = usimpufhandler.MakeInputAccessValue(challenge,1);

            byte [] enc_data = Util.encValue(master_key,input_access_value);
            // 실제로 secure store에서 계산 한다고 가정 한다. ( no padding)

            byte [] sign = Arrays.copyOfRange(enc_data,enc_data.length-16,enc_data.length);
            //암호화 된 데이타의 뒤에 16바이트를 서명값으로 취한다.
            /*######################################################################*/


            //0을 넣어 준다.
            //서명 대상 파일을 만든다.

            boolean ret = usimpufhandler.VerifyAccessValue(challenge,sign,1);



            byte[] init_pw = usimpufhandler.GetSn();//초기 pw 는 sn 이다.




            ret = usimpufhandler.UpdatePassword(init_pw);





            //result.setText(ret ? "UPDATE SUCCESS":"UPDATE FAIL");
        } catch (Exception e) {
            result.setText("FAIL!!!");
        }


        usimpufhandler.G3_CloseChannel();



    }


    public void getPukSample(TextView result) throws UsimConnectionException {
        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();
//
//
//
        byte[] respapdu = new byte[0];
        try {
            respapdu = usimpufhandler.GetPuk(UsimPufHandler.EC_MODE_PUF);
            result.setText(Util.toHexStr(respapdu));
        } catch (Exception e) {
            result.setText("FAIL!!!");
        }


        usimpufhandler.G3_CloseChannel();



    }

    public void decryptTest(TextView result) throws UsimConnectionException {

        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();


        usimpufhandler.G3_CloseChannel();



    }

    class InitPwdSamplClass implements Runnable {
        TextView result;
        public InitPwdSamplClass(TextView result_){
            result = result_;


        }
        @Override
        public void run() {
            try {

                usimpufhandler.wait_to_connected();
                usimpufhandler.G3_OpenChannel();
                usimpufhandler.G3_WakeUp();

                byte []challenge = usimpufhandler.GetChallenge();

//                byte [] input_access_value = usimpufhandler.MakeInputAccessValue(challenge,1);
//
//                byte [] enc_data = Util.encValue(master_key,input_access_value);
//                // 실제로 secure store에서 계산 한다고 가정 한다. ( no padding)
//
//                byte [] sign = Arrays.copyOfRange(enc_data,enc_data.length-16,enc_data.length);
                //암호화 된 데이타의 뒤에 16바이트를 서명값으로 취한다.
                /*######################################################################*/


                //0을 넣어 준다.
                //서명 대상 파일을 만든다.



                //byte []challenge =Util.toBytes("6097c93153716ee0b77bd21552aae8c6a818d62833db79f62c787c4b2f790267");
                URL url = new URL("http://pqc.ictk-puf.com:8095/debug/public/aes/sign"); // 호출할 url


                String jsonInputString = String.format("{\"header\": {\"trId\": \"999001\", \"function\": \"getAesSign\"}, \"body\": {\"challenge\": \"%s\", \"header\": \"\"}}",Util.toHexStr(challenge));


                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                //conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJVU0VSX0lEIjoiZGVidWdBZXMiLCJUT0tFTl9UWVBFIjoiREVCVUdfQUVTX1RPS0VOIiwiZXhwIjoxNjYyNzAwMTM3LCJVU0VSX0xFVkVMIjoiREVCVUdfQUVTX1VTRVIifQ.G9jEoUcRYl_9WrlMhjiZEVjsu1lpo8XoZYQB0WS8_BiQsIzo78kuPzHPdL0S_CcvWV2GFQ9jlCdMaVOU-HRRyA");


                conn.setDoOutput(true);
                try(OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }


                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                String inputLine;
                StringBuffer sb = new StringBuffer();
                while((inputLine = in.readLine()) != null) { // response 출력
                    sb.append(inputLine);
                }

                in.close();

                Log.d(LOG_TAG,"result:"+sb.toString());

                JSONObject jsonObject = new JSONObject(sb.toString());

                JSONObject body = jsonObject.getJSONObject("body");
                String sign = body.getString("sign");
                result.setText("sign:"+sign);

                boolean ret = usimpufhandler.VerifyAccessValue(challenge,sign,1);

                Log.d(LOG_TAG,"VerifyAccessValue result"+ret);

                byte[] init_pw = usimpufhandler.GetSn();//초기 pw 는 sn 이다.




                ret = usimpufhandler.UpdatePassword(init_pw);

                result.setText(ret ? "UPDATE SUCCESS":"UPDATE FAIL");



            } catch (Exception e) {
                Log.d(LOG_TAG,"exception",e);
                result.setText("FAIL!!!"+e);
            }


        }
    }





    TsmClientConnectListener mConnectListener = new TsmClientConnectListener() {
        @Override
        public void onServiceConnected() {
            sample_result.setText("onServiceConnected");

        }
        @Override
        public void onServiceConnectFail() {
            sample_result.setText("onServiceConnectFail");


        }
    };

}//Test_Debug
