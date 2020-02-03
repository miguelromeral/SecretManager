package es.miguelromeral.secretmanager.ui.fragments

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.miguelromeral.secretmanager.databinding.FragmentFileConverterBinding
import es.miguelromeral.secretmanager.ui.viewmodelfactories.FileConverterFactory
import es.miguelromeral.secretmanager.ui.viewmodels.FileConverterViewModel
import java.lang.Exception
import android.os.Parcelable
import android.widget.CheckBox
import androidx.preference.PreferenceManager
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.classes.*
import es.miguelromeral.secretmanager.ui.utils.createAlertDialog
import es.miguelromeral.secretmanager.ui.utils.showHidePassword


class FileConverterFragment(val data: Intent? = null) : Fragment() {

    private lateinit var viewModel: FileConverterViewModel
    private lateinit var binding: FragmentFileConverterBinding

    private val TAG = "FCVM"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get the current application
        val application = requireNotNull(this.activity).application
        // Get the View Model Factory
        val vmf = FileConverterFactory(application)
        // Initialize View Model
        viewModel = ViewModelProviders.of(this, vmf).get(FileConverterViewModel::class.java)
        // Initialize Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_file_converter, container, false)
        // Passing the parameters to binding variables
        binding.viewModel = viewModel
        binding.fileItem = viewModel.item

        binding.bPickFile.setOnClickListener{
            // We open an intent to get the info of the file
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            startActivityForResult(intent, PICK_FILE_CODE)
        }


        // Functionality to Show / Hide Password in this fragment
        binding.passwordLayout.cbShowPassword.setOnClickListener{
            showHidePassword(
                it.context,
                it as CheckBox,
                binding.passwordLayout.etPassword
            )
        }

        binding.executeButton.bExecute.setOnClickListener{
            viewModel.item.uri?.let { data ->
                try
                {
                    // Obtain file info.
                    val types = getFileMimeType(context!!, data)
                    val filename = PreferenceManager.getDefaultSharedPreferences(context)
                        .getString(
                            getString(R.string.preference_key_filename),
                            getString(R.string.preference_filename_default))

                    // We create the document first and later we'll fill it with the data.
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = types
                        putExtra(Intent.EXTRA_TITLE,  filename)
                    }
                    startActivityForResult(intent, CREATE_FILE_CODE)
                }catch (e: Exception){
                    Log.i(TAG, "Error while attempting to create the file: ${e.message}")
                    Toast.makeText(context, R.string.error_file_not_created, Toast.LENGTH_LONG).show()
                }
            }
        }


        // If we received a file from another app, let's open it!
        arguments?.let{
            val args = FileConverterFragmentArgs.fromBundle(arguments!!)
            (args.data.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                viewModel.proccessNewFile(context!!, it)
            }
            Log.i(TAG, "Processed the file opened form another app.")
        }

        viewModel.loading.observe(this, Observer {
            if(it){
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModel.errorMessage.observe(this, Observer {message ->
            message?.let{
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        })

        viewModel.errorDecrypting.observe(this, Observer { error ->
            error?.let{
                val title = getString(if(it) R.string.error_decryption else R.string.error_encryption)
                val body = getString(if(it) R.string.error_mismatch_password else R.string.error_wrong_encryption)

                val builder = createAlertDialog(
                    context!!,
                    title = title,
                    body = body,
                    negative = getString(R.string.ok_answer)
                )
                builder.create().show()
                viewModel.clearErrorExecuting()
            }
        })

        // We create the channel for notifications when a file is encrypted.
        createChannel(
            getString(R.string.channel_files_id),
            getString(R.string.channel_files_name)
        )

        // Returning the binding root
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        context?.let { context ->
            when (requestCode) {
                PICK_FILE_CODE -> {
                    if (resultCode == Activity.RESULT_OK) {
                        viewModel.proccessNewFile(context, data?.data)
                    }
                }
                CREATE_FILE_CODE -> {
                    if (resultCode == Activity.RESULT_OK) {
                        viewModel.execute(context, data?.data)
                    }
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        // TODO: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                // TODO: Step 2.4 change importance
                NotificationManager.IMPORTANCE_HIGH
            )// TODO: Step 2.6 disable badges for this channel
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.channel_files_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(notificationChannel)

        }
        // TODO: Step 1.6 END create a channel
    }

    companion object{
        private const val PICK_FILE_CODE = 10
        private const val CREATE_FILE_CODE = 43
    }
}