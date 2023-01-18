package com.example.shwemisale.pawnInterestModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.databinding.DialogChangeFeatureBinding
import com.example.shwemisale.databinding.DialogSellTypeBinding
import com.example.shwemisale.databinding.DialogStockCheckBinding
import com.example.shwemisale.databinding.FragmentGoldFromHomePawnInterestBinding
import com.example.shwemisale.sellModule.GoldFromHomeData
import com.example.shwemisale.sellModule.GoldFromHomeRecyclerAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PawnInterestGoldFromHomeFragment:Fragment() {

    lateinit var binding: FragmentGoldFromHomePawnInterestBinding
    lateinit var alertDialogBinding: DialogStockCheckBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentGoldFromHomePawnInterestBinding.inflate(inflater).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.imageBtnSelect.setOnClickListener {
            showStockCheckDialog()
        }



        val adapter = GoldFromHomeRecyclerAdapter()
        binding.rvGoldFromHome.adapter = adapter
        adapter.submitList(listOf(
            GoldFromHomeData("1","လက်စွပ်","550,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("2","လက်စွပ်","650,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("3","လက်စွပ်","750,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("4","လက်စွပ်","850,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("5","လက်စွပ်","950,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("6","လက်စွပ်","150,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("7","လက်စွပ်","5350,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("8","လက်စွပ်","5550,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("9","လက်စွပ်","5506,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("10","လက်စွပ်","5507,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("11","လက်စွပ်","550,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("12","လက်စွပ်","550,000","0K 1P 6Y","550,000"),
            GoldFromHomeData("13","လက်စွပ်","550,000","0K 1P 6Y","550,000"),
        ))

    }


    fun showStockCheckDialog() {
            val builder = MaterialAlertDialogBuilder(requireContext())
            val inflater = LayoutInflater.from(builder.context)
            alertDialogBinding = DialogStockCheckBinding.inflate(inflater, ConstraintLayout(builder.context), false)
            builder.setView(alertDialogBinding.root)
            val alertDialog = builder.create()
        alertDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            alertDialog.setCancelable(false)
            alertDialogBinding.btnContinue.setOnClickListener {
                binding.rvGoldFromHome.visibility = View.VISIBLE
                alertDialog.dismiss()
            }
            alertDialogBinding.ivClose.setOnClickListener {
                alertDialog.dismiss()
            }
        alertDialogBinding.rvStockCheck.setLayoutManager(
            LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            )
        )


            alertDialog.show()



    }

}
