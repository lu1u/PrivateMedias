package com.lpi.photosprivees.media;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import com.lpi.photosprivees.activities.DialogAvertissementPrives;
import com.lpi.photosprivees.MainActivity;
import com.lpi.photosprivees.R;
import com.lpi.photosprivees.utils.AndroidGalleryManager;
import com.lpi.photosprivees.utils.FileUtils;
import com.lpi.photosprivees.utils.SuppresseurDeMedia;

import java.io.File;

/***
 * Classe de gestion des deplacement des medias du public vers le prive et vice versa
 */
public class PublicPrive
{
	private static final String TAG = "PhotosPrivees";
	public static final String REPERTOIRE_PRIVE = "private";
	public static final String FICHIER_NO_MEDIA = ".nomedia";
	public static final String EXTENSION_FICHIER_INFOS = ".txt";
	private static String _repertoireRacine;
	private static String _cheminMediaPrive, _cheminMediaInformation; // Bug de startIntentSenderForResult ???
	private static Media _mediaASupprimer;

	private final Context _context;
	public static final int IMAGE_REQUEST_CODE = 1287;

	private static PublicPrive INSTANCE = null;

	public interface Listener
	{
		void onOk();
	}

	/***
	 * Obtenir l'instance (unique) de Preferences
	 */
	public static synchronized PublicPrive getInstance(@Nullable final Context context)
	{
		if (INSTANCE == null)
			if (context != null)
				INSTANCE = new PublicPrive(context);
		return INSTANCE;
	}

	/***
	 * Constructeur: preparer le repertoire Private
	 */
	private PublicPrive(@NonNull final Context context)
	{
		_context = context;
		try
		{
			// Repertoire de base de stockage des medias prives
			_repertoireRacine = context.getExternalFilesDir(REPERTOIRE_PRIVE).getAbsolutePath();
			FileUtils.creerRepertoireSiNonExiste(_repertoireRacine);

			// Creer un fichier .nomedia pour qu'il ne soit pas inclus dans la gallerie
			String noMedia = FileUtils.combine(_repertoireRacine, FICHIER_NO_MEDIA);
			FileUtils.creerFichier(noMedia);
		}
		catch (Exception e)
		{
			Log.e(TAG, "initRepertoire prive " + e.getLocalizedMessage());
		}
	}


	/***
	 * Rend privé un media qui etait publique
	 * @param activity
	 * @param media Le media a rendre prive
	 */
	public void rendPrive(@NonNull Activity activity, @NonNull final MediaPublique media, @NonNull final Listener listener)
	{
		// Avertir l'utilisateur que les medias prives risquent d'être perdus si l'application est
		// desinstallee
		DialogAvertissementPrives.executeSiOk(activity, () ->
		{
			try
			{
				_cheminMediaInformation = getCheminFichierInformation(media);
				_cheminMediaPrive = createFichierMediaPrive(media);

				// Enregistrer les informations pour le media prive
				media.ecritInfosDansFichier(_cheminMediaInformation, _cheminMediaPrive);

				// Copier le fichier vers le stockage des privés
				final String cheminOrigine = media.getMediaPath();
				FileUtils.copieFichier(cheminOrigine, _cheminMediaPrive);

				// Supprimer le media de la Gallerie Android
				supprimeMediaDeGallerie(activity, media);
				notification(activity, R.string.media_rendu_prive);
				listener.onOk();
			} catch (Exception e)
			{
				notification(activity, R.string.media_rendu_prive_erreur);
				Log.e(TAG, "rendPrive " + e.getLocalizedMessage());
			}
		});
	}


	/***
	 * Affiche un message de notification (Toast)
	 */
	private void notification(@NonNull final Activity activity, @StringRes int stringId)
	{
		Toast.makeText(activity, stringId, Toast.LENGTH_SHORT).show();
	}

