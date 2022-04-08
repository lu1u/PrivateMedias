package com.lpi.photosprivees.media;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.lpi.photosprivees.R;


public class MediasRecyclerViewAdapter extends RecyclerView.Adapter<MediasRecyclerViewAdapter.MyViewHolder>
{
	public interface Listener
	{
		void onClic(@Nullable final ListeItem item);
	}

	private final LayoutInflater _inflater;
	private final ListeMedias _medias;
	private int _selectedItem = 0;
	private final Listener _listener;

	// data is passed into the constructor
	public MediasRecyclerViewAdapter(@NonNull final Context context, @NonNull final ListeMedias c, @NonNull final Listener listener)
	{
		_inflater = LayoutInflater.from(context);
		_medias = c;
		_listener = listener;
	}

	@NonNull
	@Override
	public MediasRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View view = null;
		switch (viewType)
		{
			case ListeItem.TYPE_ENTETE:
				return new ViewHolderEntete(_inflater.inflate(R.layout.element_liste_entete, parent, false));
			case ListeItem.TYPE_MEDIA:
				return new ViewHolderMedia(_inflater.inflate(R.layout.element_liste, parent, false));
		}

		return null;
	}


	@Override
	public void onBindViewHolder(@NonNull MediasRecyclerViewAdapter.MyViewHolder holder, int position)
	{
		final ListeItem media = _medias.getMedia(position);
		if (media != null)
		{
			media.remplitListeItem(_inflater.getContext(), holder);

//			holder.itemView.setOnCreateContextMenuListener((contextMenu, view, contextMenuInfo) ->
//			{
//				contextMenu.setHeaderTitle(media.getNom());
//
//				if (media.isPrive())
//				{
//					if (contextMenu.findItem(R.id.menu_action_rendre_publique) == null)
//						contextMenu.add(0, R.id.menu_action_rendre_publique, 0, R.string.menu_rendre_publique);
//				}
//				else
//				{
//					if (contextMenu.findItem(R.id.menu_action_rendre_prive) == null)
//						contextMenu.add(0, R.id.menu_action_rendre_prive, 0, R.string.menu_rendre_prive);
//				}
//			});

			holder.itemView.setOnClickListener(view ->
			{
				if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) return;

				// Updating old as well as new positions
				notifyItemChanged(_selectedItem);
				_selectedItem = holder.getAdapterPosition();
				notifyItemChanged(_selectedItem);
				ListeItem m = get(_selectedItem);
				_listener.onClic(m);
			});
		}
	}

	@Override
	public int getItemCount()
	{
		if (_medias == null)
			return 0;
		else
			return _medias.getCount();
	}


	@Override
	public int getItemViewType(int position)
	{
		return _medias.getMedia(position).getItemType();
	}

	public @Nullable
	ListeItem get(int position)
	{
		return _medias.getMedia(position);
	}

	public int getSelectedItem()
	{
		return _selectedItem;
	}

	public abstract class MyViewHolder extends RecyclerView.ViewHolder
	{
		MyViewHolder(View itemView)
		{
			super(itemView);
		}
	}

	public class ViewHolderMedia extends MyViewHolder
	{
		public final ImageView imThumbnail;
		public final ImageView imPrivee;
		public final ImageView imType;
		public final TextView tvLongueur;

		public ViewHolderMedia(View itemView)
		{
			super(itemView);
			imThumbnail = itemView.findViewById(R.id.imThumbnail);
			imPrivee = itemView.findViewById(R.id.imPrivee);
			imType = itemView.findViewById(R.id.ivType);
			tvLongueur = itemView.findViewById(R.id.tvLongueurVideo);

			itemView.setOnLongClickListener(view ->
			{
				notifyItemChanged(_selectedItem);
				_selectedItem = getAdapterPosition();
				notifyItemChanged(_selectedItem);
				return false;
			});
		}
	}

	public class ViewHolderEntete extends MyViewHolder
	{
		public final TextView tvEntete;
		public final TextView tvNombre;

		public ViewHolderEntete(View itemView)
		{
			super(itemView);
			tvEntete = itemView.findViewById(R.id.tvEntete);
			tvNombre = itemView.findViewById(R.id.tvNb);
		}
	}
}
