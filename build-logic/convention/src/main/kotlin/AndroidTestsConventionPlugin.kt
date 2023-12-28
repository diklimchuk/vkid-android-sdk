import com.android.build.api.dsl.CommonExtension
import com.vk.id.UninstallTestAppTask
import com.vk.id.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidTestsConventionPlugin : Plugin<Project> {

    override fun apply(
        target: Project
    ): Unit = with(target) {
        with(pluginManager) {
            apply("vkid.placeholders")
        }
        val android = extensions.getByName("android") as CommonExtension<*, *, *, *, *>
        android.apply {
            defaultConfig {
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }
        }

        dependencies {
            // https://github.com/KasperskyLab/Kaspresso/issues/578
            add("debugImplementation", libs.findLibrary("android-material").get())
            add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
            add("androidTestUtil", libs.findLibrary("androidx-test-orchestrator").get())
            add("androidTestImplementation", libs.findLibrary("kaspresso").get())
            add("androidTestImplementation", libs.findLibrary("kaspresso-compose").get())
            add("androidTestImplementation", libs.findLibrary("androidx-compose-ui-test-junit4").get())
            add("androidTestImplementation", libs.findLibrary("kotest-assertions").get())
            add("androidTestImplementation", libs.findLibrary("androidx-test-junit-ktx").get())
        }

        tasks.register("uninstallTestApp", UninstallTestAppTask::class.java)
        tasks.configureEach {
            if (name == "connectedDebugAndroidTest") {
                finalizedBy("uninstallTestApp")
            }
        }
    }
}