package com.example.shwemisale.repositoryImpl

import com.example.shwemi.network.dto.ResponseDto
import com.example.shwemi.util.Resource
import com.example.shwemi.util.parseError
import com.example.shwemi.util.parseErrorWithDataClass
import com.example.shwemisale.data_layers.domain.product.ProductInfoDomain
import com.example.shwemisale.data_layers.domain.product.ProductSizeAndReasonDomain
import com.example.shwemisale.data_layers.dto.SimpleError
import com.example.shwemisale.data_layers.dto.product.asDomain
import com.example.shwemisale.localDataBase.LocalDatabase
import com.example.shwemisale.network.api_services.PawnService
import com.example.shwemisale.network.api_services.ProductService
import com.example.shwemisale.repository.ProductRepository
import retrofit2.Response
import javax.inject.Inject

class ProductRepoImpl @Inject constructor(
    private val localDatabase: LocalDatabase,
    private val productService: ProductService
) :ProductRepository {
    override suspend fun getProductInfo(
        token: String,
        productId: String
    ): Resource<ProductInfoDomain> {
        return try {
            val response = productService.getProductInfo(
                localDatabase.getAccessToken().orEmpty(),
                productId
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.asDomain())
            } else {
                val errorJsonString = response.errorBody()?.string().orEmpty()
                val singleError =
                    response.errorBody()?.parseErrorWithDataClass<SimpleError>(errorJsonString)
                if (singleError != null) {
                    Resource.Error(singleError.response.message)
                } else {
                    val errorMessage =
                        response.errorBody()?.parseError(errorJsonString)
                    val list: List<Map.Entry<String, Any>> =
                        ArrayList<Map.Entry<String, Any>>(errorMessage!!.entries)
                    val (key, value) = list[0]
                    Resource.Error(value.toString())
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getProductSizeAndReason(
        token: String,
        productId: String
    ): Resource<ProductSizeAndReasonDomain> {
        return try {
            val response = productService.getProductSizeAndReason(
                localDatabase.getAccessToken().orEmpty(),
                productId
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.data.asDomain())
            } else {
                val errorJsonString = response.errorBody()?.string().orEmpty()
                val singleError =
                    response.errorBody()?.parseErrorWithDataClass<SimpleError>(errorJsonString)
                if (singleError != null) {
                    Resource.Error(singleError.response.message)
                } else {
                    val errorMessage =
                        response.errorBody()?.parseError(errorJsonString)
                    val list: List<Map.Entry<String, Any>> =
                        ArrayList<Map.Entry<String, Any>>(errorMessage!!.entries)
                    val (key, value) = list[0]
                    Resource.Error(value.toString())
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateProductInfo(
        token: String,
        productId: String,
        gold_and_gem_weight_gm: String?,
        gem_weight_ywae: String?,
        gem_value: String?,
        promotion_discount: String?,
        jewellery_type_size_id: String?,
        edit_reason_id: String?,
        pt_and_clip_cost: String?,
        general_sale_item_id: String?,
        new_clip_wt_gm: String?
    ): Resource<ResponseDto> {
        return try {
            val response = productService.updateProductInfo(
                localDatabase.getAccessToken().orEmpty(),
                productId,
                gold_and_gem_weight_gm, gem_weight_ywae, gem_value, promotion_discount, jewellery_type_size_id, edit_reason_id, pt_and_clip_cost, general_sale_item_id, new_clip_wt_gm
            )

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.response)
            } else {
                val errorJsonString = response.errorBody()?.string().orEmpty()
                val singleError =
                    response.errorBody()?.parseErrorWithDataClass<SimpleError>(errorJsonString)
                if (singleError != null) {
                    Resource.Error(singleError.response.message)
                } else {
                    val errorMessage =
                        response.errorBody()?.parseError(errorJsonString)
                    val list: List<Map.Entry<String, Any>> =
                        ArrayList<Map.Entry<String, Any>>(errorMessage!!.entries)
                    val (key, value) = list[0]
                    Resource.Error(value.toString())
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}