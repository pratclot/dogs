package com.pratclot.dogs.data

import android.util.Log
import androidx.appcompat.widget.ShareActionProvider
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.pratclot.dogs.domain.*
import com.pratclot.dogs.service.DogApi
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

var TAG = "MainViewModel"

class MainViewModel @ViewModelInject constructor(
    var dogApi: DogApi,
    var repo: Repo
) : ViewModel() {
    lateinit var shareActionProvider: ShareActionProvider
    var likesObservable: Flowable<List<LikedBreed>> = repo.getLiked()
    lateinit var imagesObservable: Observable<BreedImages>
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

    fun getLikeFor(imageUrl: String): Flowable<Like> {
        return repo.getLikeFor(imageUrl)
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