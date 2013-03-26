/**
 * CredentialListActivity.java
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

import java.io.IOException;
import java.util.ArrayList;

import net.sourceforge.scuba.smartcards.CardServiceException;
import net.sourceforge.scuba.smartcards.IsoDepCardService;

import org.irmacard.android.util.credentials.CredentialPackage;
import org.irmacard.android.util.pindialog.EnterPINDialogFragment;
import org.irmacard.androidmanagement.util.TransmitResult;
import org.irmacard.credentials.idemix.IdemixCredentials;
import org.irmacard.credentials.info.CredentialDescription;

import org.irmacard.credentials.util.log.LogEntry;
import org.irmacard.idemix.IdemixService;
import org.irmacard.idemix.IdemixSmartcard;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * An activity representing a list of Credentials. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link CredentialDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items combined with
 * log and settings buttons is a {@link MenuFragment} and the item details (if
 * present) is a {@link CredentialDetailFragment}.
 * <p>
 * This activity also implements the required {@link MenuFragment.Callbacks}
 * interface to listen for item selections and button callbacks.
 */
public class CredentialListActivity extends FragmentActivity implements
		MenuFragment.Callbacks, EnterPINDialogFragment.PINDialogListener,
		CardMissingDialogFragment.CardMissingDialogListener {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	
	private ArrayList<CredentialPackage> credentials;
	private ArrayList<LogEntry> logs;
	private Tag tag;

	private interface CardProgram {
		public TransmitResult run(IdemixService is) throws CardServiceException;
	}

	private enum Action {
		NONE,
		DELETE_CREDENTIAL,
		CHANGE_CARD_PIN,
		CHANGE_CREDENTIAL_PIN
	}

	private enum State {
		NORMAL,
		TEST_CARD_PRESENCE,
		WAITING_FOR_CARD,
		CONFIRM_ACTION,
		PERFORM_ACTION,
		ACTION_PERFORMED,
		ACTION_FAILED
	}

	private Action currentAction = Action.NONE;
	private State currentState = State.NORMAL;
	private DialogFragment cardMissingDialog;
	private CredentialDescription toBeDeleted;
	private String pinCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Pass the list of CredentialPackages on to the ListFragement
		Intent intent = getIntent();
		@SuppressWarnings("unchecked")
		ArrayList<CredentialPackage> credentials = (ArrayList<CredentialPackage>) intent
				.getSerializableExtra(WaitingForCardActivity.EXTRA_CREDENTIAL_PACKAGES);
		setCredentials(credentials);
		
		@SuppressWarnings("unchecked")
		ArrayList<LogEntry> logs = (ArrayList<LogEntry>) intent
				.getSerializableExtra(WaitingForCardActivity.EXTRA_LOG_ENTRIES);
		setLogs(logs);

		Tag tag = (Tag) intent
				.getParcelableExtra(WaitingForCardActivity.EXTRA_TAG);
		setTag(tag);

		setContentView(R.layout.activity_credential_list);

		if (findViewById(R.id.credential_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((MenuFragment) getSupportFragmentManager()
					.findFragmentById(R.id.credential_menu_fragment))
					.setTwoPaneMode(true);

			InitFragment initFragment = new InitFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.credential_detail_container, initFragment)
					.commit();

			Log.i("blaat", "Simulating initial click!!");
			((MenuFragment) getSupportFragmentManager()
					.findFragmentById(R.id.credential_menu_fragment)).simulateListClick(0);
		}
	}

	private void setCredentials(ArrayList<CredentialPackage> credentials) {
		this.credentials = credentials;
	}

	private void setLogs(ArrayList<LogEntry> logs) {
		this.logs = logs;
	}

	private void setTag(Tag tag) {
		this.tag = tag;
	}

	/**
	 * Callback method from {@link MenuFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(short id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putShort(CredentialDetailFragment.ARG_ITEM_ID, id);
			CredentialDetailFragment fragment = new CredentialDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.credential_detail_container, fragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					CredentialDetailActivity.class);
			detailIntent.putExtra(CredentialDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
	
	/**
	 * Callback method from {@link MenuFragment.Callbacks} indicating
	 * that the log was selected.
	 */
	public void onLogSelected() {
		Log.i("cla", "log selected");
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			LogFragment fragment = new LogFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.credential_detail_container, fragment)
					.commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			// FIXME: make single window version of this application
		}
	}

	/**
	 * Callback method from {@link MenuFragment.Callbacks} indicating
	 * that the settings were selected.
	 */
	public void onSettingsSelected() {
		Log.i("cla", "settings selected");
	}

	public void onCardMissingCancel() {
		// If cancelled we cannot continue with the action
		gotoState(State.NORMAL);
	}

	public void onCardMissingRetry() {
		// We should retry
		gotoState(State.TEST_CARD_PRESENCE);
	}

	protected ArrayList<CredentialPackage> getCredentials() {
		return credentials;
	}

	public ArrayList<LogEntry> getLogs() {
		return logs;
	}

	private void gotoState(State state) {
		String TAG = "CLA:State";

		//State previousState = currentState;
		currentState = state;

		switch(state) {
		case NORMAL:
			Log.i(TAG, "Returning to default state");
			break;
		case TEST_CARD_PRESENCE:
			Log.i(TAG, "Checking card presence");
			new CheckCardPresentTask(this).execute(tag);
			break;
		case WAITING_FOR_CARD:
			Log.i(TAG, "Going to wait for card");
			cardMissingDialog = new CardMissingDialogFragment();
			cardMissingDialog.show(getFragmentManager(), "cardmissing");
			break;
		case CONFIRM_ACTION:
			Log.i(TAG, "Confirming action");
			// For now we just handle deleting of credentials
			DialogFragment pinDialog = new EnterPINDialogFragment();
			pinDialog.show(getFragmentManager(), "pinentry");
			break;
		case ACTION_FAILED:
			Log.i(TAG, "Action failed");
			// TODO notify user
			break;
		case ACTION_PERFORMED:
			Log.i(TAG, "Action succeeded");
			completeAction();
			gotoState(State.NORMAL);
			break;
		case PERFORM_ACTION:
			Log.i(TAG, "Performing action");
			runAction();
			break;
		}
	}

    private class CheckCardPresentTask extends AsyncTask<Tag, Void, TransmitResult> {
    	private final String TAG = "CheckCardPresentTask";
    	private Context context;

		protected CheckCardPresentTask(Context context) {
    		this.context = context;
    	}

		@Override
		protected TransmitResult doInBackground(Tag... arg0) {
			IsoDep tag = IsoDep.get(arg0[0]);

			// Make sure time-out is long enough (10 seconds)
			tag.setTimeout(10000);

			IdemixService is = new IdemixService(new IsoDepCardService(tag));
			TransmitResult result = null;

			try {
				is.open();
				is.close();
				result = new TransmitResult(TransmitResult.Result.SUCCESS);
			} catch (CardServiceException e) {
				Log.e(TAG, "Unable to select idemix applet");
				e.printStackTrace();
				return new TransmitResult(e);
			} finally {
				try {
					tag.close();
				} catch (IOException e) {
					Log.e(TAG, "Failed to close tag connection");
					e.printStackTrace();
				}
			}

			return result;
		}

		@Override
		protected void onPostExecute(TransmitResult tresult) {
			switch(tresult.getResult()) {
			case FAILURE:
				gotoState(State.WAITING_FOR_CARD);
				Log.i(TAG, "Cannot connect to card, proceeding to waiting for card");
				break;
			case SUCCESS:
				gotoState(State.CONFIRM_ACTION);
				break;
			}
		}
    }

    private class TransmitAPDUsTask extends AsyncTask<Tag, Void, TransmitResult> {
    	private final String TAG = "TransmitAPDUsTask";
    	private String pin;
    	private Context context;
    	private CardProgram cardProgram;

		protected TransmitAPDUsTask(Context context, String pin,
				CardProgram cardProgram) {
    		this.context = context;
    		this.pin = pin;
    		this.cardProgram = cardProgram;
    	}

		@Override
		protected TransmitResult doInBackground(Tag... arg0) {
			IsoDep tag = IsoDep.get(arg0[0]);

			// Make sure time-out is long enough (10 seconds)
			tag.setTimeout(10000);

			IdemixService is = new IdemixService(new IsoDepCardService(tag));
			TransmitResult result = null;

			try {
				is.open();
				is.sendPin(IdemixSmartcard.PIN_CARD, pin.getBytes());

				Log.i(TAG,"Performing requested actions now");
				result = cardProgram.run(is);
				is.close();
				Log.i(TAG, "Performed action succesfully!");
			} catch (Exception e) {
				Log.e(TAG, "Reading verification caused exception");
				e.printStackTrace();
				return new TransmitResult(e);
			} finally {
				try {
					tag.close();
				} catch (IOException e) {
					Log.e(TAG, "Failed to close tag connection");
					e.printStackTrace();
				}
			}

			return result;
		}

		@Override
		protected void onPostExecute(TransmitResult tresult) {
			switch(tresult.getResult()) {
			case SUCCESS:
				Log.i(TAG, "Complete succesfully, finishing task");
				gotoState(State.ACTION_PERFORMED);
				break;
			case FAILURE:
				Log.i(TAG, "Action failed, notifying user");
				gotoState(State.ACTION_FAILED);
				break;
			case INCORRECT_PIN:
				Log.i(TAG, "Pincode incorrect, notifying user");
				gotoState(State.NORMAL);
			}
		}
    }

	@Override
	public void onPINEntry(String pincode) {
		pinCode = pincode;
		gotoState(State.PERFORM_ACTION);
	}

	public void runAction() {
		CardProgram program = null;

		switch (currentAction) {
		case DELETE_CREDENTIAL:
			program = new CardProgram() {
				@Override
				public TransmitResult run(IdemixService is) throws CardServiceException {
					IdemixCredentials ic = new IdemixCredentials(is);
					ic.removeCredential(toBeDeleted);
					return new TransmitResult(TransmitResult.Result.SUCCESS);
				}
			};
			break;
		}
		new TransmitAPDUsTask(this, pinCode, program).execute(tag);
	}

	public void completeAction() {
		// This is run after action completes succesfully
		switch (currentAction) {
		case DELETE_CREDENTIAL:
			Log.i("CLA:completeAction", "Removing item from list");

			// Find deleted package
			int deletedIdx = -1;
			for(int i = 0 ; i <  credentials.size(); i++) {
				CredentialPackage cp = credentials.get(i);
				if(cp.getCredentialDescription().equals(toBeDeleted)) {
					deletedIdx = i;
				}
			}

			if(deletedIdx == -1) {
				Log.i("CLA:completeAction", "Failed to locate credential");
			} else {
				credentials.remove(deletedIdx);
			}

			if(credentials.size() > 0) {
				int new_idx = deletedIdx > 0 ? deletedIdx - 1 : 0;
				((MenuFragment) getSupportFragmentManager()
						.findFragmentById(R.id.credential_menu_fragment)).simulateListClick(new_idx);
			} else {
				InitFragment initFragment = new InitFragment();
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.credential_detail_container, initFragment)
						.commit();
			}
		}
	}

	@Override
	public void onPINCancel() {
		gotoState(State.NORMAL);
	}

	public void deleteCredential(CredentialDescription cd) {
		Log.i("blaat", "Delete credential called");
		toBeDeleted = cd;
		currentAction = Action.DELETE_CREDENTIAL;
		Log.i("blaat", "Will delete: " + cd.toString());

		gotoState(State.TEST_CARD_PRESENCE);
	}
}
