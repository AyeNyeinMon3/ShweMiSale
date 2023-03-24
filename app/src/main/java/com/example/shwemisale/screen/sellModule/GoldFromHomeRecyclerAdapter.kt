package com.example.shwemisale.screen.sellModule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.databinding.ItemGoldFromHomeBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae

data class GoldFromHomeData(
    val id: String,
    val resellItem: String,
    val payMoney: String,
    val goldWeight: String,
    val pledgeMoney: String
)

class GoldFromHomeRecyclerAdapter(
    private val editClick: (item: StockFromHomeDomain) -> Unit,
    private val deleteClick: (item: StockFromHomeDomain) -> Unit
) : ListAdapter<StockFromHomeDomain, GoldFromHomeViewHolder>(
    GoldFromHomeDiffUtil
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoldFromHomeViewHolder {
        return GoldFromHomeViewHolder(
            ItemGoldFromHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), editClick, deleteClick
        )
    }

    override fun onBindViewHolder(holder: GoldFromHomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class GoldFromHomeViewHolder(
    private var binding: ItemGoldFromHomeBinding,
    private val editClick: (item: StockFromHomeDomain) -> Unit,
    private val deleteClick: (item: StockFromHomeDomain) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(data: StockFromHomeDomain) {
        val goldKpy = getKPYFromYwae(data.gold_weight_ywae!!.toDouble())
        binding.tvGoldWeight.text = goldKpy[0].toInt().toString() +"K"+ goldKpy[1].toInt()
            .toString() + goldKpy[2].let { String.format("%.2f", it) }
        binding.tvVoucherPurchasePayment.text = data.b_voucher_buying_value
        binding.tvPawnPrice.text = data.calculated_for_pawn
        binding.tvResellItem.text = data.stock_name

        binding.ivEdit.setOnClickListener {
            editClick(data)
        }
        binding.ivDelete.setOnClickListener {
            deleteClick(data)
        }
    }
}

object GoldFromHomeDiffUtil : DiffUtil.ItemCallback<StockFromHomeDomain>() {
    override fun areItemsTheSame(
        oldItem: StockFromHomeDomain,
        newItem: StockFromHomeDomain
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: StockFromHomeDomain,
        newItem: StockFromHomeDomain
    ): Boolean {
        return oldItem == newItem
    }


}