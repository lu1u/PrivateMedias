package com.lpi.photosprivees.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.lpi.photosprivees.R;
import com.lpi.photosprivees.securite.PasswordManager;
import com.lpi.photosprivees.utils.MessageBoxUtils;

/***
 * Saisie du mot de passe
 */
public class SaisieMotDePasseActivity
	{
	public interface Listener
		{
		void onOK();
		void onCancel();
		}

	/***
	 * Demarrer la saisie du nouveau mot de passe
	 */
	public static void start(@NonNull final Activity activity, @NonNull final Listener listener)
		{
		PasswordManager pwdManager = PasswordManager.getInstance(activity);

		pwdManager.annuleMotDePasse();
		pwdManager.executeIfPasswordOk(activity, R.string.ui_passwd_pour_nouveau, new PasswordManager.Listener()
			{
			@Override public void onOK()
				{
				saisieMotDePasse(activity, listener);
				}

			@Override public void onCancel()
				{
				}
			});
		}

	/***
	 * Dialogue de saisie d'un nouveau mot de passe
	 */
	public static void saisieMotDePasse(@NonNull final Activity activity, @NonNull final Listener listener)
		{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.modif_mot_de_passe, null);
		EditText etPwd1 = dialogView.findViewById(R.id.etPassword1);
		EditText etPwd2 = dialogView.findViewById(R.id.etPassword2);

		Button bOk = dialogView.findViewById(R.id.btOk);
		Button bCancel = dialogView.findViewById(R.id.btCancel);

		bOk.setOnClickListener(view ->
			                       {
								   String pwd1 = etPwd1.getText().toString();
								   String pwd2 = etPwd2.getText().toString();
								   if ( checkPwd(activity, pwd1, pwd2))
									   {
									   PasswordManager pwdManager = PasswordManager.getInstance(activity);
									   pwdManager.setMotDePasse(activity, pwd1);
									   listener.onOK();
									   dialogBuilder.dismiss();
									   }
			                       });

		bCancel.setOnClickListener(view ->
			                           {
			                           listener.onCancel();
			                           dialogBuilder.dismiss();
			                           });
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
		}

	/***
	 * Verifier la saisie du mot de passe et sa confirmation
	 */
	private static boolean checkPwd(@NonNull final Activity activity, @NonNull final String pwd1, @NonNull final String pwd2)
		{
		if ( !pwd1.equals(pwd2))
			{
			MessageBoxUtils.messageBox(activity, R.string.ui_passwords_differents_titre, R.string.ui_passwords_differents, MessageBoxUtils.BOUTON_OK, null);
			return false;
			}

		if (pwd1.length()< PasswordManager.LONGUEUR_MIN)
			{
			MessageBoxUtils.messageBox(activity,  activity.getString(R.string.ui_passwords_differents_titre), activity.getString(R.string.ui_password_trop_court, PasswordManager.LONGUEUR_MIN), MessageBoxUtils.BOUTON_OK, null);
			return false;
			}
		return true;
		}
	}
