package com.example.shwemisale.screen.goldFromHome.bucket

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shwemi.util.loadImageWithGlide
import com.example.shwemisale.R
import com.example.shwemisale.data_layers.ui_models.OldStockBucketUiModel
import com.example.shwemisale.databinding.ItemOldStockBucketBinding

class OldStockBucketRecyclerAdapter(
    private val viewDetailClick: (item:OldStockBucketUiModel) -> Unit,
    private val removeClick: (item:OldStockBucketUiModel) -> Unit,
) :
    ListAdapter<OldStockBucketUiModel, OldStockBucketViewHolder>(OldStockBucketDiffUtil) {
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
    private val viewDetailClick: (item:OldStockBucketUiModel) -> Unit, private val removeClick: (item:OldStockBucketUiModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    fun bind(data: OldStockBucketUiModel) {
        binding.ivOldStock.loadImageWithGlide(data.imageUrl)
        binding.tvOldStockName.text = data.name
        if (data.weightGm.isNullOrEmpty().not()) {
            binding.tvOldStockWeight.text = "${data.weightGm.orEmpty()} gm"
        } else {
            binding.tvOldStockWeight.text =
                "${data.weightK.orEmpty()} K ${data.weightP.orEmpty()} P ${data.weightY.orEmpty()} Y"
        }
        binding.tvDataEmptyState.isVisible = data.oldStockId.isNullOrEmpty()
        binding.tvDataFilledState.isVisible = data.oldStockId.isNullOrEmpty().not()

        if (data.oldStockId.isNullOrEmpty()) {
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

object OldStockBucketDiffUtil : DiffUtil.ItemCallback<OldStockBucketUiModel>() {
    override fun areItemsTheSame(
        oldItem: OldStockBucketUiModel,
        newItem: OldStockBucketUiModel
    ): Boolean {
        return oldItem.imageUrl == newItem.imageUrl
    }

    override fun areContentsTheSame(
        oldItem: OldStockBucketUiModel,
        newItem: OldStockBucketUiModel
    ): Boolean {
        return oldItem == newItem
    }

}