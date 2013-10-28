package com.snowden.nfcinventory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

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
		public static final String MIME_TEXT_PLAIN = "text/plain";
		public static final String TAG = "NfcInventory";

		private NfcAdapter mNfcAdapter;

		public Checkout() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_checkout,container, false);
			TextView checkoutTextView = (TextView) rootView.findViewById(R.id.section_label);
			checkoutTextView.setText(getArguments().getString(ARG_SECTION_NUMBER));
			mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
			if (mNfcAdapter == null) {
				// Stop here, we definitely need NFC
				Toast.makeText(getActivity(), "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
				return rootView;
			}
			if (!mNfcAdapter.isEnabled()) {
				Toast.makeText(getActivity(), "NFC is disabled.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getActivity(), "NFC is enabled.", Toast.LENGTH_LONG).show();
			}
			handleIntent(getActivity().getIntent());

			return rootView;
		}

		private void handleIntent(Intent intent) {
			String action = intent.getAction();
		    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
		        String type = intent.getType();
		        if (MIME_TEXT_PLAIN.equals(type)) {
		            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		            new NdefReaderTask().execute(tag);
		        } else {
		            Log.d(TAG, "Wrong mime type: " + type);
		        }
		    } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
		        // In case we would still use the Tech Discovered Intent
		        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		        String[] techList = tag.getTechList();
		        String searchedTech = Ndef.class.getName();
		        for (String tech : techList) {
		            if (searchedTech.equals(tech)) {
		                new NdefReaderTask().execute(tag);
		                break;
		            }
		        }
		    }
		}

		@Override
		public void onResume() {
			super.onResume();
			/**
			 * It's important, that the activity is in the foreground (resumed). Otherwise
			 * an IllegalStateException is thrown.
			 */
			setupForegroundDispatch(getActivity(), mNfcAdapter);
		}
		@Override
		public void onPause() {
			/**
			 * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
			 */
			stopForegroundDispatch(getActivity(), mNfcAdapter);
			super.onPause();
		}
		protected void onNewIntent(Intent intent) {
			/**
			 * This method gets called, when a new Intent gets associated with the current activity instance.
			 * Instead of creating a new activity, onNewIntent will be called. For more information have a look
			 * at the documentation.
			 *
			 * In our case this method gets called, when the user attaches a Tag to the device.
			 */
			handleIntent(intent);
		}

		/**
		 * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
		 * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
		 */
		public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
			final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
			IntentFilter[] filters = new IntentFilter[1];
			String[][] techList = new String[][]{};
			// Notice that this is the same filter as in our manifest.
			filters[0] = new IntentFilter();
			filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
			filters[0].addCategory(Intent.CATEGORY_DEFAULT);
			try {
				filters[0].addDataType(MIME_TEXT_PLAIN);
			} catch (MalformedMimeTypeException e) {
				throw new RuntimeException("Check your mime type.");
			}
			adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
		}
		/**
		 * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
		 * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
		 */
		public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
			adapter.disableForegroundDispatch(activity);
		}
		
		private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
		    @Override
		    protected String doInBackground(Tag... params) {
		        Tag tag = params[0];
		        Ndef ndef = Ndef.get(tag);
		        if (ndef == null) {
		            // NDEF is not supported by this Tag.
		            return null;
		        }
		        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
		        NdefRecord[] records = ndefMessage.getRecords();
		        for (NdefRecord ndefRecord : records) {
		            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
		                try {
		                    return readText(ndefRecord);
		                } catch (UnsupportedEncodingException e) {
		                    Log.e(TAG, "Unsupported Encoding", e);
		                }
		            }
		        }
		        return null;
		    }
		    private String readText(NdefRecord record) throws UnsupportedEncodingException {
		        /*
		         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
		         *
		         * http://www.nfc-forum.org/specs/
		         *
		         * bit_7 defines encoding
		         * bit_6 reserved for future use, must be 0
		         * bit_5..0 length of IANA language code
		         */
		        byte[] payload = record.getPayload();
		        // Get the Text Encoding
		        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
		        // Get the Language Code
		        int languageCodeLength = payload[0] & 0063;
		        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
		        // e.g. "en"
		        // Get the Text
		        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
		    }
		    @Override
		    protected void onPostExecute(String result) {
		        if (result != null) {
		        	Toast.makeText(getActivity(), "Content: "+result, Toast.LENGTH_LONG).show();
		        }
		    }
		}
		
	}
	
	

}
