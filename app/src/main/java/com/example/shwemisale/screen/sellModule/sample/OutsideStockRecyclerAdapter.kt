package com.example.shwemisale.screen.sellModule.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemi.util.loadImageWithGlide
import com.example.shwemisale.data_layers.domain.sample.SampleDomain
import com.example.shwemisale.databinding.ItemOutsideStockBinding

data class OutsideStockData(
    val id: String,
    val weight: String,
    val name: String,
    val specification: String
)

class OutsideStockRecyclerAdapter( private val removeClick: (item: SampleDomain) -> Unit) : ListAdapter<SampleDomain, OutsideStockViewHolder>(
    OutsideStockDiffUtil
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OutsideStockViewHolder {
        return OutsideStockViewHolder(
            ItemOutsideStockBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),removeClick
        )
    }

    override fun onBindViewHolder(holder: OutsideStockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class OutsideStockViewHolder(
    private var binding: ItemOutsideStockBinding,
    private val removeClick: (item: SampleDomain) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: SampleDomain) {
        binding.tvItemName.text = data.name
        binding.tvSpecification.text = data.specification
        binding.ivItem.loadImageWithGlide(data.thumbnail)
        binding.ivCancel.setOnClickListener {
            removeClick(data)
        }
        binding.tvSpecification.text = data.specification

    }
}

object OutsideStockDiffUtil : DiffUtil.ItemCallback<SampleDomain>() {
    override fun areItemsTheSame(
        oldItem: SampleDomain,
        newItem: SampleDomain
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: SampleDomain,
        newItem: SampleDomain
    ): Boolean {
        return oldItem == newItem
    }

}
