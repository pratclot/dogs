package com.pratclot.dogs.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDbDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(likeEntity: LikeEntity): Long

    @Update
    fun update(likeEntity: LikeEntity)

    @Query("SELECT * FROM likes")
    fun getAll(): Flowable<List<LikeEntity>>

    @Query("SELECT breed, SUM(liked) as totalLikes FROM likes GROUP BY breed HAVING totalLikes > 0")
    fun getLiked(): Flowable<List<LikedEntity>>

    @Query("SELECT * FROM likes WHERE imageUrl = :imageUrl")
    fun getLikeFor(imageUrl: String): Flow<LikeEntity>

    @Query("UPDATE likes SET liked = ((liked | 1) - (liked & 1)) WHERE imageUrl = :imageUrl")
    fun toggleLikeFor(imageUrl: String)

    @Query("SELECT * FROM likes WHERE breed = :breed AND liked = 1")
    fun getLikedImagesFor(breed: String): Single<List<LikeEntity>>
}