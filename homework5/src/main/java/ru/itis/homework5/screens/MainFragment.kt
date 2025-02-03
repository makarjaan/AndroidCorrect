package ru.itis.homework5.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Surface
import androidx.core.content.ContextCompat
import ru.itis.homework5.MainActivity
import ru.itis.homework5.handler.PermissionsHandler
import ru.itis.homework5.R
import ru.itis.homework5.databinding.FragmentMainBinding
import ru.itis.homework5.ui.MainScreen

class MainFragment : BaseFragment(R.layout.fragment_main) {

    private var viewBinding : FragmentMainBinding? = null
    private var permissionHandler: PermissionsHandler? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMainBinding.inflate(inflater)
        permissionHandler = (requireActivity() as? MainActivity)?.permissionHandler
        permissionHandler?.initContracts(requireActivity() as MainActivity)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isPermissionGranted = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

            if (!isPermissionGranted) {
                permissionHandler?.requestSinglePermission(Manifest.permission.POST_NOTIFICATIONS)
            }

            (requireActivity() as? MainActivity)?.let { activity ->
                if (!isPermissionGranted && activity.getPermissionDeniedCount() >= 2) {
                    activity.showPermissionDialog()
                }
            }
        }

        viewBinding?.composeContainerId?.setContent {
            Surface {
                MainScreen()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }

    companion object {
        const val TAG = "MainFragment"
    }
}