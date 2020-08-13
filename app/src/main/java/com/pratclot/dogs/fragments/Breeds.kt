package com.pratclot.dogs.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.ext.T
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pratclot.dogs.R
import com.pratclot.dogs.adapters.BreedListAdapter
import com.pratclot.dogs.adapters.ClickListener
import com.pratclot.dogs.data.MainViewModel
import com.pratclot.dogs.databinding.BreedsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.breeds_fragment.*

const val TAG = "BreedsFragment"

@AndroidEntryPoint
class Breeds : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.changeTitle("Breeds")

        val binding: BreedsFragmentBinding =
            DataBindingUtil.inflate(
                inflater, R.layout.breeds_fragment, container, false
            )

        val layoutManager = LinearLayoutManager(context)
        val adapter = BreedListAdapter(
            ClickListener {
                viewModel.changeTitle(it.name)
                viewModel.currentBreed = it
                when (it.hasSubBreeds()) {
                    false -> findNavController().navigate(
                        BreedsDirections.actionBreedsToBreedPager(
                            it.name,
                            null
                        )
                    )
                    true -> findNavController().navigate(BreedsDirections.actionBreedsToSubBreed(it.name))
                }
            }
        )

        binding.apply {
            list.layoutManager = layoutManager
            list.adapter = adapter
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.breedsObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    breeds_placeholder.visibility = View.GONE
                    list.visibility = View.VISIBLE

                    viewModel.saveBreeds(it)
                    adapter.submitList(it.toList())
                },
                onError = {
                    showAlert(it.message)
                    Log.e(TAG, "${it.toString()}, ${it.stackTrace.toString()}")
                },
                onComplete = {Log.e(TAG, "Complete")}
            )

        return binding.root
    }

    private fun showAlert(e: String?) {
        AlertDialog.Builder(requireContext()).apply {
            setPositiveButton("Ok") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setTitle("Some server error")
            setMessage(
                when (e) {
                    null -> "Try connect later"
                    else -> e
                }
            )
            show()
        }
    }
}