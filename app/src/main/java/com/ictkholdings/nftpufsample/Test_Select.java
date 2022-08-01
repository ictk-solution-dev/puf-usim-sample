package com.ictkholdings.nftpufsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test_Select extends AppCompatActivity {
    private static final String LOG_TAG = Test_Select.class.getSimpleName();
    UsimPufHandler usimpufhandler;
    private TextView sample_result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_select);

        usimpufhandler = new UsimPufHandler(this);

        sample_result = findViewById(R.id.sample_result);;


        ((Button) findViewById(R.id.select_test_etc)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //getBuildSample(sample_result);
                    Intent intent = new Intent(getApplicationContext(), TestWallet.class);//neo1seok test
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        ((Button) findViewById(R.id.select_usim_puf)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //getBuildSample(sample_result);
                    Intent intent = new Intent(getApplicationContext(), TestPufAC.class);//neo1seok test
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        ((Button) findViewById(R.id.select_init)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Thread thread = new Thread(new Test_Select.InitPwdSamplClass(sample_result));
                    thread.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });



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

                //byte []challenge =Util.toBytes("6097c93153716ee0b77bd21552aae8c6a818d62833db79f62c787c4b2f790267");
                URL url = new URL("http://pqc.ictk.com:8095/debug/public/aes/sign"); // 호출할 url

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

}