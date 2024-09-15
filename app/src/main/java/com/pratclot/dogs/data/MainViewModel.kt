package com.pratclot.dogs.data

import android.util.Log
import androidx.appcompat.widget.ShareActionProvider
import androidx.lifecycle.ViewModel
import com.pratclot.dogs.domain.Breed
import com.pratclot.dogs.domain.BreedImage
import com.pratclot.dogs.domain.BreedImages
import com.pratclot.dogs.domain.Breeds
import com.pratclot.dogs.domain.Like
import com.pratclot.dogs.domain.LikedBreed
import com.pratclot.dogs.service.DogApi
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

var TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    var dogApi: DogApi,
    var repo: Repo
) : ViewModel() {
    lateinit var shareActionProvider: ShareActionProvider
    var likesObservable: Flowable<List<LikedBreed>> = repo.getLiked()
    lateinit var imagesObservable: Maybe<BreedImages>
    lateinit var imagesSingle: Single<BreedImages>
    lateinit var breeds: Breeds
    var breedsObservable = dogApi.listAllBreeds()
    lateinit var currentBreed: Breed

    var title: BehaviorSubject<String> = BehaviorSubject.create()

    fun changeTitle(newTitle: String) {
        title.onNext(newTitle)
    }

    fun saveBreeds(retrievedBreeds: Breeds) {
        breeds = retrievedBreeds
    }

    fun getImagesFor(breed: String, subBreed: String? = null) {
        when (subBreed) {
            null -> imagesObservable = dogApi.getBreedImages(breed)
            else -> imagesObservable = dogApi.getSubBreedImages(breed, subBreed)
        }
    }

    fun getLikeFor(imageUrl: String): Flow<Boolean> {
        return repo.getLikeFor(imageUrl).map { it.liked }
    }

    fun toggleLikeFor(imageUrl: String) {
        Completable.fromAction { repo.toggleLikeFor(imageUrl) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { Log.e(TAG, it.toString()) },
                onComplete = {}
            )
    }

    fun touchLikeFor(imageUrl: String) {
        Completable.fromAction { repo.insert(Like.likeFromBreedImage(BreedImage(imageUrl))) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { Log.e(TAG, it.toString()) },
                onComplete = {}
            )
    }

    fun getLikedImagesFor(breed: String) {
        imagesSingle = repo.getLikedImagesFor(breed)
    }
}