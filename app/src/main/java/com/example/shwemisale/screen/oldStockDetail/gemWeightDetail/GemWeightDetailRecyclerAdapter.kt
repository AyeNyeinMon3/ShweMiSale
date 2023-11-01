package com.example.shwemisale.screen.oldStockDetail.gemWeightDetail

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.R
import com.example.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.example.shwemisale.databinding.ItemViewGemWeightBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram


class GemWeightDetailRecyclerAdapter(
    private val update: (id: String, qty: Int, weightGmPerUnit: Double, weightYwaePerUnit: Double) -> Unit,
    private val delete: (id: String) -> Unit,
    private val onGemQtyChanged: (id: String, changedqty: Int) -> Unit,
    private val onGemWeightGmChanged: (id: String, changedGemWeightGm: Double) -> Unit,
    private val onGemWeightKpyChanged: (id: String) -> Unit,
    private val onCalculateTotalGemWeight: (id: String, qty: Int, gemWeightYwaePerUnit: Double) -> Unit,
) :
    ListAdapter<GemWeightDetailDomain, GemWeightDetailRecyclerAdapter.GemWeightDetailViewHolder>(
        GemWeightDetailDiffUtil
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GemWeightDetailViewHolder {
        return GemWeightDetailViewHolder(
            ItemViewGemWeightBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(
        holder: GemWeightDetailViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        holder.bind(getItem(position), payloads)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: GemWeightDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GemWeightDetailViewHolder(
        private var binding: ItemViewGemWeightBinding,

        ) : RecyclerView.ViewHolder(binding.root) {
        private var _data: GemWeightDetailDomain? = null
        val data
            get() = _data!!

        init {
            binding.edtQuantity.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    onGemQtyChanged(data.id, binding.edtQuantity.text.toString().toInt())
                }
            }
            binding.edtWeightGm.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    onGemWeightGmChanged(data.id, binding.edtWeightGm.text.toString().toDouble())
                }
            }
            binding.edtWeightKPY.setOnClickListener {
                onGemWeightKpyChanged(data.id)
            }
            binding.btnCalculateTotalGemWeight.setOnClickListener {
                if (data.isCalculatable){
                    val ywae = if (data.gem_weight_ywae_per_unit != 0.0) data.gem_weight_ywae_per_unit else getYwaeFromGram(data.gem_weight_gm_per_unit)
                    onCalculateTotalGemWeight(data.id,data.gem_qty,ywae)
                    update(data.id ,data.gem_qty, data.gem_weight_gm_per_unit,data.gem_weight_ywae_per_unit)
                }
            }
            binding.icDelete.setOnClickListener {
                delete(data.id)
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(data: GemWeightDetailDomain, payloads: MutableList<Any>) {
            _data = data
            when (payloads.firstOrNull()) {
                "gemQtyChanged" -> updateGemQty(data.gem_qty)
                "gemWeightKpyChanged" -> updateGemWeightKpy(data.gem_weight_ywae_per_unit)
                "gemWeightGmChanged" -> updateGemWeightGm(data.gem_weight_gm_per_unit)
                "totalWeightYwaeChanged" -> updateTotalGemWeight(data.totalWeightYwae)
                else ->bind(data)
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(data: GemWeightDetailDomain) {
            _data = data
            updateGemQty(data.gem_qty)
            updateGemWeightKpy(data.gem_weight_ywae_per_unit)
            updateGemWeightGm(data.gem_weight_gm_per_unit)
            updateTotalGemWeight(data.totalWeightYwae)
        }

        fun updateGemWeightKpy(ywae:Double){
            val kpy = if (ywae!=data.gem_weight_ywae_per_unit) getKPYFromYwae(ywae) else getKPYFromYwae(data.gem_weight_ywae_per_unit)
            binding.edtWeightKPY.setText(binding.root.context.getString(R.string.kpy_value,kpy[0].toInt().toString(),kpy[1].toInt().toString(),kpy[2].toString()))

        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun updateGemQty(qty: Int) {
            if (data.isCalculatable){
                binding.btnCalculateTotalGemWeight.setTextColor(binding.root.context.getColorStateList(R.color.primary))
            }else{
                binding.btnCalculateTotalGemWeight.setTextColor(binding.root.context.getColorStateList(R.color.grey_placeholder))
            }
            if (qty != data.gem_qty) binding.edtQuantity.setText(qty.toString()) else binding.edtQuantity.setText(data.gem_qty.toString())
        }
        fun updateGemWeightGm(weightGm:Double){
            if (weightGm != data.gem_weight_gm_per_unit){
                binding.edtWeightGm.setText(weightGm.toString())
            }else{
                binding.edtWeightGm.setText(data.gem_weight_gm_per_unit.toString())

            }
        }
        fun updateTotalGemWeight(totalWeightYwae:Double){
            val kpy = if (totalWeightYwae!=data.totalWeightYwae) getKPYFromYwae(totalWeightYwae) else getKPYFromYwae(data.totalWeightYwae)
            binding.tvTotalWeightKPY.text = binding.root.context.getString(R.string.kpy_value,kpy[0].toInt().toString(),kpy[1].toInt().toString(),kpy[2].toString())

        }
    }
}


object GemWeightDetailDiffUtil : DiffUtil.ItemCallback<GemWeightDetailDomain>() {
    override fun getChangePayload(
        oldItem: GemWeightDetailDomain,
        newItem: GemWeightDetailDomain
    ): Any? {
        return when {
            oldItem.gem_qty != newItem.gem_qty -> "gemQtyChanged"
            oldItem.gem_weight_ywae_per_unit != newItem.gem_weight_ywae_per_unit -> "gemWeightKpyChanged"
            oldItem.gem_weight_gm_per_unit != newItem.gem_weight_gm_per_unit -> "gemWeightGmChanged"
            oldItem.totalWeightYwae != newItem.totalWeightYwae -> "totalWeightYwaeChanged"
            else -> super.getChangePayload(oldItem, newItem)
        }
    }

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