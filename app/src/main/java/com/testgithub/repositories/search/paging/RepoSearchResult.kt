package com.testgithub.repositories.search.paging

import androidx.paging.PagedList
import com.testgithub.repositories.model.Repository
import io.reactivex.Flowable

data class RepoSearchResult(
    val data: Flowable<PagedList<Repository>>,
    val networkState: Flowable<NetworkState>,
    val retryCallback: () -> Unit
    )
