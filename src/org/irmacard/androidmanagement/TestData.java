package org.irmacard.androidmanagement;

import java.util.List;
import java.util.Vector;

import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.AttributeDescription;

public class TestData {
	static List<AttributeDescription> getAttributeDescriptions() {
		Vector<AttributeDescription> result = new Vector<AttributeDescription>();
		
		result.add(new AttributeDescription("Name", "Totally your own fully qualified personal name"));
		result.add(new AttributeDescription("City", "City that you live in"));
		result.add(new AttributeDescription("Age", "Your age in years"));
		
		return result;
	}
	
	static Attributes getAttributes() {
		Attributes result = new Attributes();

		result.add("Name", "Pietje Puk".getBytes());
		result.add("City", "Groningen".getBytes());
		result.add("Age", "25".getBytes());

		return result;
	}

}
