package com.testgithub.repositories.search.api

import com.google.gson.annotations.SerializedName
import com.testgithub.repositories.model.Repository

data class SearchRepositoriesResponse(
    @SerializedName("total_count") val itemsCount: Int = -1,
    @SerializedName("items") val data: List<Repository> = emptyList()
)