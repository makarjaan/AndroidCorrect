package ru.itis.homework5.screens

import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

open class BaseFragment(layoutId: Int) : Fragment(layoutId){
    protected var composeView : ComposeView? = null
}