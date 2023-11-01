package com.shwemigoldshop.shwemisale.screen.goldFromHome.bucket

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shwemigoldshop.shwemisale.util.loadImageWithGlide
import com.shwemigoldshop.shwemisale.R
import com.shwemigoldshop.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.shwemigoldshop.shwemisale.databinding.ItemOldStockBucketBinding
import com.shwemigoldshop.shwemisale.screen.goldFromHome.getKPYFromYwae

class OldStockBucketRecyclerAdapter(
    private val viewDetailClick: (item:StockFromHomeDomain) -> Unit,
    private val removeClick: (item:StockFromHomeDomain) -> Unit,
) :
    ListAdapter<StockFromHomeDomain, OldStockBucketViewHolder>(OldStockBucketDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OldStockBucketViewHolder {

        return OldStockBucketViewHolder(
            ItemOldStockBucketBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), viewDetailClick, removeClick
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: OldStockBucketViewHolder, position: Int) {

        holder.bind(getItem(position))
    }

}

class OldStockBucketViewHolder(
    private var binding: ItemOldStockBucketBinding,
    private val viewDetailClick: (item:StockFromHomeDomain) -> Unit, private val removeClick: (item:StockFromHomeDomain) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    fun bind(data: StockFromHomeDomain) {
        binding.ivOldStock.loadImageWithGlide(data.image?.url)
        binding.tvOldStockName.text = data.stock_name.orEmpty()
        if (data.showGram) {
            binding.tvOldStockWeight.text = "${data.gold_and_gem_weight_gm.orEmpty()} gm"
        } else {
            val kpy = getKPYFromYwae(data.gold_gem_weight_ywae!!.toDouble())
            binding.tvOldStockWeight.text = binding.root.context.getString(R.string.kpy_value,kpy[0].toInt().toString(),kpy[1].toInt().toString(),kpy[2].toString())
        }
        binding.tvDataEmptyState.isVisible = !data.dataFilled
        binding.tvDataFilledState.isVisible = data.dataFilled

        if ( !data.dataFilled) {
            binding.btnViewDetail.text = "Add detail"
            binding.tvRemove.setTextColor(binding.root.context.getColorStateList(R.color.primary))
            binding.tvRemove.setOnClickListener {
                removeClick(data)
            }
        } else {
            binding.btnViewDetail.text = "View detail"
            binding.tvRemove.setTextColor(binding.root.context.getColorStateList(R.color.grey_line))
        }
        binding.btnViewDetail.setOnClickListener {
            viewDetailClick(data)
        }

    }
}

object OldStockBucketDiffUtil : DiffUtil.ItemCallback<StockFromHomeDomain>() {
    override fun areItemsTheSame(
        oldItem: StockFromHomeDomain,
        newItem: StockFromHomeDomain
    ): Boolean {
        return oldItem.image.url == newItem.image.url
    }

    override fun areContentsTheSame(
        oldItem: StockFromHomeDomain,
        newItem: StockFromHomeDomain
    ): Boolean {
        return oldItem == newItem
    }

}