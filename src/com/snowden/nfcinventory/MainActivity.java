package com.snowden.nfcinventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the two
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		   
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment;
			Bundle args = new Bundle();
			
			switch (position) {
				case 0:
				default:
					fragment = new ItemList();
					args.putString(ItemList.ARG_SECTION_NUMBER, "List of Current Inventory");
					break;
				case 1:
					fragment = new Checkout();
					args.putString(Checkout.ARG_SECTION_NUMBER, "Checkout or Check-in an Item");
					break;
			}
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class ItemList extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		private ArrayList<String> items;
		private DatabaseHandler db;
		private ArrayAdapter<String> adapter;

		public ItemList() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
			ListView listView = (ListView) rootView.findViewById(R.id.section_list);
			TextView checkoutTextView = (TextView) rootView.findViewById(R.id.section_label);
			checkoutTextView.setText(getArguments().getString(ARG_SECTION_NUMBER));

			db = new DatabaseHandler(getActivity());
			
			items = db.getAllItemsAsString();
	        
	        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, items);
	        listView.setAdapter(adapter);
	        
	        registerForContextMenu(listView);
	        
	        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,long id) {
	                TextView clickedView = (TextView) view;
	                Toast.makeText(getActivity(), "["+id+"] - "+clickedView.getText(), Toast.LENGTH_LONG).show();
	            }
	        });
 
	        return rootView;
		}
		
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {

		      super.onCreateContextMenu(menu, v, menuInfo);
		      AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;
		      
		      menu.setHeaderTitle("Options");
		      menu.add(1, 1, 1, "Details");
		      menu.add(1, 2, 2, "Delete");
		}
		
		@Override
		public boolean onContextItemSelected(MenuItem item) {
		    int itemId = item.getItemId();
		    		    
		    if (itemId == 2) {
		    	items.remove(itemId);
		    	adapter.notifyDataSetChanged();
		    	Toast.makeText(getActivity(), itemId+ " Item deleted", Toast.LENGTH_SHORT).show();
		    }
		    
		    return true;
		}

	}
	
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class Checkout extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public Checkout() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_checkout,
					container, false);
			TextView checkoutTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			checkoutTextView.setText(getArguments().getString(
					ARG_SECTION_NUMBER));
			return rootView;
		}
	}

}
