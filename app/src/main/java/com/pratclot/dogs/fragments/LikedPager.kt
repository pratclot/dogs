package com.pratclot.dogs.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pratclot.dogs.R
import com.pratclot.dogs.adapters.PagerAdapter
import com.pratclot.dogs.data.MainViewModel
import com.pratclot.dogs.databinding.LikedPagerFragmentBinding
import com.pratclot.dogs.domain.BreedImage
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.liked_pager_fragment.*

@AndroidEntryPoint
class LikedPager : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        (arguments?.get("chosen_breed") as String).let {
            viewModel.changeTitle(it)
            viewModel.getLikedImagesFor(it)
        }

        val binding = DataBindingUtil.inflate<LikedPagerFragmentBinding>(
            inflater,
            R.layout.liked_pager_fragment,
            container,
            false
        )

        viewModel.imagesSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    createAdapter(it.toList())
                },
                onError = { Log.e(TAG, it.message.toString()) }
            )

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.share_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val item = menu.findItem(R.id.action_share)
        viewModel.shareActionProvider =
            MenuItemCompat.getActionProvider(item) as ShareActionProvider
    }

    private fun createAdapter(imageList: List<BreedImage>) {
        val adapter = PagerAdapter(this, imageList)
        liked_pager.adapter = adapter
    }
}