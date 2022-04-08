package com.lpi.photosprivees.fragments;
import android.content.res.ColorStateList;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lpi.photosprivees.R;
import com.lpi.photosprivees.media.ListeItem;
import com.lpi.photosprivees.media.Media;

/***
 * Classe de base pour les fragments de l'application
 */
public class MesFragments extends Fragment
	{

		public  void setControlVisibility(int ViewVisibility){};

	public void createContextMenu(MenuInflater menuInflater, ContextMenu menu, View v)
		{
		}

	public interface Listener
		{
		void onItemClic(ListeItem item);
		}
	public void onShow(boolean visible){}

	protected @Nullable Listener _listener;
	public void setListener(@Nullable final Listener listener)
		{
		_listener = listener;
		}

		public void changeButtonTint(@NonNull final ImageButton bouton, boolean selectionne)
		{
			int c = getContext().getColor(selectionne ? R.color.couleurOptionSelectionnee : R.color.couleurOptionNonSelectionnee);
			bouton.setImageTintList(ColorStateList.valueOf(c));
		}
	}
