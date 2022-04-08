package com.lpi.photosprivees.media;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.photosprivees.R;
import com.lpi.photosprivees.options.OptionsAffichage;
import com.lpi.photosprivees.securite.PasswordManager;
import com.lpi.photosprivees.utils.BackgroundTaskWithProgress;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

/***
 * Classe de stockage de la liste des medias, utilisee par MediaRecyclerViewAdapter
 */
public class ListeMedias
{
	ArrayList<ListeItem> _medias = new ArrayList<>();

	/***
	 * Retourne le nombre de colonnes que doit occuper un element
	 */
	public int getSpanSize(int position)
	{
		return _medias.get(position).getSpanSize();
	}

	public interface Listener
	{
		void onListe(@NonNull final ListeMedias l);
	}


	public static void getListe(@NonNull final Activity activity, @NonNull final OptionsAffichage optionsAffichage, @NonNull final Listener listener)
	{
		ListeMedias liste = new ListeMedias();
		switch (optionsAffichage.getOptionVue())
		{
			case OptionsAffichage.OPTION_VUE_PRIVEES:
				getListePrivees(activity, liste, optionsAffichage, listener);
				break;
			case OptionsAffichage.OPTION_VUE_PUBLIQUES:
				getListePublique(activity, liste, optionsAffichage, listener);
				break;
			case OptionsAffichage.OPTION_VUE_TOUTES:
				getListeToutes(activity, liste, optionsAffichage, listener);
				break;
			default:
		}
	}

	/***
	 * Obtient la liste des medias publiques et prives
	 */
	private static void getListeToutes(@NonNull final Activity activity, @NonNull final ListeMedias liste, @NonNull final OptionsAffichage optionsAffichage, @NonNull final Listener listener)
	{
		// Demander le mot de passe si besoin
		PasswordManager.getInstance(activity).executeIfPasswordOk(activity, R.string.ui_afficher_prives, new PasswordManager.Listener()
		{
			@Override
			public void onOK()
			{
				BackgroundTaskWithProgress.execute(activity, R.layout.working_spinner, new BackgroundTaskWithProgress.Listener()
				{
					@Override
					public void execute()
					{
						MediaPrive.ajouteMedias(activity, liste._medias, optionsAffichage.getOptionType());
						MediaPublique.ajouteMedias(activity, liste._medias, optionsAffichage.getOptionType());
					}

					@Override
					public void onFinished()
					{
						liste.regroupe(optionsAffichage.getOptionTri());
						listener.onListe(liste);
					}
				});
			}

			@Override
			public void onCancel()
			{
			}
		});
	}

	/***
	 * Regroupe les
	 * @param optionTri
	 * @return
	 */
	private void regroupe(int optionTri)
	{
		switch (optionTri)
		{
			case OptionsAffichage.OPTION_TRI_EMPLACEMENT:
				regroupeNom();
				break;
			case OptionsAffichage.OPTION_TRI_CREATION:
				regroupeCreation();
				break;
			default:
		}
	}

	/**
	 * Regroupe les medias par jour de creation et intercale un entete entre chaque categorie
	 */
	private void regroupeCreation()
	{
		if (_medias.size() > 0)
		{
			// D'abord trier les medias
			triParDates();

			ArrayList<ListeItem> result = new ArrayList<>();

			// Intercaler des entetes entre chaque categorie d'item
			Calendar cEntete = Calendar.getInstance();
			cEntete.setTimeInMillis(_medias.get(0).getDateCreation());
			Entete dernierEntete = new Entete(dateToString(cEntete));
			int nbElements = 0;
			result.add(dernierEntete);

			for (ListeItem item : _medias)
				if (item instanceof Media)
				{
					Media media = (Media) item;
					long date = media.getDateCreation();
					if (jourDifferent(cEntete, date))
					{
						dernierEntete.setNombre(nbElements);

						cEntete.setTimeInMillis(date);
						dernierEntete = new Entete(dateToString(cEntete));
						result.add(dernierEntete);
						nbElements = 0;
					}
					nbElements++;
					result.add(media);
				}
			dernierEntete.setNombre(nbElements);

			_medias = result;
		}
	}

