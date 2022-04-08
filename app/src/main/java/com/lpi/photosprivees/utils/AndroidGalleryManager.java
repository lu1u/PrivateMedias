package com.lpi.photosprivees.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lpi.photosprivees.MainActivity;
import com.lpi.photosprivees.media.Media;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/***
 * Gestion des medias de la gallerie Android
 */
public class AndroidGalleryManager
	{

	/***
	 * Suppression d'un media dans la gallerie
	 */
	public static void deleteMedia(@NonNull final Context context, @NonNull final Media media) throws Exception
		{
		ContentResolver contentResolver = context.getContentResolver();
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

		contentResolver.delete(deleteUri, null, null);
		}


	/***
	 * Demander la mise a jour de la gallerie
	 */
	public static void refreshGallery(@NonNull final Context context, @NonNull final String path)
		{
		MediaScannerConnection.scanFile(context,
		                                new String[]{path},
		                                null,
		                                (path1, uri) ->
			                                {
			                                Log.i("ExternalStorage", "Scanned " + path1 + ":");
			                                Log.i("ExternalStorage", "-> uri=" + uri);
			                                });
		}

	/***
	 * Ajoute un media a la gallerie
	 * @return true si operation OK
	 */
	public static boolean addMedia(@NonNull final Context context, @NonNull final Media media, @NonNull final String source, boolean repertoireOrigine) throws Exception
	{
		ContentValues values;
		values = new ContentValues();
		media.toContentValues(values, repertoireOrigine);
		values.put(MediaStore.Video.Media.IS_PENDING, 1);

		ContentResolver resolver = context.getContentResolver();
		Uri collection;
		switch (media.getType())
			{
			case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
				collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
				break;
			case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
				collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
				break;
			default:
				collection = null;
			}

		if (collection != null)
			{
			Uri uriSavedMedia = resolver.insert(collection, values);

			try
				{
				OutputStream out = resolver.openOutputStream(uriSavedMedia);
				InputStream in = new FileInputStream(source);
				FileUtils.copieFichier(in, out);
				out.close();
				in.close();
				}
			catch (Exception e)
				{
				Log.e(MainActivity.TAG, e.getMessage());
				AndroidGalleryManager.deleteMedia(context, media);
				return false;
				}

			values.clear();
			values.put(MediaStore.Video.Media.IS_PENDING, 0);
			context.getContentResolver().update(uriSavedMedia, values, null, null);

			AndroidGalleryManager.refreshGallery(context, uriSavedMedia.getPath());
			return true;
			}

		return false;
		}
	}
