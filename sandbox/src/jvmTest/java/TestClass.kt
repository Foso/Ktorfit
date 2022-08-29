import com.example.api.createGithubService
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfit
import org.junit.Assert
import org.junit.Test

class TestClass {

    private val ktorfit: Ktorfit by lazy {
        ktorfit {
            baseUrl("https://localhost/")

        }
    }

    @Test
    fun addition_isCorrect() {
        ktorfit.createGithubService()
        Assert.assertEquals(4, 2 + 2)
    }

}