	/***
	 * Supprime le media de la gallerie
	 */
	public void supprimeMediaDeGallerie(@NonNull Activity activity, @NonNull final Media media)
	{
		SuppresseurDeMedia.supprimeMedia(activity, media, new SuppresseurDeMedia.Listener()
		{
			@Override
			public void onOk()
			{
				AndroidGalleryManager.refreshGallery(_context, media.getMediaPath());
			}

			@Override
			public void onCanceled(int raison)
			{

			}
		});
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//			try
//		{
//			AndroidGalleryManager.deleteMedia(activity, media);
//			AndroidGalleryManager.refreshGallery(_context, media.getMediaPath());
//		} catch (RecoverableSecurityException re)
//		{
//			// Securité Android: le systeme demande à l'utilisateur la permission de supprimer le media
//			IntentSender intentSender = re.getUserAction().getActionIntent().getIntentSender();
//			_mediaASupprimer = media;
//			try
//			{
//				activity.startIntentSenderForResult(intentSender, IMAGE_REQUEST_CODE, null, 0, 0, 0, null);
//			} catch (IntentSender.SendIntentException e)
//			{
//				Log.e(TAG, e.getLocalizedMessage());
//			}
//		} catch (Exception e)
//		{
//			Log.e(TAG, e.getLocalizedMessage());
//		}
	}

	/***
	 * Calcule le chemin du fichier prive pour enregistrer les informations d'un media
	 */
	private String getCheminFichierInformation(@NonNull final Media media)
	{
		String fichier = FileUtils.changeExtension(FileUtils.getFileName(media.getMediaPath()), EXTENSION_FICHIER_INFOS);
		String repertoire = getRepertoireStockagePrive();
		return FileUtils.combine(repertoire, fichier);
	}

	/***
	 * Calcule le chemin du media dans l'espace prive
	 */
	private String createFichierMediaPrive(@NonNull final Media media)
	{
		String fichier = FileUtils.getFileName(media.getMediaPath());
		String repertoire = getRepertoireStockagePrive();
		return FileUtils.combine(repertoire, fichier);
	}

	/***
	 * Retroune un DocumentFile contenant un media privé
	 */
	private String getFichierMediaPrive(MediaPrive media)
	{
		String fichier = FileUtils.getFileName(media.getMediaPath());
		String repertoire = getRepertoireStockagePrive();
		return FileUtils.combine(repertoire, fichier);
	}


	/***
	 * Rendre publique un media qui est privé
	 */
	public void rendPublique(@NonNull Activity activity, @NonNull final MediaPrive media)
	{
		final String sourceMedia = getFichierMediaPrive(media);
		String sourceInformations = media.getFichierInformation();

		boolean renduPublique;
		try
		{
			// D'abord, essayer de remettre ce média dans son répertoire d'origine
			renduPublique = AndroidGalleryManager.addMedia(_context, media, sourceMedia, true);
		} catch (Exception e)
		{
			try
			{
				// Impossible de reecrire le media dans son dossier d'origine, essayer de le reecrire
				// dans le dossier standard
				renduPublique = AndroidGalleryManager.addMedia(_context, media, sourceMedia, false);
			} catch (Exception ex)
			{
				renduPublique = false;
			}
		}

		if (renduPublique)
		{
			// Supprimer les fichiers de l'espace prive
			FileUtils.supprimeFichier(sourceInformations);
			FileUtils.supprimeFichier(sourceMedia);

			notification(activity, R.string.media_rendu_publique);
		}
		else
			notification(activity, R.string.media_rendu_publique_erreur);
	}


	/***
	 * Traitement du resultat de la demande d'autorisation de modifier un media
	 */
	public boolean activityResult(int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			// Autorisation de supprimer un media accordee
			if (_mediaASupprimer != null)
			{
				try
				{
					AndroidGalleryManager.deleteMedia(_context, _mediaASupprimer);
					AndroidGalleryManager.refreshGallery(_context, _mediaASupprimer.getMediaPath());
				} catch (Exception e)
				{
					Log.e(MainActivity.TAG, e.getMessage());
				}
				_mediaASupprimer = null;
			}
			return true;
		}
		else
		{
			// Autorisation refusee, supprimer les fichiers prives qu'on avait preparés
			FileUtils.supprimeFichier(_cheminMediaPrive);
			FileUtils.supprimeFichier(_cheminMediaInformation);
		}

		return false;
	}

	public String getRepertoireStockagePrive()
	{
		return _repertoireRacine;
	}
}
