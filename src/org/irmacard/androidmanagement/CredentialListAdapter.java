package org.irmacard.androidmanagement;

import java.util.ArrayList;
import java.util.List;

import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.AttributeDescription;
import org.irmacard.credentials.info.CredentialDescription;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CredentialListAdapter extends BaseAdapter {
	private Activity activity;
	private static LayoutInflater inflater = null;

	private List<CredentialPackage> credentials;

	public CredentialListAdapter(Activity activity,
			List<CredentialPackage> credentials) {
		this.activity = activity;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (credentials != null) {
			this.credentials = credentials;
		} else {
			this.credentials = new ArrayList<CredentialPackage>();
		}
	}

	@Override
	public int getCount() {
		return credentials.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		return credentials.get(position).getCredentialDescription().getId();
	}

	@Override
	public View getView(int position, View convert_view, ViewGroup parent) {
		View view = convert_view;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item, null);
		}

		TextView name = (TextView) view
				.findViewById(R.id.item_label);

		CredentialDescription desc = credentials.get(position).getCredentialDescription();
		name.setText(desc.getShortName());
		return view;
	}

}
