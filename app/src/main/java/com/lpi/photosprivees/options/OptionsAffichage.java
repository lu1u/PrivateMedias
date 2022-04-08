package com.lpi.photosprivees.options;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.lpi.photosprivees.R;
import com.lpi.photosprivees.utils.Preferences;

public class OptionsAffichage
{
	// Options de tri
	public static final int OPTION_TRI_EMPLACEMENT = 0;
	public static final int OPTION_TRI_CREATION = 1;

	// Options de types
	public static final int OPTION_TYPES_IMAGES = 1 << 0;
	public static final int OPTION_TYPES_VIDEOS = 1 << 1;
	public static final int OPTION_TYPES_TOUS = OPTION_TYPES_IMAGES | OPTION_TYPES_VIDEOS;

	// Options de vue
	public static final int OPTION_VUE_PUBLIQUES = 1 << 0 ;
	public static final int OPTION_VUE_PRIVEES = 1 << 1;
	public static final int OPTION_VUE_TOUTES = OPTION_VUE_PUBLIQUES | OPTION_VUE_PRIVEES;

	private int _optionVue, _optionTri, _optionType;
	private Preferences _preferences;
	public OptionsAffichage(@NonNull final Preferences preferences)
	{
		_preferences = preferences;
		_optionTri = preferences.getInt(Preferences.PREFERENCES_OPTION_TRI, OptionsAffichage.OPTION_TRI_EMPLACEMENT);
		_optionVue = OptionsAffichage.OPTION_VUE_PUBLIQUES; // Forcer l'affichage des images publiques tant qu'on n'a pas saisi le mot de passe _preferences.getInt(Preferences.PREFERENCES_OPTION_VUE, OptionVue.OPTION_VUE_TOUTES);
		_optionType = preferences.getInt(Preferences.PREFERENCES_OPTION_TYPE, OptionsAffichage.OPTION_TYPES_TOUS);
	}

	/***
	 * Demarrer le dialogue de choix des options de tri
	 * @param activity
	 * @param optionSelectionnee
	 * @param listener
	 */
	public static void startOptionTri(@NonNull final Activity activity, int optionSelectionnee, @NonNull final Listener listener)
		{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.option_tri, null);

		int id = getIdFromOption(optionSelectionnee);

		RadioGroup rGroup = dialogView.findViewById(R.id.rgOptionsTri);
		rGroup.check(id);

		setListener(dialogBuilder, dialogView, R.id.rbTriNom, OPTION_TRI_EMPLACEMENT, listener);
		setListener(dialogBuilder, dialogView, R.id.rbTriDateCreation, OPTION_TRI_CREATION, listener);

		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
		}

	private static void setListener(@NonNull final AlertDialog alertDialog, @NonNull final View dialogView, int id, int optionTri, Listener listener)
		{
		RadioButton rb = dialogView.findViewById(id);
		rb.setOnCheckedChangeListener((compoundButton, b) ->
			                              {
										  if ( b)
											  {
											  listener.onSelection(optionTri);
											  alertDialog.dismiss();
											  }
			                              });
		}

	private static int getIdFromOption(int optionSelectionnee)
		{
		if (optionSelectionnee == OPTION_TRI_CREATION)
			{
			return R.id.rbTriDateCreation;
			}
		return R.id.rbTriNom;
		}

	/***
	 * Change l'option de tri
	 * @param option
	 */
	public void setOptionTri(int option)
	{
		_optionTri = option;
		_preferences.setInt(Preferences.PREFERENCES_OPTION_TRI, _optionTri);
	}

	public int getOptionTri()
	{
		return _optionTri;
	}
	public int getOptionVue()
	{
		return _optionVue;
	}

	public void setOptionVue(int option)
	{
		_optionVue = option;
		_preferences.setInt(Preferences.PREFERENCES_OPTION_VUE, _optionVue);
	}

	public int getOptionType()
	{
		return _optionType;
	}

	public void setOptionType(int option)
	{
		_optionType = option;
		_preferences.setInt(Preferences.PREFERENCES_OPTION_TYPE, _optionType);
	}

	public interface Listener
	{
	void onSelection(int selection);
	}
}
