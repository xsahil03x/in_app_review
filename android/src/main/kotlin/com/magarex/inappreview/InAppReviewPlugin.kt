package com.magarex.inappreview

import android.app.Activity
import android.content.Context
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.play.core.review.ReviewManagerFactory
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry.Registrar
import kotlinx.coroutines.ExperimentalCoroutinesApi

/** InAppReviewPlugin */
class InAppReviewPlugin : FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler {

    private var activity: Activity? = null
    private lateinit var context: Context
    private lateinit var viewModel: InAppReviewViewModel
    private lateinit var channel: MethodChannel

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    companion object {
        private const val CHANNEL_NAME = "in_app_review"
        private const val INIT = "init"
        private const val PRE_WARM_REVIEW = "preWarmReview"
        private const val LAUNCH_REVIEW = "launchReview"

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val plugin = InAppReviewPlugin()
            plugin.setupChannel(registrar.messenger(), registrar.context())
        }
    }

    private fun setupChannel(messenger: BinaryMessenger, context: Context) {
        this.context = context
        channel = MethodChannel(messenger, CHANNEL_NAME)
        channel.setMethodCallHandler(this)
    }

    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        setupChannel(binding.binaryMessenger, binding.applicationContext)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        teardownChannel(binding.binaryMessenger)
    }

    private fun teardownChannel(messenger: BinaryMessenger) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    @ExperimentalCoroutinesApi
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            INIT -> init(result)
            PRE_WARM_REVIEW -> preWarmReview(result)
            LAUNCH_REVIEW -> launchReview(result)
            else -> result.notImplemented()
        }
    }

    private fun init(result: MethodChannel.Result) {
        if (activity == null || activity!!.isFinishing) {
            result.error("No Activity", "in_app_review plugin requires a foreground activity", null)
        }
        if (activity !is FragmentActivity) {
            result.error("No Fragment Activity", "in_app_review requires activity to be a FragmentActivity", null)
        }
        val reviewManager = ReviewManagerFactory.create(context)
        val factory = ReviewViewModelProviderFactory(reviewManager)
        viewModel = ViewModelProvider(activity as FragmentActivity, factory).get(InAppReviewViewModel::class.java)
        result.success(null)
    }

    private fun preWarmReview(result: MethodChannel.Result) {
        if (activity == null || activity!!.isFinishing) {
            result.error("No Activity", "in_app_review plugin requires a foreground activity", null)
        }
        if (activity !is FragmentActivity) {
            result.error("No Fragment Activity", "in_app_review requires activity to be a FragmentActivity", null)
        }
        viewModel.preWarmReview()
        result.success(null)
    }

    @ExperimentalCoroutinesApi
    private fun launchReview(result: MethodChannel.Result) {
        if (activity == null || activity!!.isFinishing) {
            result.error("No Activity", "in_app_review plugin requires a foreground activity", null)
        }
        if (activity !is FragmentActivity) {
            result.error("No Fragment Activity", "in_app_review requires activity to be a FragmentActivity", null)
        }
        viewModel.launchReview(activity!!) {
            result.success(null)
        }
    }
}
