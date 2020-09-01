package com.magarex.inappreview_example

import android.util.Log
import com.magarex.inappreview.InAppReviewPlugin
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import java.lang.reflect.Method


class EmbeddingV2Activity : FlutterFragmentActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        flutterEngine.plugins.add(InAppReviewPlugin())

//        registerPlugins(flutterEngine)
    }

    /**
     * Registers all plugins that an app lists in its pubspec.yaml.
     *
     *
     * The Flutter tool generates a class called GeneratedPluginRegistrant, which includes the code
     * necessary to register every plugin in the pubspec.yaml with a given `FlutterEngine`. The
     * GeneratedPluginRegistrant must be generated per app, because each app uses different sets of
     * plugins. Therefore, the Android embedding cannot place a compile-time dependency on this
     * generated class. This method uses reflection to attempt to locate the generated file and then
     * use it at runtime.
     *
     *
     * This method fizzles if the GeneratedPluginRegistrant cannot be found or invoked. This
     * situation should never occur, but if any eventuality comes up that prevents an app from using
     * this behavior, that app can still write code that explicitly registers plugins.
     */
    private fun registerPlugins(flutterEngine: FlutterEngine) {
        try {
            val generatedPluginRegistrant = Class.forName("io.flutter.plugins.GeneratedPluginRegistrant")
            val registrationMethod: Method = generatedPluginRegistrant.getDeclaredMethod("registerWith", FlutterEngine::class.java)
            registrationMethod.invoke(null, flutterEngine)
        } catch (e: Exception) {
            Log.w(
                    "EmbeddingV2Activity",
                    "Tried to automatically register plugins with FlutterEngine ("
                            + flutterEngine
                            + ") but could not find and invoke the GeneratedPluginRegistrant.")
        }
    }
}