package com.lpi.photosprivees.media;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;

import com.lpi.photosprivees.R;
import com.lpi.photosprivees.options.OptionsAffichage;

import java.util.ArrayList;

public class MediaPublique extends Media
{

	/***
	 * Ajoute les medias publique dans la liste
	 */
	public static void ajouteMedias(Activity context, ArrayList<ListeItem> medias, int optionType)
	{
		String typeMedia;
		switch (optionType)
		{
			case OptionsAffichage.OPTION_TYPES_IMAGES:
				typeMedia = "" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
				break;
			case OptionsAffichage.OPTION_TYPES_VIDEOS:
				typeMedia = "" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
				break;
			default:
				typeMedia = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + "," + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
		}

		String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " IN " + "( " + typeMedia + " )";
		Cursor c = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), null, selection, null, null);

		if (c != null)
		{
			c.moveToFirst();
			while (c.moveToNext())
				medias.add(new MediaPublique(c));
			c.close();
		}
	}


	public MediaPublique(@NonNull final Cursor cursor)
	{
		for (int i = 0; i < cursor.getColumnCount(); i++)
		{
			switch (cursor.getType(i))
			{
				case Cursor.FIELD_TYPE_INTEGER:
					_attributs.put(cursor.getColumnName(i), new Long(cursor.getLong(i)));
					//Log.d(MainActivity.TAG, cursor.getType(i) + "," + cursor.getColumnName(i) + "=" + cursor.getLong(i));
					break;
				case Cursor.FIELD_TYPE_STRING:
					_attributs.put(cursor.getColumnName(i), cursor.getString(i));
					//Log.d(MainActivity.TAG, cursor.getType(i) + "," + cursor.getColumnName(i) + "=" + cursor.getString(i));
					break;
				case Cursor.FIELD_TYPE_FLOAT:
					_attributs.put(cursor.getColumnName(i), new Float(cursor.getFloat(i)));
					//Log.d(MainActivity.TAG, cursor.getType(i) + "," + cursor.getColumnName(i) + "=" + cursor.getFloat(i));
					break;
				case Cursor.FIELD_TYPE_BLOB:
				case Cursor.FIELD_TYPE_NULL:
				default:
					//Log.w(MainActivity.TAG, "Ignored column type " + cursor.getType(i) + "," + cursor.getColumnName(i));
			}
		}
	}

	@Override
	public boolean isPrive()
	{
		return false;
	}


	@Override
	public void remplitListeItem(Context context, @NonNull MediasRecyclerViewAdapter.MyViewHolder h)
	{
		if ( h instanceof  MediasRecyclerViewAdapter.ViewHolderMedia)
		{
			MediasRecyclerViewAdapter.ViewHolderMedia holder = (MediasRecyclerViewAdapter.ViewHolderMedia)h;
			setThumbnail(context, holder.imThumbnail);
			holder.imPrivee.setImageResource(R.drawable.show);

			if (isPhoto())
			{
				holder.imType.setImageResource(R.drawable.image);
				holder.tvLongueur.setVisibility(View.GONE);
			}
			else if (isVideo())
			{
				holder.imType.setImageResource(R.drawable.video);
				setLongueurVideo(context, holder.tvLongueur);
			}
		}
	}

}
