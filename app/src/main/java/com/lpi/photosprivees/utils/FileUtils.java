package com.lpi.photosprivees.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.photosprivees.MainActivity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystemException;


/**
 * Created by lucien on 15/01/2018.
 * Ensemble de fonctions pour manipuler les fichiers et noms de fichiers
 */

public class FileUtils
	{
	private static final String TAG = "PhotosPrivees";
	public static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	public static final String PATH_SEPARATOR = File.separator;
	static private String _invalidPathChars;


	/***
	 * Lire le contenu d'un InputStream et le copier dans un tableau d'octets
	 */
	public static @NonNull byte[] readArray(@NonNull InputStream is)
		{
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		try
			{
			int nRead;
			byte[] data = new byte[16384];

			while ((nRead = is.read(data, 0, data.length)) != -1)
				buffer.write(data, 0, nRead);

			buffer.flush();
			} catch (Exception e)
			{
			Log.e(MainActivity.TAG, e.getMessage());
			}

		return buffer.toByteArray();
		}


	/***
	 * Change l'extension d'un nom de fichier
	 */
	@NonNull
	public static String changeExtension(@NonNull String name, @Nullable String extension)
		{
		int indicePoint = name.lastIndexOf('.');
		if (indicePoint != -1)
			{
			int indiceSlash = name.lastIndexOf(PATH_SEPARATOR);
			if (indiceSlash < indicePoint)
				name = name.substring(0, indicePoint);
			}

		if (extension != null)
			{
			if (extension.startsWith("."))
				name += extension;
			else
				name += "." + extension;
			} else
			{
			// Eventuellement, supprimer le . a la fin
			if (name.charAt(name.length() - 1) == '.')
				{
				name = name.substring(0, name.length() - 1);
				}
			}
		return name;
		}

	/***
	 * Met l'extension d'un nom de fichier
	 */
	@NonNull
	public static String setExtension(@NonNull String name, @NonNull String extension)
		{
		String res;
		if (extension.charAt(0) != '.')
			extension = "." + extension;

		int indicePoint = name.lastIndexOf('.');
		if (indicePoint == -1)
			res = name + extension;
		else
			res = name + extension.substring(1);

		return res;
		}


	/***
	 * Copie d'un fichier
	 * @param i : fichier d'entree
	 * @param o : fichier de sortie
	 */
	public static long copieFichier(InputStream i, OutputStream o) throws IOException
		{
		try
			{
			long nbCopies = 0;
			byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = i.read(buf)) > 0)
				{
				o.write(buf, 0, length);
				nbCopies += length;
				}
			return nbCopies;
			}
		catch (Exception e)
			{
			Log.e(TAG, e.getMessage());
			throw  e;
			}
		}

	/***
	 * Copie d'un fichier
	 * @param source : fichier d'entree
	 * @param destination : fichier de sortie
	 */
	public static void copieFichier(@NonNull final String source, @NonNull final String destination) throws IOException
		{
		try
			{
			Log.e(TAG, "Copie du fichier " + source + " vers " + destination);

			FileInputStream inputStream = new FileInputStream(source);
			FileOutputStream outputStream = new FileOutputStream(destination);

			long n = FileUtils.copieFichier(inputStream, outputStream);
			Log.e(TAG, n + " octets copiés sur " + new File(source).length());

			inputStream.close();
			outputStream.close();

			File fSource = new File(source);
			File fdest = new File(destination);
			fdest.setLastModified(fSource.lastModified());
			}
		catch (IOException e)
			{
			Log.e(TAG, e.getLocalizedMessage());
			throw e;
			}
		}

