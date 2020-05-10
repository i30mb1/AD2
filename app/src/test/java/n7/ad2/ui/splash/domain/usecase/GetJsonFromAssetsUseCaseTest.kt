package n7.ad2.ui.splash.domain.usecase

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
@SmallTest
class GetJsonFromAssetsUseCaseTest{

    @Test
    fun `hero file exist and not empty`() {
        val file = File("${System.getProperty("user.dir")}\\src\\main\\assets\\heroes.json")
        Truth.assertWithMessage("heroes file not Exist").that(file.exists()).isTrue()

        val text = file.readText()
        Truth.assertWithMessage("heroes file not ready! Run ParserInfo").that(text).isNotEmpty()
    }

}