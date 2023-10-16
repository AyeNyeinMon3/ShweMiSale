package com.example.shwemisale.screen.goldFromHome.bucket

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shwemisale.data_layers.domain.goldFromHome.StockFromHomeDomain
import com.example.shwemisale.data_layers.dto.goldFromHome.Image
import com.example.shwemisale.data_layers.ui_models.OldStockBucketUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class BucketShareViewModel : ViewModel() {
    var listInBucket = mutableListOf<StockFromHomeDomain>()
    val oldStockInBucketList = MutableLiveData<MutableList<StockFromHomeDomain>>()

    fun addToOldStockBucket(item: StockFromHomeDomain) {
        listInBucket.add(item)
        oldStockInBucketList.value = listInBucket
    }

    fun removeOldStockBucket(item: StockFromHomeDomain) {
        listInBucket.remove(item)
        oldStockInBucketList.value = listInBucket
    }

    fun fillData(
        id: Int?,
        a_buying_price: String,
        b_voucher_buying_value: String,
        c_voucher_buying_price: String,
        calculated_buying_value: String,
        calculated_for_pawn: String,
        d_gold_weight_ywae: String,
        e_price_from_new_voucher: String,
        f_voucher_shown_gold_weight_ywae: String,
        gem_value: String,
        gem_weight_details_session_key: String,
        gold_and_gem_weight_gm: String,
        gem_weight_ywae: String,
        gold_gem_weight_ywae: String,
        gold_weight_ywae: String,
        gq_in_carat: String,
        has_general_expenses: String,
        image: Image,
        impurities_weight_ywae: String,
        maintenance_cost: String,
        price_for_pawn: String,
        pt_and_clip_cost: String,
        qty: String,
        rebuy_price: String,
        size: String,
        stock_condition: String,
        stock_name: String,
        type: String,
        wastage_ywae: String,
        rebuy_price_vertical_option: String,
        localId: Int,
        derived_gold_type_id: String,
        dataFilled: Boolean
    ) {
        listInBucket = listInBucket.map {
            if (it.localId == localId){
                it.copy(
                    id = id,
                    a_buying_price = a_buying_price,
                    b_voucher_buying_value = b_voucher_buying_value,
                    c_voucher_buying_price = c_voucher_buying_price,
                    calculated_buying_value = calculated_buying_value,
                    calculated_for_pawn = calculated_for_pawn,
                    d_gold_weight_ywae = d_gold_weight_ywae,
                    e_price_from_new_voucher = e_price_from_new_voucher,
                    f_voucher_shown_gold_weight_ywae = f_voucher_shown_gold_weight_ywae,
                    gem_value = gem_value,
                    gem_weight_details_session_key = gem_weight_details_session_key,
                    gold_and_gem_weight_gm = gold_and_gem_weight_gm,
                    gem_weight_ywae = gem_weight_ywae,
                    gold_gem_weight_ywae = gold_gem_weight_ywae,
                    gold_weight_ywae = gold_weight_ywae,
                    gq_in_carat = gq_in_carat,
                    has_general_expenses = has_general_expenses,
                    image = image,
                    impurities_weight_ywae = impurities_weight_ywae,
                    maintenance_cost = maintenance_cost,
                    price_for_pawn = price_for_pawn,
                    pt_and_clip_cost = pt_and_clip_cost,
                    qty = qty,
                    rebuy_price = rebuy_price,
                    size = size,
                    stock_condition = stock_condition,
                    stock_name = stock_name,
                    type = type,
                    wastage_ywae = wastage_ywae,
                    rebuy_price_vertical_option = rebuy_price_vertical_option,
                    localId = localId,
                    derived_gold_type_id = derived_gold_type_id,
                    dataFilled = dataFilled

                )
            }else{
                it
            }
        }.toMutableList()
        oldStockInBucketList.value = listInBucket
    }
}

