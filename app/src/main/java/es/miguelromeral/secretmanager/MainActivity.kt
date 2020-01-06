package es.miguelromeral.secretmanager

import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import es.miguelromeral.secretmanager.classes.MyCipher
import es.miguelromeral.secretmanager.classes.encode

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
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        val c = MyCipher()
        val plain = "My plain message"
        val pwd = "pwdsadsadasdssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"

        Log.i("Test", "Plain: $plain")
        Log.i("Test", "pwd: $pwd")
        val encrypted = c.encrypt(plain, pwd)
        Log.i("Test", "Encrypted: ${encode(encrypted)}")
        val restored = c.decrypt(encrypted, pwd)
        Log.i("Test", "Restored: $restored")
    }
}
