package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid_android.CLAIDServices.ServiceAnnotation
import adamma.c4dhi.claid_android.Configuration.CLAIDPersistanceConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig

class BackgroundConfig(
    private var persistanceConfig: CLAIDPersistanceConfig = CLAIDPersistanceConfig.minimumPersistance(),
    private var specialPermissions: CLAIDSpecialPermissionsConfig = CLAIDSpecialPermissionsConfig.regularConfig(),
    private var serviceAnnotation: ServiceAnnotation = ServiceAnnotation.defaultAnnotation()
): OperatingModeConfig(specialPermissions) {

    fun getPersistanceConfig(): CLAIDPersistanceConfig {
        return this.persistanceConfig
    }

    fun getServiceAnnotation(): ServiceAnnotation {
        return this.serviceAnnotation
    }

}