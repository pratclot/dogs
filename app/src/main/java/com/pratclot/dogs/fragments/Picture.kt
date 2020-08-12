package com.pratclot.dogs.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pratclot.dogs.R
import com.pratclot.dogs.data.MainViewModel
import com.pratclot.dogs.databinding.PictureFragmentBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.picture_fragment.*
import kotlinx.android.synthetic.main.picture_fragment.view.*

const val ARG_OBJECT = "imageUrl"

@AndroidEntryPoint
class Picture : Fragment() {
    private val TAG = "Picture"
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var binding: PictureFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.picture_fragment,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireArguments().takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val imageUrl = getString(ARG_OBJECT).toString()
            Picasso.get()
                .load(imageUrl)
                .into(image)
            binding.imageUrl = imageUrl
            binding.listener = ClickListener { viewModel.toggleLikeFor(imageUrl) }

            viewModel.touchLikeFor(imageUrl)
            viewModel.getLikeFor(imageUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        when (it.liked) {
                            false -> fab.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                            true -> fab.setImageResource(R.drawable.ic_baseline_favorite_24)
                        }
                    },
                    onError = { Log.e(TAG, it.toString()) },
                    onComplete = { Log.e(TAG, "Complete") }
                )
        }
    }

    class ClickListener(val listener: (imageUrl: String) -> Unit) {
        fun onClick(imageUrl: String) = listener(imageUrl)
    }
}