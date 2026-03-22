package sk.sksv.addrecyclerview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val barcodeStringBuilder = StringBuilder()
    private val barcodeHandler = Handler(Looper.getMainLooper())
    private var barcodeRunnable: Runnable? = null

    // Bluebird specific intent actions and extras
    private val ACTION_BLUEBIRD_DECODING = "kr.co.bluebird.android.bbapi.action.BARCODE_CALLBACK_DECODING_DATA"
    private val ACTION_BLUEBIRD_LEGACY = "com.bluebird.barcode.action.SCANNER_CALLBACK"
    private val ACTION_BLUEBIRD_SCANNER = "com.bluebird.scanner.ACTION_BARCODE_CALLBACK"
    
    private val EXTRA_BARCODE_DATA_BB = "EXTRA_BARCODE_DECODING_DATA"
    private val EXTRA_BARCODE_DATA_LEGACY = "barcode"
    private val EXTRA_BARCODE_DATA_GENERIC = "data"

    private val scannerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var barcode: String? = null
            when (intent?.action) {
                ACTION_BLUEBIRD_DECODING -> {
                    val data = intent.getByteArrayExtra(EXTRA_BARCODE_DATA_BB)
                    barcode = if (data != null) String(data) else intent.getStringExtra(EXTRA_BARCODE_DATA_BB)
                }
                ACTION_BLUEBIRD_LEGACY -> {
                    barcode = intent.getStringExtra(EXTRA_BARCODE_DATA_LEGACY) ?: intent.getStringExtra(EXTRA_BARCODE_DATA_GENERIC)
                }
                ACTION_BLUEBIRD_SCANNER -> {
                    barcode = intent.getStringExtra(EXTRA_BARCODE_DATA_GENERIC) ?: intent.getStringExtra(EXTRA_BARCODE_DATA_LEGACY)
                }
            }

            if (barcode != null) {
                val scannedId = barcode.trim()
                if (scannedId.isNotEmpty()) {
                    handleScannedId(scannedId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = StudentAdapter(DemoData.students)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(ACTION_BLUEBIRD_DECODING)
            addAction(ACTION_BLUEBIRD_LEGACY)
            addAction(ACTION_BLUEBIRD_SCANNER)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(scannerReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(scannerReceiver, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(scannerReceiver)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // Fallback for Keyboard Wedge if Intent is not used
        if (event.action == KeyEvent.ACTION_DOWN) {
            val pressedKey = event.unicodeChar.toChar()

            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                barcodeRunnable?.let { barcodeHandler.removeCallbacks(it) }
                processBarcode()
                return true
            } else if (pressedKey > '\u0000') {
                barcodeStringBuilder.append(pressedKey)
                barcodeRunnable?.let { barcodeHandler.removeCallbacks(it) }
                
                barcodeRunnable = Runnable { processBarcode() }
                barcodeHandler.postDelayed(barcodeRunnable!!, 500)
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun processBarcode() {
        val scannedId = barcodeStringBuilder.toString().trim()
        if (scannedId.isNotEmpty()) {
            handleScannedId(scannedId)
        }
        barcodeStringBuilder.clear()
    }

    private fun handleScannedId(scannedId: String) {
        val matchedStudents = DemoData.students.filter { it.id.equals(scannedId, ignoreCase = true) }
        
        if (matchedStudents.size > 1) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_multiple_students, null)
            val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvStudentDialog)
            
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = SelectableStudentAdapter(matchedStudents) { selectedStudent ->
                // The adapter itself handles the green highlight selection.
            }

            AlertDialog.Builder(this)
                .setTitle("Multiple Students Found")
                .setView(dialogView)
                .setPositiveButton("Done") { dialog, _ -> dialog.dismiss() }
                .show()
        } else if (matchedStudents.size == 1) {
            val student = matchedStudents.first()
            AlertDialog.Builder(this)
                .setTitle("Student Found")
                .setMessage("ID: ${student.id}\nClass: ${student.studentClass}\nSubject: ${student.subject}\nTeacher: ${student.teacher}")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("QR Result")
                .setMessage("Result: $scannedId")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}