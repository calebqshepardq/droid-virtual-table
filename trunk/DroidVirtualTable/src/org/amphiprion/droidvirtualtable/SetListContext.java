/*
 * @copyright 2010 Gerald Jacobson
 * @license GNU General Public License
 * 
 * This file is part of My Accounts.
 *
 * My Accounts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * My Accounts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My Accounts.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.amphiprion.droidvirtualtable;

import java.util.List;

import org.amphiprion.droidvirtualtable.entity.GameSet;
import org.amphiprion.droidvirtualtable.task.LoadSetsTask;
import org.amphiprion.droidvirtualtable.view.MyScrollView;

/**
 * This class is the context of the game list view.
 * 
 * @author amphiprion
 * 
 */
public class SetListContext {
	public static final int PAGE_SIZE = 20;

	public int loadedPage;
	public List<GameSet> sets;
	public MyScrollView scrollView;
	public GameSet current;
	public boolean allLoaded;
	public boolean loading;
	public LoadSetsTask task;

}
