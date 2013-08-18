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

import org.irmacard.credentials.util.CardVersion;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

	CardVersion cardVersion;

	public static final String ARG_CARD_VERSION = "card_version";
	
	public interface Callbacks {
		public void onChangeCardPIN();
		public void onChangeCredPIN();
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SettingsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getArguments().containsKey(ARG_CARD_VERSION)) {
			cardVersion = (CardVersion) getArguments().get(ARG_CARD_VERSION);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings,
				container, false);

		return rootView;
	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// Card PIN listener
		Button cardPINButton = (Button) view.findViewById(R.id.settings_change_card_pin_button);
		cardPINButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v){
		    	((Callbacks) getActivity()).onChangeCardPIN();;
		    }
		});

		Button credPINButton = (Button) view.findViewById(R.id.settings_change_cred_pin_button);
		credPINButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v){
		        ((Callbacks) getActivity()).onChangeCredPIN();
		    }
		});

		TextView version_field = (TextView) view
				.findViewById(R.id.settings_card_version_text);
		version_field.setText(cardVersion.toString());
	}
}
