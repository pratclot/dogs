package com.pratclot.dogs.data

import com.pratclot.dogs.data.db.LikeDbDao
import com.pratclot.dogs.data.db.asBreedImages
import com.pratclot.dogs.domain.BreedImages
import com.pratclot.dogs.domain.Like
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class Repo @Inject constructor(
    private val likeDbDao: LikeDbDao
) {
    fun getLiked() = likeDbDao.getLiked().map {
        it.map { it.asLikedBreed() }
    }

    fun getLikeFor(imageUrl: String): Flowable<Like> {
        return likeDbDao.getLikeFor(imageUrl).map { it.asLike() }
    }

    fun toggleLikeFor(imageUrl: String) =
        likeDbDao.toggleLikeFor(imageUrl)

    fun insert(like: Like) = likeDbDao.insert(like.asDbLike())
    fun getLikedImagesFor(breed: String): Single<BreedImages> {
        return likeDbDao.getLikedImagesFor(breed).map {
            it.asBreedImages()
        }.subscribeOn(Schedulers.io())
    }
}