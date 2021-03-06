package es.miguelromeral.secretmanager.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import es.miguelromeral.secretmanager.R
import android.content.Intent
import android.net.Uri
import android.graphics.PorterDuff
import android.R.color
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Build
import android.R.attr.mode


class SettingsFragment : PreferenceFragmentCompat(),  SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val p = findPreference(getString(R.string.preference_help_id))
        p.setOnPreferenceClickListener {
            context?.let{
                val i = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://github.com/miguelromeral/SecretManager/blob/master/HOW-TO.md")
                }
                it.startActivity(i)
                return@setOnPreferenceClickListener true
            }
            false
        }

        setTintColor(context)
    }

    private fun setTintColor(context: Context?){
        context?.let {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val night = isNightThemeEnabled(context, sharedPreferences)
            val preferences = listOf(
                context.getString(R.string.preference_help_id),
                context.getString(R.string.preference_date_format_id),
                context.getString(R.string.preference_key_theme),
                context.getString(R.string.preference_key_filename),
                context.getString(R.string.preference_save_qr_id),
                getString(R.string.preference_auto_return_result_id)
            )

            for (spr in preferences) {
                val pref = findPreference(spr)
                pref.icon.setTint(
                    if (night) context.getColor(R.color.purple) else context.getColor(R.color.black)
                )
            }
        }
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        when(key){
            resources.getString(R.string.preference_key_theme) -> {
                setStyleTheme(context!!, preferences)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var mycontext = context
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {

        fun isNightThemeEnabled(context: Context, sharedPreferences: SharedPreferences) =
            sharedPreferences!!.getBoolean(context.getString(R.string.preference_key_theme), false)


        private fun setStyleTheme(context: Context, sharedPreferences: SharedPreferences? = null){
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val resources = context.resources

            val style = preferences!!.getBoolean(resources.getString(R.string.preference_key_theme), false)
            if(style){
                //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
/*

                val config = Configuration(resources.getConfiguration())
                config.uiMode = Configuration.UI_MODE_NIGHT_YES
                if (Build.VERSION.SDK_INT >= 17) {
                    context.createConfigurationContext(config)
                }*/


                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

}