package com.mraha.imagesearchapp.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mraha.imagesearchapp.ItemClickedDialog
import com.mraha.imagesearchapp.R
import com.mraha.imagesearchapp.data.UnsplashPhoto
import com.mraha.imagesearchapp.databinding.ItemUnsplashPhotoBinding

class UnsplashPhotoAdapter(val galleryFragment: GalleryFragment) :
    PagingDataAdapter<UnsplashPhoto, UnsplashPhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            ItemUnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class PhotoViewHolder(private val binding: ItemUnsplashPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun showDialogForPosition(position: Int) {
            val bundle = Bundle()
            bundle.putInt("position", position)
            val dialog = ItemClickedDialog()
            dialog.arguments = bundle
            dialog.show(galleryFragment.childFragmentManager, "dialog fragment")
        }

        fun bind(photo: UnsplashPhoto) {
            itemView.setOnClickListener { showDialogForPosition(absoluteAdapterPosition) }
            binding.apply {
                Glide.with(itemView)
                    .load(photo.url)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageView)
            }
        }
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto) =
                oldItem == newItem
        }
    }
}