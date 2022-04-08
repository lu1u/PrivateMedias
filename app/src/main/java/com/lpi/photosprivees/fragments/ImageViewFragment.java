package com.lpi.photosprivees.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lpi.photosprivees.MainActivity;
import com.lpi.photosprivees.R;
import com.lpi.photosprivees.customviews.ImageSource;
import com.lpi.photosprivees.customviews.SubsamplingScaleImageView;
import com.lpi.photosprivees.media.Media;
import com.lpi.photosprivees.media.MediaPrive;
import com.lpi.photosprivees.media.MediaPublique;
import com.lpi.photosprivees.media.PublicPrive;
import com.lpi.photosprivees.utils.AndroidGalleryManager;
import com.lpi.photosprivees.utils.MessageBoxUtils;

/**
 * Fragment pour afficher une image
 */
public class ImageViewFragment extends MediaViewFragment
{
	TextView _tvNom;
	//ZoomPanView _image;
	SubsamplingScaleImageView _image;


	public ImageViewFragment()
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
		View v = inflater.inflate(R.layout.fragment_image_view, container, false);
		_image = v.findViewById(R.id.sswImage);
		_tvNom = v.findViewById(R.id.tvInfosImage);

		initView(v);
		return v;
	}






	public void setMedia(@NonNull final MainActivity activity, @NonNull final Media media)
	{
		super.setMedia(activity, media);
		_tvNom.setText(media.getNom());
		//_image.setImageBitmap(media.getBitmap());
		_image.setImage(ImageSource.bitmap(media.getBitmap()).tilingEnabled());
	}
}