package com.mraha.imagesearchapp.ui.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mraha.imagesearchapp.R
import com.mraha.imagesearchapp.database.AppDatabase
import com.mraha.imagesearchapp.databinding.FragmentGalleryBinding
import com.mraha.imagesearchapp.ui.GalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_gallery) {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGalleryBinding.bind(view)


        binding.apply {
            recyclerView.setHasFixedSize(true)
        }

        appDatabase.getDao().getAll().observe(viewLifecycleOwner, {
            binding.recyclerView.adapter = FavoritesAdapter(it)
        })
    }
}