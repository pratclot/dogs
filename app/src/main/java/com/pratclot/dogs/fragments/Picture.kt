package com.pratclot.dogs.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pratclot.dogs.R
import com.pratclot.dogs.data.MainViewModel
import com.pratclot.dogs.databinding.PictureFragmentBinding
import com.pratclot.dogs.ui.compose.Picture
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

const val ARG_OBJECT = "imageUrl"

@AndroidEntryPoint
class Picture : Fragment() {
    private val TAG = "Picture"
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var binding: PictureFragmentBinding

    private lateinit var intentUpdater: Disposable

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

            binding.composeView.setContent {
                LaunchedEffect(key1 = imageUrl) {
                    viewModel.touchLikeFor(imageUrl)
                }

                val likedState by viewModel.getLikeFor(imageUrl)
                    .collectAsState(false)

                Picture(
                    imageUrl = imageUrl,
                    clickListener = ClickListener { viewModel.toggleLikeFor(imageUrl) },
                    likedState = likedState,
                    onLoadingError = { showAlert(it) },
                    onLoadingSuccess = { setShareIntent(it) }
                )
            }
        }
    }

    private fun showAlert(e: Throwable?) {
        AlertDialog.Builder(requireContext()).apply {
            setPositiveButton("Ok") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setTitle("Some server error")
            setMessage(
                when (e) {
                    null -> "Try connect later"
                    else -> e.message
                }
            )
            show()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setShareIntent(bmp: Bitmap) {
        val imgFile = File.createTempFile("prefix", ".png", requireContext().cacheDir)
        val imgUri =
            FileProvider.getUriForFile(requireContext(), "com.pratclot.dogs.provider", imgFile)
        intentUpdater = Completable.fromAction {
            bmp.compress(
                Bitmap.CompressFormat.PNG,
                100,
                imgFile.outputStream()
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete =
                {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        setType("image/*")
                        putExtra(Intent.EXTRA_STREAM, imgUri)
                    }
                    viewModel.shareActionProvider.setShareIntent(shareIntent)
                },
                onError = { Log.e(TAG, it.toString()) }
            )
    }

    class ClickListener(val listener: (imageUrl: String) -> Unit) {
        fun onClick(imageUrl: String) = listener(imageUrl)
    }
}