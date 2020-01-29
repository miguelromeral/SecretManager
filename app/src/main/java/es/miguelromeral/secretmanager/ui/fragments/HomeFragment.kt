package es.miguelromeral.secretmanager.ui.fragments

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import es.miguelromeral.secretmanager.databinding.FragmentHomeBinding
import es.miguelromeral.secretmanager.ui.viewmodelfactories.HomeFactory
import es.miguelromeral.secretmanager.ui.viewmodels.HomeViewModel
import androidx.lifecycle.Observer
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.classes.IsBase64Encoded
import es.miguelromeral.secretmanager.database.SecretDatabase
import es.miguelromeral.secretmanager.ui.activities.MainActivity
import es.miguelromeral.secretmanager.ui.models.TextItem
import es.miguelromeral.secretmanager.ui.utils.shareContentText
import es.miguelromeral.secretmanager.ui.utils.showHidePassword
import kotlinx.android.synthetic.main.layout_qr.view.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.opengl.Visibility
import android.os.Environment
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.preference.PreferenceManager
import com.google.android.material.button.MaterialButton
import es.miguelromeral.secretmanager.classes.createQrImage
import es.miguelromeral.secretmanager.classes.exportSecrets
import es.miguelromeral.secretmanager.network.ServiceQR
import es.miguelromeral.secretmanager.ui.utils.createAlertDialog
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var item: TextItem
    private lateinit var binding: FragmentHomeBinding

    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null

    private val TAG = "HomeFragment"

    private var toReturn = false

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
        binding = DataBindingUtil.inflate(inflater, es.miguelromeral.secretmanager.R.layout.fragment_home, container, false)
        // Passing the parameters to binding variables
        binding.viewModel = viewModel
        item = viewModel.item
        binding.item = item

        myClipboard = context!!.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

        // Floating Action Button behaviour
        binding.floatingActionButton.setOnClickListener{view ->
            if(item.output.isNotEmpty()) {
                shareContentText(view.context, item.output)
            }else{
                Toast.makeText(context, es.miguelromeral.secretmanager.R.string.error_output_empty, Toast.LENGTH_LONG).show()
            }
        }

        // Execution Button
        binding.executeButton.bExecute.setOnClickListener{
            if(viewModel.execute(it.context) && toReturn){
                // We only send back the result if it was called by an intent-filter
                val i = Intent()
                i.putExtra(Intent.EXTRA_PROCESS_TEXT, item.output)
                activity!!.setResult(Activity.RESULT_OK, i)
                activity!!.finish()
                Log.i(TAG, "Sending back the result.")
            }
        }

        // Functionality to Show / Hide Password in this fragment
        binding.passwordLayout.cbShowPassword.setOnClickListener{
            showHidePassword(
                it.context,
                it as CheckBox,
                binding.passwordLayout.etPassword
            )
        }

        // Update QR iamge if there's new content to display
        viewModel.qr.observe(this, Observer {
            val image = binding.qrLayout.imageQr
            val icon = binding.qrLayout.iconQr

            if(it != null){
                icon.visibility = View.GONE

                // We save the image to internal storage according to the preferences.
                if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(getString(R.string.preference_save_qr_id), false))
                    createQrImage(context!!, it, item.alias)

                // Set the QR image from raw data
                image.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))

                // Long click behaviour
                image.setOnLongClickListener{
                    ServiceQR.openQRIntent(it.context, item.output)
                    return@setOnLongClickListener true
                }

                image.visibility = View.VISIBLE

                /*
                val animator = ObjectAnimator.ofFloat(image, View.ALPHA, 0f)
                animator.repeatCount = 1
                animator.duration = 500
                animator.repeatMode = ObjectAnimator.REVERSE
                animator.start()
*/

                val fadeIn = AlphaAnimation(0f, 1f)
                fadeIn.interpolator = DecelerateInterpolator() //add this
                fadeIn.duration = 1000

                val animation = AnimationSet(false) //change to false
                animation.addAnimation(fadeIn)
                //image.setAnimation(animation)
                image.startAnimation(animation)
            }
            else{
                icon.visibility = View.VISIBLE
                image.setOnClickListener{}
                image.visibility = View.GONE
            }
        })

        // Check if there's data from the bundle
        arguments?.let{
            val args = HomeFragmentArgs.fromBundle(arguments!!)
            Log.i(TAG, "Getting input from bundle.")
            item.input = args.input
            item.decrypt = true
            item.password = String()
        }

        // Check if the fragment was initiated by the EXTRA_PROCESS_TEXT intent-filter
        activity?.intent?.let{ intent ->
            val shared = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
            shared?.let {
                val input = it.toString()
                item.decrypt = IsBase64Encoded(input)
                item.input = input

                if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(getString(R.string.preference_auto_return_result_id), true))
                    toReturn = !intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            }
        }

        // The fragment has a menu
        setHasOptionsMenu(true)

        // Returning the binding root
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(es.miguelromeral.secretmanager.R.menu.options_home, menu)
    }


    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem != null){
            when(menuItem.itemId) {
                R.id.option_output_to_input -> {
                    item.let{
                        if(it.output.isNotEmpty()) {
                            it.input = it.output
                            it.output = ""
                        }else{
                            Toast.makeText(context, R.string.error_output_empty, Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
                R.id.option_output_to_clipboard -> {
                    if(item.output.isNotEmpty()){
                        val myClip = ClipData.newPlainText(getString(R.string.app_name), item.output)
                        myClipboard?.setPrimaryClip(myClip)

                        Toast.makeText(context!!, R.string.output_copied, Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "Output copied to clipboard.")

                    }
                    true
                }
                R.id.option_delete_input -> {
                    item.input = ""
                }
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

}