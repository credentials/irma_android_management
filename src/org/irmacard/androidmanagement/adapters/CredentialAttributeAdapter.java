/**
 * CredentialAttributeAdapter.java
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

package org.irmacard.androidmanagement.adapters;

import java.util.List;

import org.irmacard.demo.androidmanagement.R;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.AttributeDescription;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CredentialAttributeAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;
	
	private List<AttributeDescription> attr_desc;
	private Attributes attr_vals;
	
	public CredentialAttributeAdapter(Activity activity, List<AttributeDescription> attr_desc, Attributes attr_vals) {
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

		AttributeDescription desc = attr_desc.get(position);
		name.setText(desc.getName() + ":");
		value.setText(new String(attr_vals.get(desc.getName())));

		return view;
	}

}
