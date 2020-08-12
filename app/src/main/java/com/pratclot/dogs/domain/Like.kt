package com.pratclot.dogs.domain

import com.pratclot.dogs.data.db.LikeEntity

data class Like(
    val breed: String,
    val parentBreed: String?,
    val liked: Boolean,
    val imageUrl: String
) {
    fun asDbLike(): LikeEntity {
        return LikeEntity(
            breed = breed,
            parentBreed = parentBreed,
            liked = liked,
            imageUrl = imageUrl
        )
    }

    companion object {
        fun likeFromBreedImage(breedImage: BreedImage): Like {
            return Like(
                breedImage.breed,
                breedImage.parentBreed,
                false,
                breedImage.imageUrl
            )
        }
    }
}

data class LikedBreed(
    val breed: String,
    val totalLikes: Int
) {
    fun asBreed(): Breed {
        return Breed(breed, emptyList())
    }
}