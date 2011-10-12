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
package org.amphiprion.droidvirtualtable.view;

import org.amphiprion.droidvirtualtable.R;
import org.amphiprion.droidvirtualtable.dto.Criteria;
import org.amphiprion.droidvirtualtable.dto.Criteria.Operator;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * View used to display a criteria for card search.
 * 
 * @author amphiprion
 * 
 */
public class CriteriaSummaryView extends LinearLayout {
	private Criteria criteria;
	private ImageView deleteButton;
	private EditText secondTxt;
	private TextView andTxt;

	/**
	 * Construct an deck view.
	 * 
	 * @param context
	 *            the context
	 * @param deck
	 *            the entity
	 */
	public CriteriaSummaryView(Context context, Criteria criteria) {
		super(context);
		this.criteria = criteria;
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setLayoutParams(lp);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.list_item_black_background_states));

		addView(createName());

		addView(createOperantor());

		addView(createFirstValue());
		addView(createAnd());
		addView(createSecondValue());

		addView(createDeleteButton());

		update();
	}

	/**
	 * @return the deck
	 */
	public Criteria getCriteria() {
		return criteria;
	}

	/**
	 * Create the collection icon view.
	 * 
	 * @return the view
	 */
	private View createOperantor() {
		final Spinner tv = new Spinner(getContext());
		String[] ops = getContext().getResources().getStringArray(R.array.operators);
		tv.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ops));
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		// imglp.gravity = Gravity.CENTER_VERTICAL;
		// imglp.rightMargin = 5;
		tv.setLayoutParams(imglp);

		tv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				criteria.setOperator(Operator.values()[tv.getSelectedItemPosition()]);
				update();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		tv.setSelection(1);

		return tv;
	}

	/**
	 * Create the collection icon view.
	 * 
	 * @return the view
	 */
	private View createName() {
		TextView tv = new TextView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		// imglp.gravity = Gravity.CENTER_VERTICAL;
		// imglp.rightMargin = 5;
		tv.setLayoutParams(imglp);
		tv.setText(criteria.getName());
		return tv;
	}

	private View createFirstValue() {
		EditText tv = new EditText(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		// imglp.gravity = Gravity.CENTER_VERTICAL;
		// imglp.rightMargin = 5;
		tv.setLayoutParams(imglp);
		tv.setText(criteria.getFirstValue());
		tv.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				criteria.setFirstValue(s.toString());
			}
		});

		return tv;
	}

	private View createAnd() {
		TextView tv = new TextView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		// imglp.gravity = Gravity.CENTER_VERTICAL;
		// imglp.rightMargin = 5;
		tv.setLayoutParams(imglp);
		tv.setText(" and ");
		andTxt = tv;
		return tv;
	}

	private View createSecondValue() {
		EditText tv = new EditText(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		// imglp.gravity = Gravity.CENTER_VERTICAL;
		// imglp.rightMargin = 5;
		tv.setLayoutParams(imglp);
		tv.setText(criteria.getFirstValue());

		tv.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				criteria.setSecondValue(s.toString());
			}
		});

		secondTxt = tv;
		return tv;
	}

	public void update() {
		if (criteria.getOperator() == Operator.between) {
			secondTxt.setVisibility(VISIBLE);
			andTxt.setVisibility(VISIBLE);
		} else {
			secondTxt.setVisibility(GONE);
			andTxt.setVisibility(GONE);
		}

	}

	private View createDeleteButton() {
		ImageView tv = new ImageView(getContext());
		LayoutParams imglp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		imglp.gravity = Gravity.CENTER_VERTICAL;
		// imglp.rightMargin = 5;
		tv.setLayoutParams(imglp);
		tv.setImageDrawable(getContext().getResources().getDrawable(R.drawable.delete_48));

		deleteButton = tv;
		return tv;
	}

	public ImageView getDeleteButton() {
		return deleteButton;
	}
}
