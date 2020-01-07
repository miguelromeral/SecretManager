package es.miguelromeral.secretmanager.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import es.miguelromeral.secretmanager.databinding.FragmentHomeBinding
import es.miguelromeral.secretmanager.ui.viewmodelfactories.HomeFactory
import es.miguelromeral.secretmanager.ui.viewmodels.HomeViewModel
import android.widget.Toast
import es.miguelromeral.secretmanager.ui.shareContentText


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
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
        viewModel = ViewModelProviders.of(this, vmf).get(HomeViewModel::class.java)
        // Initialize Binding
        binding = DataBindingUtil.inflate(inflater, es.miguelromeral.secretmanager.R.layout.fragment_home, container, false)
        // Passing the parameters to binding variables
        binding.viewModel = viewModel
        binding.item = viewModel.item

        binding.bShareOutput.setOnClickListener{view ->
            if(viewModel.item.output.isNotEmpty()) {
                shareContentText(view.context, viewModel.item.output)
            }else{
                Toast.makeText(context, "Output is empty yet.", Toast.LENGTH_LONG).show()
            }
        }

        binding.bExchange.setOnClickListener{
            viewModel.item.let{
                if(it.output.isNotEmpty()) {
                    it.input = it.output
                    it.output = ""
                }else{
                    Toast.makeText(context, "Output is empty yet.", Toast.LENGTH_LONG).show()
                }
            }
        }


        binding.executeButton.bExecute.setOnClickListener{
            viewModel.execute()
        }

        // DEBUGGING:
        viewModel.item.password = "password"
        viewModel.item.input = "Debugging! \uD83D\uDE01"
        //////////////


        // Returning the binding root
        return binding.root
    }
}