package com.toptech.floatball.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootStartBroadcast extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            //Intent mainActivityIntent  = new Intent(context, FloatService.class);
			//context.startService(mainActivityIntent);
			Intent startFloatBall = new Intent();
			startFloatBall.setAction("com.toptech.floatball.SERVICESTART");
			startFloatBall.setPackage("com.toptech.floatball");
			context.startService(startFloatBall);
		}
	};
}
