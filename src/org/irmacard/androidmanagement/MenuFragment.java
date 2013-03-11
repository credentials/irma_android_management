package org.irmacard.androidmanagement;

import java.util.ArrayList;

import org.irmacard.androidmanagement.adapters.CredentialListAdapter;
import org.irmacard.androidmanagement.util.AndroidWalker;
import org.irmacard.androidmanagement.util.CredentialPackage;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MenuFragment extends Fragment {	
	private CredentialListAdapter listAdapter;
	private ArrayList<CredentialPackage> credentials = null;
	private ListView listView;
	private Button log_button;
	private Button settings_button;

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections, as well as the pressing of log and settings buttons.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(short id);

		/**
		 * Callback for when log button has been selected
		 */
		public void onLogSelected();

		/**
		 * Callback for when the settings button has been selected
		 */
		public void onSettingsSelected();
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(short id) {}

		@Override
		public void onLogSelected() {}

		@Override
		public void onSettingsSelected() {}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MenuFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("menu", "On Create on MenuFragment Called");

	    AndroidWalker aw = new AndroidWalker(getResources().getAssets());
	    DescriptionStore.setTreeWalker(aw);

	    try {
			DescriptionStore.getInstance();
		} catch (InfoException e) {
			// TODO Auto-generated catch block
			Log.e("error", "something went wrong");
			e.printStackTrace();
		}

	    credentials = ((CredentialListActivity) getActivity()).getCredentials();
		if (credentials == null) {
			Log.i("blaat", "No credentials available yet");
		} else {
			for (CredentialPackage cp : credentials) {
				Log.i("blaat", cp.toString());
			}
		}

	    listAdapter = new CredentialListAdapter(getActivity(), credentials);
	    
	    Log.i("menu", "On create of menu fragment finished");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("menu", "Inflating fragment layout");
		View rootView = inflater.inflate(R.layout.fragment_menu,
				container, false);
		Log.i("menu", "Done inflating fragment layout");
		return rootView;
	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		listView = (ListView) view
				.findViewById(R.id.credential_menu_list);
		log_button = (Button) view.findViewById(R.id.credential_menu_log_button);
		settings_button = (Button) view.findViewById(R.id.credential_menu_settings_button);

		listView.setAdapter(listAdapter);

		// On item clicked in list
		listView.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id){
		        clickedListItem(position, v);
		    }
		});

		log_button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v){
		        clickedLogButton();
		    }
		});

		settings_button.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v){
		        clickedSettingsButton();
		    }
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	/**
	 * Turns work in two-pane mode. If this si the case, items remain
	 * activitated after they have been clicked.
	 */
	public void setTwoPaneMode(boolean twoPane) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		listView.setChoiceMode(
				twoPane ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void simulateListClick(int pos) {
	    if(pos < credentials.size()) {
	    	listView.performItemClick(getView(), pos, listView.getItemIdAtPosition(pos));
	    }
	}

	private void clickedLogButton() {
		listView.setItemChecked(listView.getCheckedItemPosition(), false);
		settings_button.setActivated(false);
		log_button.setActivated(true);

		mCallbacks.onLogSelected();
	}

	private void clickedSettingsButton() {
		listView.setItemChecked(listView.getCheckedItemPosition(), false);
		settings_button.setActivated(true);
		log_button.setActivated(false);

		mCallbacks.onSettingsSelected();
	}

	private void clickedListItem(int position, View item) {
		settings_button.setActivated(false);
		log_button.setActivated(false);
		mCallbacks.onItemSelected(credentials.get(position).getCredentialDescription().getId());
	}
}
