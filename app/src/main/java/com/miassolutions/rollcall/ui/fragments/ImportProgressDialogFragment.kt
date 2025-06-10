package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.DialogProgressBinding

class ImportProgressDialogFragment : DialogFragment() {

    private var _binding: DialogProgressBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "ImportProgressDialogFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NO_FRAME, R.style.TransparentDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogProgressBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}