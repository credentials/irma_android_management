/**
 * LogFragment.java
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
 * Copyright (C) Wouter Lueks, Radboud University Nijmegen, March 2013.
 */

package org.irmacard.androidmanagement;

import java.util.ArrayList;

import org.irmacard.androidmanagement.adapters.LogListAdapter;
import org.irmacard.credentials.util.log.LogEntry;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class LogFragment extends Fragment {	
	private LogListAdapter listAdapter;
	private ArrayList<LogEntry> logs = null;
	private ListView listView;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LogFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    logs = ((CredentialListActivity) getActivity()).getLogs();
		if (logs == null) {
			Log.i("blaat", "No credentials available yet");
		} else {
			for (LogEntry cp : logs) {
				Log.i("blaat", cp.toString());
			}
		}

	    listAdapter = new LogListAdapter(getActivity(), logs);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_log,
				container, false);
		return rootView;
	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		listView = (ListView) view
				.findViewById(R.id.log_list);
		listView.setAdapter(listAdapter);
	}
}
