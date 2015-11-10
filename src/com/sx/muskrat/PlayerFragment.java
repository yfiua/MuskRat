package com.sx.muskrat;

import com.sx.detector.BackgroundService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class PlayerFragment extends Fragment {
	//private Button mStopRunning,mStartRunning;
	
	protected static final CharSequence DISPLAY_NO_DATA = "- -";

	public TextView mCurrentCadence;
	

	public static Fragment newInstance() {
		PlayerFragment fragment = new PlayerFragment();
        return fragment;
    }
	
	private SharedPreferences preferences;
	public String sensitivity, stop_id, low_id, high_id; //敏感度（秒数），播放列表ID
	private ImageButton btnPlay; 
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.player_fragment, container, false);
		
		
		btnPlay = (ImageButton) v.findViewById(R.id.play);
		
		//获取设置
		preferences = getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
		
		sensitivity = preferences.getString("sensitivity", "10");
		stop_id = preferences.getString("playlist_stop", "empty");
		low_id = preferences.getString("playlist_low", "empty");
		high_id = preferences.getString("playlist_high", "empty");
		
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MediaPlayer player = ((MainActivity) getActivity()).mpObject;
            	if (player.getDuration() != -1) {	// has data stream
	            	if (player.isPlaying()) {
	            		player.pause();
	            		btnPlay.setImageResource(R.drawable.btn_play);
	            	} else {
	            		player.start();
	            		btnPlay.setImageResource(R.drawable.btn_playing);
	            	}
            	} else {
            		((MainActivity) getActivity()).PlayRandomMusic();	//SongsFromAPlaylist(Integer.parseInt(stop_id));
            		btnPlay.setImageResource(R.drawable.btn_playing);
            	}
				
			}
		});
		
		btnPlay.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				((MainActivity) getActivity()).PlayRandomMusic();
				btnPlay.setImageResource(R.drawable.btn_playing);
				return true;
			}
		});
		
		
		   //��ʼ��������
        mCurrentCadence = (TextView) v.findViewById(R.id.currentCadence);
		mCurrentCadence.setText(DISPLAY_NO_DATA);
		
		if (((MainActivity) getActivity()).mpObject.isPlaying())
			btnPlay.setImageResource(R.drawable.btn_playing);

        return v;
    }
}
