package org.amphiprion.droidvirtualtable.driver.octgn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class OctgnRelationShipHandler {
	private HashMap<String, String> relationShips;

	public OctgnRelationShipHandler() {
		relationShips = new HashMap<String, String>();
	}

	public HashMap<String, String> parse(File file) throws IOException, SAXException, ParserConfigurationException {
		relationShips.clear();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();

		SaxHandler myXMLHandler = new SaxHandler();
		xr.setContentHandler(myXMLHandler);
		FileInputStream fis = new FileInputStream(file);
		xr.parse(new InputSource(fis));
		fis.close();

		return relationShips;
	}

	class SaxHandler extends DefaultHandler {
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (localName.equals("Relationship")) {
				String target = attributes.getValue("Target");
				String id = attributes.getValue("Id");
				relationShips.put(id, target);
			}
		}
	}
}
