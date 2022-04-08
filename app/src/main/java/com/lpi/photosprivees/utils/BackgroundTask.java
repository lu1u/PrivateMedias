package com.lpi.photosprivees.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executors;

public class BackgroundTask
	{
		public interface Listener
			{
			void onFinished( Object o);
			Object execute();
			}

		public static void execute(@NonNull final Listener listener)
			{
				Executors.newSingleThreadExecutor().execute(() ->
				{
					Handler mainHandler = new Handler(Looper.getMainLooper());
					Object o = listener.execute();

					mainHandler.post(() ->
					{
						listener.onFinished(o);
					});
				});
			}
	}
