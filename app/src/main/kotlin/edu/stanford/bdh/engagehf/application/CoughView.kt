package edu.stanford.bdh.engagehf.application

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.claid.cough_detection.CoughSampleVector
import edu.stanford.bdh.engagehf.application.modules.datastore.LocalStorage
import edu.stanford.healthconnectonfhir.Loinc
import io.mockk.InternalPlatformDsl.toStr
import kotlinx.coroutines.delay

class CoughView {

    private val storage by OptionalDependency<LocalStorage>()

    @Composable
    fun Render() {
        val storage = storage
        if (storage == null) {
            return
        } else {

            var coughCount by remember { mutableStateOf(0) }

            LaunchedEffect(Unit) {
                while (true) {
                    val coughs: CoughSampleVector = storage.read(Loinc.COUGH.toString())
                    coughCount = coughs.coughSamplesCount
                    delay(1000)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Cough Count", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("$coughCount", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
