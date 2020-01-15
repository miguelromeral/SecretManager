package es.miguelromeral.secretmanager.ui.fragments

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
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
        binding = DataBindingUtil.inflate(inflater, es.miguelromeral.secretmanager.R.layout.fragment_file_converter, container, false)
        // Passing the parameters to binding variables
        binding.viewModel = viewModel
        binding.fileItem = viewModel.item

        binding.bPickFile.setOnClickListener{
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            startActivityForResult(intent, REQUEST_CODE)
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
                    /*
                    val file = viewModel.item.uri!!
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        //setDataAndType(file, getFileMimeType(context!!, file))
                        setData(file)
                    }
                    startActivity(intent)
*/




                    val types = getFileMimeType(context!!, data)


                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        // Filter to only show results that can be "opened", such as
                        // a file (as opposed to a list of contacts or timezones).
                        addCategory(Intent.CATEGORY_OPENABLE)

                        // Create a file with the requested MIME type.
                        type = types
                        putExtra(Intent.EXTRA_TITLE, "testing")
                    }
                    startActivityForResult(intent, WRITE_REQUEST_CODE)
                }catch (e: Exception){
                    Log.i(TAG, "Error: ${e.message}")
                    e.printStackTrace()
                }finally {
                    Log.i(TAG, "Pasó")
                }


                /*
            when (item.decrypt) {
                false -> item.encrypt()
                true -> item.decrypt()
            }*/
            }


        }





        arguments?.let{
            val args = FileConverterFragmentArgs.fromBundle(arguments!!)
            handleSendImage(args.data)
            Log.i("TAG", "YAY!")
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
                val title = if(it) "Error while decryption" else "Error while encryption"
                val body = if(it) "The password doesn't match with the file to be decrypted" else
                    "There was a problem while encrypting your file. Please, try again later"

                val builder = createAlertDialog(
                    context!!,
                    title = title,
                    body = body,
                    negative = "OK"
                )
                builder.create().show()
                viewModel.clearErrorExecuting()
            }
        })

        createChannel(
            getString(R.string.channel_id_files),
            getString(R.string.channel_id_files_name)
        )

        // Returning the binding root
        return binding.root
    }

    private fun handleSendImage(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            viewModel.proccessNewFile(context!!, it)
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK){
                    viewModel.proccessNewFile(context!!, data?.data)
                }
            }
            WRITE_REQUEST_CODE -> {
                if(resultCode == Activity.RESULT_OK) {
                    viewModel.execute(context!!, data?.data)
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
            notificationChannel.description = "Notification Description"

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )

            notificationManager.createNotificationChannel(notificationChannel)

        }
        // TODO: Step 1.6 END create a channel
    }

    companion object{
        private const val REQUEST_CODE = 10
        private const val WRITE_REQUEST_CODE = 43

        const val ARGUMENT_INTENT = "argument_intent"

        fun getInstance(data: Intent?): FileConverterFragment{
            return FileConverterFragment(data)

            /*
            val intent = Intent(context, MainActivity::class.java).apply{
                putExtra("message", "Message")
            }
            data?.let{
                intent.putExtra(ARGUMENT_INTENT, data)
            }
            return intent*/
        }

    }
}