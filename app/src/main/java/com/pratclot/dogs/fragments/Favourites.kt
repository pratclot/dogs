package com.pratclot.dogs.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pratclot.dogs.R
import com.pratclot.dogs.adapters.LikeClickListener
import com.pratclot.dogs.adapters.LikeListAdapter
import com.pratclot.dogs.data.MainViewModel
import com.pratclot.dogs.databinding.FavouritesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class Favourites : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private val TAG = "Favourites"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.changeTitle("Favourites")

        val binding = DataBindingUtil.inflate<FavouritesFragmentBinding>(
            inflater,
            R.layout.favourites_fragment,
            container,
            false
        )

        val layoutManager = LinearLayoutManager(context)
        val adapter = LikeListAdapter(
            LikeClickListener {
                viewModel.currentBreed = it.asBreed()
                findNavController().navigate(FavouritesDirections.actionFavouritesToLikedPager(it.breed))
            }
        )

        binding.apply {
            list.layoutManager = layoutManager
            list.adapter = adapter
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.likesObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    adapter.submitList(it)
                },
                onError = { Log.e(TAG, it.toString()) }
            )

        return binding.root
    }
}