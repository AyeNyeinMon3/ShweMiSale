package com.example.shwemisale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shwemi.util.Resource
import com.example.shwemi.util.getAlertDialog
import com.example.shwemisale.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var loading : AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentLoginBinding.inflate(inflater).also {
            binding = it
        }.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loading = requireContext().getAlertDialog()
        requireActivity().deleteDatabase("AppDatabase")

        binding.btnLogin.setOnClickListener {
            if (binding.edtPassword.text.toString().isEmpty()){
                binding.userNameTextInputLayout.helperText = "required *"
            }else if (binding.edtUsername.text.toString().isEmpty()){
                binding.userNameTextInputLayout.helperText = "required *"
            }else{
                viewModel.login(binding.edtUsername.text.toString(),binding.edtPassword.text.toString())
            }
        }

        viewModel.loginLiveData.observe(viewLifecycleOwner){
            when (it){
                is Resource.Loading->{
                    loading.show()
                }
                is Resource.Success->{
                    loading.dismiss()
                    Toast.makeText(requireContext(),"log in successful",Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                is Resource.Error->{
                    loading.dismiss()
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()

                }
                else -> {}
            }
        }
    }

}