package edu.stanford.bdh.engagehf.new_onboarding


open class OnboardingView(private var next: OnboardingView? = null) {

    fun hasNext(): Boolean {
        return next != null
    }

    fun getNext(): OnboardingView? {
        return next
    }

    fun setNext(next: OnboardingView) {
        this.next = next
    }
}