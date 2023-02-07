package com.example.shwemisale.sellModule.goldFromHomeInfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.CustomerFavItemsData
import com.example.shwemisale.databinding.ItemCustomerFavItemsBinding
import com.example.shwemisale.databinding.ItemGemWeightBinding


data class GemWeightInResellStock(
    val id : String,
    val gemCount: String,
    val weightForOneGm : String,
    val weightForOneKPY : String,
    val totalWeightKPY : String
)

class GemWeightRecyclerAdapter:
    ListAdapter<GemWeightInResellStock, GemWeightInResellStockViewHolder>(GemWeightInResellStockDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GemWeightInResellStockViewHolder {
        return GemWeightInResellStockViewHolder(ItemGemWeightBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: GemWeightInResellStockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class GemWeightInResellStockViewHolder(private var binding: ItemGemWeightBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(data: GemWeightInResellStock){
        binding.edtGemQuantity.setText(data.gemCount)
        binding.edtOneGemWeightGm.setText(data.weightForOneGm)
        binding.edtOneGemWeightK.setText(data.weightForOneKPY)
        binding.edtOneGemWeightP.setText(data.weightForOneKPY)
        binding.edtOneGemWeightY.setText(data.weightForOneKPY)
        binding.tvTotalWeightK.setText(data.totalWeightKPY)
        binding.tvTotalWeightP.setText(data.totalWeightKPY)
        binding.tvTotalWeightY.setText(data.totalWeightKPY)
    }
}

object GemWeightInResellStockDiffUtil : DiffUtil.ItemCallback<GemWeightInResellStock>(){
    override fun areItemsTheSame(
        oldItem: GemWeightInResellStock,
        newItem: GemWeightInResellStock
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: GemWeightInResellStock,
        newItem: GemWeightInResellStock
    ): Boolean {
        return oldItem == newItem
    }

}