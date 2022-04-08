package com.lpi.photosprivees.fragments;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lpi.photosprivees.MainActivity;
import com.lpi.photosprivees.R;
import com.lpi.photosprivees.media.Media;
import com.lpi.photosprivees.media.MediaPrive;
import com.lpi.photosprivees.media.MediaPublique;
import com.lpi.photosprivees.media.PublicPrive;
import com.lpi.photosprivees.utils.AndroidGalleryManager;
import com.lpi.photosprivees.utils.MessageBoxUtils;
import com.lpi.photosprivees.utils.SuppresseurDeMedia;

public abstract class MediaViewFragment extends  MesFragments
{
	protected Media _media;
	protected MainActivity _mainActivity;
	protected ImageButton _ibInfos, _ibRendrePrive, _ibRendrePublique, _ibSupprimer;

	protected void initView(View v)
	{
		_ibInfos = v.findViewById(R.id.ibInfos);
		_ibRendrePrive = v.findViewById(R.id.ibRendrePrive);
		_ibRendrePublique = v.findViewById(R.id.ibRendrePublique);
		_ibSupprimer = v.findViewById(R.id.ibSupprimer);

		_ibInfos.setOnClickListener(view ->
		{
			if (_media != null)
				afficheInfosMedia();
		});

		_ibSupprimer.setOnClickListener(view ->
		{
			if (_media != null)
				supprimerMedia();
		});

		_ibRendrePrive.setOnClickListener(view ->
		{
			if (_media != null)
				rendrePrive();
		});

		_ibRendrePublique.setOnClickListener(view ->
		{
			if (_media != null)
				rendrePublique();
		});
	}


	/***
	 * Rendre le media Publique
	 */
	private void rendrePublique()
	{
		if (_media != null)
			if (_media.isPrive())
			{
				PublicPrive.getInstance(getContext()).rendPublique(getActivity(), (MediaPrive) _media);
				_mainActivity.onBackPressed();
			}
	}

	/***
	 * Rendre le media Privé
	 */
	private void rendrePrive()
	{
		if (_media != null)
			if (!_media.isPrive())
			{
				PublicPrive.getInstance(getContext()).rendPrive(getActivity(), (MediaPublique) _media, () -> _mainActivity.onBackPressed());
			}
	}

	/***
	 * Supprime le media
	 */
	private void supprimerMedia()
	{
		MessageBoxUtils.messageBox(getContext(), getString(R.string.supprimer_titre), getString(R.string.supprimer, _media.getNom()), MessageBoxUtils.BOUTON_OK | MessageBoxUtils.BOUTON_CANCEL,
				new MessageBoxUtils.Listener()
				{
					@Override
					public void onOk()
					{
						SuppresseurDeMedia.supprimeMedia(getActivity(), _media, new SuppresseurDeMedia.Listener()
								{
									@Override
									public void onOk()
									{
										Toast.makeText(getContext(), "Supprimé", Toast.LENGTH_SHORT).show();
										_mainActivity.onBackPressed();
									}

									@Override
									public void onCanceled(int raison)
									{

									}
								});
					}

					@Override
					public void onCancel()
					{

					}
				});
	}

	protected void setMedia(MainActivity activity, Media media)
	{
		_mainActivity = activity;
		_media = media;
		if (_media.isPrive())
		{
			// Afficher le bouton Rendre publique
			_ibRendrePublique.setVisibility(View.VISIBLE);
			_ibRendrePrive.setVisibility(View.GONE);
		}
		else
		{
			// Afficher le bouton Rendre Prive
			_ibRendrePublique.setVisibility(View.GONE);
			_ibRendrePrive.setVisibility(View.VISIBLE);
		}
	}

	/***
	 * Affiche les informations du media dans une fenetre flottante
	 */
	protected void afficheInfosMedia()
	{
		if ( _media !=null)
		{
			final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View dialogView = inflater.inflate(R.layout.image_infos, null);

			TextView tvInfos = dialogView.findViewById(R.id.tvInfos);
			tvInfos.setText(_media.getTexteInformations(getContext()));

			TextView tvNom = dialogView.findViewById(R.id.tvInfosNom);
			tvNom.setText(_media.getNom());

			dialogBuilder.setView(dialogView);
			dialogBuilder.show();
		}
	}

}
