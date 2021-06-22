package com.mraha.imagesearchapp.ui.gallery

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mraha.imagesearchapp.R
import com.mraha.imagesearchapp.data.Photo
import com.mraha.imagesearchapp.database.AppDatabase
import com.mraha.imagesearchapp.databinding.ItemUnsplashPhotoBinding
import com.mraha.imagesearchapp.di.MEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


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
                            .asFile()
                            .load(getItem(position))
                            .into(object : CustomTarget<File>() {


                                override fun onLoadCleared(placeholder: Drawable?) {
                                }

                                override fun onResourceReady(
                                    resource: File,
                                    transition: Transition<in File>?
                                ) {
                                    val resolver = fragment.requireContext().contentResolver
                                    ContentValues().apply {
                                        put(MediaStore.Audio.Media.DISPLAY_NAME, "My Song.mp3")
                                    }
                                }

                            })
                    }
                }
            }
        }

        return builder.create()
    }

    @Throws(IOException::class)
    private fun saveImage(bitmap: Bitmap, name: String) {
        val fos: OutputStream?
        val resolver: ContentResolver = fragment.requireContext().contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.jpg")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        val imageUri: Uri? =
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = resolver.openOutputStream(Objects.requireNonNull(imageUri))

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        Objects.requireNonNull(fos).close()
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