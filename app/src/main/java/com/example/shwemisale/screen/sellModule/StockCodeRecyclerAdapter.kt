package com.example.shwemisale

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.data_layers.ui_models.product.ProductInfoUiModel
import com.example.shwemisale.databinding.ItemStockCodeItemBinding

data class StockCodeData(
    val id:String,
    val code:String,
    val size:String,
    val price:String
)

class StockCodeRecyclerAdapter:ListAdapter<ProductInfoUiModel,StockCodeViewHolder>(StockCodeDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockCodeViewHolder {
       return StockCodeViewHolder(ItemStockCodeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: StockCodeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class StockCodeViewHolder(private val binding: ItemStockCodeItemBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(data: ProductInfoUiModel){
        binding.tvCode.text=data.code
        binding.tvSize.text=data.size
        binding.tvPrice.text=data.jewellery_type_id
    }
}

object StockCodeDiffUtil:DiffUtil.ItemCallback<ProductInfoUiModel>(){
    override fun areItemsTheSame(oldItem: ProductInfoUiModel, newItem: ProductInfoUiModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductInfoUiModel, newItem: ProductInfoUiModel): Boolean {
          return oldItem == newItem
    }

}