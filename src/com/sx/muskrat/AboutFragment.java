package com.sx.muskrat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;

public class AboutFragment extends Fragment {
	
	public static Fragment newInstance() {
		AboutFragment fragment = new AboutFragment();
        return fragment;
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
        return inflater.inflate(R.layout.about_fragment, container, false);

    }
}
