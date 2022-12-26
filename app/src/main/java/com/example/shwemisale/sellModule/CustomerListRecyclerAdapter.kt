package com.example.shwemisale

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.databinding.ItemCustomerListBinding
import java.util.*

data class CustomerListData(
    val id:String,
    val name:String,
    val phNumber: String,
    val NRC : String,
    val birthDate: String,
    val township : String,
    val account : String,
)

class CustomerListRecyclerAdapter() : ListAdapter<CustomerListData,CustomerListViewHolder>(CustomerListDiffUtils){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerListViewHolder {
        return CustomerListViewHolder(ItemCustomerListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: CustomerListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class CustomerListViewHolder(private var binding: ItemCustomerListBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(data: CustomerListData){
        binding.tvName.text = data.name
        binding.tvPhNumber.text = data.phNumber
        binding.tvNRC.text = data.NRC
        binding.tvBirthDate.text = data.birthDate
        binding.tvTownship.text = data.township
        binding.tvAcc.text = data.account
//        while (true){
//            binding.ivArrow.setImageResource(R.drawable.side_arrow)
//
//        }
    }


}

object CustomerListDiffUtils:DiffUtil.ItemCallback<CustomerListData>(){
    override fun areItemsTheSame(oldItem: CustomerListData, newItem: CustomerListData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CustomerListData, newItem: CustomerListData): Boolean{
        return oldItem == newItem
    }

}