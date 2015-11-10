package com.sx.muskrat;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sx.detector.BackgroundService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {
	protected static final CharSequence DISPLAY_NO_DATA = "- -";
	public SlidingMenu slidingMenu;
	public Fragment fg_about;
	public PlayerFragment fg_player;
	private Cursor c;
	private int currentMusicType = 0;
	private int recentMusicType = 0;

	MediaPlayer mpObject = null;

	public Intent serviceIntent;
	private final ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			((BackgroundService.LocalBinder) binder)
					.gimmeHandler(updateCadenceDisplayHandler);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// do nothing
		}
	};
	// update the number displayed
	private final Handler updateCadenceDisplayHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			int cadence = message.arg1;
			if (cadence >= 0) {
				fg_player.mCurrentCadence.setText(String.valueOf(cadence));
				if (cadence == 0 ) {	// stopping
					currentMusicType = 0;
					Message message1 = Message.obtain();
					message1.arg1 = currentMusicType;
					updateMusicTypeHandler.sendMessage(message1);
					
				} else {
					if (cadence < Integer.parseInt(fg_player.sensitivity)) {// slowing
						currentMusicType = 1;
						Message message2 = Message.obtain();
						message2.arg1 = currentMusicType;
						updateMusicTypeHandler.sendMessage(message2);
					} else {			// highing
						currentMusicType = 2;
						Message message3 = Message.obtain();
						message3.arg1 = currentMusicType;
						updateMusicTypeHandler.sendMessage(message3);
					}
				}
			} else {
				fg_player.mCurrentCadence.setText(DISPLAY_NO_DATA);
			}

		}

	};
	
	// change playlist
	private final Handler updateMusicTypeHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			// do nothing, just set recentMusicType
			recentMusicType = message.arg1;
			/*
			int currentmusicType = message.arg1;// 
			if (currentmusicType != recentMusicType) {
				switch (currentmusicType) {
				case 1:
					PlaySongsFromAPlaylist(Integer.parseInt(fg_player.low_id));
					break;
				case 2:
					PlaySongsFromAPlaylist(Integer.parseInt(fg_player.high_id));
					break;
				default:
					PlaySongsFromAPlaylist(Integer.parseInt(fg_player.stop_id));
					recentMusicType = currentmusicType;
				}
			}
			*/
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

			// creating instance of the Fragments.
			this.fg_about = AboutFragment.newInstance();
			this.fg_player = new PlayerFragment();

			// In case this activity was started with special instructions from
			// an
			// Intent, pass the Intent's extras to the fragment as arguments
			this.fg_about.setArguments(getIntent().getExtras());
			this.fg_player.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, this.fg_player).commit();
		}

		// Add sliding menu
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		slidingMenu.setMenu(R.layout.slidingmenu);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		serviceIntent = new Intent(MainActivity.this, BackgroundService.class);
		startService(serviceIntent);

		mpObject = new MediaPlayer();
		mpObject.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				try {
					PlayRandomMusic();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	protected void onStop() {
		super.onStop();
		unbindService(serviceConnection);
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (c != null)
			c.close();
		
		mpObject.release();
		fg_player.mCurrentCadence.setText(DISPLAY_NO_DATA);
		stopService(new Intent(this, BackgroundService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		if (slidingMenu.isMenuShowing()) {
			slidingMenu.toggle();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			this.slidingMenu.toggle();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.slidingMenu.toggle();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void PlayRandomMusic() {
		try {
			switch (recentMusicType) {
			case 1:
				PlaySongsFromAPlaylist(Integer
						.parseInt(fg_player.low_id));
				break;
			case 2:
				PlaySongsFromAPlaylist(Integer
						.parseInt(fg_player.high_id));
				break;
			default:
				PlaySongsFromAPlaylist(Integer
						.parseInt(fg_player.stop_id));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void PlaySongsFromAPlaylist(int playListID) {

		String[] proj = { MediaStore.Audio.Playlists.Members.AUDIO_ID,
				MediaStore.Audio.Playlists.Members.DATA,
				MediaStore.Audio.Playlists.Members.TITLE,
				MediaStore.Audio.Playlists.Members._ID, };
		this.c = getContentResolver().query(
				MediaStore.Audio.Playlists.Members.getContentUri("external",
						playListID), proj, null, null, null);
		startManagingCursor(c);
		if (c != null) {
			int theSongIDIwantToPlay = (int)(Math.random() * c.getCount()); // play random music
		
			c.moveToPosition(theSongIDIwantToPlay);
			String DataStream = c.getString(c.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
			PlayMusic(DataStream);
		}
		
		
	}

	public void PlayMusic(String DataStream) {
		if (mpObject != null) {
			if (mpObject.isPlaying()) {
				mpObject.stop();
			}
			mpObject.reset();
		} else {
			mpObject = new MediaPlayer();
		}
		if (DataStream == null)
			return;
		try {
			mpObject.setDataSource(DataStream);
			mpObject.prepare();
			mpObject.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
