package com.ictkholdings.nftpufsample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ictk.pufusim.CryptoUtil;
import com.lguplus.usimlib.TsmClientConnectListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.interfaces.ECPrivateKey;
import java.util.Locale;

import com.ictk.pufusim.UsimPufHandler;
import com.ictk.pufusim.Util;
import com.ictk.pufusim.UsimConnectionException;
import com.ictk.pufusim.EcdsaResult;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;


public class TestWallet extends AppCompatActivity {
    private static final String LOG_TAG = TestWallet.class.getSimpleName();
    private TextView sample_result;
    Context mContext;
    UsimPufHandler usimpufhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_wallet);
        sample_result = findViewById(R.id.sample_result);;

        mContext = this;
        usimpufhandler = new UsimPufHandler(mContext,mConnectListener);


        ((Button) findViewById(R.id.ecdsa_der_sample_button))
                .setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        byte[] sig = Util.getRandom(64);

                        try {
                            EcdsaResult inst = EcdsaResult.FromRAWBYTES(sig);

                            String str_res = String.format("64bytes:\n%s\n\n\nDER:\n%s\n\n\nR:\n%d\nS:\n%s\n",Util.toHexStr(sig),Util.toHexStr(inst.toDER()),inst.getR(),inst.getS());



                            sample_result.setText(str_res);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                    }
                });



        ((Button) findViewById(R.id.sign_ecdsa_sample_button)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signEcdsaSample(sample_result,UsimPufHandler.EC_MODE_PUF);
                } catch (UsimConnectionException e) {
                    e.printStackTrace();
                }


            }
        });



        ((Button) findViewById(R.id.sign_ecdsa_r1_button)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signEcdsaSample(sample_result,UsimPufHandler.EC_MODE_PRK_R1);
                } catch (UsimConnectionException e) {
                    e.printStackTrace();
                }


            }
        });

        ((Button) findViewById(R.id.sign_ecdsa_k1_button)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signEcdsaSample(sample_result,UsimPufHandler.EC_MODE_PRK_K1);
                } catch (UsimConnectionException e) {
                    e.printStackTrace();
                }


            }
        });

        ((Button) findViewById(R.id.make_address_button)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    usimpufhandler.wait_to_connected();
                    usimpufhandler.G3_OpenChannel();
                    usimpufhandler.G3_WakeUp();

                    String challenge = "c24ba6fd69caee58e5ac423eaf12704e35813f46650b2aa4710f7aa0f182f211";

                    byte[] respapdu = new byte[0];
                    byte[] sign_value = new byte[0];
                    byte[] sign_der_value = new byte[0];
                    try {


                        long sttime = System.currentTimeMillis();

                        byte[] puksample = Util.toBytes("af7992f73dda9ffa1e8e5e6ba6f75883e87675c162c93f17c8b0b28ddb144acf1905147f157dcd1c63b4fe6c75a59a83e26534aa89a69d2c7bcb0d7631edef13");
                        byte[] puk =usimpufhandler.GetPuk(UsimPufHandler.EC_MODE_PRK_R1);


                        Log.d(LOG_TAG,String.format("puk: %s",Util.toHexStr(puk)));
                        Log.d(LOG_TAG,String.format("puksample: %s",Util.toHexStr(puksample)));


                        String address = CryptoUtil.makeAddressFromPuk64(puk);
                        String addressSample = CryptoUtil.makeAddressFromPuk64(puksample);

                        Log.d(LOG_TAG,String.format("address %s",address));
                        Log.d(LOG_TAG,String.format("addressSample %s",addressSample));

                        String restext = String.format
                        ("puk:\n%s\n\naddress:\n%s\n\nsample puk:\n%s\n\nsample address:\n%s",
                                Util.toHexStr(puk),
                                address,
                                Util.toHexStr(puksample),
                                addressSample
                                );


                        sample_result.setText(restext);

                        String filename = "/data/myfile.txt";
                        String fileContents = "Hello world!";

                        //File file = new File(this.mContext.getFilesDir(), filename);
                        //       Log.d(LOG_TAG,String.format("getFilesDir: %s",this.mContext.getFilesDir()));

//            try (FileOutputStream fos = this.mContext.openFileOutput(filename, Context.MODE_PRIVATE)) {
//                fos.write(fileContents.getBytes(StandardCharsets.UTF_8));
//            }

                        //result.setText(Util.toHexStr(sign_value));

                    } catch (Exception e) {
                        e.printStackTrace();
                        sample_result.setText("FAIL!!!");

                    }
                    usimpufhandler.G3_CloseChannel();
                } catch (UsimConnectionException e) {
                    e.printStackTrace();
                }


            }
        });

        ((Button) findViewById(R.id.gen_csr_button)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    byte[] prk = Util.toBytes("7170c5103ead7da65adb4f568b3c66417b22716bf4d577540726ed719869d86f");
                    ECPrivateKey privateKey = CryptoUtil.toECPrivateKey(prk);
                    ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
                    ECPoint G = ecSpec.getG();

                    ECPoint addval = G.add(G);
                    Log.d(LOG_TAG,String.format("Q:%s %s",addval.getXCoord().toBigInteger(),addval.getRawXCoord().toBigInteger()));

                    for(int i =1 ; i <100;i++){

                        ECPoint Q = G.multiply(BigInteger.valueOf(i));
                        //Log.d(LOG_TAG,String.format("i:%d",i));
                        //Log.d(LOG_TAG,String.format("Q X:%s Y:%s",Q.getXCoord().toBigInteger(),Q.getYCoord().toBigInteger()));
                        Log.d(LOG_TAG,String.format("i:%d Q:%s %s",i,Q.getXCoord().toBigInteger(),Q.getRawXCoord().toBigInteger()));
                        Log.d(LOG_TAG,"\n");

                    }



                    //byte [] puk = CryptoUtil.toRawBytesFromECPointBC(Q);
                    byte [] puk = Util.toBytes("0983d9649032fc561a46ed0732dd7a5b52b75ae2cd4a70c7af85ee1a7973ef60619937d1b4d3f73fbb3c87d11bcea170f782d8728477e76d026a9a20dbd646d9");
                    Log.d(LOG_TAG,String.format("prk: %s",Util.toHexStr(prk)));
                    Log.d(LOG_TAG,String.format("prk int: %s",privateKey.getS()));
                    Log.d(LOG_TAG,String.format("prk int 2: %s",new BigInteger(prk)));

                    Log.d(LOG_TAG,String.format("G: X:%s Y:%s",G.getXCoord().toBigInteger(),G.getYCoord().toBigInteger()));

                    Log.d(LOG_TAG,String.format("puk: %s",Util.toHexStr(puk)));

                    KeyPair keypair = new KeyPair(CryptoUtil.toECPublicKey(puk),
                            privateKey);


                    Log.d(LOG_TAG,String.format("csr_pem: %s",csr_pem));

                    String restext = String.format
                            ("prk:\n%s\n\npuk:\n%s\n\ncsr_pem:\n%s\n\n",
                                    Util.toHexStr(prk),
                                    Util.toHexStr(puk),
                                    csr_pem
                            );


                    sample_result.setText(restext);




                } catch (Exception e) {
                    sample_result.setText("FAIL!!!");
                    e.printStackTrace();
                }


            }
        });
    }


    public void signEcdsaSample(TextView result,int mode) throws UsimConnectionException {
        usimpufhandler.wait_to_connected();
        usimpufhandler.G3_OpenChannel();
        usimpufhandler.G3_WakeUp();

        String challenge = "c24ba6fd69caee58e5ac423eaf12704e35813f46650b2aa4710f7aa0f182f211";

        byte[] respapdu = new byte[0];
        byte[] sign_value = new byte[0];
        byte[] sign_der_value = new byte[0];
        try {


            long sttime = System.currentTimeMillis();
            byte[] puk =usimpufhandler.GetPuk(mode);
            sign_value = usimpufhandler.SignEcdsa(challenge,mode);


            sign_der_value = EcdsaResult.FromRAWBYTES(sign_value).toDER();


            long tktime = System.currentTimeMillis()-sttime;

            Log.d(LOG_TAG,String.format("puk: %s",Util.toHexStr(puk)));
            Log.d(LOG_TAG,String.format("challenge: %s",Util.toHexStr(challenge)));
            Log.d(LOG_TAG,String.format("sign_value: %s",Util.toHexStr(sign_value)));
            String restext = String.format("challenge:\n%s\n\npuk:\n%s\n\nsign_value:\n%s\n\nsign_der_value:\n%s\n\ntktime : %.02f",
                    challenge,
                    Util.toHexStr(puk),
                    Util.toHexStr(sign_value),
                    Util.toHexStr(sign_der_value),

                    (float)tktime);


            result.setText(restext);

            String filename = "/data/myfile.txt";
            String fileContents = "Hello world!";

            //File file = new File(this.mContext.getFilesDir(), filename);
     //       Log.d(LOG_TAG,String.format("getFilesDir: %s",this.mContext.getFilesDir()));

//            try (FileOutputStream fos = this.mContext.openFileOutput(filename, Context.MODE_PRIVATE)) {
//                fos.write(fileContents.getBytes(StandardCharsets.UTF_8));
//            }

            //result.setText(Util.toHexStr(sign_value));

        } catch (Exception e) {
            result.setText("FAIL!!!");
        }
        usimpufhandler.G3_CloseChannel();

    }
    private static final int CREATE_FILE = 1;

    private void createFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, CREATE_FILE);
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

}