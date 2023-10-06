package com.example.shwemisale.screen.oldStockDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.databinding.ItemViewStockBinding


class RebuyItemsRecyclerAdapter(
    private val onTextChange: (id: String, text: String) -> Unit,
    private val increaseQty:(id: String)->Unit,
    private val decreaseQty:(id: String)->Unit
) : ListAdapter<RebuyItemDto, RebuyItemsRecyclerAdapter.RebuyItemsViewHolder>(RebuyItemsDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RebuyItemsViewHolder {
        return RebuyItemsViewHolder(
            ItemViewStockBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RebuyItemsViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
       holder.bind(getItem(position),payloads)
    }

    override fun onBindViewHolder(holder: RebuyItemsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RebuyItemsViewHolder(
        private var binding: ItemViewStockBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var _data :RebuyItemDto?=null
        val data
            get() = _data!!
        init {
            binding.edtStockName.addTextChangedListener {
                onTextChange(data.id, it.toString())
            }
            binding.btnPlus.setOnClickListener {
               increaseQty(data.id)
            }
            binding.btnMinus.setOnClickListener {
                decreaseQty(data.id)
            }

        }
        fun bind(data: RebuyItemDto,payloads: MutableList<Any>){
            _data = data
            when(payloads.firstOrNull()){
                "nameChanged"-> updateName(data.name)
                "qtyChanged"-> updateQty(data.qty)
                else-> bind(data)
            }
        }
        fun updateName(name:String){
            if (name != binding.edtStockName.text.toString()){
                binding.edtStockName.setText(name)
                binding.edtStockName.setSelection(name.length)
            }
        }
        fun updateQty(qty:Int){
            binding.btnMinus.isEnabled = data.isMinusEnable
            binding.tvCount.text = qty.toString()

        }
        fun bind(data: RebuyItemDto) {
            _data = data
            updateQty(data.qty)
           updateName(data.name)
        }

    }

}


object RebuyItemsDiffUtil : DiffUtil.ItemCallback<RebuyItemDto>() {
    override fun getChangePayload(oldItem: RebuyItemDto, newItem: RebuyItemDto): Any? {
        return when{
            oldItem.name!=newItem.name->"nameChanged"
            oldItem.qty!=newItem.qty->"qtyChanged"
            else->super.getChangePayload(oldItem, newItem)
        }
    }

    override fun areItemsTheSame(
        oldItem: RebuyItemDto,
        newItem: RebuyItemDto
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: RebuyItemDto,
        newItem: RebuyItemDto
    ): Boolean {
        return oldItem == newItem
    }

}