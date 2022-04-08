package com.lpi.photosprivees.media;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Size;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.photosprivees.MainActivity;
import com.lpi.photosprivees.R;
import com.lpi.photosprivees.utils.BackgroundTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/***
 * Classe abstraite de base pour tous les medias prives et publics, photos et videos
 */
public abstract class Media extends ListeItem
{
	protected final static Size SIZE = new Size(320, 320);
	public static final String SEPARATEUR_VALEURS = "=";
	public static final String KEY_URI_MEDIA = "com.lpi.photosprivees.media.uri";

	// Les clefs des attributs sont MediaStore.Files.FileColumns.*
	protected final HashMap<String, Object> _attributs = new HashMap<>();
	protected Bitmap _thumbnail;

	/***
	 * Retourne une des constantes TYPE_XXXXX
	 * @return
	 */
	@Override
	public int getItemType()
	{
		return TYPE_MEDIA;
	}

	public String getCategorie()
	{
		return getAttributString(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME);
	}

	/***
	 * Affiche la vignette du media, en le chargeant en tache de fond
	 * @param context
	 * @param imgView
	 */
	public void setThumbnail(@NonNull final Context context, @NonNull final ImageView imgView)
	{
		BackgroundTask.execute(new BackgroundTask.Listener()
		{
			@Override
			public Object execute()
			{
				return getThumbnail(context);
			}

			@Override
			public void onFinished(Object o)
			{
				if (o == null)
					return;
				if (o instanceof Bitmap)
					imgView.setImageBitmap((Bitmap) o);
			}
		});
	}

	/***
	 * Lit la longueur video et la met dans le texted d'une TextView, en arriere plan
	 */
	public void setLongueurVideo(@NonNull final Context context, @NonNull final TextView textView)
	{
		if (isVideo())
			BackgroundTask.execute(new BackgroundTask.Listener()
			{
				@Override
				public Object execute()
				{
					return getLongueurVideo(context);
				}

				@Override
				public void onFinished(Object o)
				{
					textView.setText((String) o);
				}
			});
	}

	@Override
	public @Nullable
	String getNom()
	{
		return getAttributString(MediaStore.Files.FileColumns.DISPLAY_NAME);
	}

	/***
	 * Retourne le chemin du fichier qui stocke le media
	 * @return
	 */
	public @NonNull
	String getMediaPath()
	{
		String res = getAttributString(MediaStore.Files.FileColumns.DATA);
		if (res == null)
			return "";
		else
			return res;
	}

	@Override
	public long getDateCreation()
	{
		long date = getAttributLong(MediaStore.Files.FileColumns.DATE_MODIFIED);
		if (date != 0)
			return date * 1000; // DATE_MODIFIED en secondes

		date = getAttributLong(MediaStore.Files.FileColumns.DATE_ADDED);
		if (date != 0)
			return date;

		date = getAttributLong(MediaStore.Files.FileColumns.DATE_TAKEN);
		if (date != 0)
			return date;

		File f = new File(getMediaPath());
		return f.lastModified();
	}

	public long getId()
	{
		return getAttributLong(MediaStore.Files.FileColumns._ID);
	}

	public int getType()
	{
		return getAttributInt(MediaStore.Files.FileColumns.MEDIA_TYPE);
	}

	public String getMimeType()
	{
		return getAttributString(MediaStore.Files.FileColumns.MIME_TYPE);
	}

	public @Nullable
	String getAttributString(@NonNull final String name)
	{
		Object o = _attributs.get(name);
		if (o instanceof String)
			return (String) o;
		return null;
	}

	public long getAttributLong(@NonNull final String name)
	{
		Object o = _attributs.get(name);
		if (o instanceof Long)
			return (long) o;
		if (o instanceof String)
		{
			try
			{
				return Long.parseLong((String) o);
			} catch (NumberFormatException e)
			{
				return 0;
			}
		}
		return 0;
	}

	public int getAttributInt(@NonNull final String name)
	{
		Object o = _attributs.get(name);
		if (o instanceof Integer)
			return (int) o;

		if (o instanceof Long)
			return (int) (long) o;

		if (o instanceof String)
		{
			try
			{
				return Integer.parseInt((String) o);
			} catch (NumberFormatException e)
			{
				return 0;
			}
		}
		return 0;
	}

	/***
	 * Ecrit toutes les information dans un fichier
	 */
	public void ecritInfosDansFichier(@NonNull final String fichierInformation, @NonNull final String fichierMedia)
	{
		try
		{
			OutputStream out = new FileOutputStream(fichierInformation);
			PrintStream writer = new PrintStream(out, true, "UTF-8");
			for (String key : _attributs.keySet())
				writer.println(key + SEPARATEUR_VALEURS + _attributs.get(key));

			writer.println(KEY_URI_MEDIA + SEPARATEUR_VALEURS + fichierMedia);
			writer.flush();
			writer.close();
			out.close();
		} catch (IOException e)
		{
			Log.e(MainActivity.TAG, e.getLocalizedMessage());
		}
	}

