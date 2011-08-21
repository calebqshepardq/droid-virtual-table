package org.amphiprion.droidvirtualtable.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameDeck implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

	private List<DeckSection> sections;

	public GameDeck() {
		sections = new ArrayList<DeckSection>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DeckSection> getSections() {
		return sections;
	}

	public DeckSection getSection(String name) {
		for (DeckSection section : sections) {
			if (name.equals(section)) {
				return section;
			}
		}
		return null;
	}
}
