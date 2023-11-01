package com.shwemigoldshop.shwemisale.screen.goldFromHomeInfo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shwemigoldshop.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.shwemigoldshop.shwemisale.databinding.ItemGemWeightBinding


class GemWeightRecyclerAdapter(
    private val update: (id:String,qty: String, oneGemGm: String, oneGemYwae: String) -> Unit,
    private val delete: (id:String) -> Unit
) :
    ListAdapter<GemWeightDetailDomain, GemWeightInResellStockViewHolder>(
        GemWeightInResellStockDiffUtil
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GemWeightInResellStockViewHolder {
        return GemWeightInResellStockViewHolder(
            ItemGemWeightBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            ), delete, update
        )
    }

    override fun onBindViewHolder(holder: GemWeightInResellStockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class GemWeightInResellStockViewHolder(
    private var binding: ItemGemWeightBinding,
    private val delete: (id:String) -> Unit,
    private val update: (id:String,qty: String, oneGemGm: String, oneGemYwae: String) -> Unit,

    ) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(data: GemWeightDetailDomain) {

    }
}

object GemWeightInResellStockDiffUtil : DiffUtil.ItemCallback<GemWeightDetailDomain>() {
    override fun areItemsTheSame(
        oldItem: GemWeightDetailDomain,
        newItem: GemWeightDetailDomain
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: GemWeightDetailDomain,
        newItem: GemWeightDetailDomain
    ): Boolean {
        return oldItem == newItem
    }

}