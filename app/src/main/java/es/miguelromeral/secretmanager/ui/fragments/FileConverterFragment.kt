package es.miguelromeral.secretmanager.ui.fragments

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.classes.readTextFromUri
import es.miguelromeral.secretmanager.databinding.FragmentFileConverterBinding
import es.miguelromeral.secretmanager.ui.shareContentText
import es.miguelromeral.secretmanager.ui.viewmodelfactories.FileConverterFactory
import es.miguelromeral.secretmanager.ui.viewmodelfactories.HomeFactory
import es.miguelromeral.secretmanager.ui.viewmodels.FileConverterViewModel
import es.miguelromeral.secretmanager.ui.viewmodels.HomeViewModel
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import android.webkit.MimeTypeMap
import android.content.ContentResolver
import es.miguelromeral.secretmanager.classes.MyCipher
import es.miguelromeral.secretmanager.classes.getFileMimeType

private const val TAG = "FCVM"

class FileConverterFragment : Fragment() {

    private lateinit var viewModel: FileConverterViewModel
    private lateinit var binding: FragmentFileConverterBinding

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

        binding.executeButton.bExecute.setOnClickListener{

            viewModel.item.uri?.let { data ->
                try
                {

                    //write()

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

        // Returning the binding root
        return binding.root
    }



    private fun write(from: Uri?, to: Uri?, decrypt: Boolean){
        var fos: FileOutputStream? = null
        try {
            val stream = readTextFromUri(from!!, context!!.contentResolver)
            val cypher = MyCipher()
            val password = viewModel.item.password

            val new_stream = if(decrypt){
                cypher.decrypt(stream!!, password)
            }else{
                cypher.encrypt(stream!!, password)
            }

            context!!.contentResolver.openFileDescriptor(to!!, "w")?.use {
                // use{} lets the document provider know you're done by automatically closing the stream
                fos = FileOutputStream(it.fileDescriptor)
                fos?.let { fos ->
                    fos.write(new_stream)
                }
                Toast.makeText(context!!, "File was saved!", Toast.LENGTH_LONG).show()
                Log.i(TAG, "File written")
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fos?.close()
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
                val tmp = data?.data

                write(viewModel.item.uri, tmp, viewModel.item.decrypt)
            }
        }
    }

    companion object{
        private const val REQUEST_CODE = 10
        private const val WRITE_REQUEST_CODE = 43
    }
}