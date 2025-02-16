package edu.stanford.bdh.heartbeat.app.fake

import java.util.concurrent.TimeUnit
import kotlin.random.Random

interface FakeComponent {
    suspend fun delay() {
        kotlinx.coroutines.delay(timeMillis = Random.nextLong(TimeUnit.SECONDS.toMillis(FakeConfigs.MAX_DELAY_SECONDS)))
    }
}