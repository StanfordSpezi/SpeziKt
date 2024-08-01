package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.WeightRecord
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HealthRecordModule {

    @Provides
    fun provideBloodPressureRecordClass(): Class<BloodPressureRecord> {
        return BloodPressureRecord::class.java
    }

    @Provides
    fun provideWeightRecordClass(): Class<WeightRecord> {
        return WeightRecord::class.java
    }

    @Provides
    fun provideHeartRateRecordClass(): Class<HeartRateRecord> {
        return HeartRateRecord::class.java
    }
}
