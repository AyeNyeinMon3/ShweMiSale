package com.shwemigoldshop.shwemisale.screen.sellModule

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.shwemigoldshop.shwemisale.databinding.ItemGoldFromHomeBinding
import com.shwemigoldshop.shwemisale.screen.goldFromHome.getKPYFromYwae

data class GoldFromHomeData(
    val id: String,
    val resellItem: String,
    val payMoney: String,
    val goldWeight: String,
    val pledgeMoney: String
)

class GoldFromHomeRecyclerAdapter(
    private val type:String?,
    private val editClick: (item: StockFromHomeDomain) -> Unit,
    private val deleteClick: (item: StockFromHomeDomain) -> Unit,
    private val checkBox:(id:String,isChecked:Boolean)->Unit
) : ListAdapter<StockFromHomeDomain, GoldFromHomeViewHolder>(
    GoldFromHomeDiffUtil
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoldFromHomeViewHolder {
        return GoldFromHomeViewHolder(
            ItemGoldFromHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), type,editClick, deleteClick, checkBox
        )
    }

    override fun onBindViewHolder(holder: GoldFromHomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class GoldFromHomeViewHolder(
    private var binding: ItemGoldFromHomeBinding,
    private val type:String?,
    private val editClick: (item: StockFromHomeDomain) -> Unit,
    private val deleteClick: (item: StockFromHomeDomain) -> Unit,
    private val checkBox:(id:String,isChecked:Boolean)->Unit
) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(data: StockFromHomeDomain) {
        val goldKpy = getKPYFromYwae(data.f_voucher_shown_gold_weight_ywae!!.toDouble())
        binding.tvGoldWeight.text = goldKpy[0].toInt().toString() +"K  "+ goldKpy[1].toInt()
            .toString() +"P  "+ goldKpy[2].let { String.format("%.2f", it) }+"Y"
        binding.tvVoucherPurchasePayment.text = data.b_voucher_buying_value
        binding.tvPawnPrice.text = data.calculated_for_pawn
        binding.tvResellItem.text = data.stock_name

        binding.btnEdit.setOnClickListener {
            editClick(data)
        }
        binding.btnDelete.setOnClickListener {
            deleteClick(data)
        }
        binding.cbForPawn.isChecked= data.isChecked
       if (type.orEmpty().startsWith("Pawn")){
           if (type == "PawnSelect"){
               binding.btnEdit.isVisible = true
               binding.btnDelete.isVisible = false
               binding.cbForPawn.isVisible = true
               binding.cbForPawn.setOnCheckedChangeListener { compoundButton, isChecked ->
                   checkBox(data.id.toString(),isChecked)
               }
           }else if(type == "PawnSelectNoEdit"){
               binding.cbForPawn.isVisible = true
               binding.btnDelete.isVisible = false
               binding.btnEdit.isVisible = false
               binding.cbForPawn.setOnCheckedChangeListener { compoundButton, isChecked ->
                   checkBox(data.id.toString(),isChecked)
               }
           }else {
               binding.btnEdit.isVisible = type == "PawnNewCanEdit" && data.isEditable
               binding.btnDelete.isVisible = type == "PawnNewCanEdit" && data.isEditable
           }
       }
    }
}

object GoldFromHomeDiffUtil : DiffUtil.ItemCallback<StockFromHomeDomain>() {
    override fun areItemsTheSame(
        oldItem: StockFromHomeDomain,
        newItem: StockFromHomeDomain
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: StockFromHomeDomain,
        newItem: StockFromHomeDomain
    ): Boolean {
        return oldItem == newItem
    }
}