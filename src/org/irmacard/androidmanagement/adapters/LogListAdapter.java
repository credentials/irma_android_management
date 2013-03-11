/**
 * LogListAdapter.java
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

package org.irmacard.androidmanagement.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.irmacard.androidmanagement.R;
import org.irmacard.credentials.util.log.IssueLogEntry;
import org.irmacard.credentials.util.log.LogEntry;
import org.irmacard.credentials.util.log.RemoveLogEntry;
import org.irmacard.credentials.util.log.VerifyLogEntry;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LogListAdapter extends BaseAdapter {
	private static LayoutInflater inflater = null;

	private List<LogEntry> logs;

	public LogListAdapter(Activity activity,
			List<LogEntry> credentials) {
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (credentials != null) {
			this.logs = credentials;
		} else {
			this.logs = new ArrayList<LogEntry>();
		}
	}

	@Override
	public int getCount() {
		return logs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO: is this ok to do it like this?
		return position;
	}

	@Override
	public View getView(int position, View convert_view, ViewGroup parent) {
		View view = convert_view;
		if (view == null) {
			view = inflater.inflate(R.layout.log_item, null);
		}

		TextView header = (TextView) view.findViewById(R.id.log_item_header);
		TextView datetime = (TextView) view.findViewById(R.id.log_item_datetime);

		ImageView actionImage = (ImageView) view
				.findViewById(R.id.log_item_action_image);
		ImageView actorLogo = (ImageView) view
				.findViewById(R.id.log_item_actor_logo);

		LogEntry log = logs.get(position);
		String header_text = "";
		int actionImageResource = R.drawable.irma_icon_warning_064px; 

		if(log instanceof VerifyLogEntry) {
			header_text = "Verified: ";
			actionImageResource = R.drawable.irma_icon_ok_064px;
		} else if(log instanceof RemoveLogEntry) {
			header_text = "Removed: ";
			actionImageResource = R.drawable.irma_icon_missing_064px;
		} else if(log instanceof IssueLogEntry) {
			header_text = "Issued: ";
			actionImageResource = R.drawable.irma_icon_warning_064px;
		}

		header_text += log.getCredential().getName();
		header.setText(header_text);
		datetime.setText(SimpleDateFormat.getDateTimeInstance().format(log.getTimestamp()));
		actionImage.setImageResource(actionImageResource);
		actorLogo.setImageResource(R.drawable.irma_logo_150);

		return view;
	}
}
