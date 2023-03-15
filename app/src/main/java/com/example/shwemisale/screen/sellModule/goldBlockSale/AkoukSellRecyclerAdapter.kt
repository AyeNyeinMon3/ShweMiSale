package com.example.shwemisale.screen.sellModule.goldBlockSale

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.data_layers.domain.pureGoldSale.PureGoldListDomain
import com.example.shwemisale.databinding.ItemAkoukSellBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae

data class AkoukSellData(
    val id:String,
    val type:String,
    val weight:String,
    val reduce:String,
    val fee:String,
    val nan_fee:String,
    val charge:String
)

class AkoukSellRecyclerAdapter(private val goldPrice: String):ListAdapter<PureGoldListDomain, AkoukSellViewHolder>(AkoukSellDiffUtil){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AkoukSellViewHolder {
        return AkoukSellViewHolder(ItemAkoukSellBinding.inflate(LayoutInflater.from(parent.context),parent,false),goldPrice)
    }

    override fun onBindViewHolder(holder: AkoukSellViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class AkoukSellViewHolder(private var binding: ItemAkoukSellBinding,
private val goldPrice:String): RecyclerView.ViewHolder(binding.root){
    fun bind(data: PureGoldListDomain){
        binding.tvItemType.text = data.type
        val goldWeightKpy = getKPYFromYwae(data.gold_weight_ywae!!.toDouble())
        val goldWeight = goldWeightKpy[0].toInt().toString() + "K "+
                goldWeightKpy[1].toInt().toString()+ "P "+
                goldWeightKpy[2].let { String.format("%.2f", it) }+ "Y "
        binding.tvGoldWeight.text = goldWeight

        val wastageKpy = getKPYFromYwae(data.wastage_ywae!!.toDouble())
        val wastageWeight = wastageKpy[0].toInt().toString() + "K "+
                wastageKpy[1].toInt().toString()+ "P "+
                wastageKpy[2].let { String.format("%.2f", it) }+ "Y "
        binding.tvSellReduce.text = wastageWeight
        binding.tvFee.text = data.maintenance_cost.orEmpty()
        binding.tvNanHtoeFee.text = data.threading_fees.orEmpty()
        binding.tvCharge.text =(( data.gold_weight_ywae.toDouble()/128) * goldPrice.toInt()).toInt().toString()


    }


}

object AkoukSellDiffUtil: DiffUtil.ItemCallback<PureGoldListDomain>(){
    override fun areItemsTheSame(oldItem: PureGoldListDomain, newItem: PureGoldListDomain): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PureGoldListDomain, newItem: PureGoldListDomain): Boolean{
        return oldItem == newItem
    }

}