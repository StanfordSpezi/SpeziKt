package edu.stanford.spezi.module.account.firebase.account.models

data class ReauthenticationOperation(
    val result: Result,
    val credential: Any? = null,
) {
    enum class Result {
        SUCCESS, CANCELLED
    }

    companion object {
        val cancelled = ReauthenticationOperation(result = Result.CANCELLED)
        val success = ReauthenticationOperation(result = Result.SUCCESS)
        fun success(credential: Any?) = ReauthenticationOperation(result = Result.SUCCESS, credential = credential)
    }
}
