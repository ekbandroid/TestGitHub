package com.testgithub.repositories.search.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.testgithub.repositories.search.db.SearchedRepositoryEntity.Companion.TABLE_NAME
import org.jetbrains.annotations.NotNull

@Entity(tableName = TABLE_NAME)
data class SearchedRepositoryEntity(
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = COLUMN_ID)
    val id: String = "",
    @ColumnInfo(name = COLUMN_NAME)
    val name: String = "",
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    val description: String = "",
    @ColumnInfo(name = COLUMN_FORKS)
    val forks: Int = 0,
    @ColumnInfo(name = COLUMN_SCORE)
    val score: Float = 0F,
    @ColumnInfo(name = COLUMN_CREATED_AT)
    val createdAt: String = "",
    @ColumnInfo(name = COLUMN_OWNER_ID)
    val ownerId: String = "",
    @ColumnInfo(name = COLUMN_OWNER_LOGIN)
    val ownerLogin: String = "",
    @ColumnInfo(name = COLUMN_OWNER_AVATAR_URL)
    val ownerAvatarUrl: String = "",
    @ColumnInfo(name = COLUMN_IS_FAVORITE)
    val isFavorite: Boolean = false,
    @ColumnInfo(name = COLUMN_SEARCH_TEXT)
    val searchText: String = ""
) {
    companion object {
        const val TABLE_NAME = "searched_repositories"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_FORKS = "forks"
        const val COLUMN_SCORE = "score"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_OWNER_ID = "owner_id"
        const val COLUMN_OWNER_LOGIN = "owner_login"
        const val COLUMN_OWNER_AVATAR_URL = "owner_avatar_url"
        const val COLUMN_IS_FAVORITE = "is_favorite"
        const val COLUMN_SEARCH_TEXT = "search_text"
    }
}