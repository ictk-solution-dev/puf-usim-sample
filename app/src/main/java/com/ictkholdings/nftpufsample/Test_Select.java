package com.ictkholdings.nftpufsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import com.ictk.pufusim.EcdsaResult;
import com.ictk.pufusim.UsimPufHandler;
import com.ictk.pufusim.Util;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


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
        String getSignFromServer(byte [] challenge) throws JSONException, IOException, NoSuchAlgorithmException, KeyManagementException {
            URL url = new URL("https://43.200.98.151:8443/pqc3/device/admin/puf/reset"); // 호출할 url


            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    Log.d(LOG_TAG,"getAcceptedIssuers");

                    return null;

                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    Log.d(LOG_TAG,"checkClientTrusted"+certs.length+authType);

                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {

                    Log.d(LOG_TAG,"checkClientTrusted"+certs.length);

                }

            } };
            SSLContext sc = SSLContext.getInstance("SSL");

            sc.init(null, trustAllCerts, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            //HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());



            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    // Ignore host name verification. It always returns true.
                    return true;
                }
            });

            String jsonInputString = String.format("{\"header\": {\"trId\": \"500684\"}, \"body\": {\"challenge\": \"%s\", \"type\": \"nft\"}}",Util.toHexStr(challenge));

            //HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            //conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJJQ1RLIiwiVVNFUl9JRCI6InB1Zi1yZXNldCIsImV4cCI6MzIxNDEwMjQ2NSwiVVNFUl9MRVZFTCI6IlJFU0VUUFVGIn0.NOhoQbwXM0xH9QBCNSOYHt5arPwoujzXay8_Ccr2dSYzFNhwW-fIGvj0T6iANRsChCrdTJ0on6OM_DT5SgfRJg");

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
            String sign = body.getString("rawBytes");
            return sign;
        }
        public PrivateKey keyToValue(byte[] pkcs8key)
                throws GeneralSecurityException {

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8key);
            KeyFactory factory = KeyFactory.getInstance("ECDSA");
            PrivateKey privateKey = factory.generatePrivate(spec);
            return privateKey;
        }
        @Override
        public void run() {
            try {

                usimpufhandler.wait_to_connected();
                usimpufhandler.G3_OpenChannel();
                usimpufhandler.G3_WakeUp();


                byte []challenge = usimpufhandler.GetChallenge();

                String strsig = getSignFromServer(challenge);



                result.setText("sign:"+strsig);


                boolean ret = usimpufhandler.InitPassword(challenge,strsig);


                //boolean ret = usimpufhandler.VerifyAccessValue(challenge,sign,1);

                Log.d(LOG_TAG,"InitPassword result"+ret);

                //byte[] init_pw = usimpufhandler.GetSn();//초기 pw 는 sn 이다.

                //ret = usimpufhandler.UpdatePassword(init_pw);

                result.setText(ret ? "UPDATE SUCCESS":"UPDATE FAIL");

            } catch (Exception e) {
                Log.d(LOG_TAG,"exception",e);
                result.setText("FAIL!!!"+e);
            }

        }
    }

}