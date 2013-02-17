package org.irmacard.androidmanagement;

import java.util.List;
import java.util.Vector;

import net.sourceforge.scuba.smartcards.CardService;
import net.sourceforge.scuba.smartcards.IsoDepCardService;

import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.idemix.IdemixCredentials;
import org.irmacard.credentials.idemix.test.TestSetup;
import org.irmacard.credentials.idemix.util.CredentialInformation;
import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;

import service.IdemixSmartcard;
import service.IdemixService;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;

public class WaitingForCardActivity extends Activity {
	private NfcAdapter nfcA;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	
	private final String TAG = "WaitingForCard";
	
	private final int STATE_IDLE = 0;
	private final int STATE_CHECKING = 1;
	private int activityState = STATE_IDLE;
	
    public static final byte[] DEFAULT_PIN = {0x30, 0x30, 0x30, 0x30};
    public static final byte[] DEFAULT_MASTER_PIN = {0x30, 0x30, 0x30, 0x30, 0x30, 0x30};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waiting_for_card);
		
		// Make sure all configuration files can be found
	    AndroidWalker aw = new AndroidWalker(getResources().getAssets());
	    DescriptionStore.setTreeWalker(aw);
	    CredentialInformation.setTreeWalker(aw);
	    
	    try {
			DescriptionStore.getInstance();
		} catch (InfoException e) {
			// TODO Auto-generated catch block
			Log.e("error", "something went wrong");
			e.printStackTrace();
		}
		
        // NFC stuff
        nfcA = NfcAdapter.getDefaultAdapter(getApplicationContext());
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for all TECH based dispatches
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[] { tech };

        // Setup a tech list for all IsoDep cards
        mTechLists = new String[][] { new String[] { IsoDep.class.getName() } };
        
        // Set initial state
        activityState = STATE_IDLE;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_waiting_for_card, menu);
		return true;
	}
	
    @Override
    public void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }        
        if (nfcA != null) {
        	nfcA.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        }
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	if (nfcA != null) {
    		nfcA.disableForegroundDispatch(this);
    	}
    }
    
    public void processIntent(Intent intent) {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    	IsoDep tag = IsoDep.get(tagFromIntent);
    	if (tag != null) {
    		Log.i(TAG,"Found IsoDep tag!");
    		
    		// Make sure we're not already communicating with a card
    		if (activityState != STATE_CHECKING) {
    			activityState = STATE_CHECKING;
	    		new LoadCredentialsFromCardTask().execute(tag);
    		}
    	}    	
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        Log.i(TAG, "Discovered tag with intent: " + intent);
        setIntent(intent);
    }
    
    private class LoadCredentialsFromCardTask extends AsyncTask<IsoDep, Void, List<CredentialPackage>> {
    	private final String TAG = "LoadingTask";

		@Override
		protected List<CredentialPackage> doInBackground(IsoDep... arg0) {
			Vector<CredentialPackage> results = new Vector<CredentialPackage>();
			IsoDep tag = arg0[0];
			
			// Make sure time-out is long enough (10 seconds)
			tag.setTimeout(10000);

			IdemixService is = new IdemixService(new IsoDepCardService(tag));
			IdemixCredentials ic = new IdemixCredentials(is);
			
			try {
				ic.issuePrepare();
				is.sendPin(DEFAULT_PIN);
				is.sendPin(IdemixSmartcard.PIN_CARD, DEFAULT_MASTER_PIN);
				Log.i(TAG,"Retrieving credentials now"); 
				List<CredentialDescription> credentials = ic.getCredentials();
				for(CredentialDescription cd : credentials) {
					Log.i(TAG, "Found credential: " + cd);
					Attributes attr = ic.getAttributes(cd);
					Log.i(TAG, "With attributes: " + attr);
					results.add(new CredentialPackage(cd, attr));
				}
				is.close();
				tag.close();
				
				Log.i(TAG, "All attributes read!");
			} catch (Exception e) {
				Log.e(TAG, "Reading verification caused exception");
				e.printStackTrace();
				return null;
			}
			
			return results;
		}
		
		@Override
		protected void onPostExecute(List<CredentialPackage> verification) {
			Log.i(TAG, "On post execute now with nice results");
			activityState = STATE_IDLE;
		}
    }

}
