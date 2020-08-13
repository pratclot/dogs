package com.pratclot.dogs.service

import com.pratclot.dogs.domain.BreedImages
import com.pratclot.dogs.domain.Breeds
import io.reactivex.rxjava3.core.Maybe
import retrofit2.http.GET
import retrofit2.http.Path

interface DogApi {
    @GET("api/breeds/list/all")
    fun listAllBreeds(): Maybe<Breeds>

    @GET("api/breed/{breed}/images")
    fun getBreedImages(@Path("breed") name: String): Maybe<BreedImages>

    @GET("api/breed/{breed}/{subBreed}/images")
    fun getSubBreedImages(@Path("breed") breed: String, @Path("subBreed") subBreed: String): Maybe<BreedImages>
}
