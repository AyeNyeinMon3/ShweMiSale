package com.shwemigoldshop.shwemisale.screen.sellModule.sample

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shwemigoldshop.shwemisale.util.loadImageWithGlide
import com.shwemigoldshop.shwemisale.data_layers.domain.sample.SampleDomain
import com.shwemigoldshop.shwemisale.databinding.ItemInventoryStockBinding

data class InventoryStockData(
    val id: String,
    val code: String
)

class InventoryStockRecyclerAdapter(private val removeClick: (item:SampleDomain) -> Unit) :
    ListAdapter<SampleDomain, InventoryStockViewHolder>(
        InventoryStockDiffUtil
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryStockViewHolder {
        return InventoryStockViewHolder(
            ItemInventoryStockBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),removeClick
        )
    }

    override fun onBindViewHolder(holder: InventoryStockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class InventoryStockViewHolder(
    private var binding: ItemInventoryStockBinding,
    private val removeClick: (item:SampleDomain) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: SampleDomain) {
        binding.tvStockCode.text = data.product_code
        binding.tvSpecification.text = data.specification
        binding.ivItem.loadImageWithGlide(data.thumbnail)
        binding.ivCancel.setOnClickListener {
            removeClick(data)
        }
        binding.tvSpecification.isVisible = !data.specification.isNullOrEmpty()
        binding.editSpecification.isVisible = data.specification.isNullOrEmpty()

        binding.editSpecification.setText(data.specification)

        binding.editSpecification.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                data.specification = s.toString()
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
    }
}

object InventoryStockDiffUtil : DiffUtil.ItemCallback<SampleDomain>() {
    override fun areItemsTheSame(
        oldItem: SampleDomain,
        newItem: SampleDomain
    ): Boolean {
        return oldItem.product_id == newItem.product_id
    }

    override fun areContentsTheSame(
        oldItem: SampleDomain,
        newItem: SampleDomain
    ): Boolean {
        return oldItem == newItem
    }

}