package com.testgithub.repositories.model

import com.google.gson.annotations.SerializedName

data class Repository(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("owner") val owner: Owner,
    @SerializedName("description") val description: String? = "",
    @SerializedName("forks") val forks: Int = 0,
    @SerializedName("score") val stars: Float = 0F,
    @SerializedName("created_at") val dateCreate: String = "",
    val isFavorite: Boolean = false
)