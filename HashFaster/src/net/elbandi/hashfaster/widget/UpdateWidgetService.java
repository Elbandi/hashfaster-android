package net.elbandi.hashfaster.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.interfaces.RefreshListener;
import net.elbandi.hashfaster.managers.MinerManager;
import net.elbandi.hashfaster.managers.PrefManager;
import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.models.Worker;
import net.elbandi.hashfaster.tasks.GetDataTask;

public class UpdateWidgetService extends Service {
	private static final String LOG = "HASHFASTER";
	public static final String UPDATE = "update";
	Miner mMiner = new Miner();
	RefreshListener refreshListener;

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		final Context ctx = getApplicationContext();
		final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ctx);
		if (intent == null) {
			ctx.toString();
		}
		final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
		final String pool = PrefManager.getWidgetPoolKey(ctx, appWidgetId);
		Log.i(LOG, "UpdateWidgetService: onStart() called " + startId + ":" + appWidgetId);

		refreshListener = new RefreshListener() {
			@Override
			public void onRefresh() {
				Log.w(LOG, "onRefresh() called");
				RemoteViews remoteViews = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout);
				mMiner = MinerManager.getInstance().getMiner(pool);

				int count = 0, active = 0;
				for (Worker w : MinerManager.getInstance().getMiner(pool).getWorkers()) {
					count++;
					if (w.hashrate > 0)
						active++;
				}

				remoteViews.setTextViewText(R.id.widget_hashrate, mMiner.total_hashrate + " Kh/s");
				remoteViews.setTextViewText(R.id.widget_round_shares, mMiner.round_shares + "/" + mMiner.round_shares_invalid);
				remoteViews.setTextViewText(R.id.widget_active_workers, active + "/" + count);

				appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

				Toast.makeText(getApplicationContext(), R.string.widget_updated_stats, Toast.LENGTH_LONG).show();
			};
		};

		TypedArray pools_url = getResources().obtainTypedArray(R.array.pool_urls);
		TypedArray apikeys = getResources().obtainTypedArray(R.array.pool_keys);
		try {
			new GetDataTask(ctx, refreshListener, pool).execute();
		} finally {
			pools_url.recycle();
			apikeys.recycle();
		}
		// stopSelf();
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}