package org.irmacard.androidmanagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;
import org.irmacard.credentials.info.IssuerDescription;
import org.irmacard.credentials.info.TreeWalkerI;

import android.content.res.AssetManager;
import android.util.Log;

public class AndroidWalker implements TreeWalkerI {
	static final String IRMA_CORE = "irma_configuration/";
	
	DescriptionStore descriptionStore;
	AssetManager assetManager;
	
	public AndroidWalker(AssetManager assetManager) {
		this.assetManager = assetManager;
	}

	@Override
	public void parseConfiguration(DescriptionStore descriptionStore)
			throws InfoException {
		this.descriptionStore = descriptionStore;
		
		InputStream s;
		try {
			s = assetManager.open(IRMA_CORE + "android/issuers.txt");

			BufferedReader in = new BufferedReader(new InputStreamReader(s));
			String issuer = null;
			while ((issuer = in.readLine()) != null) {
				Log.i("filecontent", "Found issuer: " + issuer);
				String issuerDesc = IRMA_CORE + issuer + "/description.xml";
				
				Log.i("filecontent", "Parsing log of: " + issuerDesc);
				IssuerDescription id = new IssuerDescription(assetManager.open(issuerDesc));
				descriptionStore.addIssuerDescription(id);
				Log.i("filecontent", "Issuer added to result");
				
				tryProcessCredentials(issuer);
			}
		} catch (IOException e) {
			new InfoException(e,
					"Failed to read (from) android/issuers.txt file");
		}
	}
	
	private void tryProcessCredentials(String issuer) throws InfoException {
		String path = IRMA_CORE + issuer + "/Issues";
		
		try {
			Log.i("credential", "Examining path " + path);
			for(String cred : assetManager.list(path)) {
				String credentialSpec = path + "/" + cred + "/description.xml";
				Log.i("issuer+credential", "Reading credential " + credentialSpec);
				CredentialDescription cd = new CredentialDescription(assetManager.open(credentialSpec));
				descriptionStore.addCredentialDescription(cd);
				Log.i("credential", cd.toString());
			}
		} catch (IOException e) {
			new InfoException(e,
					"Failed to read credentials issued by " + issuer + ".");
		}
	}


}
