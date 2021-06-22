package com.mraha.imagesearchapp.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mraha.imagesearchapp.data.Repository

class GalleryViewModel @ViewModelInject constructor(
    private val repository: Repository,
) : ViewModel() {
    val photos =
        repository.getSearchResults().cachedIn(viewModelScope)
}



