package com.example.shwemisale.sellModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.databinding.ItemResellStockInfoBinding

data class ResellStockData(
    val id : String,
    val item:String,
    val number: String

)

class ResellStockRecyclerAdapter() : ListAdapter<ResellStockData, ResellStockViewHolder>(
    ResellStockDiffUtil
){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResellStockViewHolder {
        return ResellStockViewHolder(ItemResellStockInfoBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ResellStockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class ResellStockViewHolder(private var binding: ItemResellStockInfoBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(data: ResellStockData){
       binding.tvItem.text = data.item
        binding.tvNumber.text = data.number

    }


}

object ResellStockDiffUtil: DiffUtil.ItemCallback<ResellStockData>(){
    override fun areItemsTheSame(oldItem: ResellStockData, newItem: ResellStockData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ResellStockData, newItem: ResellStockData): Boolean{
        return oldItem == newItem
    }

}