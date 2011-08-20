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
import org.amphiprion.droidvirtualtable.dao.ActionDao;
import org.amphiprion.droidvirtualtable.dao.CardDefinitionDao;
import org.amphiprion.droidvirtualtable.dao.CardPropertyDao;
import org.amphiprion.droidvirtualtable.dao.CounterDao;
import org.amphiprion.droidvirtualtable.dao.GameDao;
import org.amphiprion.droidvirtualtable.dao.GroupDao;
import org.amphiprion.droidvirtualtable.dao.ScriptDao;
import org.amphiprion.droidvirtualtable.dao.SectionDao;
import org.amphiprion.droidvirtualtable.entity.Action;
import org.amphiprion.droidvirtualtable.entity.CardDefinition;
import org.amphiprion.droidvirtualtable.entity.CardProperty;
import org.amphiprion.droidvirtualtable.entity.Counter;
import org.amphiprion.droidvirtualtable.entity.Entity.DbState;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.entity.Group;
import org.amphiprion.droidvirtualtable.entity.Group.Type;
import org.amphiprion.droidvirtualtable.entity.Group.Visibility;
import org.amphiprion.droidvirtualtable.entity.Script;
import org.amphiprion.droidvirtualtable.entity.Section;
import org.amphiprion.droidvirtualtable.util.FileUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

public class OctgnGameHandler {
	private Game game;
	private Context context;
	private ImportOctgnGameTask task;
	private HashMap<String, String> relationships;

	public OctgnGameHandler(Context context, ImportOctgnGameTask task, HashMap<String, String> relationships) {
		this.context = context;
		this.task = task;
		this.relationships = relationships;
	}

	/**
	 * 
	 * @param gamesDir
	 *            the directory for all games "DroidVirtualTable/games"
	 * @param currentDir
	 *            the directory were the game pack have been unzipped
	 *            "DroidVirtualTable/import/games/current"
	 * @param file
	 *            the game xml file
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public Game parse(File gamesDir, File currentDir, File file) throws IOException, SAXException, ParserConfigurationException {

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();

		SaxHandler myXMLHandler = new SaxHandler(gamesDir, currentDir);
		xr.setContentHandler(myXMLHandler);
		FileInputStream fis = new FileInputStream(file);
		xr.parse(new InputSource(fis));
		fis.close();

		return game;

	}

	class SaxHandler extends DefaultHandler {
		private File gamesDir;
		/** The root directory of this "game. DroidVirtualTable/games/<game_id>" */
		private File rootDir;
		private File currentDir;
		private String xmlPath;
		private CardDefinition def;
		private Group currentGroup;

		public SaxHandler(File gamesDir, File currentDir) {
			this.gamesDir = gamesDir;
			this.currentDir = currentDir;
		}

