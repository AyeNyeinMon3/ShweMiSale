package com.example.shwemisale.screen.sellModule
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.data_layers.ui_models.goldFromHome.StockFromHomeInfoUiModel
import com.example.shwemisale.databinding.ItemGoldFromHomeBinding

data class GoldFromHomeData(
    val id : String,
    val resellItem: String,
    val payMoney : String,
    val goldWeight:String,
    val pledgeMoney:String
)

class GoldFromHomeRecyclerAdapter(private val editClick:(id:String)->Unit,
                                  private val deleteClick:(id:String)->Unit):ListAdapter<StockFromHomeInfoUiModel,GoldFromHomeViewHolder>(
    GoldFromHomeDiffUtil
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoldFromHomeViewHolder {
        return GoldFromHomeViewHolder(ItemGoldFromHomeBinding.inflate(LayoutInflater.from(parent.context),parent,false),editClick, deleteClick)
    }

    override fun onBindViewHolder(holder: GoldFromHomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class GoldFromHomeViewHolder(private var binding: ItemGoldFromHomeBinding,
                             private val editClick:(id:String)->Unit,
                             private val deleteClick:(id:String)->Unit) : RecyclerView.ViewHolder(binding.root){
    fun bind(data: StockFromHomeInfoUiModel){
       binding.tvGoldWeight.text = data.oldStockDGoldWeightY
        binding.tvVoucherPurchasePayment.text = data.oldStockc_voucher_buying_value
        binding.tvPawnPrice.text = data.calculatedPriceForPawn
        binding.tvResellItem.text = data.name
        binding.ivEdit.setOnClickListener {
            editClick(data.id)
        }
        binding.ivDelete.setOnClickListener {
            deleteClick(data.id)
        }
    }
}

object GoldFromHomeDiffUtil : DiffUtil.ItemCallback<StockFromHomeInfoUiModel>(){
    override fun areItemsTheSame(
        oldItem: StockFromHomeInfoUiModel,
        newItem: StockFromHomeInfoUiModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: StockFromHomeInfoUiModel,
        newItem: StockFromHomeInfoUiModel
    ): Boolean {
        return oldItem == newItem
    }


}