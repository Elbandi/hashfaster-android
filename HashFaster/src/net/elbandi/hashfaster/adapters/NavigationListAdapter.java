package net.elbandi.hashfaster.adapters;

import net.elbandi.hashfaster.network.R;
import net.elbandi.hashfaster.managers.PoolManager;

import android.content.Context;
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

	/**
	 * Constructor
	 */
	public NavigationListAdapter(Context p_context) {
		m_layoutInflater = LayoutInflater.from(p_context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return PoolManager.getPoolCount();
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
		return p_position;//m_titles.getResourceId(p_position, 0);
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
		String key = PoolManager.getPoolKey(p_position);

		/*
		 * Display...
		 */
		// Title...
		TextView tv_title = (TextView) view.findViewById(R.id.title);
		tv_title.setText(PoolManager.getTitles(key));

		// Subtitle...
		TextView tv_subtitle = ((TextView) view.findViewById(R.id.subtitle));
		tv_subtitle.setText(PoolManager.getSubTitles(key));
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

		String key = PoolManager.getPoolKey(p_position);
		/*
		 * Display...
		 */

		// Icon...
		ImageView iv_logo = (ImageView) view.findViewById(R.id.logo);
		iv_logo.setImageDrawable(PoolManager.getLogo(key));

		// Title...
		TextView tv_title = (TextView) view.findViewById(R.id.title);
		tv_title.setText(PoolManager.getTitles(key));

		// Subtitle...
		TextView tv_subtitle = ((TextView) view.findViewById(R.id.subtitle));
		tv_subtitle.setText(PoolManager.getSubTitles(key));
		tv_subtitle.setVisibility("".equals(tv_subtitle.getText()) ? View.GONE : View.VISIBLE);

		/*
		 * Return...
		 */
		return view;
	}
}