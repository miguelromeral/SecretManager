package es.miguelromeral.secretmanager.ui.activities

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import es.miguelromeral.secretmanager.R
import es.miguelromeral.secretmanager.classes.MyCipher
import es.miguelromeral.secretmanager.classes.encode
import es.miguelromeral.secretmanager.ui.fragments.FileConverterFragment
import es.miguelromeral.secretmanager.ui.fragments.HomeFragment
import es.miguelromeral.secretmanager.ui.fragments.SecretsFragment
import es.miguelromeral.secretmanager.ui.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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


/*
        navView.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.navigation_home -> {
                    val fragment = HomeFragment()
                    openFragment(fragment)
                    true
                }
                R.id.navigation_dashboard -> {
                    val fragment = FileConverterFragment()
                    openFragment(fragment)
                    true
                }
                R.id.navigation_notifications -> {
                    val fragment = SecretsFragment()
                    openFragment(fragment)
                    true
                }
                R.id.action_settings -> {
                    val fragment = SettingsFragment()
                    openFragment(fragment)
                    true
                }
                else -> false
            }
        }*/

    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
