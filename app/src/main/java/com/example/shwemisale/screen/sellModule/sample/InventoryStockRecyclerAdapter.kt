package com.example.shwemisale.screen.sellModule.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.databinding.ItemInventoryStockBinding

data class InventoryStockData(
    val id : String,
    val code : String
)

class InventoryStockRecyclerAdapter:ListAdapter<InventoryStockData, InventoryStockViewHolder>(
    InventoryStockDiffUtil
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryStockViewHolder {
        return InventoryStockViewHolder(ItemInventoryStockBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: InventoryStockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class InventoryStockViewHolder(private var binding:ItemInventoryStockBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(data: InventoryStockData){
        binding.tvStockCode.text = data.code
    }
}

object InventoryStockDiffUtil:DiffUtil.ItemCallback<InventoryStockData>(){
    override fun areItemsTheSame(
        oldItem: InventoryStockData,
        newItem: InventoryStockData
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: InventoryStockData,
        newItem: InventoryStockData
    ): Boolean {
        return oldItem == newItem
    }

}