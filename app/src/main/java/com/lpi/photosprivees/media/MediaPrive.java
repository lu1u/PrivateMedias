package com.lpi.photosprivees.media;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.photosprivees.MainActivity;
import com.lpi.photosprivees.R;
import com.lpi.photosprivees.options.OptionsAffichage;
import com.lpi.photosprivees.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MediaPrive extends Media
{
	private String _fichierInformation;

	/***
	 * Lit un media privé
	 * Les infos sont contenues dans un fichier .txt avec le meme nom que le media
	 */
	public MediaPrive( @NonNull final String f)
	{
		// Lire les infos du media a partir du fichier
		try
		{
			_fichierInformation = f;
			InputStream in = new FileInputStream(f);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			String line;

			while ((line = br.readLine()) != null)
			{
				String[] tokens = line.split(SEPARATEUR_VALEURS);
				if (tokens.length > 1)
					_attributs.put(tokens[0], tokens[1]);
			}
			br.close();
			in.close();
		} catch (Exception e)
		{
			Log.e(MainActivity.TAG, e.getMessage());
		}
	}

	/***
	 * Ajoute les medias privés qui sont stockés dans le repertoire des medias prives
	 */
	public static void ajouteMedias(@Nullable final Activity context, @NonNull ArrayList<ListeItem> medias, int optionType)
	{
		try
		{
			File directory = new File(PublicPrive.getInstance(context).getRepertoireStockagePrive());
			File[] fichiers = directory.listFiles(file ->
			{
				String extension = "." + FileUtils.getExtension(file.getName());
				return PublicPrive.EXTENSION_FICHIER_INFOS.equals( extension );
			});

			if (fichiers != null)
			{
				final boolean inclurePhotos = (optionType & OptionsAffichage.OPTION_TYPES_IMAGES) != 0;
				final boolean inclureVideos = (optionType & OptionsAffichage.OPTION_TYPES_VIDEOS) != 0;

				for (File f : fichiers)
				{
					//if (extensionOk(f))
					{
						MediaPrive media = new MediaPrive(f.getAbsolutePath());
						boolean inclure = false;
						if (media.isPhoto())
							inclure = inclurePhotos;
						else if (media.isVideo())
							inclure = inclureVideos;
						if (inclure)
							medias.add(media);
					}
				}
			}
		} catch (Exception e)
		{
			Log.e(MainActivity.TAG, e.getMessage());
		}
	}


	/***
	 * Retourne le chemin contenant le media
	 */
	@NonNull
	@Override
	public String getMediaPath()
	{
		return getAttributString(Media.KEY_URI_MEDIA);
	}

	@Override
	public void remplitListeItem(Context context , @NonNull MediasRecyclerViewAdapter.MyViewHolder h)
	{
		if ( h instanceof  MediasRecyclerViewAdapter.ViewHolderMedia)
		{
			MediasRecyclerViewAdapter.ViewHolderMedia holder = (MediasRecyclerViewAdapter.ViewHolderMedia) h;
			setThumbnail(context, holder.imThumbnail);
			holder.imPrivee.setImageResource(R.drawable.hide);

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

	@Override
	public boolean isPrive()
	{
		return true;
	}


	/***
	 *
	 */
	public String getFichierInformation()
	{
		return _fichierInformation;
	}

}
