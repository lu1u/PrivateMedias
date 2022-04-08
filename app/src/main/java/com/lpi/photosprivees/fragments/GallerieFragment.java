package com.lpi.photosprivees.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.lpi.photosprivees.R;
import com.lpi.photosprivees.activities.DialogAPropos;
import com.lpi.photosprivees.activities.SaisieMotDePasseActivity;
import com.lpi.photosprivees.media.ListeItem;
import com.lpi.photosprivees.media.ListeMedias;
import com.lpi.photosprivees.media.Media;
import com.lpi.photosprivees.media.MediaPrive;
import com.lpi.photosprivees.media.MediaPublique;
import com.lpi.photosprivees.media.MediasRecyclerViewAdapter;
import com.lpi.photosprivees.media.PublicPrive;
import com.lpi.photosprivees.options.OptionsAffichage;
import com.lpi.photosprivees.utils.Preferences;

public class GallerieFragment extends MesFragments
{
	RecyclerView _rvMedias;
	TextView _infosListeMedias;
	MediasRecyclerViewAdapter _adapter;
	private OptionsAffichage _optionsAffichage;
	private PublicPrive _publiquePrive;
	ImageButton _ibSort, _ibMenu;
	Switch _swVoirPubliques, _swVoirPrives, _swVoirImages, _swVoirVideos;

	public GallerieFragment()
	{
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_gallerie, container, false);
		Preferences preferences = Preferences.getInstance(getContext());
		_optionsAffichage = new OptionsAffichage(preferences);
		_publiquePrive = PublicPrive.getInstance(getContext());

		_rvMedias = view.findViewById(R.id.rvListeMedias);
		_infosListeMedias = view.findViewById(R.id.tvInfosListeMedias);
		_rvMedias.setLayoutManager((new GridLayoutManager(getContext(), 3)));

