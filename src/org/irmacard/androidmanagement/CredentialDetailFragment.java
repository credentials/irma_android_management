package org.irmacard.androidmanagement;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.irmacard.androidmanagement.dummy.DummyContent;
import org.irmacard.androidmanagement.dummy.TestAttributes;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.AttributeDescription;
import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;
import org.irmacard.credentials.info.IssuerDescription;

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
	
	CredentialAttributeAdapter mAdapter;
	CredentialPackage credential;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CredentialDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// FIXME: this will not work in single screen mode
			ArrayList<CredentialPackage> credentials = ((CredentialListActivity) getActivity())
					.getCredentials();
			
			short cred_id = getArguments().getShort(
					CredentialDetailFragment.ARG_ITEM_ID);
			
			credential = null;
			for(CredentialPackage cp : credentials) {
				if(cp.getCredentialDescription().getId() == cred_id) {
					credential = cp;
				}
			}
		}
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
		
		IssuerDescription issuer = credential.getCredentialDescription().getIssuerDescription();
		issuerName.setText(issuer.getName());
		issuerAddress.setText(issuer.getContactAddress());
		issuerEMail.setText(issuer.getContactEMail());
		
		credentialDescription.setText(credential.getCredentialDescription().getDescription());
		
		list.setAdapter(mAdapter);
	}
}
