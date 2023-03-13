package com.androidarchitectureplayground.network.services

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class LazyRetrofitConverterFactory(private val parentFactory: Lazy<Converter.Factory>) : Converter.Factory() {
   override fun responseBodyConverter(
      type: Type,
      annotations: Array<out Annotation>,
      retrofit: Retrofit
   ): Converter<ResponseBody, *> {
      val lazyConverter = lazy {
         requireNotNull(
            parentFactory.value.responseBodyConverter(
               type,
               annotations,
               retrofit
            )
         ) { "Moshi converter should never be null" }
      }

      return Converter { lazyConverter.value.convert(it) }
   }

   override fun requestBodyConverter(
      type: Type,
      parameterAnnotations: Array<out Annotation>,
      methodAnnotations: Array<out Annotation>,
      retrofit: Retrofit
   ): Converter<*, RequestBody> {
      val lazyConverter = lazy {
         @Suppress("UNCHECKED_CAST")
         requireNotNull(
            parentFactory.value.requestBodyConverter(
               type,
               parameterAnnotations,
               methodAnnotations,
               retrofit
            ) as Converter<Any?, RequestBody>
         ) { "Moshi converter should never be null" }
      }

      return Converter<Any?, RequestBody> { value -> lazyConverter.value.convert(value) }
   }
}
