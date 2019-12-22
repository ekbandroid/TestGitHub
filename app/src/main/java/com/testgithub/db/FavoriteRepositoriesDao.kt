package com.testgithub.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Maybe

@Dao
interface FavoriteRepositoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(repository: FavoriteRepositoryEntity)

    @Query(
        """
        DELETE FROM ${FavoriteRepositoryEntity.TABLE_NAME}
        WHERE ${FavoriteRepositoryEntity.COLUMN_ID}=:id
    """
    )
    fun delete(id: String)

    @Query(
        """
        SELECT * FROM ${FavoriteRepositoryEntity.TABLE_NAME}
        WHERE ${FavoriteRepositoryEntity.COLUMN_ID}=:id
    """
    )
    fun getFavoriteRepositoryById(id: String): Maybe<FavoriteRepositoryEntity>

    @Query("SELECT * FROM ${FavoriteRepositoryEntity.TABLE_NAME}")
    fun loadAllUsers(): Maybe<List<FavoriteRepositoryEntity>>
}