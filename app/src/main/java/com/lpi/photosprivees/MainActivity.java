package com.lpi.photosprivees;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lpi.photosprivees.fragments.GallerieFragment;
import com.lpi.photosprivees.fragments.ImageViewFragment;
import com.lpi.photosprivees.fragments.MesFragments;
import com.lpi.photosprivees.fragments.VideoViewFragment;
import com.lpi.photosprivees.media.ListeItem;
import com.lpi.photosprivees.media.Media;
import com.lpi.photosprivees.media.PublicPrive;
import com.lpi.photosprivees.securite.PasswordManager;
import com.lpi.photosprivees.utils.SuppresseurDeMedia;

public class MainActivity extends AppCompatActivity
{
	public static final String TAG = "PhotosPrivees";
	static final int FRAGMENT_AFFICHE_GALLERIE = 0;
	static final int FRAGMENT_AFFICHE_VIDEO = 1;
	static final int FRAGMENT_AFFICHE_IMAGE = 2;
	private int _fragmentAffiche = -1;

	private PublicPrive _publiquePrive;
	private GallerieFragment _fragmentGallerie;
	private ImageViewFragment _fragmentImage;
	private VideoViewFragment _fragmentVideo;



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);
		_publiquePrive = PublicPrive.getInstance(this);
		_fragmentGallerie = (GallerieFragment) getSupportFragmentManager().findFragmentById(R.id.frGallerie);
		_fragmentImage = (ImageViewFragment) getSupportFragmentManager().findFragmentById(R.id.frImage);
		_fragmentVideo = (VideoViewFragment) getSupportFragmentManager().findFragmentById(R.id.frVideo);

		checkAllPermissions();
		initFragments();
		afficheFragment(FRAGMENT_AFFICHE_GALLERIE);
	}

	private void initFragments()
	{
		_fragmentGallerie.setListener(new MesFragments.Listener()
		{
			@Override
			public void onItemClic(ListeItem item)
			{
				if (item instanceof Media)
					onAfficher((Media) item);
			}
		});
	}

	/***
	 * Selectionne un fragment a afficher
	 * @param fragmentAAfficher : une des constantes FRAGMENT_xxx
	 */
	private void afficheFragment(int fragmentAAfficher)
	{
		FragmentManager fr = getSupportFragmentManager();
		FragmentTransaction tr = fr.beginTransaction().setCustomAnimations(
				R.anim.fade_in,  // enter
				R.anim.fade_out,  // exit
				R.anim.fade_in,   // popEnter
				R.anim.fade_out  // popExit
		)
				;

		if (fragmentAAfficher == FRAGMENT_AFFICHE_GALLERIE)
			tr.show(_fragmentGallerie);
		else tr.hide(_fragmentGallerie);

		if (fragmentAAfficher == FRAGMENT_AFFICHE_IMAGE)
			tr.show(_fragmentImage);
		else tr.hide(_fragmentImage);

		if (fragmentAAfficher == FRAGMENT_AFFICHE_VIDEO)
			tr.show(_fragmentVideo);
		else tr.hide(_fragmentVideo);

		tr.commit();

		_fragmentGallerie.onShow(fragmentAAfficher == FRAGMENT_AFFICHE_GALLERIE);
		_fragmentImage.onShow(fragmentAAfficher == FRAGMENT_AFFICHE_IMAGE);
		_fragmentVideo.onShow(fragmentAAfficher == FRAGMENT_AFFICHE_VIDEO);
		_fragmentAffiche = fragmentAAfficher;
	}

	/***
	 * Verifie que toutes les permissions necessaires sont accordees par Android
	 * Demande les permission si necessaire
	 */
	private void checkAllPermissions()
	{
		boolean allOk = true;
		for (String p : PremiereActivity.PERMISSIONS)
			if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED)
			{
				allOk = false;
				break;
			}

		if (!allOk)
			requestPermissions(PremiereActivity.PERMISSIONS, PremiereActivity.PERMISSIONS_REQUEST_CODE);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		if (requestCode == PremiereActivity.PERMISSIONS_REQUEST_CODE)
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				initFragments();
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}


	@Override
	public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		_fragmentGallerie.createContextMenu(getMenuInflater(), menu, v);
	}


	/***
	 * Choix d'un item dans le menu contextuel
	 * @param item
	 */
	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item)
	{
		if (_fragmentGallerie.onContextItemSelected(item))
			return true;
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		switch (requestCode)
		{
			case PublicPrive.IMAGE_REQUEST_CODE:
			{
				if (_publiquePrive.activityResult(resultCode, data))
					_fragmentGallerie.refreshListe();
			}
			break;

			case SuppresseurDeMedia.IMAGE_REQUEST_CODE:
			{
				SuppresseurDeMedia.onAndroidResult(this, resultCode);
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/***
	 * Afficher le media
	 */
	private void onAfficher(@Nullable Media media)
	{
		// Afficher le media
		if (media != null)
		{
			if (media.isPhoto())
			{
				_fragmentImage.setMedia(this, media);
				afficheFragment(FRAGMENT_AFFICHE_IMAGE);
			}
			else if (media.isVideo())
			{
				_fragmentVideo.setMedia(this, media);
				afficheFragment(FRAGMENT_AFFICHE_VIDEO);
			}
		}
	}

	@Override
	protected void onPause()
	{
		PasswordManager.getInstance(this).annuleMotDePasse();
		super.onPause();
		_fragmentGallerie.videListeSiPrivees();
	}

	@Override
	public void onBackPressed()
	{
		switch (_fragmentAffiche)
		{
			case FRAGMENT_AFFICHE_VIDEO:
			case FRAGMENT_AFFICHE_IMAGE:
				//_fragmentImage.stop();
				_fragmentVideo.stop();
				afficheFragment(FRAGMENT_AFFICHE_GALLERIE);
				break;

			default:
				super.onBackPressed();
		}
	}
}