package com.example.shwemi.util

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shwemisale.R
import com.github.chrisbanes.photoview.PhotoView
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


inline fun <reified T> ResponseBody.parseErrorWithDataClass(errorJsonString: String): T? {
    val moshi = Moshi.Builder().build()
    val builder = Moshi.Builder().build()
//    val parser = moshi.adapter(T::class.java)
    val parser = moshi.adapter(T::class.java)

    try {
        return parser.fromJson(errorJsonString)
    } catch (e: JsonDataException) {
        e.printStackTrace()
    }
    return null
}

fun getErrorMessageFromHashMap(errorMessage: Map<String, List<String>>): String {
    val list: List<Map.Entry<String, Any>> =
        ArrayList<Map.Entry<String, Any>>(errorMessage.entries)
    val (key, value) = list[0]
    return value.toString()
}

inline fun ResponseBody.parseError(errorJsonString: String): Map<String, List<String>>? {
    val moshi = Moshi.Builder().build()
    val type =
        Types.newParameterizedType(Map::class.java, String::class.java, List::class.javaObjectType)
    val adapter = moshi.adapter<Map<String, List<String>>>(type)
    var jsonObject = JSONObject(errorJsonString)


    try {
        return adapter.fromJson(
            jsonObject.getJSONObject("response").getJSONObject("message").toString()
        )
    } catch (e: JsonDataException) {
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

fun generateNumberFromEditText(editText: EditText): String {
    return if (editText.text.isNullOrEmpty()) {
        "0"
    } else {
        editText.text.toString()
    }
}

fun isNumeric(input: String): Boolean {
    val number = input.toDoubleOrNull()
    return number != null
}

fun hideKeyboard(activity: FragmentActivity?, view: View) {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun getRoundDownForPrice(price: Int): Int {
    val lastTwoDigits = price % 50
    return price - lastTwoDigits
}

fun getRoundDownForPawn(price: Int): Int {
    val lastTwoDigits = price % 100
    val firstDigits = price - lastTwoDigits
    var newTwoDigits =
        if (lastTwoDigits in 0..25) {
            0
        } else if (lastTwoDigits in 26..50) {
            50
        } else if (lastTwoDigits in 51..75) {
            50
        } else{
            100
        }
    return firstDigits + newTwoDigits
}

fun compressImage(imageFilePath: String): RequestBody {
    var bitmap = BitmapFactory.decodeFile(imageFilePath)
    val width = bitmap.width
    val height = bitmap.height
    val ratio = (width * height).toFloat() / (2 * 1024 * 1024)

    if (ratio > 1) {
        val scale = Math.sqrt(ratio.toDouble()).toFloat()
        val newWidth = (width / scale).toInt()
        val newHeight = (height / scale).toInt()
        bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
    val body = MultipartBody.Part.createFormData("image", "image.jpg", requestBody)

    return requestBody
}



