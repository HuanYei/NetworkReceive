package com.toptech.floatball.service;

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
	private long downTime, upTime;
	int lastX, lastY;
	int paramX, paramY;
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
					Log.e("ss","网站"+httpUrl);
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
		},0,1000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
// (2) 使用handler处理接收到的消息

	}
	private Handler mHandler = new Handler(){

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
				}else {
					SendKey(Integer.parseInt(msg.obj.toString()));
				}
			}
		}
	};

	int width;
	private void showFloatWindow() {
		// 通过service类获取实例
		wManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		width = wManager.getDefaultDisplay().getWidth();

		params = new WindowManager.LayoutParams();

		// 设置type类型
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 这个比  TYPE_PHONE 优先级要高

		// 设置背景图片
		params.format = PixelFormat.RGBA_8888; // 透明效果

		// 设置标志,这个两个标志为了让就算出现了悬浮窗，不影响其他区域，只在悬浮窗的区域受干扰
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		// 设置大小
		params.width = LayoutParams.MATCH_PARENT;
		params.height =LayoutParams.MATCH_PARENT;

//		params.gravity = Gravity.BOTTOM | Gravity.TOP;

		params.gravity = Gravity.BOTTOM;
		// 设置悬浮窗的布局
		LayoutInflater inflater = LayoutInflater.from(mcontext); // 获取共享全局布局
		mLayout = (RelativeLayout) inflater.inflate(R.layout.floatball, null); // 这里的浮窗口定义成圆球
		floatBallImageView = (ImageView) mLayout.findViewById(R.id.show_floatball);
		Button exitButton=(Button) mLayout .findViewById(R.id.exit);
		Button homeButton=(Button) mLayout .findViewById(R.id.home);
		wManager.addView(mLayout, params); // 把配置好的视图添加进去


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
	 * 让小球从半圆状恢复过来
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
