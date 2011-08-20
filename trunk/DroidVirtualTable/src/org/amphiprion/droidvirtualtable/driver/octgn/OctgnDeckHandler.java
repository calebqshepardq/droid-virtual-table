package org.amphiprion.droidvirtualtable.driver.octgn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.R;
import org.amphiprion.droidvirtualtable.dao.CardDao;
import org.amphiprion.droidvirtualtable.dao.DeckContentDao;
import org.amphiprion.droidvirtualtable.dao.DeckDao;
import org.amphiprion.droidvirtualtable.dao.SectionDao;
import org.amphiprion.droidvirtualtable.entity.Card;
import org.amphiprion.droidvirtualtable.entity.Deck;
import org.amphiprion.droidvirtualtable.entity.DeckContent;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.entity.Section;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

public class OctgnDeckHandler {
	private Game game;
	private Deck deck;
	private Context context;
	private ImportOctgnDeckTask task;

	public OctgnDeckHandler(Context context, Game game, ImportOctgnDeckTask task) {
		this.context = context;
		this.task = task;
		this.game = game;
	}

	/**
	 * 
	 * @param file
	 *            the deck xml file
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public Deck parse(File file) throws IOException, SAXException, ParserConfigurationException {

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();

		String filename = file.getName();
		String deckName = filename.substring(0, filename.lastIndexOf("."));

		SaxHandler myXMLHandler = new SaxHandler(deckName);
		xr.setContentHandler(myXMLHandler);
		FileInputStream fis = new FileInputStream(file);
		xr.parse(new InputSource(fis));
		fis.close();

		return deck;

	}

	class SaxHandler extends DefaultHandler {

		private String xmlPath;
		private int count = 0;
		private String deckName;
		private Section currentSection;

		public SaxHandler(String deckName) {
			this.deckName = deckName;
		}

		@Override
		public void startDocument() throws SAXException {
			xmlPath = "";
			count = 0;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (localName.equals("deck")) {
				task.publishProgress(R.string.import_deck_step_deck);

				String name = deckName;
				String gameId = attributes.getValue("game").toLowerCase();
				if (!gameId.equals(game.getId())) {
					throw new RuntimeException("This Deck is not linked to " + game.getName());
				}
				deck = DeckDao.getInstance(context).getDeck(game.getId(), name);
				if (deck == null) {
					deck = new Deck();
				}
				deck.setName(name);
				deck.setGame(game);
				DeckDao.getInstance(context).persist(deck);

				Log.d(ApplicationConstants.PACKAGE, "Deck:" + deck.getName() + "  state:" + deck.getState());
			} else if (localName.equals("section")) {
				String name = attributes.getValue("name");
				currentSection = SectionDao.getInstance(context).getSection(game.getId(), name);
			} else if (localName.equals("card")) {
				task.publishProgress(R.string.import_deck_step_card, count);
				count++;

				String cardId = attributes.getValue("id").toLowerCase();
				int quantity = Integer.parseInt(attributes.getValue("qty"));

				Card card = CardDao.getInstance(context).getCard(cardId);

				DeckContent deckContent = DeckContentDao.getInstance(context).getDeckContent(deck.getId(), currentSection.getId(), card.getName());
				if (deckContent == null) {
					deckContent = new DeckContent();
				}
				deckContent.setDeck(deck);
				deckContent.setSection(currentSection);
				deckContent.setName(card.getName());
				deckContent.setCard(card);
				deckContent.setQuantity(quantity);

				DeckContentDao.getInstance(context).persist(deckContent);

				Log.d(ApplicationConstants.PACKAGE, "Card:" + card.getName() + "  quantity=" + quantity + "   section:" + currentSection.getName() + "   state:" + card.getState());
			}

			xmlPath += "/" + localName;
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			xmlPath = xmlPath.substring(0, xmlPath.lastIndexOf("/"));
		}

	}
}
