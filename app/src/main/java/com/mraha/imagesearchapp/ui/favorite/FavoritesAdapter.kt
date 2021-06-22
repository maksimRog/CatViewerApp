package com.mraha.imagesearchapp.ui.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mraha.imagesearchapp.R
import com.mraha.imagesearchapp.data.Photo

class FavoritesAdapter(private val photos: List<Photo>) :
    RecyclerView.Adapter<FavoritesAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_unsplash_photo, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Glide.with(holder.imageView)
            .load(photos[position].url)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_error)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class Holder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.image_view)
    }
}
