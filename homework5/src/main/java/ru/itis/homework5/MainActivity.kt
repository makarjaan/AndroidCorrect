package ru.itis.homework5

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ru.itis.homework5.databinding.ActivityMainBinding
import ru.itis.homework5.handler.PermissionsHandler
import ru.itis.homework5.screens.MainFragment


class MainActivity : AppCompatActivity() {

    private val containerId: Int = R.id.main_fragment_container
    var viewBinding: ActivityMainBinding? = null
    var permissionHandler: PermissionsHandler? = null
    private var permissionDeniedCount = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                add (
                    containerId,
                    MainFragment(),
                    MainFragment.TAG
                ).commit()
            }
        }
        if (permissionHandler == null) {
            permissionHandler = PermissionsHandler(::plusPermissionDeniedCount)
        }
        sharedPreferences = getSharedPreferences(resources.getString(R.string.shared_preferences_name), MODE_PRIVATE)
        permissionDeniedCount = sharedPreferences.getInt(DENIED_COUNT_TAG, 0)
    }

    fun plusPermissionDeniedCount() {
        permissionDeniedCount++
    }

    fun getPermissionDeniedCount() : Int{
        return permissionDeniedCount
    }

    fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.permission_need))
            .setMessage(resources.getString(R.string.givemepermissionplease))
            .setPositiveButton(resources.getString(R.string.open_setting)) { _, _ -> openAppSettings() }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        sharedPreferences.edit().putInt(DENIED_COUNT_TAG, permissionDeniedCount).apply();
    }

    override fun onDestroy() {
        super.onDestroy()
        permissionHandler = null
        viewBinding = null
    }

    companion object {
        const val DENIED_COUNT_TAG = "deniedCount"
    }
}