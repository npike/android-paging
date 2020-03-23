package com.example.android.codelabs.paging.data

import android.util.Log
import androidx.paging.PagingSource
import com.example.android.codelabs.paging.api.GithubService
import com.example.android.codelabs.paging.model.Repo
import retrofit2.HttpException
import java.io.IOException

class SearchReposDataSource(
        private val githubService: GithubService,
        private val searchTerm: String

) : PagingSource<Int, Repo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        return try {
            // TODO why does lint complain about execute here? We are in a suspending method.
            val response = githubService.searchRepos(searchTerm, params.key ?: 0, 5).execute()

            if (response.isSuccessful) {
                val nextPage = response.body()?.nextPage
                val repos = response.body()?.items ?: emptyList()
                LoadResult.Page(
                        data = repos,
                        prevKey = null,
                        nextKey = nextPage
                )

            } else {
                // TODO unclear
                // LoadResult.Error(e)
                Log.e("PagingSource", "Response not successful.")
                LoadResult.Error(throwable = RuntimeException("Response not successful."))
            }

        } catch (e: IOException) {
            // TODO unclear
            Log.e("PagingSource", e.message)
            LoadResult.Error(e)
        } catch (e: HttpException) {
            // TODO unclear
            Log.e("PagingSource", e.message)
            LoadResult.Error(e)
        }
    }
}