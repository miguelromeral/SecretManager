package es.miguelromeral.secretmanager.ui.activities

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.classes.MyCipher
import es.miguelromeral.secretmanager.classes.encode
import es.miguelromeral.secretmanager.classes.exportSecrets
import es.miguelromeral.secretmanager.database.SecretDatabase
import es.miguelromeral.secretmanager.ui.fragments.FileConverterFragment
import es.miguelromeral.secretmanager.ui.fragments.HomeFragment
import es.miguelromeral.secretmanager.ui.fragments.SecretsFragment
import es.miguelromeral.secretmanager.ui.fragments.SettingsFragment
import android.content.Intent
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService





class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {


        //Thread.sleep(4000)

        setTheme(es.miguelromeral.secretmanager.R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(es.miguelromeral.secretmanager.R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(es.miguelromeral.secretmanager.R.id.nav_view)

        val t: View = findViewById(es.miguelromeral.secretmanager.R.id.nav_host_fragment)

        val navController = findNavController(es.miguelromeral.secretmanager.R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                es.miguelromeral.secretmanager.R.id.navigation_home,
                es.miguelromeral.secretmanager.R.id.navigation_dashboard,
                es.miguelromeral.secretmanager.R.id.navigation_notifications,
                es.miguelromeral.secretmanager.R.id.action_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
        exportSecrets(baseContext, SecretDatabase.getInstance(baseContext))?.let{uri ->

            val view: View = findViewById(es.miguelromeral.secretmanager.R.id.nav_view)

            val snackbar = Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            snackbar.setAction("Open file", View.OnClickListener {


                Log.i("ExportCSV", "Preparing action: "+uri.path)
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.setDataAndType(uri, "*/*")


                if (intent.resolveActivityInfo(packageManager, 0) != null) {
                    startActivity(intent)
                    Log.i("ExportCSV", "Opening it!")
                } else {
                    // if you reach this place, it means there is no any file
                    // explorer app installed on your device
                    Log.i("ExportCSV", "No apps to open it")

                }

            })
            //snackbar.setActionTextColor(Color.BLUE)
            snackbar.show()
            Log.i("ExportCSV", "Snack showed")
        }

    }

}
