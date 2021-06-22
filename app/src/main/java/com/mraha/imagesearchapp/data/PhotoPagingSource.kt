package com.mraha.imagesearchapp.data

import androidx.paging.PagingSource
import com.mraha.imagesearchapp.api.UnsplashApi
import retrofit2.HttpException
import java.io.IOException


class PhotoPagingSource(
    private val unsplashApi: UnsplashApi
) : PagingSource<Int, Photo>() {


    companion object {
        private const val UNSPLASH_STARTING_PAGE_INDEX = 1
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX

        return try {
            val response = unsplashApi.searchPhotos(position, params.loadSize)

            LoadResult.Page(
                data = response,
                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (response.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

}