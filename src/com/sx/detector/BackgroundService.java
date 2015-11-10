package com.sx.detector;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class BackgroundService extends Service {

	public class LocalBinder extends Binder {
		public void gimmeHandler(Handler handler) {
			clientHandler = handler;
		}
	}

	private static final String LOG_TAG = "BackgroundService";
	private FootFallDetector footFallDetector;
	private Handler clientHandler;

	private final IBinder mBinder = new LocalBinder();

	private final Handler serviceHandler = new Handler();

	private final Runnable mUpdateCadenceTask = new Runnable() {
		@Override
		public void run() {
			Log.d(LOG_TAG, "UpdateCadenceTask runs.");
			int cadence = footFallDetector.getCurrentCadence();
			
				if (clientHandler != null) {
				
				Log.v(LOG_TAG, "Update cadence display.");
				Message message = Message.obtain();
				message.arg1 = cadence;
				clientHandler.sendMessage(message);
				//recentMusicType=currentMusicType;
			  }
			
			
			serviceHandler.postDelayed(this, 1000);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		Log.v(LOG_TAG, "onBind'd");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.v(LOG_TAG, "onUnbind'd");
		clientHandler = null;
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(LOG_TAG, "onDestroy");
		serviceHandler.removeCallbacks(mUpdateCadenceTask);
		footFallDetector.stop();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(LOG_TAG, "onCreate");
		footFallDetector = new FootFallDetector(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStart(intent, startId);
		Log.v(LOG_TAG, "onStart");
		start();
		return START_STICKY;
	}

	private void start() {
		footFallDetector.start();
		serviceHandler.post(mUpdateCadenceTask);
	}

	
}
