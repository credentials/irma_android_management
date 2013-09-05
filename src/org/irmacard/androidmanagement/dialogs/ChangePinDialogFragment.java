package org.irmacard.androidmanagement.dialogs;

import org.irmacard.androidmanagement.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ChangePinDialogFragment extends DialogFragment {
	private static final String EXTRA_PIN_NAME = "AndroidManagement.ChangePinDialog.pinname";
	private static final String EXTRA_TRIES = "AndroidManagement.ChangePinDialog.tries";
	private static final String EXTRA_NEW_PIN = "AndroidManagement.ChangePinDialog.new_pin";
	private static final String EXTRA_PIN_LENGTH = "AndroidManagement.ChangePinDialog.pinLength";

	private String pinName;
	private int tries;
	private String newPin;
	private int pinLength;

    private Boolean canceled;

    private static enum Error {
    	NONE,
    	PINS_NOT_EQUAL,
    	PIN_LENGTH_WRONG
    }
    private Error error = Error.NONE;
    
    private EditText old_pin_field;
    private EditText new_pin_field;
    private EditText new_pin_again_field;
    private TextView error_field;
	
	public interface ChangePinDialogListener {
		public void onPINChange(String oldpin, String newpin);
        public void onPINChangeCancel();
	}
	
	ChangePinDialogListener mListener;
	private AlertDialog dialog;
	
	public static ChangePinDialogFragment getInstance(String pinName, int tries, String new_pin, int pinLength) {
        ChangePinDialogFragment f = new ChangePinDialogFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_PIN_NAME, pinName);
        args.putInt(EXTRA_TRIES, tries);
        args.putString(EXTRA_NEW_PIN, new_pin);
        args.putInt(EXTRA_PIN_LENGTH, pinLength);
        f.setArguments(args);

        return f;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

		pinName = getArguments().getString(EXTRA_PIN_NAME);
		tries = getArguments().getInt(EXTRA_TRIES);
		newPin = getArguments().getString(EXTRA_NEW_PIN);
		pinLength = getArguments().getInt(EXTRA_PIN_LENGTH);
		
		Log.i("Blaat", "Tries: " + tries);
    }
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_changepin, null);
        builder.setView(dialogView)
        	.setTitle("Changing " + pinName + " PIN")
	           .setPositiveButton("OK", null)
	           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						canceled = true;
					}
				});
        // Create the AlertDialog object and return it
        dialog = builder.create();
        
        Log.i("Blaat", "On create called, error state is " + error.toString());
        old_pin_field = (EditText) dialogView.findViewById(R.id.old_pincode);
        new_pin_field = (EditText) dialogView.findViewById(R.id.new_pin_code);
        new_pin_again_field = (EditText) dialogView.findViewById(R.id.new_pin_code_again);
        error_field = (TextView) dialogView.findViewById(R.id.changepin_error);
        
        if(error != Error.NONE || tries != -1) {
        	error_field.setVisibility(View.VISIBLE);

        	switch(error) {
        	case PINS_NOT_EQUAL:
        		error_field.setText(R.string.pins_unequal);
        		break;
			case PIN_LENGTH_WRONG:
				error_field.setText(String.format(getResources().getString(
						R.string.pin_length_wrong, pinLength)));
				break;
        	case NONE:
        		// Then report number of tries
				error_field.setText(String.format(
						getResources().getString(R.string.change_pin_tries),
						tries));
				new_pin_field.setText(newPin);
				new_pin_again_field.setText(newPin);
        	}
        } else {
        	error_field.setVisibility(View.GONE);
        }

        // Make sure that the keyboard is always shown and doesn't require an additional touch
        // to focus the TextEdit view.
        dialog.getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        old_pin_field.requestFocus();
        
        new_pin_again_field.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					dismissDialog();
				}
				return false;
            }
        });

        return dialog;
    }
    
    // Override the Fragment.onAttach() method to instantiate the ChangePinDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ChangePinDialogListener so we can send events to the host
            mListener = (ChangePinDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PINDialogListener");
        }
    }
    
    public void show(android.app.FragmentManager fm, String str) {
        canceled = false;
        super.show(fm, str);
    }
    
    @Override 
    public void onDismiss(DialogInterface dialog) {
    	super.onDismiss(dialog);
        if(!canceled) {
            // Test for validity here
        	String old_pin = old_pin_field.getText().toString();
        	String new_pin = new_pin_field.getText().toString();
        	String new_pin_again = new_pin_again_field.getText().toString();
        	
        	if(!new_pin.equals(new_pin_again)) {
        		error = Error.PINS_NOT_EQUAL;
        		show(getFragmentManager(), getTag());
        	} else if(new_pin.length() != pinLength) {
        		error = Error.PIN_LENGTH_WRONG;
        		show(getFragmentManager(), getTag());
        	} else {
        		mListener.onPINChange(old_pin, new_pin);
        	}
        }
    }

    private void dismissDialog() {
    	dialog.dismiss();
    }
}
