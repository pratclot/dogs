package com.pratclot.dogs.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pratclot.dogs.domain.BreedImage
import com.pratclot.dogs.fragments.ARG_OBJECT
import com.pratclot.dogs.fragments.Picture

class PagerAdapter(fragment: Fragment, val imageList: List<BreedImage>) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun createFragment(position: Int): Fragment {
        return Picture().apply {
            arguments = Bundle().apply {
                putString(ARG_OBJECT, imageList[position].imageUrl)
            }
        }
    }
}