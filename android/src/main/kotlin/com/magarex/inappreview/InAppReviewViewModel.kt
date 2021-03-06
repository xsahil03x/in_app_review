package com.magarex.inappreview

import android.app.Activity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import kotlinx.coroutines.*

class InAppReviewViewModel(private val reviewManager: ReviewManager) : ViewModel() {

    private var reviewInfo: Deferred<ReviewInfo>? = null

    /**
     * Start requesting the review info that will be needed later in advance.
     */
    @MainThread
    fun preWarmReview() {
        if (reviewInfo == null) {
            reviewInfo = viewModelScope.async { reviewManager.requestReview() }
        }
    }

    /**
     * Only return ReviewInfo object if the prewarming has already completed,
     * i.e. if the review can be launched immediately.
     */
    @ExperimentalCoroutinesApi
    private suspend fun obtainReviewInfo(): ReviewInfo? = withContext(Dispatchers.Main.immediate) {
        if (reviewInfo?.isCompleted == true && reviewInfo?.isCancelled == false) {
            reviewInfo?.getCompleted().also {
                reviewInfo = null
            }
        } else null
    }

    @ExperimentalCoroutinesApi
    fun launchReview(activity: Activity, onReviewComplete: () -> Unit) {
        viewModelScope.launch {
            val reviewInfo = obtainReviewInfo()
            withContext(viewModelScope.coroutineContext) {
                reviewInfo?.let {
                    reviewManager.launchReview(activity, it)
                }
            }
            onReviewComplete()
        }
    }
}

class ReviewViewModelProviderFactory(
        private val manager: ReviewManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ReviewManager::class.java).newInstance(manager)
    }
}
