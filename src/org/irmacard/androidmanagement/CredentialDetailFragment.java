/**
 * CredentialDetailFragment.java
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.irmacard.android.util.credentials.AndroidWalker;
import org.irmacard.android.util.credentials.CredentialPackage;
import org.irmacard.androidmanagement.adapters.CredentialAttributeAdapter;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.AttributeDescription;
import org.irmacard.credentials.info.IssuerDescription;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A fragment representing a single Credential detail screen. This fragment is
 * either contained in a {@link CredentialListActivity} in two-pane mode (on
 * tablets) or a {@link CredentialDetailActivity} on handsets.
 */
public class CredentialDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	public static final String ARG_ITEM = "item";
	
	CredentialAttributeAdapter mAdapter;
	CredentialPackage credential;
	AndroidWalker aw;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CredentialDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM)) {
			credential = (CredentialPackage) getArguments().getSerializable(ARG_ITEM);
		}
		
		aw = new AndroidWalker(getResources().getAssets());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_credential_detail,
				container, false);

		if (credential != null) {
			List<AttributeDescription> attr_desc = credential.getCredentialDescription().getAttributes();
			Attributes attr_vals = credential.getAttributes();
			mAdapter = new CredentialAttributeAdapter(getActivity(), attr_desc,
					attr_vals);
		}

		return rootView;
	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ListView list = (ListView) view
				.findViewById(R.id.detail_attribute_list);
		
		TextView issuerName = (TextView) view.findViewById(R.id.detail_issuer_description_name);
		TextView issuerAddress = (TextView) view.findViewById(R.id.detail_issuer_description_address);
		TextView issuerEMail = (TextView) view.findViewById(R.id.detail_issuer_description_email);
		TextView credentialDescription = (TextView) view.findViewById(R.id.detail_credential_desc_text);
		TextView validityValue = (TextView) view.findViewById(R.id.detail_validity_value);
		TextView validityRemaining = (TextView) view.findViewById(R.id.detail_validity_remaining);
		ImageView issuerLogo = (ImageView) view.findViewById(R.id.detail_issuer_logo);
		Button deleteButton = (Button) view.findViewById(R.id.detail_delete_button);
		
		IssuerDescription issuer = credential.getCredentialDescription().getIssuerDescription();
		issuerName.setText(issuer.getName());
		issuerAddress.setText(issuer.getContactAddress());
		issuerEMail.setText(issuer.getContactEMail());
		
		// Display expiry
		if (credential.getAttributes().isValid()) {
			DateFormat sdf = SimpleDateFormat.getDateInstance(DateFormat.LONG);
			Date expirydate = credential.getAttributes().getExpiryDate();
			validityValue.setText(sdf.format(expirydate));

			int deltaDays = (int) ((expirydate.getTime() - Calendar
					.getInstance().getTime().getTime())
					/ (1000 * 60 * 60 * 24));
			// FIXME: text should be from resources
			validityRemaining.setText(deltaDays + " days remaining");

		} else {
			// Credential has expired
			validityValue.setText(R.string.credential_no_longer_valid);
			validityValue.setTextColor(getResources().getColor(R.color.irmared));
			validityRemaining.setText("");
		}
		
		credentialDescription.setText(credential.getCredentialDescription().getDescription());
		
		// Setting logo of issuer
		Bitmap logo = aw.getIssuerLogo(credential.getCredentialDescription()
				.getIssuerDescription());

		if(logo != null) {
			issuerLogo.setImageBitmap(logo);
		}

		// On delete button clicked
		deleteButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v){
		        clickedDeleteButton();
		    }
		});

		list.setAdapter(mAdapter);
	}

	private void clickedDeleteButton() {
		Log.i("blaat", "Delete button clicked");
		((CredentialListActivity) getActivity()).deleteCredential(credential
				.getCredentialDescription());
	}
}
