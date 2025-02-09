package ru.itis.homework5.handler


import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class PermissionsHandler(
    private val onSinglePermissionGranted: (() -> Unit)? = null,
) {

    private var singlePermissionResult: ActivityResultLauncher<String>? = null

    fun initContracts(activity: AppCompatActivity) {
        if (singlePermissionResult == null) {
            singlePermissionResult = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    onSinglePermissionGranted?.invoke()
                }
            }
        }
    }

    fun requestSinglePermission(permission: String) {
        singlePermissionResult?.launch(permission)
    }

}