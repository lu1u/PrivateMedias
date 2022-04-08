
package com.lpi.photosprivees.utils;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class MessageBoxUtils
	{
	public static final int BOUTON_OK = 1;
	public static final int BOUTON_CANCEL = 2;

	public static void messageBox(@NonNull Context a, @StringRes int titre, @StringRes int texte, int boutons, final @Nullable Listener listener)
		{
		messageBox(a, a.getResources().getString(titre), a.getResources().getString(texte), boutons, listener);
		}

	public static void messageBox(@NonNull Context a, @NonNull String titre, @NonNull String texte, int boutons, final @Nullable Listener listener)
		{
		AlertDialog.Builder builder = new AlertDialog.Builder(a);
		builder.setTitle(titre);
		builder.setMessage(texte);
		if ((boutons & BOUTON_OK) != 0)
			builder.setPositiveButton("OK", (dialog, id) ->
			{
			if (listener != null)
				listener.onOk();
			});

		if ((boutons & BOUTON_CANCEL) != 0)
			builder.setNegativeButton("Annuler", (dialog, id) ->
			{
			if (listener != null)
				listener.onCancel();
			});

		// Create the AlertDialog object and return it
		builder.create().show();
		}

	public interface Listener
		{
		void onOk();

		void onCancel();
		}
	}
