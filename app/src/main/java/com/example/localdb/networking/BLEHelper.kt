package com.example.localdb.networking

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat

class BLEHelper(private val context: Context) {

    @Suppress("Deprecation")
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val scanHandler = Handler(Looper.getMainLooper())
    private val scanDuration: Long = 10000 // Scan for 10 seconds

    fun startScanning(scanCallback: ScanCallback) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(context, "Bluetooth devre dışı, lütfen etkinleştirin", Toast.LENGTH_SHORT).show()
            return
        }

        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "Cihaz BLE desteklemiyor", Toast.LENGTH_SHORT).show()
            return
        }

        // İzin kontrolü
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Bluetooth tarama izni verilmemiş", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            bluetoothLeScanner?.startScan(scanCallback)
            Toast.makeText(context, "BLE taraması başlatıldı", Toast.LENGTH_SHORT).show()

            // Belirtilen süre sonunda taramayı durdur
            scanHandler.postDelayed({
                bluetoothLeScanner?.stopScan(scanCallback)
                Toast.makeText(context, "BLE taraması durduruldu", Toast.LENGTH_SHORT).show()
            }, scanDuration)
        } catch (e: SecurityException) {
            Toast.makeText(context, "BLE taraması sırasında bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopScanning(scanCallback: ScanCallback) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(context, "Bluetooth devre dışı, lütfen etkinleştirin", Toast.LENGTH_SHORT).show()
            return
        }

        // İzin kontrolü
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Bluetooth tarama izni verilmemiş", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
            bluetoothLeScanner?.stopScan(scanCallback)
            Toast.makeText(context, "BLE taraması durduruldu", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(context, "BLE taramasını durdururken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val exampleScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context, "Bluetooth bağlantı izni verilmemiş", Toast.LENGTH_SHORT).show()
                return
            }
            val device: BluetoothDevice = result.device
            Toast.makeText(context, "Cihaz bulundu: ${device.name ?: "Bilinmeyen cihaz"}", Toast.LENGTH_SHORT).show()
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            for (result in results) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(context, "Bluetooth bağlantı izni verilmemiş", Toast.LENGTH_SHORT).show()
                    continue
                }

                val device: BluetoothDevice = result.device
                Toast.makeText(context, "Cihaz bulundu: ${device.name ?: "Bilinmeyen cihaz"}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Toast.makeText(context, "BLE taraması başarısız: $errorCode", Toast.LENGTH_SHORT).show()
        }
    }
}
