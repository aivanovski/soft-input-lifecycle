package com.github.ai.fprovider.demo.examples

import android.content.Context
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
import com.github.ai.fprovider.demo.extensions.setupActionBar
import com.github.ai.fprovider.demo.utils.Event

class NoSoftInputLifecycleExampleFragment : Fragment() {

    private val viewModel: NoSoftInputLifecycleViewModel by viewModels(
        ownerProducer = {
           this@NoSoftInputLifecycleExampleFragment
        }
    )
    private lateinit var binding: FragmentSimpleBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setupActionBar {
            title = ""
        }
    }

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

        viewModel.showKeyboard.observe(viewLifecycleOwner) {
            binding.editText.requestFocus()

            requireContext()
                .getInputMethodManager()
                .showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}

class NoSoftInputLifecycleViewModel : ViewModel() {

    private val _showKeyboard = MutableLiveData<Event<Unit>>()
    val showKeyboard: LiveData<Event<Unit>> = _showKeyboard

    init {
        _showKeyboard.value = Event(Unit)
    }
}