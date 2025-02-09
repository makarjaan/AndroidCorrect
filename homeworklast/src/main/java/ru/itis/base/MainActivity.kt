package ru.itis.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ru.itis.homeworklast.R
import ru.itis.homeworklast.databinding.ActivityMainBinding
import ru.itis.screens.AuthorizationFragment
import ru.itis.screens.MainPageFragment
import ru.itis.screens.RegistrationFragment

class MainActivity : AppCompatActivity() {

    private val conteinerId: Int = R.id.main_fragment_container
    private var viewBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val userId = getPreferences(Context.MODE_PRIVATE).getString(USER_ID_TAG, null)
        val correctFargment : Fragment
        if (userId == null) {
            correctFargment = AuthorizationFragment()
        } else {
            correctFargment = MainPageFragment()
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                add(
                    conteinerId,
                    correctFargment,
                    correctFargment::class.java.simpleName
                ).commit()
            }
        }
    }

    fun replaceFragment(tagNew: String) {
        var newFragment = supportFragmentManager.findFragmentByTag(tagNew)
        if (newFragment == null) {
            newFragment = when (tagNew) {
                RegistrationFragment.TAG -> RegistrationFragment()
                MainPageFragment.TAG -> MainPageFragment()
                AuthorizationFragment.TAG -> AuthorizationFragment()
                else -> null
            }
        }
        newFragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(conteinerId, it, tagNew)
                .commit()
        }
    }

    fun addFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val currentFragment = supportFragmentManager.findFragmentById(conteinerId)
        currentFragment?.let {
            fragmentTransaction.hide(it)
        }
        fragmentTransaction
            .add(conteinerId, fragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
    }

    companion object {
        const val USER_ID_TAG = "userId"
        const val ERROR_TAG = "Error"
    }
}