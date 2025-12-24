plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.application)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    wasmJs {
        compilerOptions {
            freeCompilerArgs.add("-Xwasm-attach-js-exception")
        }
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.executable()
    }
    
    // Copy skiko.mjs to the kotlin output directory so webpack can find it
    // This is needed for both development and production builds
    val copySkikoForDevelopment = tasks.register("copySkikoForDevelopmentWebpack") {
        dependsOn("wasmJsDevelopmentExecutableCompileSync")
        doLast {
            val skikoSource = file("${rootProject.layout.buildDirectory.get()}/js/packages_imported/skiko-js-wasm-runtime/0.8.15/skiko.mjs")
            val skikoWasm = file("${rootProject.layout.buildDirectory.get()}/js/packages_imported/skiko-js-wasm-runtime/0.8.15/skiko.wasm")
            val kotlinOutput = file("${rootProject.layout.buildDirectory.get()}/js/packages/BesteChef-app-wasm-js/kotlin")
            
            if (skikoSource.exists() && kotlinOutput.exists()) {
                copy {
                    from(skikoSource, skikoWasm)
                    into(kotlinOutput)
                }
                println("✓ Copied skiko files to ${kotlinOutput}")
            }
        }
    }
    
    val copySkikoForProduction = tasks.register("copySkikoForProductionWebpack") {
        dependsOn("wasmJsProductionExecutableCompileSync")
        doLast {
            val skikoSource = file("${rootProject.layout.buildDirectory.get()}/js/packages_imported/skiko-js-wasm-runtime/0.8.15/skiko.mjs")
            val skikoWasm = file("${rootProject.layout.buildDirectory.get()}/js/packages_imported/skiko-js-wasm-runtime/0.8.15/skiko.wasm")
            val kotlinOutput = file("${rootProject.layout.buildDirectory.get()}/js/packages/BesteChef-app-wasm-js/kotlin")
            
            if (skikoSource.exists() && kotlinOutput.exists()) {
                copy {
                    from(skikoSource, skikoWasm)
                    into(kotlinOutput)
                }
                println("✓ Copied skiko files to ${kotlinOutput}")
            }
        }
    }
    
    tasks.named("wasmJsBrowserDevelopmentWebpack") {
        dependsOn(copySkikoForDevelopment)
    }
    
    tasks.named("wasmJsBrowserProductionWebpack") {
        dependsOn(copySkikoForProduction)
        doLast {
            // Copy our custom HTML with error handlers to the output
            val customHtml = file("src/wasmJsMain/resources/index.html")
            val outputHtml = file("${layout.buildDirectory.get()}/dist/wasmJs/productionExecutable/index.html")
            if (customHtml.exists() && outputHtml.exists()) {
                copy {
                    from(customHtml)
                    into(outputHtml.parentFile)
                    rename { "index.html" }
                }
                println("✓ Copied custom HTML with error handlers")
            }
        }
    }
    
    tasks.named("wasmJsBrowserDevelopmentWebpack") {
        dependsOn(copySkikoForDevelopment)
        doLast {
            // Copy our custom HTML with error handlers to the output
            val customHtml = file("src/wasmJsMain/resources/index.html")
            val outputHtml = file("${layout.buildDirectory.get()}/dist/wasmJs/developmentExecutable/index.html")
            if (customHtml.exists() && outputHtml.exists()) {
                copy {
                    from(customHtml)
                    into(outputHtml.parentFile)
                    rename { "index.html" }
                }
                println("✓ Copied custom HTML with error handlers")
            }
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.multiplatform.ui)
                implementation(libs.compose.multiplatform.ui.graphics)
                implementation(libs.compose.multiplatform.foundation)
                implementation(libs.compose.multiplatform.material3)
                implementation(libs.kotlinx.coroutines.core)
                implementation(project(":core"))
                implementation(project(":feature:login"))
                implementation(project(":feature:chef"))
                implementation(project(":feature:diner"))
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.androidx.activity.compose)
            }
        }
        
        val wasmJsMain by getting {
            dependencies {
                // Web-specific dependencies
            }
        }
    }
}

android {
    namespace = "nl.tue.hci.bestechef"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "nl.tue.hci.bestechef"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/main/res")
        }
    }
}
