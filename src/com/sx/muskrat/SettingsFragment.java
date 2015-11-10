package com.sx.muskrat;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class SettingsFragment extends Fragment {
	public static SettingsFragment newInstance() {
		SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }
	
	private SharedPreferences preferences;
	
	private EditText txtSensitivity;//���жȣ�����
	private Spinner spinner_stop, spinner_low, spinner_high;
	private Button btnSave, btnCancel;
	
	private String stop_id, low_id, high_id;//�����б�ID
	private ArrayList<String> playlistNames = new ArrayList<String>();
	private ArrayList<String> playlistIDs = new ArrayList<String>();
	private Cursor playListCursor;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.settings_fragment, container, false);
		
		txtSensitivity = (EditText) v.findViewById(R.id.sensitivity);
		spinner_stop = (Spinner) v.findViewById(R.id.playlist_stop);
		spinner_low  = (Spinner) v.findViewById(R.id.playlist_low);
		spinner_high = (Spinner) v.findViewById(R.id.playlist_high);
		btnSave = (Button) v.findViewById(R.id.save);
		btnCancel = (Button) v.findViewById(R.id.cancel);
		
		preferences = getActivity().getSharedPreferences("user",Context.MODE_PRIVATE);
		
		String oldSensitivity = preferences.getString("sensitivity", "130");
		String oldStop = preferences.getString("playlist_stop", "empty");
		String oldLow = preferences.getString("playlist_low", "empty");
		String oldHigh = preferences.getString("playlist_high", "empty");
		
		txtSensitivity.setText(oldSensitivity);
	
		String[] proj = {"*"};
	    Uri playlistURI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

	    playListCursor = getActivity().managedQuery(playlistURI, proj, null, null, null);

	    if (playListCursor == null) {
	        System.out.println("Not having any Playlist on phone --------------");
	        return v;//don't have list on phone
	    }

	    System.gc();

	    String playListName, playListID;

	    System.out.println(">>>>>>>  CREATING AND DISPLAYING LIST OF ALL CREATED PLAYLIST  <<<<<<");
	    
	    //��ȡ������б�
	    int playlistCount = playListCursor.getCount();
	    for (int i = 0; i < playlistCount; i++) {
	        playListCursor.moveToPosition(i);
	        playListName = playListCursor.getString(playListCursor.getColumnIndex("name"));
	        playListID = playListCursor.getString(playListCursor.getColumnIndex("_id"));
	        playlistNames.add(playListName);
	        playlistIDs.add(playListID);
	    }

	    
	    
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
	            android.R.layout.simple_spinner_item, playlistNames);
		
		spinner_stop.setAdapter(adapter);
		spinner_low.setAdapter(adapter);
		spinner_high.setAdapter(adapter);
		
		for (int i = 0; i < playlistCount; i++) {
	        if (oldStop.equals(playlistIDs.get(i)))
	        	spinner_stop.setSelection(i);
	        if (oldLow.equals(playlistIDs.get(i)))
	        	spinner_low.setSelection(i);
	        if (oldHigh.equals(playlistIDs.get(i)))
	        	spinner_high.setSelection(i);
	    }
	
		//ѡ��ֹͣʱ���ŵĲ����б�
		spinner_stop.setOnItemSelectedListener (
				new AdapterView.OnItemSelectedListener() {
	                public void onItemSelected(
	                        AdapterView<?> parent, 
	                        View view, 
	                        int position, 
	                        long id) {
	                    stop_id = playlistIDs.get((int) position);
	                }
	                
	                public void onNothingSelected(AdapterView<?> parent) {
	                }
	            }
		    );
		
		//Ƶ����ʱ���ŵĲ����б�
		spinner_low.setOnItemSelectedListener (
				new AdapterView.OnItemSelectedListener() {
	                public void onItemSelected(
	                        AdapterView<?> parent, 
	                        View view, 
	                        int position, 
	                        long id) {
	                    low_id = playlistIDs.get((int) position);
	                }
	                
	                public void onNothingSelected(AdapterView<?> parent) {
	                }
	            }
		    );
		
		//Ƶ�ʿ�ʱ���ŵĲ����б�
		spinner_high.setOnItemSelectedListener (
				new AdapterView.OnItemSelectedListener() {
	                public void onItemSelected(
	                        AdapterView<?> parent, 
	                        View view, 
	                        int position, 
	                        long id) {
	                    high_id = playlistIDs.get((int) position);
	                }
	                
	                public void onNothingSelected(AdapterView<?> parent) {
	                }
	            }
		    );
		
		//��������
		btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// save settings
            	Editor editor = preferences.edit();
            	editor.putString("sensitivity", txtSensitivity.getText().toString());
            	editor.putString("playlist_stop", stop_id);
            	editor.putString("playlist_low", low_id);
            	editor.putString("playlist_high", high_id);
            	editor.commit();
            	
            	MainActivity fa = (MainActivity) getActivity();
            	fa.getSupportFragmentManager().beginTransaction()
            		.replace(R.id.fragment_container, fa.fg_player).commit();
            }
        });
		
		//ȡ��
		btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	MainActivity fa = (MainActivity) getActivity();
            	fa.getSupportFragmentManager().beginTransaction()
            		.replace(R.id.fragment_container, fa.fg_player).commit();
            }
        });
		
        return v;
    }
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		//if (playListCursor != null)
	    //    playListCursor.close();
	
	}
}
 
