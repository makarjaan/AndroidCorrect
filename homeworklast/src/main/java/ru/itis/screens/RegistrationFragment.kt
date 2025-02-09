package ru.itis.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.itis.base.MainActivity
import ru.itis.data.entities.UserEntity
import ru.itis.di.ServiceLocator
import ru.itis.homeworklast.R
import ru.itis.homeworklast.databinding.FragmentRegistrationBinding
import ru.itis.util.Keys
import java.util.UUID

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private var viewBinding: FragmentRegistrationBinding? = null
    private var userRepository = ServiceLocator.getUserRepository()
    private var pref: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        initView()
    }

    private fun initView() {
        viewBinding?.run {

            authorizationBtn.setOnClickListener {
                (requireActivity() as? MainActivity)?.replaceFragment(AuthorizationFragment.TAG)
            }

            registrationBtn.setOnClickListener {
                val email = viewBinding?.emailEt?.text.toString()
                val name = viewBinding?.nameEt?.text.toString()
                val passwordFirst = viewBinding?.passwordFirstEt?.text.toString()
                val passwordSecond = viewBinding?.passwordSecondEt?.text.toString()

                checkConstraints(email, name, passwordFirst, passwordSecond)

                if (email.contains("@") && name.isNotEmpty() && checkPassword(passwordFirst)
                    && passwordFirst == passwordSecond
                ) {

                    val user = UserEntity(
                        id = UUID.randomUUID().toString(),
                        userName = name,
                        email = email,
                        password = passwordSecond
                    )

                    lifecycleScope.launch {
                        runCatching {
                            val count = userRepository.userInDataBase(email)
                            if (count == 0) {
                                userRepository.saveUser(user)

                                pref?.edit()?.apply {
                                    putString(MainActivity.USER_ID_TAG, user.id)
                                    commit()
                                }

                                (requireActivity() as? MainActivity)?.replaceFragment(MainPageFragment.TAG)
                            } else {
                                Toast.makeText(context, R.string.userindb_info, Toast.LENGTH_SHORT).show()
                                throw Exception(resources.getString(R.string.userindb_info))
                            }
                        }.onFailure { ex ->
                            Log.e(Keys.ERROR_MESSAGE, "${resources.getString(R.string.save_user_error)} ${ex.message}", ex)
                        }
                    }
                }
            }
        }
    }



    private fun checkConstraints(email: String, name: String, passwordFirst: String, passwordSecond: String) {
        if (!email.contains("@")) {
            viewBinding?.emailInput?.error = resources.getString(R.string.email_error)
        } else {
            viewBinding?.emailInput?.error = null
        }

        if (name.isEmpty()) {
            viewBinding?.nameInput?.error = resources.getString(R.string.name_error)
        } else {
            viewBinding?.nameInput?.error = null
        }

        if (!checkPassword(passwordFirst)) {
            viewBinding?.passwordFirstInput?.error = resources.getString(R.string.password_error)
        } else {
            viewBinding?.passwordFirstInput?.error = null
        }

        if (passwordFirst != passwordSecond) {
            viewBinding?.passwordSecondInput?.error = resources.getString(R.string.password_second_error)
        } else {
            viewBinding?.passwordSecondInput?.error = null
        }
    }

    private fun checkPassword (password : String) : Boolean {
        val passwordPattern = "^(?=.*[!;%:?№#$&*]).{6,}$".toRegex()
        return passwordPattern.matches(password)
    }


    companion object {
        const val TAG = "RegistrationFragment"
    }

}