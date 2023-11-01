package com.shwemigoldshop.shwemisale.screen.sellModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shwemigoldshop.shwemisale.util.loadImageWithGlide
import com.shwemigoldshop.shwemisale.data_layers.domain.sample.SampleDomain
import com.shwemigoldshop.shwemisale.databinding.ItemSampleListBinding

data class SampleListData(
    val id:String,
)

class SampleListRecyclerAdapter(private val removeClick:(item:SampleDomain)->Unit):ListAdapter<SampleDomain,SampleListViewHolder>(SampleListDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleListViewHolder {
        return SampleListViewHolder(ItemSampleListBinding.inflate(LayoutInflater.from(parent.context),parent,false),removeClick)
    }

    override fun onBindViewHolder(holder: SampleListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class SampleListViewHolder(private var binding: ItemSampleListBinding,
                           private val removeClick:(item:SampleDomain)->Unit): RecyclerView.ViewHolder(binding.root){
    fun bind(data: SampleDomain){
        binding.ivSampleItem.loadImageWithGlide(data.thumbnail)
        binding.mcvRemove.setOnClickListener {
            removeClick(data)
        }
    }
}

object SampleListDiffUtil: DiffUtil.ItemCallback<SampleDomain>(){
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