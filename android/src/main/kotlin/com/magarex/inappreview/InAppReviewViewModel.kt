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
    /**
     * For this sample, we check if the user has been asked to review during this application session
     * already. Every time the app process starts fresh, it will be reset.
     *
     * In a real app, you should implement your own back-off strategy, for example:
     * you could persist the last time the user was asked in a database,
     * and only ask if at least a week has passed from the previous request.
     */
    private var alreadyAskedForReview = false
    private var reviewInfo: Deferred<ReviewInfo>? = null

    /**
     * Start requesting the review info that will be needed later in advance.
     */
    @MainThread
    fun preWarmReview() {
        if (!alreadyAskedForReview && reviewInfo == null) {
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
    fun launchReview(activity: Activity, callback: () -> Unit) {
        viewModelScope.launch {
            val reviewInfo = obtainReviewInfo()
            withContext(viewModelScope.coroutineContext) {
                reviewInfo?.let {
                    reviewManager.launchReview(activity, it)
                }
            }
            callback()
        }
    }

    /**
     * The view should call this to let the ViewModel know that an attempt to show the review dialog
     * was made.
     *
     * A real app could record the time when this request was made to implement a back-off strategy.
     *
     * @see alreadyAskedForReview
     */
    fun notifyAskedForReview() {
        alreadyAskedForReview = true
    }
}

class ReviewViewModelProviderFactory(
        private val manager: ReviewManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ReviewManager::class.java).newInstance(manager)
    }
}
