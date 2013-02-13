package org.irmacard.androidmanagement;

import java.util.List;

import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.AttributeDescription;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CredentialAttributeAdapter extends BaseAdapter {
	private Activity activity;
	private static LayoutInflater inflater = null;
	
	private List<AttributeDescription> attr_desc;
	private Attributes attr_vals;
	
	public CredentialAttributeAdapter(Activity activity, List<AttributeDescription> attr_desc, Attributes attr_vals) {
		this.activity = activity;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.attr_desc = attr_desc;
		this.attr_vals = attr_vals;
	}

	@Override
	public int getCount() {
		return attr_desc.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convert_view, ViewGroup parent) {
		View view = convert_view;
		if(view == null) {
			view = inflater.inflate(R.layout.row_attribute, null);
		}

		TextView name = (TextView) view.findViewById(R.id.detail_attribute_name);
		TextView value = (TextView) view.findViewById(R.id.detail_attribute_value);
		TextView description = (TextView) view.findViewById(R.id.detail_attribute_desc);

		AttributeDescription desc = attr_desc.get(position);
		name.setText(desc.getName() + ":");
		value.setText(new String(attr_vals.get(desc.getName())));
		description.setText(desc.getDescription());

		return view;
	}

}
