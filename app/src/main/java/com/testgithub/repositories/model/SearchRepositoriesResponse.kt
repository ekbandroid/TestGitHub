package com.testgithub.repositories.model

import com.google.gson.annotations.SerializedName

data class SearchRepositoriesResponse(
    @SerializedName("total_count") val itemsCount: Int = -1,
    @SerializedName("items") val data: List<Repository> = emptyList()
)