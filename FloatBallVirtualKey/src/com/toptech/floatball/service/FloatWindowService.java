package com.toptech.floatball.service;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toptech.floatball.R;
import com.toptech.floatball.util.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.Timer;
import java.util.TimerTask;

public class FloatWindowService extends Service {
	private Timer timer;
	private static final String TAG = "FloatWindowService";
	private FloatWindowService mcontext;
	private static WindowManager wManager;
	private static WindowManager.LayoutParams params;
	private RelativeLayout mLayout;
	private TextView textView;
	private ImageView floatBallImageView;
	public boolean open = false;
	private Animation mAnimation;
	private boolean showNavigation=false;
	@Override
	public IBinder onBind(Intent arg0) {
		mcontext = this;

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		showNavigation=intent.getBooleanExtra("showNavigation",false);
		if (!showNavigation){
			mLayout.setVisibility(View.GONE);
		}else {
			mLayout.setVisibility(View.VISIBLE);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		mcontext = this;
		super.onCreate();
		Notification notification = new Notification();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
		startForeground(1, notification);
		mAnimation = AnimationUtils.loadAnimation(mcontext, R.anim.floatrotate_open);
		showFloatWindow();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.topteck.floatball_START_TIMER");

		IntentFilter filter = new IntentFilter();
		filter.addAction(Utils.STOP_FLOATBALL_BROADCAST);

		network();
	}

	private void network() {
		String url = "http://172.168.3.215:8080/getblackpanel";
		timer=new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					String   httpUrl = "http://liufujun.top/getblackpanel";
					Log.e("ss","??????"+httpUrl);
					HttpClient client=new DefaultHttpClient();
					HttpGet httpGet=new HttpGet(httpUrl);
					HttpResponse httpResponse=client.execute(httpGet);

					if (httpResponse.getStatusLine().getStatusCode()==200){
						HttpEntity httpEntity=httpResponse.getEntity();
						String content= EntityUtils.toString(httpEntity,"UTF-8");
						Message msg=new Message();
						msg.what=1;
						msg.obj=content;
						mHandler.sendMessage(msg);
					}
				}catch (Exception e){
				}
			}
		},0,1000);//??????????????????handler??????????????????,?????????????????????????????????,??????????????????
// (2) ??????handler????????????????????????

	}
	private Handler mHandler = new Handler(){

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				if (msg.obj.toString().equals("true")){
					Intent service = new Intent();
					service.setClassName("com.toptech.floatball", "com.toptech.floatball.service.FloatWindowService");
					service.putExtra("showNavigation", true);
					try {
						startService(service);
					} catch (Resources.NotFoundException e) {
						e.printStackTrace();
					}
				}else if (msg.obj.toString().equals("false")){
					Intent service = new Intent();
					service.setClassName("com.toptech.floatball", "com.toptech.floatball.service.FloatWindowService");
					service.putExtra("showNavigation", false);
					try {
						startService(service);
					} catch (Resources.NotFoundException e) {
						e.printStackTrace();
					}
				}else if (msg.obj.toString().indexOf("text:")!=-1){
					String s=msg.obj.toString();
					String text=s.substring(5);
					textView.setText(text);
				}
				else {
					SendKey(Integer.parseInt(msg.obj.toString()));
				}
			}
		}
	};

	int width;
	private void showFloatWindow() {
		// ??????service???????????????
		wManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		width = wManager.getDefaultDisplay().getWidth();

		params = new WindowManager.LayoutParams();

		// ??????type??????
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // ?????????  TYPE_PHONE ???????????????

		// ??????????????????
		params.format = PixelFormat.RGBA_8888; // ????????????

		// ????????????,???????????????????????????????????????????????????????????????????????????????????????????????????????????????
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		// ????????????
		params.width = LayoutParams.MATCH_PARENT;
		params.height =LayoutParams.MATCH_PARENT;

//		params.gravity = Gravity.BOTTOM | Gravity.TOP;

		params.gravity = Gravity.BOTTOM;
		// ????????????????????????
		LayoutInflater inflater = LayoutInflater.from(mcontext); // ????????????????????????
		mLayout = (RelativeLayout) inflater.inflate(R.layout.floatball, null); // ?????????????????????????????????
		floatBallImageView = (ImageView) mLayout.findViewById(R.id.show_floatball);
		Button exitButton=(Button) mLayout .findViewById(R.id.exit);
		Button homeButton=(Button) mLayout .findViewById(R.id.home);
		textView=mLayout.findViewById(R.id.text);
		wManager.addView(mLayout, params); // ?????????????????????????????????


		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SendKey(KeyEvent.KEYCODE_BACK);
			}
		});
		homeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SendKey(KeyEvent.KEYCODE_HOME);
			}
		});

	}

	private void SendKey(final int keycode){
		new Thread() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(keycode);
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
		}.start();
	}
	/**
	 * ?????????????????????????????????
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mcontext = this;
		stopForeground(true);
		super.onDestroy();
		Log.d(TAG, "FloatWindowService OnDestroy");
		if (mLayout != null) {
			wManager.removeView(mLayout);
			mLayout = null;
		}


	}

}
