// We are forced to use this package to access packate-private retrofit things
@file:Suppress("PackageNaming")

package retrofit2

import okhttp3.Response

fun <T> Call<T>.parseResponse(response: Response): retrofit2.Response<T> {
   return (this as OkHttpCall<T>).parseResponse(response)
}
