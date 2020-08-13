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
import com.pratclot.dogs.databinding.BreedPagerFragmentBinding
import com.pratclot.dogs.domain.BreedImage
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.breed_pager_fragment.*

@AndroidEntryPoint
class BreedPager : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var binding: BreedPagerFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        setHasOptionsMenu(true)

        (arguments?.get("chosen_breed") as String).let {
            viewModel.changeTitle(it)
            arguments?.get("parent_breed").let { parentBreed ->
                when (parentBreed) {
                    null -> viewModel.getImagesFor(it)
                    else -> viewModel.getImagesFor(parentBreed as String, it)
                }
            }
        }

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.breed_pager_fragment,
            container,
            false
        )

        viewModel.imagesObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    createAdapter(it.toList())
                },
                onError = { Log.e(TAG, it.message.toString()) },
                onComplete = {}
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
        breed_pager.adapter = adapter
    }
}