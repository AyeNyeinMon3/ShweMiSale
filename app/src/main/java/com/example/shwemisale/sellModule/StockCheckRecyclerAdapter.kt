package com.example.shwemisale.sellModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.databinding.ItemStockCheckBinding

data class StockCheckData(
    val id : String,
    val stockCode : String,
    val weight : String
)

class StockCheckRecyclerAdapter:ListAdapter<StockCheckData, StockCheckViewHolder>(StockCheckDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockCheckViewHolder {

        return StockCheckViewHolder(ItemStockCheckBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: StockCheckViewHolder, position: Int) {

        holder.bind(getItem(position))
    }

}

class StockCheckViewHolder(private var binding:ItemStockCheckBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(data: StockCheckData){
        binding.tvStockCode.text = data.stockCode
        binding.tvGoldAndGemWeightGm.text = data.weight
    }
}

object StockCheckDiffUtil:DiffUtil.ItemCallback<StockCheckData>(){
    override fun areItemsTheSame(oldItem: StockCheckData, newItem: StockCheckData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StockCheckData, newItem: StockCheckData): Boolean {
      return  oldItem == newItem
    }

}