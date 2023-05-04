package com.example.shwemisale

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.data_layers.ui_models.customers.CustomerDataUiModel
import com.example.shwemisale.databinding.ItemCustomerListBinding
import java.util.*

data class CustomerListData(
    val id: String,
    val name: String,
    val phNumber: String,
    val NRC: String,
    val birthDate: String,
    val township: String,
    val account: String,
)

class CustomerListRecyclerAdapter(private val navigateToDetail:(customer:CustomerDataUiModel)->Unit) :
    PagingDataAdapter<CustomerDataUiModel, CustomerListViewHolder>(CustomerListDiffUtils) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerListViewHolder {
        return CustomerListViewHolder(
            ItemCustomerListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),navigateToDetail
        )
    }

    override fun onBindViewHolder(holder: CustomerListViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }


}

class CustomerListViewHolder(private var binding: ItemCustomerListBinding,
                             private val navigateToDetail:(customer:CustomerDataUiModel)->Unit) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: CustomerDataUiModel) {
        binding.ivArrow.setOnClickListener {
            navigateToDetail(data)
        }
        binding.tvName.text = data.name
        binding.tvPhNumber.text = data.phone
        binding.tvNRC.text = data.nrc
        binding.tvBirthDate.text = data.date_of_birth
        binding.tvTownship.text = data.township_name
//        binding.tvAcc.text = "-"
//        while (true){
//            binding.ivArrow.setImageResource(R.drawable.side_arrow)
//
//        }
    }


}

object CustomerListDiffUtils : DiffUtil.ItemCallback<CustomerDataUiModel>() {
    override fun areItemsTheSame(
        oldItem: CustomerDataUiModel,
        newItem: CustomerDataUiModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CustomerDataUiModel,
        newItem: CustomerDataUiModel
    ): Boolean {
        return oldItem == newItem
    }

}