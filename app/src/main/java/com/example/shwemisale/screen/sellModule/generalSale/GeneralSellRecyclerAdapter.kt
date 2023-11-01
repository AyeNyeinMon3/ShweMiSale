package com.example.shwemisale.screen.sellModule.generalSale

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.util.getRoundDownForPrice
import com.example.shwemisale.data_layers.domain.generalSale.GeneralSaleListDomain
import com.example.shwemisale.data_layers.dto.GeneralSaleDto
import com.example.shwemisale.databinding.ItemGeneralSellBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae

data class GeneralSellData(
    val id: String,
    val group: String,
    val content: String,
    val quantity: String,
    val goldWeight: String,
    val underCount: String,
    val fee: String,
    val charge: String
)

class GeneralSellRecyclerAdapter(
    private val goldPrice: String,
    private val generalSaleItemListForMap:List<GeneralSaleDto>,
    private val editClick: (item: GeneralSaleListDomain) -> Unit,
    private val deleteClick: (item: GeneralSaleListDomain) -> Unit
) : ListAdapter<GeneralSaleListDomain, GeneralSellViewHolder>(
    GeneralSellDiffUtil
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneralSellViewHolder {
        return GeneralSellViewHolder(
            ItemGeneralSellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),goldPrice, generalSaleItemListForMap,editClick,deleteClick
        )
    }

    override fun onBindViewHolder(holder: GeneralSellViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class GeneralSellViewHolder(
    private var binding: ItemGeneralSellBinding,
    private val goldPrice: String,
    private val generalSaleItemListForMap:List<GeneralSaleDto>,
    private val editClick: (item: GeneralSaleListDomain) -> Unit,
    private val deleteClick: (item: GeneralSaleListDomain) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: GeneralSaleListDomain) {
//        binding.tvCharge.text = data.charge
        binding.tvFee.text = data.maintenance_cost
        binding.tvGoldWeight.text = data.gold_weight_gm
        binding.tvQuantity.text = data.qty
        val name = generalSaleItemListForMap.find { it.id == data.general_sale_item_id }?.name
        binding.tvContent.text = name

        val wastageKpy = getKPYFromYwae(data.wastage_ywae.toDouble())
        val wastageWeight = wastageKpy[0].toInt().toString() + "K " +
                wastageKpy[1].toInt().toString() + "P " +
                wastageKpy[2].let { String.format("%.2f", it) } + "Y "
        binding.tvWastageYwae.text = wastageWeight
        val cost =(((((data.gold_weight_gm.toDouble()/16.6)+(data.wastage_ywae.toDouble()) / 128)) * goldPrice.toInt())+
               data.maintenance_cost.let {
            if (it.isEmpty()) 0 else it.toInt()
        })
        binding.tvCharge.text = getRoundDownForPrice(cost.toInt()).toString()
//        binding.tvContent.text = data.name
        binding.ivDelete.setOnClickListener {
            deleteClick(data)
        }
        binding.ivDeleteBg.setOnClickListener {
            deleteClick(data)

        }
        binding.ivEdit.setOnClickListener {
            editClick(data)
        }
        binding.ivEditBg.setOnClickListener {
            editClick(data)

        }
    }


}

object GeneralSellDiffUtil : DiffUtil.ItemCallback<GeneralSaleListDomain>() {
    override fun areItemsTheSame(oldItem: GeneralSaleListDomain, newItem: GeneralSaleListDomain): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GeneralSaleListDomain, newItem: GeneralSaleListDomain): Boolean {
        return oldItem == newItem
    }

}