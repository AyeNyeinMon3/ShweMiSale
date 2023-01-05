package com.example.shwemisale.sellModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.databinding.ItemAkoukSellBinding

data class AkoukSellData(
    val id:String,
    val type:String,
    val weight:String,
    val reduce:String,
    val fee:String,
    val nan_fee:String,
    val charge:String
)

class AkoukSellRecyclerAdapter:ListAdapter<AkoukSellData,AkoukSellViewHolder>(AkoukSellDiffUtil){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AkoukSellViewHolder {
        return AkoukSellViewHolder(ItemAkoukSellBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: AkoukSellViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class AkoukSellViewHolder(private var binding: ItemAkoukSellBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(data: AkoukSellData){
        binding.tvItemType.text = data.type
        binding.tvCharge.text = data.charge
        binding.tvFee.text = data.fee
        binding.tvSellReduce.text = data.reduce
        binding.tvGoldWeight.text = data.weight
        binding.tvNanHtoeFee.text = data.nan_fee
    }


}

object AkoukSellDiffUtil: DiffUtil.ItemCallback<AkoukSellData>(){
    override fun areItemsTheSame(oldItem: AkoukSellData, newItem: AkoukSellData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AkoukSellData, newItem: AkoukSellData): Boolean{
        return oldItem == newItem
    }

}