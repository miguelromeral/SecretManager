package es.miguelromeral.secretmanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.databinding.FragmentHomeBinding
import es.miguelromeral.secretmanager.ui.models.TextItem
import es.miguelromeral.secretmanager.ui.viewmodelfactories.HomeFactory
import es.miguelromeral.secretmanager.ui.viewmodels.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get the current application
        val application = requireNotNull(this.activity).application
        // Get the View Model Factory
        val vmf = HomeFactory(application)
        // Initialize View Model
        homeViewModel = ViewModelProviders.of(this, vmf).get(HomeViewModel::class.java)
        // Initialize Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        // Passing the parameters to binding variables
        binding.viewModel = homeViewModel
        binding.item = homeViewModel.item

        // Returning the binding root
        return binding.root
    }
}