package edu.stanford.spezikt

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import edu.stanford.spezikt.core.bluetooth.BluetoothViewModel
import edu.stanford.spezikt.core.bluetooth.screen.BluetoothScreen
import edu.stanford.spezikt.core.designsystem.theme.SpeziKtTheme

class BluetoothActivity : ComponentActivity() {
    private lateinit var viewModel: BluetoothViewModel

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[BluetoothViewModel::class.java]

        setContent {
            SpeziKtTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BluetoothScreen(viewModel)
                }
            }
            viewModel.start(LocalContext.current, this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.bluetoothScanner?.stopScan()
        viewModel.deviceConnector?.close()
        viewModel.connectedDevices.value = emptyList()
    }
}