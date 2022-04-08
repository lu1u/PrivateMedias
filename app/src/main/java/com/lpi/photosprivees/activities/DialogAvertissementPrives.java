package com.lpi.photosprivees.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.photosprivees.R;
import com.lpi.photosprivees.utils.Preferences;

public class DialogAvertissementPrives
{
	public interface Listener
	{
		void onOk();
	}

	/***
	 * Montre le dialogue d'avertissement et execute une fonction si OK
	 * Possibilite d'enregistrer "Ne plus afficher", dans ce cas, la fonction est executée directement
	 * @param activity
	 * @param listener
	 */
	public static void executeSiOk(@Nullable final Activity activity, @NonNull final Listener listener)
	{
		final Preferences preferences = Preferences.getInstance(activity);
		if ( preferences.getBoolean(Preferences.PREFERENCES_NEPLUSAFFICHER_AVERTISSEMENT, false))
		{
			// Si 'ne plus afficher' a été enregistrer, executer l'action directement
			listener.onOk();
			return;
		}

		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.layout_avertissement_privees, null);

		// Controles de la fenetre
		Button bOk = dialogView.findViewById(R.id.btnOk);
		Button bCancel = dialogView.findViewById(R.id.btnCancel);
		CheckBox cbNePlusAfficher = dialogView.findViewById(R.id.cbNePlusAfficher);

		// Listeners
		bOk.setOnClickListener(view ->
		{
			listener.onOk();
			dialogBuilder.dismiss();
		});

		bCancel.setOnClickListener(view->
		{
			dialogBuilder.dismiss();
		});

		cbNePlusAfficher.setOnCheckedChangeListener((compoundButton, b) -> preferences.setBoolean(Preferences.PREFERENCES_NEPLUSAFFICHER_AVERTISSEMENT, b));

		// Afficher
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}
}
