package es.miguelromeral.secretmanager.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.database.SecretDatabase
import es.miguelromeral.secretmanager.databinding.FragmentSecretsBinding
import es.miguelromeral.secretmanager.ui.adapters.SecretAdapter
import es.miguelromeral.secretmanager.ui.listeners.DecryptSecretListener
import es.miguelromeral.secretmanager.ui.listeners.RemoveSecretListener
import es.miguelromeral.secretmanager.ui.viewmodelfactories.HomeFactory
import es.miguelromeral.secretmanager.ui.viewmodelfactories.SecretsFactory
import es.miguelromeral.secretmanager.ui.viewmodels.HomeViewModel
import es.miguelromeral.secretmanager.ui.viewmodels.SecretsViewModel

class SecretsFragment : Fragment() {

    private lateinit var viewModel: SecretsViewModel
    private lateinit var binding: FragmentSecretsBinding
    private lateinit var adapter: SecretAdapter

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
        val vmf = SecretsFactory(dataSource, application)
        // Initialize View Model
        viewModel = ViewModelProviders.of(this, vmf).get(SecretsViewModel::class.java)
        // Initialize Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_secrets, container, false)
        // Passing the parameters to binding variables
        binding.viewModel = viewModel

        binding.setLifecycleOwner(this)

        adapter = SecretAdapter(DecryptSecretListener { item ->
                viewModel.openSecret(application.baseContext, binding.secretsList, item)
            },
            RemoveSecretListener { item ->
                context?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setTitle(resources.getString(R.string.clear_secret_title))
                    builder.setMessage(resources.getString(R.string.clear_secret_body, item.alias))

                    // Set the alert dialog yes button click listener
                    builder.setPositiveButton(resources.getString(R.string.clear_secret_yes),
                        DialogInterface.OnClickListener { dialog, which ->

                            viewModel.removeSecret(item)
                        })
                    builder.setNegativeButton(resources.getString(R.string.clear_secret_no),null)

                    val dialog = builder.create()
                    dialog.show()
                }
            })
        binding.secretsList.adapter = adapter

        viewModel.secrets.observe(this, Observer {
            it?.let{
                adapter.submitList(it)
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.options_secrets, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item != null){
            when(item.itemId) {
                R.id.option_clear_secrets -> {

                    context?.let {

                        val builder = AlertDialog.Builder(it)
                        builder.setTitle(resources.getString(R.string.clear_secrets_title))
                        builder.setMessage(resources.getString(R.string.clear_secrets_body))

                        // Set the alert dialog yes button click listener
                        builder.setPositiveButton(resources.getString(R.string.clear_secrets_yes),
                            DialogInterface.OnClickListener { dialog, which ->

                                viewModel.clearDatabase()
                            })
                        builder.setNegativeButton(resources.getString(R.string.clear_secrets_no),null)

                        val dialog = builder.create()
                        dialog.show()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}