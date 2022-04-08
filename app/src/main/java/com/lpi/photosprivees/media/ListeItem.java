package com.lpi.photosprivees.media;


import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class ListeItem
{
	public static final int TYPE_ENTETE = 0;
	public static final int TYPE_MEDIA = 1;

	abstract public long getDateCreation();

	abstract public @Nullable
	String getNom();

	/***
	 * Retourne TRUE si le media est privé, faux sinon
	 * @return
	 */
	public abstract boolean isPrive();

	/***
	 * Par defaut, les elements de la liste occupent 1 colonne
	 * @return
	 */
	public int getSpanSize()
	{
		return 1;
	}

	public abstract void remplitListeItem(@NonNull final Context context, @NonNull MediasRecyclerViewAdapter.MyViewHolder holder);

	/***
	 * Retourne une des constantes TYPE_XXXXX
	 * @return
	 */
	public abstract int getItemType();

	public String getCategorie()
	{
		return "";
	}
}
