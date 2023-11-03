package com.shwemigoldshop.shwemisale.screen.sellModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shwemigoldshop.shwemisale.data_layers.ui_models.goldFromHome.StockWeightByVoucherUiModel
import com.shwemigoldshop.shwemisale.databinding.ItemStockCheckBinding

data class StockCheckData(
    val id : String,
    val stockCode : String,
    val weight : String
)

class StockCheckRecyclerAdapter(private val addStock:(data:StockWeightByVoucherUiModel)->Unit):ListAdapter<StockWeightByVoucherUiModel, StockCheckRecyclerAdapter.StockCheckViewHolder>(StockCheckDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockCheckViewHolder {

        return StockCheckViewHolder(ItemStockCheckBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: StockCheckViewHolder, position: Int) {

        holder.bind(getItem(position))
    }

    inner class StockCheckViewHolder(private var binding:ItemStockCheckBinding):RecyclerView.ViewHolder(binding.root){
        private var _data:StockWeightByVoucherUiModel? = null
        val data:StockWeightByVoucherUiModel
            get() = _data!!

        init {
            binding.btnAdd.setOnClickListener {
                addStock(data)
            }
        }
        fun bind(data: StockWeightByVoucherUiModel){
            _data = data
            binding.tvStockCode.text = data.code
            binding.tvGoldAndGemWeightGm.text = data.gold_and_gem_weight_gm
//            binding.cbDate.setOnCheckedChangeListener { compoundButton, isChecked ->
//                data.isChecked = isChecked
//            }

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