package es.miguelromeral.secretmanager.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.database.SecretDatabase
import es.miguelromeral.secretmanager.database.SecretDatabaseDao
import es.miguelromeral.secretmanager.databinding.FragmentSecretsBinding
import es.miguelromeral.secretmanager.ui.adapters.SecretAdapter
import es.miguelromeral.secretmanager.ui.listeners.DecryptSecretListener
import es.miguelromeral.secretmanager.ui.listeners.RemoveSecretListener
import es.miguelromeral.secretmanager.ui.viewmodelfactories.SecretsFactory
import es.miguelromeral.secretmanager.ui.viewmodels.SecretsViewModel
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import es.miguelromeral.secretmanager.classes.importSecretsFU
import es.miguelromeral.secretmanager.database.Secret
import es.miguelromeral.secretmanager.ui.activities.MainActivity
import es.miguelromeral.secretmanager.ui.utils.convertLongToTime
import es.miguelromeral.secretmanager.ui.utils.createAlertDialog


class SecretsFragment : Fragment(), SearchView.OnQueryTextListener {


    private lateinit var viewModel: SecretsViewModel
    private lateinit var binding: FragmentSecretsBinding
    private lateinit var adapter: SecretAdapter
    private lateinit var dataSource: SecretDatabaseDao


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get the current application
        val application = requireNotNull(this.activity).application
        // Get the database instance
        dataSource = SecretDatabase.getInstance(application).secretDatabaseDao
        // Get the View Model Factory
        val vmf = SecretsFactory(dataSource, application)
        // Initialize View Model
        viewModel = ViewModelProviders.of(this, vmf).get(SecretsViewModel::class.java)
        // Initialize Binding
        binding = DataBindingUtil.inflate(inflater, es.miguelromeral.secretmanager.R.layout.fragment_secrets, container, false)
        // Passing the parameters to binding variables
        binding.viewModel = viewModel

        binding.setLifecycleOwner(this)

        // Adding the adapter
        adapter = SecretAdapter(DecryptSecretListener { item ->
                viewModel.openSecret(application.baseContext, binding.secretsList, item)
            },
            RemoveSecretListener { item ->
                context?.let {

                    val bu = createAlertDialog(
                        it,
                        title = getString(es.miguelromeral.secretmanager.R.string.clear_secret_title),
                        body = getString(R.string.clear_secret_body, item.alias),
                        negative = getString(es.miguelromeral.secretmanager.R.string.clear_secret_no)
                    )

                    bu.setPositiveButton(
                        es.miguelromeral.secretmanager.R.string.clear_secret_yes
                    ) { dialog, which ->
                        viewModel.removeSecret(item)
                    }
                    val dialog = bu.create()
                    dialog.show()

                }
            })

        binding.secretsList.adapter = adapter

        viewModel.dataChanged.observe(this, Observer { changed ->
            if(changed == true){
                viewModel.secrets.observe(this, Observer {
                    it?.let {
                        viewModel.filteredList.postValue(it)
                        if(it.isEmpty()){
                            binding.warningEmptyDbSecrets.visibility = View.VISIBLE
                        }else{
                            binding.warningEmptyDbSecrets.visibility = View.GONE
                        }
                    }
                })
                viewModel.finalDataChanged()
            }
        })

        viewModel.filteredList.observe(this, Observer {
            adapter.submitList(it)
        })

        setHasOptionsMenu(true)


        return binding.root
    }



    private lateinit var searchView: SearchView

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(es.miguelromeral.secretmanager.R.menu.options_secrets, menu)
        searchView = menu.findItem(es.miguelromeral.secretmanager.R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return filter(p0)
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return filter(p0)
    }

    private fun filter(p0: String?): Boolean{
        val cont = context!!
        val format = PreferenceManager.getDefaultSharedPreferences(cont).getString(
            cont.getString(R.string.preference_date_format_id),
            cont.getString(R.string.date_format_one)
        ) ?: cont.getString(R.string.date_format_one)
        return viewModel.filterSecrets(format, p0)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item != null){
            when(item.itemId) {
                es.miguelromeral.secretmanager.R.id.option_clear_secrets -> {

                    context?.let {
                        val bu = createAlertDialog(
                            it,
                            title = es.miguelromeral.secretmanager.R.string.clear_secrets_title,
                            body = es.miguelromeral.secretmanager.R.string.clear_secrets_body,
                            negative = es.miguelromeral.secretmanager.R.string.clear_secrets_no
                        )

                        bu.setPositiveButton(
                            es.miguelromeral.secretmanager.R.string.clear_secrets_yes
                        ) { dialog, which ->
                            viewModel.clearDatabase()
                        }
                        val dialog = bu.create()
                        dialog.show()
                    }
                }

                R.id.option_import_secrets -> {

                    binding.warningEmptyDbSecrets.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.importSecrets(binding.secretsList)
                    binding.progressBar.visibility = View.GONE
                    val view: View = activity!!.findViewById(R.id.nav_host_fragment)
                    Snackbar.make(view, R.string.imported_secrets_title, Snackbar.LENGTH_LONG).show()
                }

                es.miguelromeral.secretmanager.R.id.option_export_secrets -> {
                    context?.let {
                        exportSecrets(it)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exportSecrets(it: Context){

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {


            Log.i("ExportCSV", "Permission NOT Granted")

            // Permission is not granted
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Toast.makeText(it, R.string.explanation_file_permissions, Toast.LENGTH_LONG).show()
                Log.i("ExportCSV", "Permission Explanation")


            }
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION)


                Log.i("ExportCSV", "Permission NO explanation needed")

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

        } else {
            // Permission has already been granted
            Log.i("ExportCSV", "Permission has already been Granted")
            (activity!! as MainActivity).exportSecrets()
        }
    }


    companion object{
        const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 20
    }
}