//	public static void deplaceFichier(@NonNull final String source, @NonNull final String destination) throws IOException
//		{
//		try
//			{
//			copieFichier(source, destination);
//			File f = new File(source);
//			f.delete();
//			} catch (IOException e)
//			{
//			Log.e(TAG, e.getLocalizedMessage());
//			throw e;
//			}
//		}

	/***
	 * Retourne vrai si le nom de fichier se termine par l'extension donnee
	 */
	public static boolean extensionOK(String name, String extension)
		{
		if (extension == null)
			return true;

		if (name == null)
			return false;

		if (name.length() < extension.length())
			return false;

		if (!extension.startsWith("."))
			extension = '.' + extension;

		int i = name.lastIndexOf(extension);
		if (i == -1)
			return false;

		return i == (name.length() - extension.length());
		}


	/***
	 * Combine deux parties d'un chemin de fichier
	 */
	public static String combine(String partage, String path)
		{
		if (partage.endsWith(PATH_SEPARATOR))
			return partage + path;
		else
			return partage + PATH_SEPARATOR + path;
		}

	static public @Nullable String getExtension(@Nullable String fileName)
		{
		if ( fileName == null)
			return null;
		//return MimeTypeMap.getFileExtensionFromUrl(fileName);
			int indice = fileName.lastIndexOf('.');
			if( indice == -1)
				return "";
			return fileName.substring(indice+1);
		}

	/***
	 * Lit un fichier raw contenu dans les ressources et l'ecrit dans un outputstream
	 */
	public static void copyRawResource(final Context context,
	                                   final BufferedOutputStream outputStream, final int rawFileId) throws Exception
		{
		InputStream inputStream = context.getResources().openRawResource(rawFileId);

		int size;
		byte[] buffer = new byte[1024];
		while ((size = inputStream.read(buffer)) != -1)
			{
			outputStream.write(buffer, 0, size);
			}
		inputStream.close();
		}

	/***
	 * Extrait le dernier repertoire d'un chemin
	 */
	public static @NonNull
	String dernierRepertoire(@NonNull final String repertoire)
		{
		int indice = repertoire.lastIndexOf(PATH_SEPARATOR);
		if (indice == -1)
			return repertoire;

		return repertoire.substring(indice + 1);
		}

	public static String getInvalidPathChar()
		{
		return _invalidPathChars;
		}

	public static void setInvalidPathChar(String s)
		{
		_invalidPathChars = s + "?:!*><\"|%";
		}

	/***
	 * Retrouve le repertoire parent d'un chemin, en supprimant la derniere partie
	 */
	public static @NonNull String getParent(@NonNull final String path)
		{
		final int indice = path.lastIndexOf(PATH_SEPARATOR);
		if (indice == -1)
			return path;

		return path.substring(0, indice);
		}

	/***
	 * Retourne un chemin, en enlevant l'extension
	 */
	public static @NonNull String sansExtension(@NonNull final String path)
		{
		int indice = path.lastIndexOf('.');
		if (indice == -1)
			return path;

		return path.substring(0, indice);
		}

	/***
	 * Retrouve le nom du fichier débarassé du chemin
	 */
	public static @NonNull String getFileName(@NonNull final String path)
		{
		final int indice = path.lastIndexOf(PATH_SEPARATOR);
		if (indice == -1)
			return path;

		return path.substring(indice + 1);
		}

	/***
	 * Retourne le nom du fichier extrait du chemin, sans extension
	 */
	public static @NonNull String fileNameSansExtension(@NonNull final String fileName)
		{
		return sansExtension(getFileName(fileName));
		}

	/***
	 * Creer un repertoire sur le telephone s'il n'existe pas
	 */
	public static void creerRepertoireSiNonExiste(final String chemin) throws Exception
		{
		try
			{
			File f = new File(chemin);
			if (!f.exists())
				if (!f.mkdirs())
					throw new FileSystemException(chemin);
			}
			catch ( Exception e)
				{
				Log.e(MainActivity.TAG, e.getMessage());
				throw e;
				}
		}

	/***
	 * Verifie qu'un fichier existe
	 */
	public static boolean existe(final String fichier)
		{
		return new File(fichier).exists();
		}

//	/***
//	 * Change la date de creation d'un fichier
//	 * @param path
//	 * @param date
//	 */
//	public static void setDate(final String path, final long date) throws Exception
//		{
//		new File(path).setLastModified(date);
//		}

	/***
	 * Creer un fichier vide
	 */
	public static void creerFichier(String fileName)
		{
			try
			{
				FileOutputStream fo = new FileOutputStream(fileName);
				fo.flush();
				fo.close();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
//		File f = new File(fileName);
//		try
//			{
//			f.createNewFile();
//			} catch (IOException e)
//			{
//			e.printStackTrace();
//			}
		}

	public static void supprimeFichier(String chemin)
		{
			if ( chemin==null)
				return;

			try
			{
				File f = new File(chemin);
				f.delete();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
