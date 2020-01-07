package es.miguelromeral.secretmanager.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.MotionEventCompat
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
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.widget.Toast
import es.miguelromeral.secretmanager.MainActivity
import es.miguelromeral.secretmanager.ui.shareContentText
import kotlinx.android.synthetic.main.fragment_home.*


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
        binding = DataBindingUtil.inflate(inflater, es.miguelromeral.secretmanager.R.layout.fragment_home, container, false)
        // Passing the parameters to binding variables
        binding.viewModel = homeViewModel
        binding.item = homeViewModel.item

        binding.bShareOutput.setOnClickListener{view ->
            if(homeViewModel.item.output.isNotEmpty()) {
                shareContentText(view.context, homeViewModel.item.output)
            }else{
                Toast.makeText(context, "Output is empty yet.", Toast.LENGTH_LONG).show()
            }
        }

        binding.bExchange.setOnClickListener{
            homeViewModel.item.let{
                if(it.output.isNotEmpty()) {
                    it.input = it.output
                    it.output = ""
                }else{
                    Toast.makeText(context, "Output is empty yet.", Toast.LENGTH_LONG).show()
                }
            }
        }


        // DEBUGGING:
        homeViewModel.item.input = "Debugging! \uD83D\uDE01"
        //////////////


        // Returning the binding root
        return binding.root
    }
}