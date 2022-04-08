package com.lpi.photosprivees;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lpi.photosprivees.activities.SaisieMotDePasseActivity;
import com.lpi.photosprivees.securite.PasswordManager;

public class PremiereActivity extends AppCompatActivity
{
	static final int PERMISSIONS_REQUEST_CODE = 1143;
	@NonNull static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION};


	private Button _bPermissions,  _bMotDePasse, _bLancerAppli;
	private TextView _tvPermissions, _tvMotDePasse,  _tvConfigurationNonOk;

	// DEBUG
	final boolean forcePermissions = false;
	final boolean forceMotDePasse = false;
	final boolean forceLancerAppli = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_premiere);

		_bPermissions = findViewById(R.id.bPermissions);
		_tvPermissions = findViewById(R.id.tvPermissions);

		_bMotDePasse = findViewById(R.id.bMotDePasse);
		_tvMotDePasse = findViewById(R.id.tvMotDePasse);

		_bLancerAppli = findViewById(R.id.bLancer);
		_tvConfigurationNonOk = findViewById(R.id.tvMessageTermine);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (checkConfiguration())
			// Configuration deja OK, lancer directement l'activity principal
			onClickLancerApplication(_bLancerAppli);
	}

	private boolean checkConfiguration()
	{
		boolean perm = checkPermissions();
		boolean mdp = checkMotDePasse();
		if ((perm && mdp) || forceLancerAppli)
		{
			_bLancerAppli.setVisibility(View.VISIBLE);
			_tvConfigurationNonOk.setVisibility(View.GONE);
			return true;
		}
		else
		{
			_bLancerAppli.setVisibility(View.GONE);
			_tvConfigurationNonOk.setVisibility(View.VISIBLE);
			return false;
		}
	}

	private boolean checkMotDePasse()
	{
		if (PasswordManager.getInstance(this).configurationMotDePasseOK() && !forceMotDePasse)
		{
			_tvMotDePasse.setVisibility(View.GONE);
			_bMotDePasse.setVisibility(View.GONE);
			return true;
		}
		else
		{
			_tvMotDePasse.setVisibility(View.VISIBLE);
			_bMotDePasse.setVisibility(View.VISIBLE);
			return false;
		}
	}


	/***
	 * Verifie que les permissions Android ont été accordées
	 */
	private boolean checkPermissions()
	{
		boolean permissions = true;
		if (forcePermissions)
			permissions = false;
		else
			for (String p : PremiereActivity.PERMISSIONS)
				if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED)
				{
					permissions = false;
					break;
				}

		if (permissions)
		{
			_tvPermissions.setVisibility(View.GONE);
			_bPermissions.setVisibility(View.GONE);
		}
		else
		{
			_tvPermissions.setVisibility(View.VISIBLE);
			_bPermissions.setVisibility(View.VISIBLE);
		}
		return permissions;
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		if (requestCode == PremiereActivity.PERMISSIONS_REQUEST_CODE)
			checkConfiguration();

		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}



	public void onClickAccorderPermissions(View v)
	{
		requestPermissions(PremiereActivity.PERMISSIONS, PremiereActivity.PERMISSIONS_REQUEST_CODE);
	}

	public void onClickMotDePasse(View v)
	{
		SaisieMotDePasseActivity.saisieMotDePasse(this, new SaisieMotDePasseActivity.Listener()
		{
			@Override
			public void onOK()
			{
				checkConfiguration();
			}

			@Override
			public void onCancel()
			{
			}
		});
	}

	public void onClickLancerApplication(View v)
	{
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}
}