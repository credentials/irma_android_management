package org.irmacard.androidmanagement.dummy;

import org.irmacard.credentials.Attributes;

public class TestAttributes extends Attributes {
	@Override
	public byte[] get(String id) {
		return id.getBytes();
	}
}
