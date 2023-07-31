package com.example.shwemisale.screen.goldFromHomeInfo

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemi.util.generateNumberFromEditText
import com.example.shwemisale.data_layers.domain.product.GemWeightDetailDomain
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.databinding.ItemGemWeightBinding
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY


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

        binding.edtGemQuantity.setText(data.gem_qty)
        binding.edtOneGemWeightGm.setText(data.gem_weight_gm_per_unit)

        val oneGemWeightKpy =
            getKPYFromYwae(data.gem_weight_ywae_per_unit.let { if (it.isEmpty()) 0.0 else it.toDouble() })
        binding.edtOneGemWeightK.setText(oneGemWeightKpy[0].toInt().toString())
        binding.edtOneGemWeightP.setText(oneGemWeightKpy[1].toInt().toString())
        binding.edtOneGemWeightY.setText(oneGemWeightKpy[2].let { String.format("%.2f", it) })


        binding.btnCalculate.isVisible = data.gem_qty == "0"
        binding.tvTotalWeightKPY.isVisible = data.gem_qty != "0.0"
        if (data.gem_weight_ywae_per_unit.isNotEmpty()) {
            val totalYwae = data.gem_qty.toInt() * data.gem_weight_ywae_per_unit.toDouble()
            val totalKpy = getKPYFromYwae(totalYwae.toDouble())
            val kyat = totalKpy[0]
            val pae = totalKpy[1]
            val ywae = String.format("%.2f", totalKpy[2])
            binding.tvTotalWeightKPY.text = kyat.toInt().toString() + "K     " + pae.toInt()
                .toString() + "P     " + ywae.toString() + "Y"
        }
        binding.edtOneGemWeightGm.isEnabled = data.gem_qty=="0"
        binding.edtOneGemWeightK.isEnabled = data.gem_qty=="0"
        binding.edtOneGemWeightP.isEnabled = data.gem_qty=="0"
        binding.edtOneGemWeightY.isEnabled = data.gem_qty=="0"
        binding.edtGemQuantity.isEnabled = data.gem_qty=="0"


        binding.btnCalculate.setOnClickListener {
            val ywaeForOneByGram =
                getYwaeFromGram(generateNumberFromEditText(binding.edtOneGemWeightGm).toDouble())
            val ywaeForOne = if (ywaeForOneByGram != 0.0) {
                ywaeForOneByGram
            } else {
                getYwaeFromKPY(
                    generateNumberFromEditText(binding.edtOneGemWeightK).toInt(),
                    generateNumberFromEditText(binding.edtOneGemWeightP).toInt(),
                    generateNumberFromEditText(binding.edtOneGemWeightY).toDouble(),
                )
            }
            data.gem_weight_ywae_per_unit = ywaeForOne.toString()

            val totalYwae = binding.edtGemQuantity.text.toString().toInt() * ywaeForOne
            val totalKpy = getKPYFromYwae(totalYwae)
            val kyat = totalKpy[0]
            val pae = totalKpy[1]
            val ywae = String.format("%.2f", totalKpy[2])

            binding.edtOneGemWeightGm.isEnabled = false
            binding.edtOneGemWeightK.isEnabled = false
            binding.edtOneGemWeightP.isEnabled = false
            binding.edtOneGemWeightY.isEnabled = false
            binding.edtGemQuantity.isEnabled = false

            binding.tvTotalWeightKPY.text = kyat.toInt().toString() + "K     " + pae.toInt()
                .toString() + "P     " + ywae.toString() + "Y"
//            binding.btnCalculate.isVisible = false
            binding.tvTotalWeightKPY.isVisible = true

            update(
                data.id,
                binding.edtGemQuantity.text.toString(),
                binding.edtOneGemWeightGm.text.toString(),
                ywaeForOne.toString()
            )
        }
        binding.btnDelete.setOnClickListener {
            delete(data.id)
        }


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