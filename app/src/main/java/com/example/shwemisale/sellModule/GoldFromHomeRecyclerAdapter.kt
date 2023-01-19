package com.example.shwemisale.sellModule
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.databinding.ItemGoldFromHomeBinding

data class GoldFromHomeData(
    val id : String,
    val resellItem: String,
    val payMoney : String,
    val goldWeight:String,
    val pledgeMoney:String
)

class GoldFromHomeRecyclerAdapter:ListAdapter<GoldFromHomeData,GoldFromHomeViewHolder>(
    GoldFromHomeDiffUtil
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoldFromHomeViewHolder {
        return GoldFromHomeViewHolder(ItemGoldFromHomeBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: GoldFromHomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class GoldFromHomeViewHolder(private var binding: ItemGoldFromHomeBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(data: GoldFromHomeData){
       binding.tvGoldWeight.text = data.goldWeight
        binding.tvVoucherPurchasePayment.text = data.payMoney
        binding.tvPledgeMoney.text = data.pledgeMoney
        binding.tvResellItem.text = data.resellItem
    }
}

object GoldFromHomeDiffUtil : DiffUtil.ItemCallback<GoldFromHomeData>(){
    override fun areItemsTheSame(
        oldItem: GoldFromHomeData,
        newItem: GoldFromHomeData
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: GoldFromHomeData,
        newItem: GoldFromHomeData
    ): Boolean {
        return oldItem == newItem
    }


}