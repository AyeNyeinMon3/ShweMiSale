package com.example.shwemi.util

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.icu.util.ValueIterator
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shwemisale.R
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.ResponseBody
import java.security.Key
import java.text.SimpleDateFormat
import java.util.*
import com.squareup.moshi.internal.Util.NO_ANNOTATIONS
import org.json.JSONObject
import java.lang.reflect.Type
import javax.inject.Inject
import kotlin.collections.HashMap


inline fun <reified T> ResponseBody.parseError(): T? {
    val moshi = Moshi.Builder().build()
    val builder = Moshi.Builder().build()
//    val parser = moshi.adapter(T::class.java)
    val parser = moshi.adapter(T::class.java)
    val response = this.string()
    try {
        return parser.fromJson(response)
    } catch (e: JsonDataException) {
        e.printStackTrace()
    }
    return null
}

fun getErrorMessageFromHashMap(errorMessage:Map<String,List<String>>):String{
    val list: List<Map.Entry<String, Any>> =
        ArrayList<Map.Entry<String, Any>>(errorMessage.entries)
    val (key, value) = list[0]
    return value.toString()
}

inline fun ResponseBody.parseError(): Map<String,List<String>>?{
    val moshi = Moshi.Builder().build()
    val type = Types.newParameterizedType(Map::class.java, String::class.java, List::class.javaObjectType)
    val adapter = moshi.adapter<Map<String, List<String>>>(type)
    var jsonObject = JSONObject(this.string())


    try {
        return adapter.fromJson(jsonObject.getJSONObject("response").getJSONObject("message").toString())
    }catch (e: JsonDataException) {
        e.printStackTrace()
    }
    return null
}


fun getErrorString(errorList: List<String?>): String {
    val gg = errorList.filterNotNull()
    return gg[0]
}

fun convertToSqlDate(calendar: Calendar): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(calendar.time)
}

fun takeThreeDecimal(number: Double): String {
    val number3digits: Double = String.format("%.3f", number).toDouble()
    val number2digits: Double = String.format("%.2f", number3digits).toDouble()
    return String.format("%.1f", number2digits).toDouble().toString()
}
fun loadImageUrlPhotoView(photoView: PhotoView, imgUrl: String?) {
    imgUrl?.let {
        Glide.with(photoView.context).load(it)
            .apply(
                RequestOptions.placeholderOf(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
            ).into(photoView)
    }
}
fun getRealPathFromUri(context: Context, contentUri: Uri): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        cursor.getString(columnIndex)
    } finally {
        cursor?.close()
    }
}

fun AutoCompleteTextView.showDropdown(adapter: ArrayAdapter<String>?) {
    if (!TextUtils.isEmpty(this.text.toString())) {
        adapter?.filter?.filter(null)
    }
}

