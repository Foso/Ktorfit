package ktorfit

import com.example.api.StarWarsApi
import com.example.model.People
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.HeaderMap
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.QueryName

interface TestApi2 : StarWarsApi,  QueryNameTestApi {
    @GET("people/{id}/")
    fun tste()

}

data class Test(val name: String)

interface QueryMapApi {
    @GET("people/{id}/")
    suspend fun testQueryMap(@Path("id") peopleId: Int, @QueryMap(true) name: Map<String, Test>): People
}

interface QueryTestApi {


    @GET("people/{id}/")
    suspend fun testQueryWithArray(@Path("id") peopleId: Int, @Query("huhu") name: Array<String?>): People

    @GET("people/{id}/")
    suspend fun testQueryWithList(@Path("id") peopleId: Int, @Query("huhu") name: List<String?>): People


    @GET("people/{id}/")
    suspend fun testQueryName(@Path("id") peopleId: Int, @QueryName name: String): People

    @GET("people/{id}/")
    suspend fun testQueryNameList(@Path("id") peopleId: Int, @QueryName(false) name: List<String?>): People


}

interface HeaderTestApi {


    @GET("people/{id}/")
    suspend fun testHeaderWithArray(@Path("id") peopleId: Int, @Header("huhu") name: Array<String?>): People

    @GET("people/{id}/")
    suspend fun testHeaderWithList(@Path("id") peopleId: Int, @Header("huhu") name: List<String?>): People


    @Headers(value = ["Accept2: application/json","Accept: application/json2"])
    @GET("people/{id}/")
    suspend fun testHeaders(@Path("id") peopleId: Int ): People

    @GET("people/{id}/")
    suspend fun testHeaderMap(@Path("id") peopleId: Int, @HeaderMap() name: Map<String,String>): People


}

interface QueryNameTestApi {


    @GET("people/{id}/")
    suspend fun testQueryName(@Path("id") peopleId: Int, @QueryName name: String): People

    @GET("people/{id}/")
    suspend fun testQueryNameList(@Path("id") peopleId: Int, @QueryName(false) name: List<String?>): People


}