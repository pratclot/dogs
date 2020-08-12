package com.pratclot.dogs.domain

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pratclot.dogs.R
import com.squareup.picasso.Picasso

@BindingAdapter("setContent")
fun TextView.setContent(breed: Breed) {
    text = breed.name

    breed.subBreeds.takeUnless { it.isEmpty() }?.let {
        text = text.toString().plus(" (${it.size} subbreeds)")
    }
}

@BindingAdapter("setContent")
fun TextView.setContent(likedBreed: LikedBreed) {
    text = "${likedBreed.breed} (${likedBreed.totalLikes} photos)"
}

@BindingAdapter("setContent")
fun ImageView.setContent(breedImage: BreedImage) {
    Picasso.get()
        .load(breedImage.imageUrl)
        .into(this)
}
