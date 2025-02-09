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
import ru.itis.di.ServiceLocator
import ru.itis.homeworklast.R
import ru.itis.homeworklast.databinding.FragmentAuthorizationBinding


class AuthorizationFragment : Fragment(R.layout.fragment_authorization) {

    private var viewBinding: FragmentAuthorizationBinding? = null
    private var userRepository = ServiceLocator.getUserRepository()
    var pref: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentAuthorizationBinding.inflate(inflater, container, false)
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
                val email = viewBinding?.emailEt?.text.toString()
                val password = viewBinding?.passwordEt?.text.toString()

                if (email.isEmpty()) {
                    emailInput.error = resources.getString(R.string.email_edit)
                } else {
                    lifecycleScope.launch {
                        runCatching {
                            val count = userRepository.userInDataBase(email)
                            if (count == 1) {
                                val id = userRepository.getUserIdByEmail(email)
                                if (id.isNotEmpty()) {
                                    val user = userRepository.getUserById(id)
                                    if (user.password != password) {
                                        passwordInput.error = resources.getString(R.string.password_user_error)
                                    } else {
                                        pref?.edit()?.apply {
                                            putString(MainActivity.USER_ID_TAG, user.id)
                                            commit()
                                        }
                                        (requireActivity() as? MainActivity)?.replaceFragment(MainPageFragment.TAG)
                                    }
                                }
                            } else {
                                Toast.makeText(context, R.string.user_not_inbd, Toast.LENGTH_SHORT).show()
                                throw Exception(resources.getString(R.string.login_error))
                            }
                        }.onFailure { ex ->
                            when (ex) {
                                is Exception -> {
                                    Log.e(MainActivity.ERROR_TAG, "${ex.message}", ex)
                                }
                            }
                        }
                    }
                }
            }

            registrationBtn.setOnClickListener {
                (requireActivity() as? MainActivity)?.replaceFragment(RegistrationFragment.TAG)
            }
        }
    }

    companion object {
        const val TAG = "AuthorizationFragment"
    }
}