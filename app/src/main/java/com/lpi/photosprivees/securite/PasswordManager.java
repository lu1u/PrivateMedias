package com.lpi.photosprivees.securite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.lpi.photosprivees.BuildConfig;
import com.lpi.photosprivees.R;
import com.lpi.photosprivees.utils.MessageBoxUtils;

/***
 * Gestion du mot de passe et de la saisie de mot de passe si besoin
 */
public class PasswordManager
{
	@NonNull
	private static final String STOCKAGE = PasswordManager.class.getName();
	private static final String STOCKAGE_PASSWORD = "value";
	public static final int LONGUEUR_MIN = BuildConfig.DEBUG ? 0 : 2;

	/***
	 * Retourne TRUE si un mot de passe est correctement configure
	 */
	public boolean configurationMotDePasseOK()
	{
		if (_motDePasseEnregistre == null)
			return false;

		return _motDePasseEnregistre.length() >= LONGUEUR_MIN;
	}

	public interface Listener
	{
		void onOK();

		void onCancel();
	}

	private boolean _passwdOk;
	private String _motDePasseEnregistre;
	private static PasswordManager INSTANCE = null;

	/***
	 * Obtenir l'instance (unique) de Preferences
	 */
	public static synchronized PasswordManager getInstance(@Nullable final Context context)
	{
		if (INSTANCE == null)
			if (context != null)
				INSTANCE = new PasswordManager(context);
		return INSTANCE;
	}

	private PasswordManager(@NonNull final Context context)
	{
		_motDePasseEnregistre = litMotDePasse(context);
		_passwdOk = motDePasseOK();
	}

	/***
	 * Annulation de l'authentification en cours
	 */
	public void annuleMotDePasse()
	{
		_passwdOk = false;
	}

	/***
	 * modification du mot de passe
	 */
	public void setMotDePasse(@NonNull final Context context, @NonNull final String pwd)
	{
		_motDePasseEnregistre = pwd;
		_passwdOk = true; // On vient de le saisir!
		sauvegardeMotDePasse(context, pwd);
	}

	/***
	 * Lecture du mot de passe enregistré
	 */
	private String litMotDePasse(@NonNull final Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(STOCKAGE, Context.MODE_PRIVATE);
		return settings.getString(STOCKAGE_PASSWORD, "");
	}

	/***
	 * Ecriture du mot de passe enregistré
	 */
	private void sauvegardeMotDePasse(@NonNull final Context context, @NonNull final String pwd)
	{
		SharedPreferences settings = context.getSharedPreferences(STOCKAGE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(STOCKAGE_PASSWORD, pwd);
		editor.apply();
	}

	/***
	 * Executer une tache si l'authentification est correcte, sinon
	 * commencer par demander le mot de passe
	 */
	public void executeIfPasswordOk(@NonNull final Activity activity, @StringRes int message, @NonNull final Listener listener)
	{
		if (motDePasseOK())
			listener.onOK();
		else
			saisieMotDePasse(activity, message, listener);
	}

	/***
	 * Verifie que l'authentification est ok
	 */
	public boolean motDePasseOK()
	{
		return _passwdOk;
	}

	private boolean motDePasseOK(@NonNull final String pwd)
	{
		return _motDePasseEnregistre.equals(pwd);
	}

	/***
	 * Saisie du mot de passe et authentification
	 */
	public void saisieMotDePasse(@NonNull final Activity activity, @StringRes int message, @NonNull final Listener listener)
	{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.saisie_mdp, null);
		final EditText etPwd = dialogView.findViewById(R.id.etPassword);
		final TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
		final Button bOk = dialogView.findViewById(R.id.btOk);
		final Button bCancel = dialogView.findViewById(R.id.btCancel);
		tvMessage.setText(activity.getString(message));

		bOk.setOnClickListener(view ->
		{
			String pwd = etPwd.getText().toString();
			if (motDePasseOK(pwd))
			{
				_passwdOk = true;
				dialogBuilder.dismiss();
				listener.onOK();
			}
			else
				MessageBoxUtils.messageBox(activity, R.string.ui_titre_password_incorrect, R.string.ui_password_incorrect, MessageBoxUtils.BOUTON_OK, null);
		});

		bCancel.setOnClickListener(view ->
		{
			_passwdOk = false;
			listener.onCancel();
			dialogBuilder.dismiss();
		});
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
	}
}
