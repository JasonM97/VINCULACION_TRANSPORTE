plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.prueba1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.prueba1"
        minSdk = 24
        targetSdk = 35
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
    implementation("com.google.firebase:firebase-analytics")

    //  DEPENDENCIAS PARA EL FIREBASE
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")


   // implementation("com.google.firebase:firebase-storage:20.0.0")
  //  implementation("com.google.firebase:firebase-auth:21.0.1")
   // implementation("com.google.firebase:firebase-firestore:24.0.0")

  // DEPENCIAS PARA EL API GOOGLE MAPS

  //  implementation("com.google.android.gms:play-services-maps:18.1.0")
  //  implementation("com.google.maps:google-maps-services:2.1.1")
   // implementation("com.google.maps.android:android-maps-utils:2.3.0")
   // implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}
