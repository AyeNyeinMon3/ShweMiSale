package com.example.shwemisale.screen.sellModule.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.databinding.ItemOutsideStockBinding

data class OutsideStockData(
    val id:String,
    val weight:String,
    val name:String,
    val specification : String
)

class OutsideStockRecyclerAdapter:ListAdapter<OutsideStockData, OutsideStockViewHolder>(
    OutsideStockDiffUtil
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutsideStockViewHolder {
        return OutsideStockViewHolder(ItemOutsideStockBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: OutsideStockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class OutsideStockViewHolder(private var binding: ItemOutsideStockBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(data: OutsideStockData){
       binding.tvItemName.text = data.name
        binding.tvWeight.text = data.weight
        binding.tvSpecification.text = data.specification
    }
}

object OutsideStockDiffUtil: DiffUtil.ItemCallback<OutsideStockData>(){
    override fun areItemsTheSame(
        oldItem: OutsideStockData,
        newItem: OutsideStockData
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: OutsideStockData,
        newItem: OutsideStockData
    ): Boolean {
        return oldItem == newItem
    }

}