	/***
	 * Regroupe les medias par Nom (bucket) et intercale un entete entre chaque categorie
	 */
	private void regroupeNom()
	{
		if (_medias.size() > 0)
		{
			triParCategotie();
			ArrayList<ListeItem> result = new ArrayList<>();
			// Intercaler des entetes entre chaque categorie d'item
			String catEntete = _medias.get(0).getCategorie();
			Entete dernierEntete = new Entete(catEntete);
			result.add(dernierEntete);
			int nbElements = 0;

			for (ListeItem item : _medias)
				if (item instanceof Media)
				{
					Media media = (Media) item;
					String catItem = media.getCategorie();
					if (!catEntete.equals(catItem))
					{
						// Nombre d'elements dans la categorie
						dernierEntete.setNombre(nbElements);
						catEntete = catItem;

						dernierEntete = new Entete(catEntete);
						result.add(dernierEntete);
						nbElements = 0;
					}

					nbElements++;
					result.add(media);
				}
			dernierEntete.setNombre(nbElements);
			_medias = result;
		}
	}

	private boolean jourDifferent(Calendar cEntete, long date)
	{
		Calendar cDate = Calendar.getInstance();
		cDate.setTimeInMillis(date);

		if (cEntete.get(Calendar.YEAR) != cDate.get(Calendar.YEAR))
			return true;

		if (cEntete.get(Calendar.DAY_OF_YEAR) != cDate.get(Calendar.DAY_OF_YEAR))
			return true;

		return false;
	}

	private String dateToString(Calendar cDate)
	{
		final int year = cDate.get(Calendar.YEAR);
		final int month = cDate.get(Calendar.MONDAY) + 1;
		final int day = cDate.get(Calendar.DAY_OF_MONTH);
		//Log.d(MainActivity.TAG, "dateToString "  + day + "/" + month + "/" + year);

		LocalDate localDate = LocalDate.of(year, month, day);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
		return localDate.format(dateFormatter);
	}


	/***
	 * Obtient la liste des medias publiques
	 */
	private static void getListePublique(@NonNull final Activity activity, @NonNull final ListeMedias liste, @NonNull final OptionsAffichage optionsAffichage, @NonNull final Listener listener)
	{
		BackgroundTaskWithProgress.execute(activity, R.layout.working_spinner, new BackgroundTaskWithProgress.Listener()
		{
			@Override
			public void execute()
			{
				MediaPublique.ajouteMedias(activity, liste._medias, optionsAffichage.getOptionType());
			}

			@Override
			public void onFinished()
			{
				liste.regroupe(optionsAffichage.getOptionTri());
				listener.onListe(liste);
			}
		});
	}

	/***
	 * Obtient la liste des medias prives
	 */
	private static void getListePrivees(@NonNull final Activity activity, @NonNull final ListeMedias liste, @NonNull final OptionsAffichage optionsAffichage, @NonNull final Listener listener)
	{
		// Demander le mot de passe si besoin
		PasswordManager.getInstance(activity).executeIfPasswordOk(activity, R.string.ui_afficher_prives, new PasswordManager.Listener()
		{
			@Override
			public void onOK()
			{
				BackgroundTaskWithProgress.execute(activity, R.layout.working_spinner, new BackgroundTaskWithProgress.Listener()
				{
					@Override
					public void execute()
					{
						MediaPrive.ajouteMedias(activity, liste._medias, optionsAffichage.getOptionType());
					}

					@Override
					public void onFinished()
					{
						liste.regroupe(optionsAffichage.getOptionTri());
						listener.onListe(liste);
					}
				});
			}

			@Override
			public void onCancel()
			{
			}
		});
	}

	private ListeMedias()
	{
	}


	private void triParDates()
	{
		_medias.sort((i1, i2) -> - Long.compare(i1.getDateCreation(), i2.getDateCreation()));
	}

	private void triParCategotie()
	{
		_medias.sort((m1, m2) ->
		{
			String n1 = m1.getCategorie();
			if (n1 == null)
				return -1;
			String n2 = m2.getCategorie();
			if (n2 == null)
				return 1;
			return n1.compareTo(n2);
		});
	}


	public int getCount()
	{
		return _medias.size();
	}

	public @Nullable
	ListeItem getMedia(int position)
	{
		if (position < 0 || position >= _medias.size())
			return null;
		return _medias.get(position);
	}

	public int getNbImages()
	{
		int i = 0;
		for (ListeItem m : _medias)
		{
			if (m instanceof Media)
				if (((Media) m).isPhoto())
					i++;
		}
		return i;
	}

	public int getNbVideos()
	{
		int i = 0;
		for (ListeItem m : _medias)
		{
			if (m instanceof Media)
				if (((Media) m).isVideo())
					i++;
		}
		return i;
	}

	public int getNbMedias()
	{
		return _medias.size();
	}
}
