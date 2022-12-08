package rule

import com.google.common.truth.Truth
import io.github.detekt.test.utils.createEnvironment
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.junit.Test

class UseLaunchSaveRuleTest {

    private val env = createEnvironment().env

    @Test
    fun `reports error`() {
        val code = """
        fun doSomething() = scope.launch {
            //...
        }
        """
        val findings = UseLaunchSaveRule(Config.empty).compileAndLintWithContext(env, code)
        Truth.assertThat(findings).hasSize(1)
    }

    @Test
    fun `do not reports error`() {
        val code = """
        fun doSomething() = scope.launchSave {
            //...
        }
        """
        val findings = UseLaunchSaveRule(Config.empty).compileAndLintWithContext(env, code)
        Truth.assertThat(findings).isEmpty()
    }

}