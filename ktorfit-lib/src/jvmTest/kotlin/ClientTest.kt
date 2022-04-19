import de.jensklingenberg.ktorfit.Ktorfit
import org.junit.Test

class ClientTest {

    @Test
    fun whenBaseUrlNotEndingWithSlashThrowError() {
        try {
            val ktorfit = Ktorfit("www.example.com")
        }catch (illegal: IllegalStateException){
            assert(illegal.message == "Base URL needs to end with /")
        }
    }

    @Test
    fun whenBaseUrlEmptyThrowError() {
        try {
            val ktorfit = Ktorfit("")
        }catch (illegal: IllegalStateException){
            assert(illegal.message == "Base URL required")
        }

    }

}