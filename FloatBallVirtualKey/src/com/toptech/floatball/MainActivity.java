package com.toptech.floatball;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.toptech.floatball.util.Utils;

import org.apache.http.client.HttpClient;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class MainActivity extends Activity {

	private boolean isFristStart = false;
	private SharedPreferences.Editor editor;
	private SharedPreferences getdata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.activity_main);
		getdata = getSharedPreferences(Utils.FLOATBALL, MODE_PRIVATE);
		isFristStart = getdata.getBoolean(Utils.ISFIRST_START, false);
		editor = getSharedPreferences(Utils.FLOATBALL, MODE_PRIVATE).edit();
		Intent service = new Intent();
		service.setClassName("com.toptech.floatball", "com.toptech.floatball.service.FloatWindowService");
		service.putExtra("showNavigation", false);
		try {
			startService(service);
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {

		super.onStart();
	}



	@Override
	public void finish() {
		super.finish();

	}


}
