package ktorfit

import com.example.api.StarWarsApi
import com.example.model.People
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryName

interface TestApi2 : StarWarsApi,  QueryNameTestApi {
    @GET("people/{id}/")
    fun tste()

}

data class Test(val name: String)


interface QueryNameTestApi {

    @GET("people/{id}/")
    suspend fun testQueryName(@Path("id") peopleId: Int, @QueryName name: String): People

    @GET("people/{id}/")
    suspend fun testQueryNameList(@Path("id") peopleId: Int, @QueryName(false) name: List<Int?>): People


}