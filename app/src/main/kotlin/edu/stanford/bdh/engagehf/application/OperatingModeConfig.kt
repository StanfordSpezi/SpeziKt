package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig

abstract class OperatingModeConfig(
    private var specialPermissions: CLAIDSpecialPermissionsConfig
) {
    fun getSpecialPermissionsConfig(): CLAIDSpecialPermissionsConfig {
        return this.specialPermissions
    }
}