package org.irmacard.androidmanagement;

import java.util.ArrayList;
import java.util.List;

import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.AttributeDescription;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class MenuFragment extends Fragment {	
	private CredentialListAdapter listAdapter;
	private ArrayList<CredentialPackage> credentials = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public MenuFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_menu,
				container, false);

		return rootView;
	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ListView list = (ListView) view
				.findViewById(R.id.credential_menu_list);
		Button log_button = (Button) view.findViewById(R.id.credential_menu_log_button);
		Button settings_button = (Button) view.findViewById(R.id.credential_menu_settings_button);
	}
}
