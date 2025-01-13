package ru.itis.homework4.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.itis.homework4.R
import ru.itis.homework4.databinding.ActivityMainBinding
import ru.itis.homework4.handler.NotificationsHandler
import ru.itis.homework4.handler.PermissionsHandler
import ru.itis.homework4.screens.MainFragment


class MainActivity : AppCompatActivity() {

    private var viewBinding: ActivityMainBinding? = null
    private val containerId: Int = R.id.main_fragment_container
    var notificationHandler: NotificationsHandler? = null
    var permissionHandler: PermissionsHandler? = null
    private var isToastShown = false


    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = intent.getIntExtra(THEME_TAG, R.style.Theme_AndroidCorrect)
        setTheme(theme)
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

        if (notificationHandler == null) notificationHandler = NotificationsHandler(applicationContext)

        if (permissionHandler == null) permissionHandler = PermissionsHandler()
        permissionHandler?.initContracts(this)

        val startNotification = intent.getBooleanExtra(NotificationsHandler.START_NOTIFICATION_TAG, false)
        if (startNotification) {
            Toast.makeText(this, R.string.toast_info_notification_start, Toast.LENGTH_SHORT).show()
        }

    }


    fun setNewTheme(themeId: Int) {
        intent.putExtra(NotificationsHandler.START_NOTIFICATION_TAG, false)
        intent.putExtra(THEME_TAG, themeId)
        recreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
        notificationHandler = null
        permissionHandler = null
    }

    companion object {
        const val THEME_TAG = "ApplicationTheme"
    }

}