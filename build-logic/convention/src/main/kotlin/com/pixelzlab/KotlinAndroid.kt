import com.android.build.api.dsl.CommonExtension
import com.pixelzlab.Configuration
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = Configuration.compileSdk

        defaultConfig {
            minSdk = Configuration.minSdk
        }

        compileOptions {
            sourceCompatibility = Configuration.javaVersion
            targetCompatibility = Configuration.javaVersion
        }

        kotlinOptions {
            allWarningsAsErrors = false

            freeCompilerArgs = freeCompilerArgs + Configuration.freeCompilerArgs
            jvmTarget = Configuration.javaVersion.toString()

        }
    }
}

fun CommonExtension<*, *, *, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}

