package com.lpi.photosprivees.fragments;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.lpi.photosprivees.MainActivity;
import com.lpi.photosprivees.R;
import com.lpi.photosprivees.media.Media;

public class VideoViewFragment extends MediaViewFragment
{
	VideoView _videoView;
	TextView _tvNom;
	MediaController _mediaController;

	public VideoViewFragment()
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
		View v = inflater.inflate(R.layout.fragment_video_view, container, false);
		_videoView = v.findViewById(R.id.videoView);
		_tvNom = v.findViewById(R.id.tvInfosVideo);

		_mediaController = new MediaController(getContext());
		_mediaController.setAnchorView(_videoView);
		_mediaController.setMediaPlayer(_videoView);
		_mediaController.setEnabled(true);
		_videoView.setMediaController(_mediaController);
		initView(v);
		return v;
	}

	public void setMedia(@NonNull final MainActivity activity, @NonNull final Media media)
	{
		super.setMedia(activity, media);
		_tvNom.setText(media.getNom());
		_videoView.setVideoURI(Uri.parse(media.getMediaPath()));
		_videoView.start();

		_mediaController.setEnabled(true);
		_mediaController.show();
	}

	public void stop()
	{
		_videoView.stopPlayback();
	}

}