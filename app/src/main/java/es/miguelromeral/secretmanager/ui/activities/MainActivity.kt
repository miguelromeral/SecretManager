package es.miguelromeral.secretmanager.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.database.SecretDatabase
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import es.miguelromeral.secretmanager.classes.*
import es.miguelromeral.secretmanager.ui.fragments.*
import java.io.File
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        // We change the Splash theme to the main theme
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the bottom navigation menu
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.action_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // If we recieve one file, send it to the FileConverter Fragment.
        if(intent?.action == Intent.ACTION_SEND) {
            if (intent.type?.startsWith("image/") == true ||
                intent.type?.startsWith("video/") == true ||
                intent.type?.startsWith("application/") == true) {

                navController.navigate(HomeFragmentDirections.actionNavigationHomeToNavigationDashboard(intent))
            }
        }

        Log.i(TAG, "MainActivity created successfully.")
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            SecretsFragment.REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    exportSecrets()
                    Log.i("ExportCSV", "Exported secrets")

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i("ExportCSV", "User clicked on Deny permission")
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun exportSecrets(){
        exportSecrets(baseContext, SecretDatabase.getInstance(baseContext))?.let{ uri ->

            val view: View = findViewById(R.id.nav_host_fragment)

            val snackbar = Snackbar.make(view, R.string.exported_secrets_title, Snackbar.LENGTH_LONG)
            snackbar.setAction(R.string.open_file){

                try {
                    startActivity(actionViewFile(baseContext, uri))
                }catch (e: Exception){
                    Log.i(TAG, "Exception while opening csv.")
                    e.printStackTrace()
                }

                /*

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(uri)
                startActivity(intent)
*/
            }
            snackbar.show()
            Log.i("ExportCSV", "Snack showed")
        }

    }
}
