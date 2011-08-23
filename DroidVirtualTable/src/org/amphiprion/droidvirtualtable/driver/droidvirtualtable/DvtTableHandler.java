package org.amphiprion.droidvirtualtable.driver.droidvirtualtable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.amphiprion.droidvirtualtable.ApplicationConstants;
import org.amphiprion.droidvirtualtable.R;
import org.amphiprion.droidvirtualtable.dao.TableDao;
import org.amphiprion.droidvirtualtable.entity.Game;
import org.amphiprion.droidvirtualtable.entity.Table;
import org.amphiprion.droidvirtualtable.util.FileUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

public class DvtTableHandler {
	private Game game;
	private Table table;
	private Context context;
	private ImportDvtTableTask task;

	public DvtTableHandler(Context context, Game game, ImportDvtTableTask task) {
		this.context = context;
		this.task = task;
		this.game = game;
	}

	/**
	 * 
	 * @param tablesDir
	 *            the directory for all tables
	 *            "DroidVirtualTable/games/<game_id>/tables"
	 * @param currentDir
	 *            the directory were the game pack have been unzipped
	 *            "DroidVirtualTable/import/tables/current"
	 * @param file
	 *            the set xml file
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public Table parse(File tablesDir, File currentDir, File file) throws IOException, SAXException, ParserConfigurationException {

		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();

		SaxHandler myXMLHandler = new SaxHandler(tablesDir, currentDir);
		xr.setContentHandler(myXMLHandler);
		FileInputStream fis = new FileInputStream(file);
		xr.parse(new InputSource(fis));
		fis.close();
		return table;

	}

	class SaxHandler extends DefaultHandler {
		private File tablesDir;
		/**
		 * The root directory of this table.
		 * "DroidVirtualTable/games/<game_id>/tables/<table_id>"
		 */
		private File rootDir;
		private File currentDir;
		private String xmlPath;
		private int count = 0;

		public SaxHandler(File tablesDir, File currentDir) {
			this.tablesDir = tablesDir;
			this.currentDir = currentDir;
		}

		@Override
		public void startDocument() throws SAXException {
			xmlPath = "";
			count = 0;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (localName.equals("table")) {
				task.publishProgress(R.string.import_table_step_table);

				String name = attributes.getValue("name");
				table = TableDao.getInstance(context).getTable(game.getId(), name);
				if (table == null) {
					table = new Table();
				}
				table.setGame(game);
				table.setMaxLocation(0);
				rootDir = new File(tablesDir, table.getId());
				rootDir.mkdirs();
				table.setName(name);
				TableDao.getInstance(context).persist(table);

				File[] files = currentDir.listFiles();
				for (File file : files) {
					count++;
					task.publishProgress(R.string.import_table_step_copy, count);
					FileUtil.copy(file, new File(rootDir, file.getName()));
				}
				Log.d(ApplicationConstants.PACKAGE, "Table:" + table.getName() + "  state:" + table.getState());
			} else if (localName.equals("location")) {
				table.setMaxLocation(table.getMaxLocation() + 1);
				task.publishProgress(R.string.import_table_step_location, table.getMaxLocation());

				Log.d(ApplicationConstants.PACKAGE, "   Location:" + table.getMaxLocation());
			}

			xmlPath += "/" + localName;
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			xmlPath = xmlPath.substring(0, xmlPath.lastIndexOf("/"));
		}

		@Override
		public void endDocument() throws SAXException {
			TableDao.getInstance(context).persist(table);
		}
	}
}
