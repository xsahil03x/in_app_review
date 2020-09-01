package com.magarex.inappreview_example

import android.os.Bundle
import com.magarex.inappreview.InAppReviewPlugin
import io.flutter.app.FlutterFragmentActivity

class EmbeddingV1Activity : FlutterFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InAppReviewPlugin.registerWith(registrarFor("dev.flutter.pigeon.InAppReviewApi.getPlatformVersion"))
    }
}
