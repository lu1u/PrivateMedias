package com.lpi.photosprivees.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.concurrent.Executors;

public class BackgroundTaskWithProgress
{
	/*******************************************************************************************
	 * Execute une tache en arriere plan, avec une fenetre affichee pendant ce temps (devrait
	 * contenir une progress bar circulaire
	 *******************************************************************************************/
	public static void execute(@NonNull final Activity context, @LayoutRes int layoutId, @NonNull final Listener listener)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
		Executors.newSingleThreadExecutor().execute(() ->
		{
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(() ->
			{
				LayoutInflater inflater = context.getLayoutInflater();
				final View dialogView = inflater.inflate(layoutId, null);
				dialogBuilder.setView(dialogView);dialogBuilder.show();
			});

			listener.execute();
			mainHandler.post(() ->
			{
				if (dialogBuilder.isShowing())
					dialogBuilder.dismiss();
				listener.onFinished();
			});
		});

//		new AsyncTask<Void, Void, Void>()
//		{
//			@Override protected void onPreExecute()
//			{
//				super.onPreExecute();
//				dialogBuilder.show();
//			}
//
//			@Override
//			protected void onProgressUpdate(final Void... values)
//			{
//				super.onProgressUpdate(values);
//			}
//
//			@Override protected void onPostExecute(final Void aVoid)
//			{
//				super.onPostExecute(aVoid);
//
//			}
//
//			@Override
//			protected Void doInBackground(final Void... voids)
//			{
//
//				return null;
//			}
//		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public interface Listener
	{
		void execute();         // Tache a effectuer en arriere plan
		void onFinished();      // Tache terminee
	}
}