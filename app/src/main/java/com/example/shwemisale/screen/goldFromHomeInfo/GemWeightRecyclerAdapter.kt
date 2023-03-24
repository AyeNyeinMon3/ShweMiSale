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
import com.example.shwemisale.data_layers.dto.goldFromHome.GemWeightDetail
import com.example.shwemisale.databinding.ItemGemWeightBinding
import com.example.shwemisale.screen.goldFromHome.GoldFromHomeViewModel
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY




class GemWeightRecyclerAdapter(private val viewModel: GoldFromHomeDetailViewModel,
private val delete:(id:String)->Unit) :
    ListAdapter<GemWeightDetail, GemWeightInResellStockViewHolder>(
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
            ), viewModel,delete
        )
    }

    override fun onBindViewHolder(holder: GemWeightInResellStockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class GemWeightInResellStockViewHolder(
    private var binding: ItemGemWeightBinding,
    private val viewModel: GoldFromHomeDetailViewModel,
    private val delete:(id:String)->Unit
) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(data: GemWeightDetail) {
        binding.edtGemQuantity.setText(data.gem_qty)
        binding.edtOneGemWeightGm.setText(data.gem_weight_gm_per_unit)

        val  oneGemWeightKpy= getKPYFromYwae(data.gem_weight_ywae_per_unit.toDouble())
        binding.edtOneGemWeightK.setText(oneGemWeightKpy[0].toInt().toString())
        binding.edtOneGemWeightP.setText(oneGemWeightKpy[1].toInt().toString())
        binding.edtOneGemWeightY.setText(oneGemWeightKpy[2].let { String.format("%.2f", it) })


        binding.btnCalculate.isVisible = data.totalWeightKpy.isEmpty()
        binding.tvTotalWeightKPY.isVisible = data.totalWeightKpy.isNotEmpty()
        binding.edtOneGemWeightGm.isEnabled = data.totalWeightKpy.isEmpty()
        binding.edtOneGemWeightK.isEnabled = data.totalWeightKpy.isEmpty()
        binding.edtOneGemWeightP.isEnabled = data.totalWeightKpy.isEmpty()
        binding.edtOneGemWeightY.isEnabled = data.totalWeightKpy.isEmpty()
        binding.edtGemQuantity.isEnabled = data.totalWeightKpy.isEmpty()


        binding.btnCalculate.setOnClickListener {
            if (data.gem_qty.isNotEmpty()){
                    val ywaeForOneByGram = getYwaeFromGram(generateNumberFromEditText(binding.edtOneGemWeightGm).toDouble())
                    val ywaeForOne =  if (ywaeForOneByGram != 0.0){
                    ywaeForOneByGram
                    }else{
                    getYwaeFromKPY(
                        generateNumberFromEditText(binding.edtOneGemWeightK).toInt(),
                        generateNumberFromEditText(binding.edtOneGemWeightP).toInt(),
                        generateNumberFromEditText(binding.edtOneGemWeightY).toDouble(),
                    )
                    }
                    if (ywaeForOne != 0.0){
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

                        binding.tvTotalWeightKPY.text = kyat.toInt().toString()+"K     "+pae.toInt().toString()+"P     "+ywae.toString()+"Y"
                        data.totalWeightKpy = totalYwae.toString()
                        binding.btnCalculate.isVisible = false
                        binding.tvTotalWeightKPY.isVisible = true
                    }else{
                        Toast.makeText(binding.root.context,"Please fill weight",Toast.LENGTH_LONG).show()

                    }


            }else{
                Toast.makeText(binding.root.context,"Please fill quantity first",Toast.LENGTH_LONG).show()

            }


        }
        binding.btnDelete.setOnClickListener {
            delete(data.id.toString())
        }

        binding.edtOneGemWeightK.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.gemWeightCustomList[bindingAdapterPosition].weightForOneK = s.toString().ifEmpty { "" }

            }
        })

        binding.edtOneGemWeightP.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.gemWeightCustomList[bindingAdapterPosition].weightForOneP = s.toString().ifEmpty { "" }

            }
        })

        binding.edtOneGemWeightY.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.gemWeightCustomList[bindingAdapterPosition].weightForOneY = s.toString().ifEmpty { "" }

            }
        })

        binding.edtGemQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.gemWeightCustomList[bindingAdapterPosition].gem_qty = s.toString().ifEmpty { "" }

            }
        })
        binding.edtOneGemWeightGm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.gemWeightCustomList[bindingAdapterPosition].gem_weight_gm_per_unit = s.toString().ifEmpty { "" }

            }
        })

    }
}

object GemWeightInResellStockDiffUtil : DiffUtil.ItemCallback<GemWeightDetail>() {
    override fun areItemsTheSame(
        oldItem: GemWeightDetail,
        newItem: GemWeightDetail
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: GemWeightDetail,
        newItem: GemWeightDetail
    ): Boolean {
        return oldItem == newItem
    }

}