		@Override
		public void startDocument() throws SAXException {
			xmlPath = "";
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (localName.equals("game")) {
				task.publishProgress(R.string.import_game_step_game);

				String name = attributes.getValue("name");
				String id = attributes.getValue("id").toLowerCase();
				boolean exists = GameDao.getInstance(context).exists(id);
				game = new Game(id);
				rootDir = new File(gamesDir, id);
				rootDir.mkdirs();
				if (!exists) {
					game.setState(DbState.NEW);
				}
				game.setName(name);
				game.setImageName("/game.png");
				File img = new File(rootDir, game.getImageName());
				if (!img.exists()) {
					FileUtil.copy(OctgnGameHandler.class.getResourceAsStream("/images/no_game_image.png"), img);
				}
				GameDao.getInstance(context).persist(game);
				Log.d(ApplicationConstants.PACKAGE, "Game:" + game.getName() + "  state:" + game.getState());
			} else if (localName.equals("card")) {
				task.publishProgress(R.string.import_game_step_card);

				def = new CardDefinition("1");
				boolean exists = CardDefinitionDao.getInstance(context).exists(def.getId());
				if (!exists) {
					def.setState(DbState.NEW);
				}
				def.setFrontImageName(relationships.get(attributes.getValue("front")));
				File destFile = new File(rootDir, def.getFrontImageName());
				if (!destFile.exists()) {
					String d = def.getFrontImageName().substring(0, def.getFrontImageName().lastIndexOf("/"));
					File destDir = new File(rootDir, d);
					destDir.mkdirs();
					File source = new File(currentDir, def.getFrontImageName());
					if (source.exists()) {
						FileUtil.copy(source, destFile);
					}
				}

				def.setBackImageName(relationships.get(attributes.getValue("back")));
				destFile = new File(rootDir, def.getBackImageName());
				if (!destFile.exists()) {
					String d = def.getBackImageName().substring(0, def.getBackImageName().lastIndexOf("/"));
					File destDir = new File(rootDir, d);
					destDir.mkdirs();
					File source = new File(currentDir, def.getBackImageName());
					if (source.exists()) {
						FileUtil.copy(source, destFile);
					}
				}

				def.setDefaultDefinition(true);
				def.setGame(game);
				def.setName("Default");
				def.setWidth(Integer.parseInt(attributes.getValue("width")));
				def.setHeight(Integer.parseInt(attributes.getValue("height")));
				CardDefinitionDao.getInstance(context).persist(def);

				Log.d(ApplicationConstants.PACKAGE, "Card Def:" + def.getName() + "  state:" + def.getState());
			} else if (localName.equals("property") && "/game/card".equals(xmlPath)) {
				String name = attributes.getValue("name");
				CardProperty prop = null;
				prop = CardPropertyDao.getInstance(context).getCardProperty(def.getId(), name);
				if (prop == null) {
					prop = new CardProperty();
				}

				prop.setDefinition(def);
				prop.setName(name);
				prop.setType(attributes.getValue("type"));

				CardPropertyDao.getInstance(context).persist(prop);

				Log.d(ApplicationConstants.PACKAGE, "Card Prop:" + prop.getName() + "  state:" + prop.getState());
			} else if (localName.equals("table")) {
				collectGroupTag(Type.TABLE, attributes);
			} else if (localName.equals("hand")) {
				collectGroupTag(Type.HAND, attributes);
			} else if (localName.equals("group")) {
				collectGroupTag(Type.PILE, attributes);
			} else if (localName.equals("groupaction")) {
				collectActionTag(Action.Type.GROUP, attributes);
			} else if (localName.equals("cardaction")) {
				collectActionTag(Action.Type.CARD, attributes);
			} else if (localName.equals("counter") && "/game/player".equals(xmlPath)) {
				String name = attributes.getValue("name");
				Counter prop = null;
				prop = CounterDao.getInstance(context).getCounter(game.getId(), name);
				if (prop == null) {
					prop = new Counter();
				}

				prop.setGame(game);
				prop.setName(name);

				String defaultValue = attributes.getValue("default");
				prop.setDefaultValue(defaultValue != null ? Integer.parseInt(defaultValue) : 0);

				prop.setImageName(relationships.get(attributes.getValue("icon")));
				File destFile = new File(rootDir, prop.getImageName());
				if (!destFile.exists()) {
					String d = prop.getImageName().substring(0, prop.getImageName().lastIndexOf("/"));
					File destDir = new File(rootDir, d);
					destDir.mkdirs();
					File source = new File(currentDir, prop.getImageName());
					if (source.exists()) {
						FileUtil.copy(source, destFile);
					}
				}

				prop.setWidth(32);
				prop.setHeight(32);
				CounterDao.getInstance(context).persist(prop);
				Log.d(ApplicationConstants.PACKAGE, "Counter:" + prop.getName() + "  state:" + prop.getState());
			} else if (localName.equals("section") && "/game/deck".equals(xmlPath)) {
				String name = attributes.getValue("name");
				String groupName = attributes.getValue("group");

				Section prop = null;
				prop = SectionDao.getInstance(context).getSection(game.getId(), name);
				if (prop == null) {
					prop = new Section();
				}

				prop.setGame(game);
				prop.setName(name);

				Group startupGroup = GroupDao.getInstance(context).getGroup(game.getId(), groupName);
				prop.setStartupGroup(startupGroup);

				SectionDao.getInstance(context).persist(prop);

				Log.d(ApplicationConstants.PACKAGE, "Section:" + prop.getName() + "  startup:" + startupGroup.getName() + "   state:" + prop.getState());
			} else if (localName.equals("script") && "/game/scripts".equals(xmlPath)) {
				String filename = relationships.get(attributes.getValue("src"));
				Script prop = null;
				prop = ScriptDao.getInstance(context).getScript(game.getId(), filename);
				if (prop == null) {
					prop = new Script();
				}
				prop.setGame(game);
				prop.setFilename(filename);
				File destFile = new File(rootDir, filename);
				if (!destFile.exists()) {
					String d = filename.substring(0, filename.lastIndexOf("/"));
					File destDir = new File(rootDir, d);
					destDir.mkdirs();
					File source = new File(currentDir, filename);
					if (source.exists()) {
						FileUtil.copy(source, destFile);
					}
				}

				ScriptDao.getInstance(context).persist(prop);

				Log.d(ApplicationConstants.PACKAGE, "Script:" + prop.getFilename() + "   state:" + prop.getState());
			}

			xmlPath += "/" + localName;
		}

