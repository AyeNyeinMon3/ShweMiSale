package com.example.shwemisale

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.databinding.FragmentCustomerInfoSellBinding
import com.example.shwemisale.databinding.ItemCustomerFavItemsBinding

data class CustomerFavItemsData(
    val id : String,
    val name: String,
    val price : String
)

class CustomerFavItemsRecyclerAdapter:ListAdapter<CustomerFavItemsData,CustomerFavItemsViewHolder>(CustomerFavItemsDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerFavItemsViewHolder {
        return CustomerFavItemsViewHolder(ItemCustomerFavItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: CustomerFavItemsViewHolder, position: Int) {
       holder.bind(getItem(position))
    }

}

class CustomerFavItemsViewHolder(private var binding: ItemCustomerFavItemsBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(data: CustomerFavItemsData){
        binding.tvItemName.text = data.name
        binding.tvItemPrice.text = data.price
    }
}

object CustomerFavItemsDiffUtil : DiffUtil.ItemCallback<CustomerFavItemsData>(){
    override fun areItemsTheSame(
        oldItem: CustomerFavItemsData,
        newItem: CustomerFavItemsData
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: CustomerFavItemsData,
        newItem: CustomerFavItemsData
    ): Boolean {
        return oldItem == newItem
    }

}