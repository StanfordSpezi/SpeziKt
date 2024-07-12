package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.bluetooth.component.OperationStatus

data class VitalDisplayData(
    val title: String,
    val value: String? = null,
    val unit: String? = null,
    val date: String? = null,
    val status: OperationStatus = OperationStatus.PENDING,
    val error: String? = null,
)
