package com.example.shwemisale.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class LenientJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        val delegate = moshi.nextAdapter<Any>(this, type, annotations)
        return LenientJsonAdapter(delegate)
    }

    private class LenientJsonAdapter<T>(private val delegate: JsonAdapter<T>) : JsonAdapter<T>() {
        override fun fromJson(reader: JsonReader): T? {
            reader.isLenient = true
            return delegate.fromJson(reader)
        }

        override fun toJson(writer: JsonWriter, value: T?) {
            delegate.toJson(writer, value)
        }
    }
}
