package com.ictkholdings.nftpufsample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class MainActivity extends AppCompatActivity {
	//private static final String AWS_SNS_PUSH_ARN = "arn:aws:sns:ap-southeast-1:089060086266:app/GCM/smart-door";//neo1seok 수정
	//private static final String AWS_SNS_SUB_ARN = "arn:aws:sns:ap-southeast-1:089060086266:smart_door_sns_topic";
	private static final String TAG = MainActivity.class.getSimpleName();

	private Fragment currentFragment;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Objects.requireNonNull(getSupportActionBar()).hide();

		// 상태바를 안보이도록 합니다.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 화면 켜진 상태를 유지합니다.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.main_activity);



		if (savedInstanceState == null) {


		}
		//showSetting();
		Intent intent = new Intent(getApplicationContext(), Test_Select.class);//neo1seok test
		startActivity(intent);



	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();


	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();


	}

	private void showFragment(Fragment fragment) {
		Log.d(TAG, "showFragment");
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
	}



	public void showSetting(){
		//Intent intent = new Intent(getApplicationContext(), Test_Login.class);
		Intent intent = new Intent(getApplicationContext(), Test_debug.class);//neo1seok test
		startActivity(intent);
	}
}