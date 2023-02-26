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
import com.example.shwemisale.databinding.ItemGemWeightBinding
import com.example.shwemisale.screen.goldFromHome.GoldFromHomeViewModel
import com.example.shwemisale.screen.goldFromHome.getKPYFromYwae
import com.example.shwemisale.screen.goldFromHome.getYwaeFromGram
import com.example.shwemisale.screen.goldFromHome.getYwaeFromKPY


data class GemWeightInStockFromHome(
    val id: String,
    var gemCount: String,
    var weightForOneGm: String,
    var weightForOneK: String,
    var weightForOneP: String,
    var weightForOneY: String,
    var totalWeightKPY: String
)

class GemWeightRecyclerAdapter(private val viewModel: GoldFromHomeDetailViewModel,
private val delete:(id:String)->Unit) :
    ListAdapter<GemWeightInStockFromHome, GemWeightInResellStockViewHolder>(
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
    fun bind(data: GemWeightInStockFromHome) {
        binding.edtGemQuantity.setText(data.gemCount)
        binding.edtOneGemWeightGm.setText(data.weightForOneGm)
        binding.edtOneGemWeightK.setText(data.weightForOneK)
        binding.edtOneGemWeightP.setText(data.weightForOneP)
        binding.edtOneGemWeightY.setText(data.weightForOneY)
        binding.btnCalculate.isVisible = data.totalWeightKPY.isEmpty()
        binding.tvTotalWeightKPY.isVisible = data.totalWeightKPY.isNotEmpty()
        binding.edtOneGemWeightGm.isEnabled = data.totalWeightKPY.isEmpty()
        binding.edtOneGemWeightK.isEnabled = data.totalWeightKPY.isEmpty()
        binding.edtOneGemWeightP.isEnabled = data.totalWeightKPY.isEmpty()
        binding.edtOneGemWeightY.isEnabled = data.totalWeightKPY.isEmpty()
        binding.edtGemQuantity.isEnabled = data.totalWeightKPY.isEmpty()


        binding.btnCalculate.setOnClickListener {
            if (data.gemCount.isNotEmpty()){
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
                        data.totalWeightKPY = totalYwae.toString()
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
            delete(data.id)
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
                viewModel.gemWeightCustomList[bindingAdapterPosition].gemCount = s.toString().ifEmpty { "" }

            }
        })
        binding.edtOneGemWeightGm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.gemWeightCustomList[bindingAdapterPosition].weightForOneGm = s.toString().ifEmpty { "" }

            }
        })

    }
}

object GemWeightInResellStockDiffUtil : DiffUtil.ItemCallback<GemWeightInStockFromHome>() {
    override fun areItemsTheSame(
        oldItem: GemWeightInStockFromHome,
        newItem: GemWeightInStockFromHome
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: GemWeightInStockFromHome,
        newItem: GemWeightInStockFromHome
    ): Boolean {
        return oldItem == newItem
    }

}