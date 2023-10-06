package com.example.shwemisale.screen.oldStockDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shwemi.util.hideKeyboard
import com.example.shwemi.util.showKeyBoard
import com.example.shwemisale.data_layers.domain.goldFromHome.RebuyItemDto
import com.example.shwemisale.databinding.ItemViewStockBinding
import com.example.shwemisale.databinding.ItemViewStockEditBinding


class RebuyItemsRecyclerAdapter(
    private val onTextChange: (id: String, text: String,size:String) -> Unit,
    private val increaseQty:(id: String,size:String)->Unit,
    private val decreaseQty:(id: String,size:String)->Unit,
    private val changeViewType:(id: String,size:String,isEditing:Boolean)->Unit,
) : ListAdapter<RebuyItemDto, ViewHolder>(RebuyItemsDiffUtil) {
    val viewStateType = 1
    val editStateType = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1){ RebuyItemsViewHolder(
            ItemViewStockBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )}else{
            RebuyItemsEditViewHolder(
                ItemViewStockEditBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isEditing) editStateType else viewStateType
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder){
            is RebuyItemsViewHolder-> holder.bind(getItem(position))
            is RebuyItemsEditViewHolder->holder.bind(getItem(position))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        when(holder){
            is RebuyItemsViewHolder-> holder.bind(getItem(position),payloads)
            is RebuyItemsEditViewHolder->holder.bind(getItem(position),payloads)
        }
    }

    inner class RebuyItemsEditViewHolder(
        private val binding: ItemViewStockEditBinding
    ):RecyclerView.ViewHolder(binding.root){
        private var _data :RebuyItemDto?=null
        val data
            get() = _data!!
        init {
            binding.edtStockName.requestFocus()
            binding.btnApprove.setOnClickListener {
                onTextChange(data.id,binding.edtStockName.text.toString(),data.size)
                changeViewType(data.id,data.size,false)
            }
            binding.btnCancel.setOnClickListener {
                changeViewType(data.id,data.size,false)
            }
        }
        fun bind(data: RebuyItemDto,payloads: MutableList<Any>){
            _data = data
            when(payloads.firstOrNull()){
                "nameChanged"-> updateName(data.name)
                else-> bind(data)
            }
        }
        fun bind(data:RebuyItemDto){
            _data = data
            updateName(data.name)
        }
        fun updateName(name:String){
            if (name != binding.edtStockName.text.toString()){
                binding.edtStockName.setText(name)
                binding.edtStockName.setSelection(name.length)
            }
        }
    }
    inner class RebuyItemsViewHolder(
        private val binding: ItemViewStockBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var _data :RebuyItemDto?=null
        val data
            get() = _data!!
        init {
            binding.btnPlus.setOnClickListener {
               increaseQty(data.id,data.size)
            }
            binding.btnMinus.setOnClickListener {
                decreaseQty(data.id,data.size)
            }
            binding.ivEdit.setOnClickListener {
                changeViewType(data.id,data.size,true)
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

        fun updateQty(qty:Int){
            binding.btnMinus.isEnabled = data.isMinusEnable
            binding.tvCount.text = qty.toString()

        }
        fun updateName(name:String){
            if (name != binding.tvStock.text.toString()){
                binding.tvStock.text = name
            }
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