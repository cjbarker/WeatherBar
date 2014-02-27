package com.cjbarker.wb;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Notifier {

	// static vars
	private static final String TAG = "Notifier";
	private static final int NOTIFICATION_ID = 14369;
		
	// member vars
	private Context ctx;
	private NotificationCompat.Builder builder;
	private Notification notification;
	private NotificationManager noteMgr;
	
	private Notifier() {}
	
	public Notifier(Context ctx) {
		Log.d(TAG, "Notifier constructor invoked for building...");
		this.ctx = ctx;
		this.noteMgr = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		this.build("Test Title", "Here is my notification!");	// TODO load from config file
		Log.i(TAG, "Notification and Manager built");
	}
	
	private void build(final String title, final String txt) {
		this.builder = new NotificationCompat.Builder(this.ctx)
	    	.setSmallIcon(R.drawable.notification_icon)	// TODO switch to AnimationDrawable to display temp
	    	.setContentTitle(title)
	    	.setContentText(txt);
		this.notification = this.builder.build();
		this.notification.flags |= Notification.FLAG_ONGOING_EVENT;
	}
	
	public void show() {
		show(null, null);
	}
	
	public void show(String title, String txt) {
		if (!Util.isEmpty(title) || !Util.isEmpty(txt)) {
			this.build(title, txt);
		}
		this.noteMgr.notify(NOTIFICATION_ID, this.notification);
	}
}
