package org.irmacard.androidmanagement;

import java.io.Serializable;

import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.CredentialDescription;

public class CredentialPackage implements Serializable{
	private static final long serialVersionUID = 6538230152019005462L;
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
	
	public String toString() {
		return "###" + credDesc.toString() + " ## " + attributes.toString() + "###";
	}
}
