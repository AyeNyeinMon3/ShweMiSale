package com.example.shwemisale.sellModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemisale.databinding.ItemSampleListBinding

data class SampleListData(
    val id:String,
)

class SampleListRecyclerAdapter:ListAdapter<SampleListData,SampleListViewHolder>(SampleListDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleListViewHolder {
        return SampleListViewHolder(ItemSampleListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SampleListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class SampleListViewHolder(private var binding: ItemSampleListBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(data: SampleListData){

    }
}

object SampleListDiffUtil: DiffUtil.ItemCallback<SampleListData>(){
    override fun areItemsTheSame(
        oldItem: SampleListData,
        newItem: SampleListData
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: SampleListData,
        newItem: SampleListData
    ): Boolean {
        return oldItem == newItem
    }

}