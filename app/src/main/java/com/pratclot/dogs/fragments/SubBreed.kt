package layout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pratclot.dogs.R
import com.pratclot.dogs.data.MainViewModel
import com.pratclot.dogs.databinding.SubbreedFragmentBinding
import com.pratclot.dogs.adapters.BreedListAdapter
import com.pratclot.dogs.adapters.ClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubBreed : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (arguments?.get("chosen_breed") as String).let {
            viewModel.changeTitle(it)
        }

        val binding = DataBindingUtil.inflate<SubbreedFragmentBinding>(
            inflater,
            R.layout.subbreed_fragment,
            container,
            false
        )

        val layoutManager = LinearLayoutManager(context)
        val adapter = BreedListAdapter(
            ClickListener {
                viewModel.changeTitle(it.name)
                findNavController().navigate(
                    SubBreedDirections.actionSubBreedToBreedPager(
                        it.name,
                        viewModel.currentBreed.name
                    )
                )
            }
        )

        binding.apply {
            list.layoutManager = layoutManager
            list.adapter = adapter
            lifecycleOwner = viewLifecycleOwner
        }

        adapter.submitList(viewModel.breeds.getOne(viewModel.currentBreed).getSubBreads())

        return binding.root
    }
}