	public boolean isPhoto()
	{
		return getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
	}

	public boolean isVideo()
	{
		return getType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
	}


	/***
	 * Retrouve la duree d'une video
	 */
	public @NonNull
	String getLongueurVideo(@NonNull final Context context)
	{
		if (!isVideo())
			return "";

		try
		{
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			//use one of overloaded setDataSource() functions to set your data source
			retriever.setDataSource(context, Uri.parse(getMediaPath()));
			String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			retriever.release();
			long timeInMillisSec = Long.parseLong(time);
			return formateLongueur(timeInMillisSec);
		} catch (Exception e)
		{
			Log.e(MainActivity.TAG, e.getMessage());
			return "";
		}
	}

	private String formateLongueur(long millis)
	{
		return String.format("%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}


	public void toContentValues(@NonNull ContentValues values, boolean repertoireOrigine)
	{
		for (String key : _attributs.keySet())
			if (!KEY_URI_MEDIA.equals(key)) // Valeur perso a ne pas transmettre a Android
			{
				String value = String.valueOf(_attributs.get(key));
				values.put(key, value);
			}

		if (!repertoireOrigine)
		{
			values.remove(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME);
			values.remove(MediaStore.Files.FileColumns.RELATIVE_PATH);
			values.remove(MediaStore.Files.FileColumns.DATA);
		}
	}


	/***
	 * Retrouver la thumbnail du media
	 */
	public @Nullable
	Bitmap getThumbnail(@NonNull final Context context)
	{
		if (_thumbnail == null)
		{
			if (isPhoto())
				getThumbnailPhoto();
			else if (isVideo())
				getThumbnailVideo();
		}
		return _thumbnail;
	}

	/***
	 * Retrouve la thumbnail d'une photo
	 */
	private void getThumbnailPhoto()
	{
		// Tenter a partir du nom de fichier
		try
		{
			Bitmap bitmap = BitmapFactory.decodeFile(getMediaPath());
			_thumbnail = ThumbnailUtils.extractThumbnail(bitmap, SIZE.getWidth(), SIZE.getHeight());
			//_thumbnail = ThumbnailUtils.createImageThumbnail(new File(getMediaPath()), SIZE, null);
		} catch (Exception ex)
		{
			_thumbnail = null;
			Log.e(MainActivity.TAG, ex.getLocalizedMessage());
			Log.e(MainActivity.TAG, "Media path: " + getMediaPath());
			//	Log.e(MainActivity.TAG, "Uri: " + contentUri.getPath());
		}
	}

	/***
	 * Retrouve la thumbnail d'une video
	 */
	@SuppressLint("NewApi")
	private void getThumbnailVideo()
	{
		File file = new File(getMediaPath());
		try
		{
			_thumbnail = ThumbnailUtils.createVideoThumbnail(file, SIZE, null);
		} catch (Exception e)
		{
			_thumbnail = null;
			Log.e(MainActivity.TAG, "Erreur getThumbnailVideo " + getMediaPath());
			Log.e(MainActivity.TAG, e.getLocalizedMessage());
		}
	}

	/***
	 * Creer un texte representant les informations du media
	 */
	public String getTexteInformations(@NonNull final Context context)
	{
		if (isVideo())
			return context.getString(R.string.infosMediaVideo,
					_attributs.get(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME).toString(),
					_attributs.get(MediaStore.Files.FileColumns.DATA).toString(),
					_attributs.get(MediaStore.Files.FileColumns.WIDTH).toString(),
					_attributs.get(MediaStore.Files.FileColumns.HEIGHT).toString(),
					formatDate(getDateCreation()),
					getLongueurVideo(context));
		else
			return context.getString(R.string.infosMedia,
					_attributs.get(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME).toString(),
					_attributs.get(MediaStore.Files.FileColumns.DATA).toString(),
					_attributs.get(MediaStore.Files.FileColumns.WIDTH).toString(),
					_attributs.get(MediaStore.Files.FileColumns.HEIGHT).toString(),
					formatDate(getDateCreation()));

	}

	private static String formatDate(Object o)
	{
		if (o == null)
			return "";

		if (o instanceof Long)
		{
			SimpleDateFormat dateFormat;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
			{
				dateFormat = new SimpleDateFormat(DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEE, MMM d, YYYY, jj:mm"));
			}
			else
			{
				dateFormat = new SimpleDateFormat("MMM/dd/yyyy hh:mm:ss aa");
			}
			Date date = new Date((Long) o);
			return dateFormat.format(date);
		}
		return "";
	}


	/***
	 * Retourne la bitmap contenue dans ce media
	 */
	public @Nullable
	Bitmap getBitmap()
	{
		return BitmapFactory.decodeFile(getMediaPath());
	}


}
