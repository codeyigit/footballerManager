package hr.algebra.footballermanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.footballermanager.adapter.FootballerAdapter
import hr.algebra.footballermanager.databinding.FragmentFirstBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), NavigableFragment {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFootballers()
        setupListeners()
    }

    private fun loadFootballers() {
        GlobalScope.launch (Dispatchers.Main){
            val footballers = withContext(Dispatchers.IO){
                (context?.applicationContext as App).getFootballerDao().getFootballers()
            }

            binding.rvFootballers.apply {
                layoutManager=LinearLayoutManager(requireContext())
                adapter=FootballerAdapter(requireContext(), footballers,this@FirstFragment)
            }
        }
    }

    private fun setupListeners() {
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun navigate(bundle: Bundle) {
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment , bundle)
    }
}