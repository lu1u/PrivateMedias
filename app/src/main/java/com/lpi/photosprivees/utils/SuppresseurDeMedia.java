package com.lpi.photosprivees.utils;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.lpi.photosprivees.MainActivity;
import com.lpi.photosprivees.media.Media;

import java.util.ArrayList;

public class SuppresseurDeMedia
{
	public static final int RAISON_ANNULE_PAR_UTILISATEUR = 1;
	public static final int RAISON_AUTORISATION = 2;
	public static final int RAISON_SYSTEME = 3;
	public static final int IMAGE_REQUEST_CODE = 11254;
	private static final String DATA_URI = "lpi.com.photoprivees.suppresseurDeMedia.uri";
	private static Uri _mediaASupprimer;
	private	static Listener _listener;

	public interface Listener
	{
		void onOk();

		void onCanceled(int raison);
	}

	/***
	 * Supprimer un media, avec eventuellement demande d'autorisation par le systeme
	 * @param activity
	 * @param media
	 * @param listener
	 */
	@RequiresApi(api = Build.VERSION_CODES.Q)
	public static void supprimeMedia(@NonNull final Activity activity, @NonNull final Media media, @NonNull final Listener listener)
	{
		ContentResolver contentResolver = activity.getContentResolver();
		Uri deleteUri;
		switch (media.getType())
		{
			case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
				deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, media.getId());
				break;
			case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
				deleteUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, media.getId());
				break;
			default:
				deleteUri = null;
		}

		try
		{
			contentResolver.delete(deleteUri, null, null);
			listener.onOk();
		} catch (RecoverableSecurityException re)
		{
			// Securité Android: le systeme demande à l'utilisateur la permission de supprimer le media
			try
			{
				IntentSender intentSender = re.getUserAction().getActionIntent().getIntentSender();
				Intent intent = new Intent();
				intent.putExtra(DATA_URI, deleteUri);
				_listener = listener;
				_mediaASupprimer = deleteUri;
				activity.startIntentSenderForResult(intentSender, IMAGE_REQUEST_CODE, intent, 0, 0, 0, null);
			} catch (IntentSender.SendIntentException e)
			{
				Log.e(MainActivity.TAG, e.getLocalizedMessage());
				listener.onCanceled(RAISON_AUTORISATION);
			}
		} catch (Exception e)
		{
			Log.e(MainActivity.TAG, e.getLocalizedMessage());
			listener.onCanceled(RAISON_SYSTEME);
		}
	}

	/***
	 * Traitement des demandes d'autorisation de suppression par Android
	 * @param resultCode
	 */
	public static void onAndroidResult(@NonNull final Context context, int resultCode)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			// Autorisation de supprimer un media accordee
			if (_mediaASupprimer != null)
			{
				try
				{
					ContentResolver contentResolver = context.getContentResolver();
					contentResolver.delete(_mediaASupprimer, null, null);
					AndroidGalleryManager.refreshGallery(context, _mediaASupprimer.getPath());
				} catch (Exception e)
				{
					_listener.onCanceled(RAISON_SYSTEME);
					Log.e(MainActivity.TAG, e.getMessage());
				}
				_mediaASupprimer = null;
				_listener.onOk();
			}
		}
		else
			_listener.onCanceled(RAISON_ANNULE_PAR_UTILISATEUR);
	}
}
