/**
 * CardMissingDialogFragment.java
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

package org.irmacard.androidmanagement.dialogs;

import org.irmacard.pilot.androidmanagement.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {
	private static final String EXTRA_TITLE = "org.irmacard.androidmanagement.dialogs.AlertDialogFragment.title";
	private static final String EXTRA_MESSAGE = "org.irmacard.androidmanagement.dialogs.AlertDialogFragment.message";
	

	public interface AlertDialogListener {
		public void onAlertDismiss();
	}

    AlertDialogListener mListener;
    String title;
    String message;
    
	public static AlertDialogFragment getInstance(String title, String message) {
        AlertDialogFragment f = new AlertDialogFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_MESSAGE, message);
        f.setArguments(args);

        return f;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

		title = getArguments().getString(EXTRA_TITLE);
		message = getArguments().getString(EXTRA_MESSAGE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle(title)
				.setMessage(message)
				.setIcon(R.drawable.irma_icon_warning_064px)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mListener.onAlertDismiss();
							}
						});

        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();

        return dialog;
    }

    // Override the Fragment.onAttach() method to instantiate the PINDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the PINDialogListener so we can send events to the host
            mListener = (AlertDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AlertDialogListener");
        }
    }
}