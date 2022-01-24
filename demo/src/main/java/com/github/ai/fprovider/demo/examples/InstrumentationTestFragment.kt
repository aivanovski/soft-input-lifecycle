package com.github.ai.fprovider.demo.examples

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.ai.fprovider.demo.databinding.FragmentSimpleBinding
import com.github.ai.fprovider.demo.extensions.getInputMethodManager
import com.github.ai.fprovider.demo.utils.Event
import com.github.ai.lifecycle.SoftInputLifecycleOwner
import com.github.ai.lifecycle.WindowFocusLiveData.Companion.asWindowFocusLiveData

class InstrumentationTestFragment : Fragment() {

    private val viewModel: InstrumentationTestViewModel by viewModels(
        ownerProducer = {
            this@InstrumentationTestFragment
        }
    )
    private lateinit var binding: FragmentSimpleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSimpleBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lifecycleOwner = SoftInputLifecycleOwner(
            viewLifecycleOwner,
            binding.editText.asWindowFocusLiveData()
        )
        viewModel.showKeyboard.observe(lifecycleOwner) {
            binding.editText.requestFocus()

            requireContext()
                .getInputMethodManager()
                .showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}

class InstrumentationTestViewModel : ViewModel() {

    private val _showKeyboard = MutableLiveData<Event<Unit>>()
    val showKeyboard: LiveData<Event<Unit>> = _showKeyboard

    init {
        _showKeyboard.value = Event(Unit)
    }
}