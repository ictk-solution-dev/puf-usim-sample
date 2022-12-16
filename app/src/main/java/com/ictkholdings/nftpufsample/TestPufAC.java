package com.ictkholdings.nftpufsample;

import android.content.Context;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText input_snd_data;

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
        input_snd_data = findViewById(R.id.input_snd_data);;



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




        ((Button) findViewById(R.id.input_snd_data_sample_button))
                .setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            inputSample(sample_result);
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
    public void inputSample(TextView result) throws UsimConnectionException {
        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();
        //usimpufhandler 에서 시작시 반드시 호출 해준다.


        try {

            String str_input = input_snd_data.getText().toString();
            //input 값에서 hex string 값을 가져온다.

            if(str_input.length()%2 !=0 || !str_input.matches("[A-Fa-f0-9]+")){
                throw new RuntimeException("NO HEX STR");
            }

            byte [] binput = Util.toBytes(str_input);

            byte []res = usimpufhandler.EnterPacket(binput);
            // PUF에 입력값 넣고 리턴 값을 받는다.  예) 80000000 -> 80:instruction,  00:p1 ,0000:p2
            // 해당 명령어와 p1,p2는 데이터 시트 확인 요망

            String restext = String.format("OK!!\n\nINPUT:%s(%d)\n\nOUTPUT:%s(%d)\n\n",
                    Util.toHexStr(binput),binput.length,
                    Util.toHexStr(res),res.length);


            sample_result.setText(restext);

            Log.d(LOG_TAG,"str_input.toString():"+str_input.toString());

           // result.setText(isok? "OK!!!":"FAIL!!!" );
        } catch (Exception e) {
            result.setText("FAIL!!!"+e.toString());

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
