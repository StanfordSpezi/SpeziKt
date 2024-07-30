package edu.stanford.bdh.engagehf.health.bloodpressure

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BloodPressureViewModel @Inject internal constructor() : ViewModel() {
    init {
        println()
    }
}
