package com.example.shwemisale.screen.sellModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.databinding.ItemGeneralSellBinding

data class GeneralSellData(
    val id:String,
    val group:String,
    val content:String,
    val quantity:String,
    val goldWeight:String,
    val underCount:String,
    val fee:String,
    val charge:String
)

class GeneralSellRecyclerAdapter:ListAdapter<GeneralSellData,GeneralSellViewHolder>(GeneralSellDiffUtil){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralSellViewHolder {
        return GeneralSellViewHolder(ItemGeneralSellBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: GeneralSellViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class GeneralSellViewHolder(private var binding: ItemGeneralSellBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(data: GeneralSellData){
        binding.tvGroup.text = data.group
        binding.tvCharge.text = data.charge
        binding.tvFee.text = data.fee
        binding.tvUnderCount.text = data.underCount
        binding.tvGoldWeight.text = data.goldWeight
        binding.tvQuantity.text = data.quantity
        binding.tvContent.text=data.content
    }


}

object GeneralSellDiffUtil: DiffUtil.ItemCallback<GeneralSellData>(){
    override fun areItemsTheSame(oldItem: GeneralSellData, newItem: GeneralSellData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GeneralSellData, newItem: GeneralSellData): Boolean{
        return oldItem == newItem
    }

}