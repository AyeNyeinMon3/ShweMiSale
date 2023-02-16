package com.example.shwemisale.screen.sellModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.databinding.ItemResellStockInfoBinding

data class ResellStockDataUiModel(
    val id : String,
    val item:String,
    val number: String

)

class ResellStockRecyclerAdapter() : ListAdapter<RebuyItemDto, ResellStockViewHolder>(
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
    fun bind(data: RebuyItemDto){
       binding.tvItem.text = data.name


    }


}

object ResellStockDiffUtil: DiffUtil.ItemCallback<RebuyItemDto>(){
    override fun areItemsTheSame(oldItem: RebuyItemDto, newItem: RebuyItemDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RebuyItemDto, newItem: RebuyItemDto): Boolean{
        return oldItem == newItem
    }

}