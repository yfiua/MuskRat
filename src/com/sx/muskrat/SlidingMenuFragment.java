package com.sx.muskrat;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.ExpandableListView;

public class SlidingMenuFragment extends Fragment implements ExpandableListView.OnChildClickListener {
    
    private ExpandableListView sectionListView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        List<Section> sectionList = createMenu();
                
        View view = inflater.inflate(R.layout.slidingmenu_fragment, container, false);
        this.sectionListView = (ExpandableListView) view.findViewById(R.id.slidingmenu_view);
        this.sectionListView.setGroupIndicator(null);
        
        SectionListAdapter sectionListAdapter = new SectionListAdapter(this.getActivity(), sectionList);
        this.sectionListView.setAdapter(sectionListAdapter); 
        
        this.sectionListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
              @Override
              public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
              }
            });
        
        this.sectionListView.setOnChildClickListener(this);
        
        int count = sectionListAdapter.getGroupCount();
        for (int position = 0; position < count; position++) {
            this.sectionListView.expandGroup(position);
        }
        
        return view;
    }

    private List<Section> createMenu() {
        List<Section> sectionList = new ArrayList<Section>();

        Section oPlayerSection = new Section("MuskRat");
        oPlayerSection.addSectionItem(101,"Player", "ic_player");
        oPlayerSection.addSectionItem(102,"Settings", "ic_settings");
        
        Section oGeneralSection = new Section("General");
        oGeneralSection.addSectionItem(203, "About", "ic_about");
        oGeneralSection.addSectionItem(204, "Quit", "ic_quit");
        
        sectionList.add(oPlayerSection);
        sectionList.add(oGeneralSection);
        return sectionList;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
            int groupPosition, int childPosition, long id) {

    	MainActivity fa = (MainActivity) getActivity();
    	
        switch ((int)id) {
        	case 101:
	        	fa.getSupportFragmentManager().beginTransaction()
	            	.replace(R.id.fragment_container, fa.fg_player).commit();
	        	fa.slidingMenu.toggle();
	            break;
        	case 102:
                SettingsFragment fg_settings = SettingsFragment.newInstance();
        		fa.getSupportFragmentManager().beginTransaction()
            		.replace(R.id.fragment_container, fg_settings).commit();
        		fa.slidingMenu.toggle();
        		break;
	        case 203:
	        	fa.getSupportFragmentManager().beginTransaction()
	            	.replace(R.id.fragment_container, fa.fg_about).commit();
	        	fa.slidingMenu.toggle();
	            break;
	            
	        case 204:
	        	//TODO: stop service
	        	fa.slidingMenu.toggle();
	        	fa.onBackPressed();
	            break;
        }
        
        return false;
    }
}
