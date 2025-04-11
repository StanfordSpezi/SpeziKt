package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig

class ForegroundConfig(
    private var specialPermissions: CLAIDSpecialPermissionsConfig = CLAIDSpecialPermissionsConfig.regularConfig()
): OperatingModeConfig(specialPermissions) {


}