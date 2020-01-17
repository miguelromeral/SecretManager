package es.miguelromeral.secretmanager.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import es.miguelromeral.secretmanager.databinding.FragmentHomeBinding
import es.miguelromeral.secretmanager.ui.viewmodelfactories.HomeFactory
import es.miguelromeral.secretmanager.ui.viewmodels.HomeViewModel
import android.widget.Toast
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.classes.IsBase64Encoded
import es.miguelromeral.secretmanager.database.SecretDatabase
import es.miguelromeral.secretmanager.ui.activities.MainActivity
import es.miguelromeral.secretmanager.ui.models.TextItem
import es.miguelromeral.secretmanager.ui.utils.shareContentText
import es.miguelromeral.secretmanager.ui.utils.showHidePassword


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var item: TextItem
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get the current application
        val application = requireNotNull(this.activity).application
        // Get the database instance
        val dataSource = SecretDatabase.getInstance(application).secretDatabaseDao
        // Get the View Model Factory
        val vmf = HomeFactory(dataSource, application)
        // Initialize View Model
        viewModel = ViewModelProviders.of(this, vmf).get(HomeViewModel::class.java)
        // Initialize Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        // Passing the parameters to binding variables
        binding.viewModel = viewModel
        item = viewModel.item
        binding.item = item

        binding.floatingActionButton.setOnClickListener{view ->
            if(item.output.isNotEmpty()) {
                shareContentText(view.context, item.output)
            }else{
                Toast.makeText(context, R.string.error_output_empty, Toast.LENGTH_LONG).show()
            }
        }

        binding.bExchange.setOnClickListener{
            item.let{
                if(it.output.isNotEmpty()) {
                    it.input = it.output
                    it.output = ""
                }else{
                    Toast.makeText(context, R.string.error_output_empty, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.executeButton.bExecute.setOnClickListener{
            viewModel.execute(it.context)
        }

        // Functionality to Show / Hide Password in this fragment
        binding.passwordLayout.cbShowPassword.setOnClickListener{
            showHidePassword(
                it.context,
                it as CheckBox,
                binding.passwordLayout.etPassword
            )
        }

        arguments?.let{
            val args = HomeFragmentArgs.fromBundle(arguments!!)
            Log.i("ARGS", "Input: ${args.input}")
            item.input = args.input
            item.decrypt = true
            item.password = String()
        }


        val shared = activity?.intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
        shared?.let {
            val input = it.toString()
            item.decrypt = IsBase64Encoded(input)
            item.input = input
        }

        // Returning the binding root
        return binding.root
    }

}