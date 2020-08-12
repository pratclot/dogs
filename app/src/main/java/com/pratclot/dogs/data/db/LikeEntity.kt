package com.pratclot.dogs.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pratclot.dogs.domain.BreedImages
import com.pratclot.dogs.domain.Like
import com.pratclot.dogs.domain.LikedBreed

@Entity(tableName = "likes")
data class LikeEntity(
    @ColumnInfo(name = "breed")
    val breed: String,
    @ColumnInfo(name = "parentBreed")
    val parentBreed: String?,
    @ColumnInfo(name = "liked")
    val liked: Boolean,
    @PrimaryKey
    @ColumnInfo(name = "imageUrl")
    val imageUrl: String
) {
    fun asLike(): Like {
        return Like(
            breed,
            parentBreed,
            liked,
            imageUrl
        )
    }
}

fun List<LikeEntity>.asBreedImages(): BreedImages {
    return BreedImages(
        map {
            it.imageUrl
        },
        ""
    )
}

data class LikedEntity(
    @ColumnInfo(name = "breed")
    val breed: String,
    @ColumnInfo(name = "totalLikes")
    val totalLikes: Int
) {
    fun asLikedBreed(): LikedBreed {
        return LikedBreed(
            breed,
            totalLikes
        )
    }
}
