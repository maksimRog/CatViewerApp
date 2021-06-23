package com.mraha.imagesearchapp.ui.gallery

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mraha.imagesearchapp.R
import com.mraha.imagesearchapp.data.Photo
import com.mraha.imagesearchapp.database.AppDatabase
import com.mraha.imagesearchapp.databinding.ItemUnsplashPhotoBinding
import com.mraha.imagesearchapp.di.MEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch
import java.io.IOException


class GalleryAdapter(
    private val fragment: Fragment
) :
    PagingDataAdapter<Photo, GalleryAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            ItemUnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PhotoViewHolder(binding)
    }

    var appDatabase: AppDatabase

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }


    init {
        val entryPoint =
            EntryPointAccessors.fromApplication(fragment.requireContext(), MEntryPoint::class.java)
        appDatabase = entryPoint.getDataBase()
    }

    private suspend fun addToDataBase(photo: Photo?) {
        if (photo != null) {
            appDatabase.getDao().insertAll(photo)
            println()
        }
    }

    inner class PhotoViewHolder(public val binding: ItemUnsplashPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(photo: Photo) {
            if (photo.url != null) {
                itemView.setOnClickListener { createDialog(absoluteAdapterPosition).show() }
                binding.apply {
                    Glide.with(imageView.context)
                        .load(photo.url)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_error)
                        .into(imageView)
                }
            } else {
                binding.apply {
                    Glide.with(imageView.context).clear(imageView);
                    imageView.setImageDrawable(null);
                }
            }
        }
    }

    fun createDialog(position: Int): Dialog {

        val array = fragment.resources.getStringArray(R.array.dialog_actions)
        val builder = AlertDialog.Builder(fragment.requireContext())
        builder.setItems(
            array
        ) { dialog, which ->
            when (array[which]) {
                fragment.resources.getString(R.string.save_cat_to_favorites) -> {
                    Toast.makeText(
                        fragment.requireContext(),
                        "pos" + position, Toast.LENGTH_SHORT
                    ).show()
                    fragment.lifecycleScope.launch { addToDataBase(getItem(position)) }
                }
                fragment.resources.getString(R.string.save_cat_to_downloads) -> {
                    if (ActivityCompat.checkSelfPermission(
                            fragment.requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            fragment.requireActivity(),
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            fragment.resources.getInteger(R.integer.write_to_storage_request)
                        )
                    } else {
                        Glide.with(fragment.requireContext())
                            .asBitmap()
                            .load(getItem(position)?.url)
                            .listener(object : RequestListener<Bitmap> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Bitmap>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Bitmap?,
                                    model: Any?,
                                    target: Target<Bitmap>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    if (resource != null) {
                                        if (savePhotoToExternalStorage("temp", resource)) {
                                            fragment.requireActivity()
                                                .runOnUiThread {
                                                    Toast.makeText(
                                                        fragment
                                                            .requireContext(),
                                                        "Image saved to downloads",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            return true
                                        } else {
                                            fragment.requireActivity().runOnUiThread {
                                                Toast.makeText(
                                                    fragment
                                                        .requireContext(),
                                                    "Error while saving image",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }

                                        }
                                    }
                                    return false
                                }
                            }).submit()
                    }
                }
            }
        }
        return builder.create()
    }

    private fun savePhotoToExternalStorage(displayName: String, bmp: Bitmap): Boolean {
        val imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bmp.width)
            put(MediaStore.Images.Media.HEIGHT, bmp.height)
        }
        return try {
            val contentResolver = fragment.requireContext().contentResolver
            contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo) =
                oldItem == newItem
        }
    }
}