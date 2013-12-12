package net.elbandi.hashfaster.adapters;

import net.elbandi.hashfaster.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

/**
 * Custom navigation list adapter.
 */
public class NavigationListAdapter extends BaseAdapter implements SpinnerAdapter {
	/**
	 * Members
	 */
	private LayoutInflater m_layoutInflater;
	private TypedArray m_logos;
	private TypedArray m_titles;
	private TypedArray m_subtitles;

	/**
	 * Constructor
	 */
	public NavigationListAdapter(Context p_context, TypedArray p_logos, TypedArray p_titles, TypedArray p_subtitles) {
		m_layoutInflater = LayoutInflater.from(p_context);
		m_logos = p_logos;
		m_titles = p_titles;
		m_subtitles = p_subtitles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return m_titles.length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int p_position) {
		return p_position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int p_position) {
		return m_titles.getResourceId(p_position, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int p_position, View p_convertView, ViewGroup p_parent) {
		/*
		 * View...
		 */
		View view = p_convertView;
		if (view == null) {
			view = m_layoutInflater.inflate(R.layout.navigation_list_item, p_parent, false);
		}

		/*
		 * Display...
		 */
		// Title...
		TextView tv_title = (TextView) view.findViewById(R.id.title);
		tv_title.setText(m_titles.getString(p_position));

		// Subtitle...
		TextView tv_subtitle = ((TextView) view.findViewById(R.id.subtitle));
		tv_subtitle.setText(m_subtitles.getString(p_position));
		tv_subtitle.setVisibility("".equals(tv_subtitle.getText()) ? View.GONE : View.VISIBLE);

		/*
		 * Return...
		 */
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getDropDownView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int p_position, View p_convertView, ViewGroup p_parent) {
		/*
		 * View...
		 */
		View view = p_convertView;
		if (view == null) {
			view = m_layoutInflater.inflate(R.layout.navigation_list_dropdown_item, p_parent, false);
		}

		/*
		 * Display...
		 */

		// Icon...
		ImageView iv_logo = (ImageView) view.findViewById(R.id.logo);
		iv_logo.setImageDrawable(m_logos.getDrawable(p_position));

		// Title...
		TextView tv_title = (TextView) view.findViewById(R.id.title);
		tv_title.setText(m_titles.getString(p_position));

		// Subtitle...
		TextView tv_subtitle = ((TextView) view.findViewById(R.id.subtitle));
		tv_subtitle.setText(m_subtitles.getString(p_position));
		tv_subtitle.setVisibility("".equals(tv_subtitle.getText()) ? View.GONE : View.VISIBLE);

		/*
		 * Return...
		 */
		return view;
	}
}