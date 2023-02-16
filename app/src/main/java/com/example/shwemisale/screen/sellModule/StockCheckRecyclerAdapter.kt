package com.example.shwemisale.screen.sellModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockWeightByVoucherUiModel
import com.example.shwemisale.databinding.ItemStockCheckBinding

data class StockCheckData(
    val id : String,
    val stockCode : String,
    val weight : String
)

class StockCheckRecyclerAdapter:ListAdapter<StockWeightByVoucherUiModel, StockCheckViewHolder>(StockCheckDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockCheckViewHolder {

        return StockCheckViewHolder(ItemStockCheckBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: StockCheckViewHolder, position: Int) {

        holder.bind(getItem(position))
    }

}

class StockCheckViewHolder(private var binding:ItemStockCheckBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(data: StockWeightByVoucherUiModel){
        binding.tvStockCode.text = data.code
        binding.tvGoldAndGemWeightGm.text = data.gold_and_gem_weight_gm
        binding.checkBox.setOnCheckedChangeListener { compoundButton, isChecked ->
            data.isChecked = isChecked
        }
    }
}

object StockCheckDiffUtil:DiffUtil.ItemCallback<StockWeightByVoucherUiModel>(){
    override fun areItemsTheSame(oldItem: StockWeightByVoucherUiModel, newItem: StockWeightByVoucherUiModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StockWeightByVoucherUiModel, newItem: StockWeightByVoucherUiModel): Boolean {
      return  oldItem == newItem
    }

}