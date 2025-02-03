package ru.itis.homework4.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.itis.homework4.R
import ru.itis.homework4.activity.MainActivity
import ru.itis.homework4.databinding.FragmentMainBinding
import ru.itis.homework4.handler.NotificationsHandler
import ru.itis.homework4.handler.PermissionsHandler
import ru.itis.homework4.model.NotificationData
import ru.itis.homework4.model.NotificationType

class MainFragment : Fragment(R.layout.fragment_main) {

    private var viewBinding: FragmentMainBinding? = null
    private var notificationHandler: NotificationsHandler? = null
    private var notificationChannelId: NotificationType? = null
    private var permissionHandler: PermissionsHandler? = null
    private var counter: Int = 0
    private var flag = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMainBinding.inflate(inflater)
        permissionHandler = (requireActivity() as? MainActivity)?.permissionHandler
        permissionHandler?.initContracts(requireActivity() as MainActivity)
        notificationHandler = (requireActivity() as? MainActivity)?.notificationHandler
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.deleteButton?.visibility = View.GONE
        initViews()
    }


    fun initViews() {
        viewBinding?.run {
            circularImageView.setOnClickListener {
                val currentDrawable = circularImageView.drawable
                if (currentDrawable is BitmapDrawable) {
                    Toast.makeText(requireContext(), R.string.toast_image_info, Toast.LENGTH_SHORT).show()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                            permissionHandler?.requestSinglePermission(Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            getContent.launch("image/*")
                            deleteButton.visibility = View.VISIBLE
                        }
                    }
                    /* Просто по нажатию на картинку
                    val url = getString(R.string.urlimage)
                    circularImageView.let { iv ->
                        Glide.with(iv)
                            .load(url)
                            .circleCrop()
                            .into(iv)
                    }
                    deleteButton.visibility = View.VISIBLE
                     */

                }
            }

            deleteButton.setOnClickListener {
                circularImageView.setImageDrawable(null)
                deleteButton.visibility = View.GONE
            }

            imageButton.setOnClickListener {
                if (!flag) {
                    linear.visibility = View.VISIBLE
                    imageButton.setImageResource(R.drawable.ic_arr_app)
                    flag = true
                    setThemeChangeListener(firstIb, R.style.Theme_Red)
                    setThemeChangeListener(secondIb, R.style.Theme_Blue)
                    setThemeChangeListener(threeIb, R.style.Theme_Green)
                } else {
                    linear.visibility = View.GONE
                    imageButton.setImageResource(R.drawable.ic_arr_down)
                    flag = false
                }
            }

            val spinner = planetsSpinner
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.planets_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    val channelType = parentView?.getItemAtPosition(position).toString()
                    notificationChannelId = NotificationType.valueOf(channelType.uppercase())
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            showNotificationBtn.setOnClickListener {
                val titleText = titleEt.text.toString()
                val infoText = textEt.text.toString()
                if (titleText.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.toast_info_title, Toast.LENGTH_SHORT).show()
                } else if (infoText.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.toast_info_text, Toast.LENGTH_SHORT).show()
                } else {
                    val data = NotificationData(++counter, titleText, infoText, notificationChannelId)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            permissionHandler?.requestSinglePermission(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            notificationHandler?.showNotification(data)
                        }
                    }
                }
            }

            resetColorBtn.setOnClickListener {
                (requireActivity() as MainActivity).setNewTheme(R.style.Theme_AndroidCorrect)
            }
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewBinding?.circularImageView?.let { imageView ->
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(imageView)
            }
            viewBinding?.deleteButton?.visibility = View.VISIBLE
        }
    }

    private fun setThemeChangeListener(imageButton: ImageButton, themeId: Int) {
        imageButton.setOnClickListener {
            val activity = requireActivity() as MainActivity
            activity.setNewTheme(themeId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
        notificationHandler = null
    }

    companion object {
        const val TAG = "MainFragment"
    }
}

