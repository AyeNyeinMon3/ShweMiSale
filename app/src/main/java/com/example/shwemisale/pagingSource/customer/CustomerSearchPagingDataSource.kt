package com.example.shwemisale.pagingSource.customer

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.shwemi.util.Resource
import com.example.shwemisale.data_layers.domain.customers.CustomerDataDomain
import com.example.shwemisale.data_layers.dto.customers.asDomain
import com.example.shwemisale.pagingSource.PagingMeta
import com.example.shwemisale.repositoryImpl.CustomerRepoImpl

class CustomerSearchPagingDataSource(
    private val code:String?,
    private val name: String?,
    private val phone: String?,
    private val date_of_birth: String?,
    private val  gender: String?,
    private val province_id: String?,
    private val township_id: String?,
    private val  address: String?,
    private val  nrc: String?,
    private val customerRepoImpl: CustomerRepoImpl
) : PagingSource<Int, CustomerDataDomain>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CustomerDataDomain> {
        return try {
            val currentPage = params.key ?: 1
            var supplyList = mutableListOf<CustomerDataDomain>()
            var errorMessage = ""
            var metaDomain: PagingMeta? = null
            val response =  customerRepoImpl.getCustomerDataByCode(code, name, phone, date_of_birth, gender, province_id, township_id, address, nrc)
            when(response){
                is Resource.Success->{
                    if (response.data!!.data.isNotEmpty()){
                        supplyList.addAll(response.data!!.data.map { it.asDomain() })
                    }else{
                        supplyList = mutableListOf()
                    }
                    metaDomain = response.data!!.meta
                }
                is Resource.Error->{
                    errorMessage = response.message.orEmpty()
                }
                else -> {}
            }

            if (errorMessage.isEmpty()) {
                LoadResult.Page(
                    supplyList,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (currentPage == metaDomain?.lastPage) null else currentPage + 1
                )
            } else {
                LoadResult.Error(
                    Exception(
                        errorMessage
                    )
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CustomerDataDomain>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}