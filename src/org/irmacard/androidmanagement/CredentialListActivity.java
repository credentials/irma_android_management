/**
 * CredentialListActivity.java
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, Februari 2013.
 */

package org.irmacard.androidmanagement;

import java.util.ArrayList;

import org.irmacard.androidmanagement.util.CredentialPackage;
import org.irmacard.credentials.util.log.LogEntry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * An activity representing a list of Credentials. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link CredentialDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items combined with
 * log and settings buttons is a {@link MenuFragment} and the item details (if
 * present) is a {@link CredentialDetailFragment}.
 * <p>
 * This activity also implements the required {@link MenuFragment.Callbacks}
 * interface to listen for item selections and button callbacks.
 */
public class CredentialListActivity extends FragmentActivity implements
		MenuFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	
	private ArrayList<CredentialPackage> credentials;
	private ArrayList<LogEntry> logs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Pass the list of CredentialPackages on to the ListFragement
		Intent intent = getIntent();
		@SuppressWarnings("unchecked")
		ArrayList<CredentialPackage> credentials = (ArrayList<CredentialPackage>) intent
				.getSerializableExtra(WaitingForCardActivity.EXTRA_CREDENTIAL_PACKAGES);
		setCredentials(credentials);
		
		@SuppressWarnings("unchecked")
		ArrayList<LogEntry> logs = (ArrayList<LogEntry>) intent
				.getSerializableExtra(WaitingForCardActivity.EXTRA_LOG_ENTRIES);
		setLogs(logs);

		setContentView(R.layout.activity_credential_list);

		if (findViewById(R.id.credential_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((MenuFragment) getSupportFragmentManager()
					.findFragmentById(R.id.credential_menu_fragment))
					.setTwoPaneMode(true);

			InitFragment initFragment = new InitFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.credential_detail_container, initFragment)
					.commit();

			Log.i("blaat", "Simulating initial click!!");
			((MenuFragment) getSupportFragmentManager()
					.findFragmentById(R.id.credential_menu_fragment)).simulateListClick(0);
		}
	}

	private void setCredentials(ArrayList<CredentialPackage> credentials) {
		this.credentials = credentials;
	}

	private void setLogs(ArrayList<LogEntry> logs) {
		this.logs = logs;
	}

	/**
	 * Callback method from {@link MenuFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(short id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putShort(CredentialDetailFragment.ARG_ITEM_ID, id);
			CredentialDetailFragment fragment = new CredentialDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.credential_detail_container, fragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					CredentialDetailActivity.class);
			detailIntent.putExtra(CredentialDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
	
	/**
	 * Callback method from {@link MenuFragment.Callbacks} indicating
	 * that the log was selected.
	 */
	public void onLogSelected() {
		Log.i("cla", "log selected");
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			LogFragment fragment = new LogFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.credential_detail_container, fragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			// FIXME: make single window version of this application
		}
	}

	/**
	 * Callback method from {@link MenuFragment.Callbacks} indicating
	 * that the settings were selected.
	 */
	public void onSettingsSelected() {
		Log.i("cla", "settings selected");
	}

	protected ArrayList<CredentialPackage> getCredentials() {
		return credentials;
	}

	public ArrayList<LogEntry> getLogs() {
		return logs;
	}
}
