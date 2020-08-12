package com.pratclot.dogs.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.pratclot.dogs.R
import com.pratclot.dogs.data.MainViewModel
import com.pratclot.dogs.databinding.PictureFragmentBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.picture_fragment.*
import java.io.File
import javax.inject.Inject

const val ARG_OBJECT = "imageUrl"

@AndroidEntryPoint
class Picture : Fragment() {
    private val TAG = "Picture"
    private val viewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var picasso: Picasso

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
            picasso
                .load(imageUrl)
                .placeholder(R.drawable.rotating_sync)
                .into(image, object : Callback {
                    override fun onSuccess() = setShareIntent()

                    override fun onError(e: Exception?) = showAlert(e)
                })
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

    private fun showAlert(e: Exception?) {
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
        setShareIntent()
        super.onResume()
    }

    private fun setShareIntent() {
        val bmp = image.drawable.toBitmap()
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

    override fun onDestroyView() {
        intentUpdater.dispose()
        super.onDestroyView()
    }

    class ClickListener(val listener: (imageUrl: String) -> Unit) {
        fun onClick(imageUrl: String) = listener(imageUrl)
    }
}