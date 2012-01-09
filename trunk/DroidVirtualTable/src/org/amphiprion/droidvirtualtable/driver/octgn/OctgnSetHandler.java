package org.amphiprion.droidvirtualtable.driver.octgn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.R;
import org.amphiprion.droidvirtualtable.dao.CardDao;
import org.amphiprion.droidvirtualtable.dao.CardPropertyDao;
import org.amphiprion.droidvirtualtable.dao.CardValueDao;
import org.amphiprion.droidvirtualtable.dao.GameSetDao;
import org.amphiprion.droidvirtualtable.dao.MarkerDao;
import org.amphiprion.droidvirtualtable.entity.Card;
import org.amphiprion.droidvirtualtable.entity.CardDefinition;
import org.amphiprion.droidvirtualtable.entity.CardProperty;
import org.amphiprion.droidvirtualtable.entity.CardValue;
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.entity.GameSet;
import org.amphiprion.droidvirtualtable.entity.Marker;
import org.amphiprion.droidvirtualtable.entity.Marker.Shape;
import org.amphiprion.droidvirtualtable.util.FileUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

public class OctgnSetHandler {
	private Game game;
	private GameSet set;
	private Context context;
	private ImportOctgnSetTask task;
	private HashMap<String, String> relationships;
	private HashMap<String, CardProperty> cardProperties;

	public OctgnSetHandler(Context context, Game game, ImportOctgnSetTask task, HashMap<String, String> relationships) {
		this.context = context;
		this.task = task;
		this.game = game;
		this.relationships = relationships;

		cardProperties = new HashMap<String, CardProperty>();
	}

	/**
	 * 
	 * @param gamesDir
	 *            the directory for all sets
	 *            "DroidVirtualTable/games/<game_id>/sets"
	 * @param currentDir
	 *            the directory were the game pack have been unzipped
	 *            "DroidVirtualTable/import/sets/current"
	 * @param file
	 *            the set xml file
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public GameSet parse(File setsDir, File currentDir, File file) throws IOException, SAXException, ParserConfigurationException {

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();

		SaxHandler myXMLHandler = new SaxHandler(setsDir, currentDir);
		xr.setContentHandler(myXMLHandler);
		FileInputStream fis = new FileInputStream(file);
		xr.parse(new InputSource(fis));
		fis.close();

		return set;

	}

	class SaxHandler extends DefaultHandler {
		private File setsDir;
		/**
		 * The root directory of this set.
		 * "DroidVirtualTable/games/<game_id>/sets/<set_id>"
		 */
		private File rootDir;
		private File currentDir;
		private String xmlPath;
		private int count = 0;
		private Card card;

		public SaxHandler(File setsDir, File currentDir) {
			this.setsDir = setsDir;
			this.currentDir = currentDir;
		}

		@Override
		public void startDocument() throws SAXException {
			xmlPath = "";
			count = 0;
			cardProperties.clear();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (localName.equals("set")) {
				task.publishProgress(R.string.import_set_step_set);

				String name = attributes.getValue("name");
				String id = attributes.getValue("id").toLowerCase();
				String gameId = attributes.getValue("gameId").toLowerCase();
				if (!gameId.equals(game.getId())) {
					throw new RuntimeException("This Set is not linked to " + game.getName());
				}
				boolean exists = GameSetDao.getInstance(context).exists(id);
				set = new GameSet(id);
				set.setGame(game);
				rootDir = new File(setsDir, id);
				rootDir.mkdirs();
				if (!exists) {
					set.setState(DbState.NEW);
				}
				set.setName(name);
				set.setImageName("/set.png");
				File img = new File(rootDir, set.getImageName());
				if (!img.exists()) {
					FileUtil.copy(OctgnSetHandler.class.getResourceAsStream("/images/no_game_image.png"), img);
				}
				GameSetDao.getInstance(context).persist(set);
				Log.d(ApplicationConstants.PACKAGE, "Set:" + set.getName() + "  state:" + set.getState());
			} else if (localName.equals("card")) {
				count++;
				task.publishProgress(R.string.import_set_step_card, count);

				String id = attributes.getValue("id");
				card = CardDao.getInstance(context).getCard(id.toLowerCase());
				if (card == null) {
					card = new Card(id.toLowerCase());
					card.setState(DbState.NEW);
				}
				card.setGameSet(set);
				card.setName(attributes.getValue("name"));
				String key = "C" + id.replaceAll("-", "");
				String imageName = relationships.get(key);
				card.setImageName(imageName);
				File destFile = new File(rootDir, imageName);
				if (!destFile.exists()) {
					String d = imageName.substring(0, imageName.lastIndexOf("/"));
					File destDir = new File(rootDir, d);
					destDir.mkdirs();
					File source = new File(currentDir, imageName);
					if (source.exists()) {
						FileUtil.copy(source, destFile);
					}
				}
				card.setDefinition(new CardDefinition(game.getId()));

				CardDao.getInstance(context).persist(card);

				// Log.d(ApplicationConstants.PACKAGE, "Card:" + card.getName()
				// + "  img=" + card.getImageName() + "   state:" +
				// card.getState());
				CardValueDao.getInstance(context).deleteAll(card.getId());
			} else if (localName.equals("property")) {

				String name = attributes.getValue("name");
				CardProperty prop = cardProperties.get(name);
				if (prop == null) {
					prop = CardPropertyDao.getInstance(context).getCardProperty(game.getId(), name);
					cardProperties.put(name, prop);
				}
				CardValue value = new CardValue();
				value.setProperty(prop);
				value.setCard(card);
				value.setValue(attributes.getValue("value"));

				// Log.d(ApplicationConstants.PACKAGE, "   prop name:" + name +
				// "   value=" + value.getValue() + "   state:" +
				// value.getState());
				CardValueDao.getInstance(context).persist(value);

			} else if (localName.equals("marker")) {
				task.publishProgress(R.string.import_set_step_marker);

				String id = attributes.getValue("id");
				String name = attributes.getValue("name");
				Marker marker = MarkerDao.getInstance(context).getMarker(game.getId(), name);
				if (marker == null) {
					marker = new Marker();
				}
				marker.setGame(game);
				marker.setName(name);
				String key = "M" + id.replaceAll("-", "");
				String imageName = relationships.get(key);
				marker.setImageName(imageName);
				File destFile = new File(rootDir, imageName);
				if (!destFile.exists()) {
					String d = imageName.substring(0, imageName.lastIndexOf("/"));
					File destDir = new File(rootDir, d);
					destDir.mkdirs();
					File source = new File(currentDir, imageName);
					if (source.exists()) {
						FileUtil.copy(source, destFile);
					}
				}
				marker.setShape(Shape.RECTANGLE);
				marker.setWidth(15);
				marker.setHeight(15);

				MarkerDao.getInstance(context).persist(marker);

				Log.d(ApplicationConstants.PACKAGE, "Marker:" + marker.getName() + "  img=" + marker.getImageName() + "   state:" + marker.getState());
			}

			xmlPath += "/" + localName;
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			xmlPath = xmlPath.substring(0, xmlPath.lastIndexOf("/"));
		}

		@Override
		public void endDocument() throws SAXException {
			cardProperties.clear();
		}
	}
}
