package com.testgithub.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.testgithub.db.FavoriteRepositoryEntity.Companion.TABLE_NAME
import org.jetbrains.annotations.NotNull

@Entity(tableName = TABLE_NAME)
class FavoriteRepositoryEntity(
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = COLUMN_ID)
    val id: String = "",
    @ColumnInfo(name = COLUMN_NAME)
    val name: String,
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    val description: String,
    @ColumnInfo(name = COLUMN_FORKS)
    val forks: Int,
    @ColumnInfo(name = COLUMN_SCORE)
    val score: Float,
    @ColumnInfo(name = COLUMN_CREATED_AT)
    val createdAt: String,
    @ColumnInfo(name = COLUMN_OWNER_ID)
    val ownerId: String,
    @ColumnInfo(name = COLUMN_OWNER_LOGIN)
    val ownerLogin: String,
    @ColumnInfo(name = COLUMN_OWNER_AVATAR_URL)
    val ownerAvatarUrl: String
) {
    companion object {
        const val TABLE_NAME = "repositories"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_FORKS = "forks"
        const val COLUMN_SCORE = "score"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_OWNER_ID = "owner_id"
        const val COLUMN_OWNER_LOGIN = "owner_login"
        const val COLUMN_OWNER_AVATAR_URL = "owner_avatar_url"
    }
}