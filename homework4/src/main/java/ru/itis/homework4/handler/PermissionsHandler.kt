package ru.itis.homework4.handler

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PermissionsHandler(
    private val onSinglePermissionGranted: (() -> Unit)? = null,
) {

    private var activity: AppCompatActivity? = null
    private var singlePermissionResult: ActivityResultLauncher<String>? = null

    fun initContracts(activity: AppCompatActivity) {
        if (this.activity == null) {
            this.activity = activity
        }
        if (singlePermissionResult == null) {
            singlePermissionResult = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    onSinglePermissionGranted?.invoke()
                }
            }
        }
    }


    fun requestSinglePermission(permission: String) {
        Log.d("TEST-TAG", "singlePermissionResult = ${singlePermissionResult}")
        singlePermissionResult?.launch(permission)
    }

}