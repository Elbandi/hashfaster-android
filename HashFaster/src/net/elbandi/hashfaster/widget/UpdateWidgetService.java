package net.elbandi.hashfaster.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import net.elbandi.hashfaster.R;
import net.elbandi.hashfaster.interfaces.RefreshListener;
import net.elbandi.hashfaster.managers.MinerManager;
import net.elbandi.hashfaster.models.Miner;
import net.elbandi.hashfaster.models.Worker;
import net.elbandi.hashfaster.tasks.GetDataTask;

public class UpdateWidgetService extends Service {
	private static final String LOG = "HASHFASTER";
	Miner mMiner = new Miner();
	RefreshListener refreshListener;

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(LOG, "UpdateWidgetService: onStart() called");
		final Context ctx = this.getApplicationContext();
		final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ctx);
		final int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		Log.w(LOG, "From Intent: " + String.valueOf(allWidgetIds.length));

		if (allWidgetIds.length == 0) {
			return;
		}

		refreshListener = new RefreshListener() {
			@Override
			public void onRefresh() {
				Log.w(LOG, "onRefresh() called");
				final RemoteViews remoteViews = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout);
				mMiner = MinerManager.getInstance().getMiner();

				int count = 0, active = 0;
				for (Worker w : MinerManager.getInstance().getMiner().getWorkers()) {
					count++;
					if (w.hashrate > 0)
						active++;
				}

				remoteViews.setTextViewText(R.id.widget_hashrate, mMiner.total_hashrate + " Kh/s");
				remoteViews.setTextViewText(R.id.widget_round_shares, mMiner.round_shares + "/" + mMiner.round_shares_invalid);
				remoteViews.setTextViewText(R.id.widget_active_workers, active + "/" + count);

				for (int widgetId : allWidgetIds) {

					// Register an onClickListener
					Intent clickIntent = new Intent(ctx, MyWidgetProvider.class);

					clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
					clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

					PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);
					appWidgetManager.updateAppWidget(widgetId, remoteViews);

				}
				Toast.makeText(getApplicationContext(), "HashFaster: Updated stats", Toast.LENGTH_LONG).show();
			};
		};

		new GetDataTask(ctx, refreshListener).execute();
		// stopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}