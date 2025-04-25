package edu.stanford.bdh.engagehf.bluetooth.data.models

import edu.stanford.bdh.engagehf.bluetooth.component.OperationStatus
import edu.stanford.spezi.ui.StringResource

data class VitalDisplayData(
    val title: StringResource,
    val value: String? = null,
    val unit: String? = null,
    val date: String? = null,
    val status: OperationStatus = OperationStatus.PENDING,
    val error: String? = null,
)
