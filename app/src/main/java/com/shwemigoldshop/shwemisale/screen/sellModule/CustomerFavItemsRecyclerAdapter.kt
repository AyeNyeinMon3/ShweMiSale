package com.shwemigoldshop.shwemisale.screen.sellModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shwemigoldshop.shwemisale.util.loadImageWithGlide
import com.shwemigoldshop.shwemisale.data_layers.ui_models.product.ProductUiModel
import com.shwemigoldshop.shwemisale.databinding.ItemCustomerFavItemsBinding

data class CustomerFavItemsData(
    val id : String,
    val name: String,
    val price : String
)

class CustomerFavItemsRecyclerAdapter:ListAdapter<ProductUiModel, CustomerFavItemsViewHolder>(
    CustomerFavItemsDiffUtil
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerFavItemsViewHolder {
        return CustomerFavItemsViewHolder(ItemCustomerFavItemsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: CustomerFavItemsViewHolder, position: Int) {
       holder.bind(getItem(position))
    }

}

class CustomerFavItemsViewHolder(private var binding: ItemCustomerFavItemsBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(data: ProductUiModel){
        binding.tvItemName.text = data.name
        binding.tvItemPrice.text = data.cost
        binding.ivCustomerfavItem.loadImageWithGlide(data.thumbnail?.url)
    }
}

object CustomerFavItemsDiffUtil : DiffUtil.ItemCallback<ProductUiModel>(){
    override fun areItemsTheSame(
        oldItem: ProductUiModel,
        newItem: ProductUiModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ProductUiModel,
        newItem: ProductUiModel
    ): Boolean {
        return oldItem == newItem
    }

}