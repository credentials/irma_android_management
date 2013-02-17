package org.irmacard.androidmanagement;

import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.CredentialDescription;

public class CredentialPackage {
	private CredentialDescription credDesc;
	private Attributes attributes;
	
	public CredentialPackage(CredentialDescription credDesc, Attributes attributes) {
		this.credDesc = credDesc;
		this.attributes = attributes;
	}

	public CredentialDescription getCredentialDescription() {
		return credDesc;
	}

	public Attributes getAttributes() {
		return attributes;
	}
}