		private void collectActionTag(Action.Type type, Attributes attributes) {
			String name = attributes.getValue("menu");
			Action action = null;
			action = ActionDao.getInstance(context).getAction(currentGroup.getId(), name);
			if (action == null) {
				action = new Action();
			}

			action.setGroup(currentGroup);
			action.setName(name);
			action.setCommand(attributes.getValue("execute"));
			if (action.getCommand() == null) {
				action.setCommand(attributes.getValue("batchExecute"));
			}
			action.setDefaultAction("true".equals(attributes.getValue("default")));
			action.setType(org.amphiprion.droidvirtualtable.entity.Action.Type.GROUP);
			ActionDao.getInstance(context).persist(action);

			Log.d(ApplicationConstants.PACKAGE, "Action:" + action.getName() + "  grp=" + action.getGroup().getName() + "  state:" + action.getState());
		}

		private void collectGroupTag(Group.Type type, Attributes attributes) {
			task.publishProgress(R.string.import_game_step_group);

			String name = attributes.getValue("name");
			Group group = null;
			group = GroupDao.getInstance(context).getGroup(game.getId(), name);
			if (group == null) {
				group = new Group();
			}
			group.setName(name);
			group.setGame(game);
			group.setVisibility(Visibility.valueOf(attributes.getValue("visibility")));

			if (type == Type.TABLE) {
				group.setImageName(relationships.get(attributes.getValue("background")));
			} else {
				group.setImageName(relationships.get(attributes.getValue("icon")));
			}
			File destFile = new File(rootDir, group.getImageName());
			if (!destFile.exists()) {
				String d = group.getImageName().substring(0, group.getImageName().lastIndexOf("/"));
				File destDir = new File(rootDir, d);
				destDir.mkdirs();
				File source = new File(currentDir, group.getImageName());
				if (source.exists()) {
					FileUtil.copy(source, destFile);
				}
			}
			group.setType(type);
			group.setWidth(Integer.parseInt(attributes.getValue("width")));
			group.setHeight(Integer.parseInt(attributes.getValue("height")));

			GroupDao.getInstance(context).persist(group);

			currentGroup = group;

			Log.d(ApplicationConstants.PACKAGE, "Group:" + group.getName() + "  state:" + group.getState());
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			xmlPath = xmlPath.substring(0, xmlPath.lastIndexOf("/"));
		}
	}
}