		_rvMedias.setOnClickListener(v -> view.setSelected(true));
		initToolbar(view);
		//registerForContextMenu(_rvMedias);
		changeAdapter();
		return view;
	}


	/***
	 * Initialisation des boutons de la toolbar
	 */
	private void initToolbar(@NonNull final View view)
	{
		_ibSort = view.findViewById(R.id.ibSort);
		_ibSort.setOnClickListener(view16 -> OptionsAffichage.startOptionTri(getActivity(), _optionsAffichage.getOptionTri(), selection ->
		{
			_optionsAffichage.setOptionTri(selection);
			changeAdapter();
		}));

		_swVoirPubliques = view.findViewById(R.id.swVoirPublique);
		_swVoirPubliques.setChecked((_optionsAffichage.getOptionVue() & OptionsAffichage.OPTION_VUE_PUBLIQUES) !=0);
		_swVoirPubliques.setOnClickListener(view15 ->
		{
			int optionVue = _optionsAffichage.getOptionVue();
			if ( _swVoirPubliques.isChecked())
				optionVue = optionVue | OptionsAffichage.OPTION_VUE_PUBLIQUES;
			else
				optionVue = optionVue & (~OptionsAffichage.OPTION_VUE_PUBLIQUES);
			if (optionVue == 0)
				optionVue = OptionsAffichage.OPTION_VUE_PUBLIQUES;
			_optionsAffichage.setOptionVue(optionVue);
			//changeButtonTint(_ibVoirPublique, (optionVue & OptionsAffichage.OPTION_VUE_PUBLIQUES) != 0);
			changeAdapter();
		});

		_swVoirPrives = view.findViewById(R.id.swVoirPrive);
		_swVoirPrives.setChecked((_optionsAffichage.getOptionVue() & OptionsAffichage.OPTION_VUE_PRIVEES) !=0);
		_swVoirPrives.setOnClickListener(view15 ->
		{
			int optionVue = _optionsAffichage.getOptionVue();
			if ( _swVoirPrives.isChecked())
				optionVue = optionVue | OptionsAffichage.OPTION_VUE_PRIVEES;
			else
				optionVue = optionVue & (~OptionsAffichage.OPTION_VUE_PRIVEES);
			if (optionVue == 0)
				optionVue = OptionsAffichage.OPTION_VUE_PRIVEES;
			_optionsAffichage.setOptionVue(optionVue);
			//changeButtonTint(_ibVoirPublique, (optionVue & OptionsAffichage.OPTION_VUE_PUBLIQUES) != 0);
			changeAdapter();
		});

		_swVoirImages = view.findViewById(R.id.swVoirImages);
		_swVoirImages.setChecked((_optionsAffichage.getOptionType() & OptionsAffichage.OPTION_TYPES_IMAGES) !=0);
		_swVoirImages.setOnClickListener(view15 ->
		{
			int optionVue = _optionsAffichage.getOptionType();
			if ( _swVoirImages.isChecked())
				optionVue = optionVue | OptionsAffichage.OPTION_TYPES_IMAGES;
			else
				optionVue = optionVue & (~OptionsAffichage.OPTION_TYPES_IMAGES);
			if (optionVue == 0)
				optionVue = OptionsAffichage.OPTION_TYPES_VIDEOS;
			_optionsAffichage.setOptionType(optionVue);
			//changeButtonTint(_ibVoirPublique, (optionVue & OptionsAffichage.OPTION_VUE_PUBLIQUES) != 0);
			changeAdapter();
		});

		_swVoirVideos = view.findViewById(R.id.swVoirVideos);
		_swVoirVideos.setChecked((_optionsAffichage.getOptionType() & OptionsAffichage.OPTION_TYPES_VIDEOS) !=0);
		_swVoirVideos.setOnClickListener(view15 ->
		{
			int optionVue = _optionsAffichage.getOptionType();
			if ( _swVoirVideos.isChecked())
				optionVue = optionVue | OptionsAffichage.OPTION_TYPES_VIDEOS;
			else
				optionVue = optionVue & (~OptionsAffichage.OPTION_TYPES_VIDEOS);
			if (optionVue == 0)
				optionVue = OptionsAffichage.OPTION_TYPES_IMAGES;
			_optionsAffichage.setOptionType(optionVue);
			//changeButtonTint(_ibVoirPublique, (optionVue & OptionsAffichage.OPTION_VUE_PUBLIQUES) != 0);
			changeAdapter();
		});

		_ibMenu = view.findViewById(R.id.ibMenu);
		registerForContextMenu(_ibMenu);
		_ibMenu.setOnClickListener(view1 ->
		{
			_ibMenu.showContextMenu();
		});
	}

	@Override
	public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		if (v == _ibMenu)
			inflater.inflate(R.menu.menu_gallerie, menu);
	}

	/***
	 * Configurer l'adapteur de taches de la RecyclerView (a faire après chaque modification dans la table des Taches),
	 * avertir les Widgets du changement
	 */
	private void changeAdapter()
	{
		_rvMedias.setAdapter(null);
		_swVoirPubliques.setChecked((_optionsAffichage.getOptionVue() & OptionsAffichage.OPTION_VUE_PUBLIQUES) != 0);
		_swVoirPrives.setChecked((_optionsAffichage.getOptionVue() & OptionsAffichage.OPTION_VUE_PRIVEES) != 0);
		_swVoirImages.setChecked((_optionsAffichage.getOptionType() & OptionsAffichage.OPTION_TYPES_IMAGES) != 0);
		_swVoirVideos.setChecked((_optionsAffichage.getOptionType() & OptionsAffichage.OPTION_TYPES_VIDEOS) != 0);

		// On nous demandera peut être de saisir le mot de passe avant d'afficher les privees
		ListeMedias.getListe(getActivity(), _optionsAffichage, listeMedias ->
		{
			GridLayoutManager manager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
			manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
			{
				@Override
				public int getSpanSize(int position)
				{
					return listeMedias.getSpanSize(position);
				}
			});

			_rvMedias.setLayoutManager(manager);
			_adapter = new MediasRecyclerViewAdapter(getActivity(), listeMedias, (final ListeItem media) ->
			{
				if (_listener != null)
					_listener.onItemClic(media);
			});


			_rvMedias.setAdapter(_adapter);

			String vue;
			switch (_optionsAffichage.getOptionVue())
			{
				case OptionsAffichage.OPTION_VUE_PRIVEES:
					vue = getString(R.string.option_vue_privees);
					break;
				case OptionsAffichage.OPTION_VUE_PUBLIQUES:
					vue = getString(R.string.option_vue_publiques);
					break;
				case OptionsAffichage.OPTION_VUE_TOUTES:
					vue = getString(R.string.option_vue_toutes);
					break;
				default:
					vue = "";
			}

			String texte = getString(R.string.info_liste_medias, listeMedias.getNbMedias(), listeMedias.getNbImages(), listeMedias.getNbVideos(), vue);
			_infosListeMedias.setText(texte);
			_infosListeMedias.setVisibility(View.VISIBLE);
			cacheApresUnDelay(_infosListeMedias);
		});
	}

	private void cacheApresUnDelay(View view)
	{
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run()
			{
				Transition transition = new Fade();
				transition.setDuration(600);
				transition.addTarget(view);

				TransitionManager.beginDelayedTransition( (ViewGroup)view.getParent(), transition);
				view.setVisibility( View.GONE);
			}
		}, 5 * 1000);
	}

	public void refreshListe()
	{
		changeAdapter();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		changeAdapter();
	}

	/***
	 * Selection d'un menu dans le menu contextuel
	 */
	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item)
	{
			switch (item.getItemId())
			{
//				case R.id.menu_action_rendre_prive:
//				{
//					ListeItem it = _adapter.get(_adapter.getSelectedItem());
//					if (it instanceof Media)
//					{
//						Media media = (Media)it;
//						onRendrePrive(media);
//						return true;
//					}
//				}
//
//				case R.id.menu_action_rendre_publique:
//				{
//					ListeItem it = _adapter.get(_adapter.getSelectedItem());
//					if (it instanceof Media)
//					{
//						Media media = (Media)it;
//						onRendrePublique(media);
//						return true;
//					}
//				}

				case R.id.action_a_propos:
					DialogAPropos.start(getActivity());
					return true;

				case R.id.action_mot_de_passe:
					saisieMotDePasse();
					return true;
			}

		return super.onContextItemSelected(item);
	}

	private void saisieMotDePasse()
	{
		SaisieMotDePasseActivity.start(getActivity(), new SaisieMotDePasseActivity.Listener()
		{
			@Override
			public void onOK()
			{
				changeAdapter();
			}

			@Override
			public void onCancel()
			{

			}
		});
	}

	private void onRendrePublique(@Nullable Media media)
	{
		if (media == null)
			return;

		if (media.isPrive())
			if (media instanceof MediaPrive)
			{
				_publiquePrive.rendPublique(getActivity(), (MediaPrive) media);
				changeAdapter();
			}
	}

	/***
	 * Rendre un media prive
	 */
	private void onRendrePrive(@Nullable Media media)
	{
		if (media == null)
			return;

		if (!media.isPrive())
			if (media instanceof MediaPublique)
			{
				_publiquePrive.rendPrive(getActivity(), (MediaPublique) media, () -> changeAdapter());
			}
	}

	public void videListeSiPrivees()
	{
		if ((_optionsAffichage.getOptionVue() & OptionsAffichage.OPTION_VUE_PRIVEES) != 0)
			_rvMedias.setAdapter(null);
	}

	@Override
	public void onShow(boolean visible)
	{
		if ( visible)
			changeAdapter();
	}
}