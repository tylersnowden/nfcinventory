package com.snowden.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.snowden.nfcinventory.DatabaseHandler;
import com.snowden.nfcinventory.Item;
import com.snowden.nfcinventory.R;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class ItemList extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	private ArrayList<String> items;
	private DatabaseHandler db;
	public ArrayAdapter<String> adapter;
	public int current;

	public ItemList() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
		ListView listView = (ListView) rootView.findViewById(R.id.section_list);
		TextView checkoutTextView = (TextView) rootView.findViewById(R.id.section_label);
		checkoutTextView.setText(getArguments().getString(ARG_SECTION_NUMBER));

		db = new DatabaseHandler(getActivity());
		
		/*db.addItem(new Item("Back to the Future","jfkeEFfesfjeess",1));
		db.addItem(new Item("The Smurfs","fet4tbv4",1));
		db.addItem(new Item("Argo","gfsresg",1));
		db.addItem(new Item("Finding Nemo","trhggrdddf",1));
		db.addItem(new Item("Inception","grsjythrd",0));
		db.addItem(new Item("Gravity","grujtjjddgfd",0));
		db.addItem(new Item("Free Willy","rgshhrrdrgdg",1));
		db.addItem(new Item("Turbo","shssh5hdgfd",1));
		db.addItem(new Item("Game of Thrones","aggtrhdhd",1));
		db.addItem(new Item("Knight's Tale","grsgshhgsh",0));
		db.addItem(new Item("Lilo and Stich","jjkehgg3ggs",1));
		db.addItem(new Item("Alladin","jskjjfddddfr456",0));*/
		
		items = db.getAllItemsAsString();
        
        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        
        registerForContextMenu(listView);

        return rootView;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    current = (int) info.id;
	    
		menu.setHeaderTitle("Options");
		menu.add(1, 1, 1, "Details");
		menu.add(1, 2, 2, "Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
	    		    
	    if (itemId == 2) {
	    	List<Item> all_items = db.getAllItems();
	    	items.remove(current);
	    	Item tmp = all_items.get(current);
	    	db.deleteItem(tmp);
	    	adapter.notifyDataSetChanged();
	    	Toast.makeText(getActivity(), tmp.getName() + " removed", Toast.LENGTH_SHORT).show();
	    }
	    
	    return true;
	}

}