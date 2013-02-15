package org.irmacard.androidmanagement;

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

	private List<CredentialDescription> cred_desc;

	public CredentialListAdapter(Activity activity,
			List<CredentialDescription> cred_desc) {
		this.activity = activity;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.cred_desc = cred_desc;
	}

	@Override
	public int getCount() {
		return cred_desc.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		return cred_desc.get(position).getId();
	}

	@Override
	public View getView(int position, View convert_view, ViewGroup parent) {
		View view = convert_view;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item, null);
		}

		TextView name = (TextView) view
				.findViewById(R.id.item_label);

		CredentialDescription desc = cred_desc.get(position);
		name.setText(desc.getShortName());
		return view;
	}

}
