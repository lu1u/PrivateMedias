package com.lpi.photosprivees.media;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Entete extends ListeItem
{
	private String _nom;
	private int _nombre;
	/***
	 * Retourne une des constantes TYPE_XXXXX
	 * @return
	 */
	@Override public  int getItemType() { return TYPE_ENTETE ; }
	public Entete(@NonNull final String nom)
	{
		_nom = nom;
	}
	@Override
	public long getDateCreation()
	{
		return 0;
	}

	@Nullable
	@Override
	public String getNom()
	{
		return _nom;
	}

	@Override
	public boolean isPrive()
	{
		return false;
	}

	/***
	 * Par defaut, les elements de la liste occupent 1 colonne
	 * @return
	 */
	public int getSpanSize()
	{
		return 3;
	}

	@Override
	public void remplitListeItem(@NonNull Context context, @NonNull MediasRecyclerViewAdapter.MyViewHolder h)
		{
			if ( h instanceof  MediasRecyclerViewAdapter.ViewHolderEntete)
			{
				MediasRecyclerViewAdapter.ViewHolderEntete holder = (MediasRecyclerViewAdapter.ViewHolderEntete) h;
				holder.tvEntete.setText(_nom);
				holder.tvNombre.setText(""+_nombre);
			}
	}

	public void setNombre(int n)
	{
		_nombre = n;
	}
}
