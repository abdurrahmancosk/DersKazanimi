package com.example.derskazanimi.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.derskazanimi.R
import com.example.derskazanimi.data.AppDatabase
import com.example.derskazanimi.data.repository.DersRepository
import com.example.derskazanimi.utils.AlarmScheduler
import com.example.derskazanimi.utils.ExcelImporter
import com.example.derskazanimi.utils.ImportResult
import com.example.derskazanimi.utils.ScheduleInitializer
import kotlinx.coroutines.launch
import java.io.File

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var repository: DersRepository
    private lateinit var selectExcelButton: Button
    private lateinit var alarmToggleSwitch: Switch
    private lateinit var statusTextView: TextView
    private lateinit var selectScheduleFileButton: Button
    private lateinit var viewScheduleFileButton: Button
    private lateinit var scheduleFileStatusText: TextView
    private val PICK_EXCEL_FILE = 1001
    private val PICK_SCHEDULE_FILE = 1002
    private val PREFS_NAME = "ders_kazanimi_prefs"
    private val KEY_SCHEDULE_FILE_NAME = "schedule_ref_file_name"
    private val KEY_SCHEDULE_FILE_MIME = "schedule_ref_file_mime"

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        repository = DersRepository(database)

        selectExcelButton = view.findViewById(R.id.selectExcelButton)
        alarmToggleSwitch = view.findViewById(R.id.alarmToggleSwitch)
        statusTextView = view.findViewById(R.id.statusTextView)
        selectScheduleFileButton = view.findViewById(R.id.selectScheduleFileButton)
        viewScheduleFileButton = view.findViewById(R.id.viewScheduleFileButton)
        scheduleFileStatusText = view.findViewById(R.id.scheduleFileStatusText)

        // Initialize TimeSlots and Schedule
        initializeScheduleData()

        // Setup listeners
        selectExcelButton.setOnClickListener {
            openFilePicker()
        }

        selectScheduleFileButton.setOnClickListener {
            openScheduleFilePicker()
        }

        viewScheduleFileButton.setOnClickListener {
            openSavedScheduleFile()
        }

        alarmToggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AlarmScheduler.scheduleAlarmNotifications(requireContext())
                statusTextView.text = "Bildirim sistemi aktif"
            } else {
                AlarmScheduler.cancelAlarmNotifications(requireContext())
                statusTextView.text = "Bildirim sistemi kapalı"
            }
        }

        // Check if alarm is already scheduled
        alarmToggleSwitch.isChecked = AlarmScheduler.isAlarmScheduled(requireContext())

        updateScheduleFileStatus()
    }

    private fun scheduleRefFile(): File = File(requireContext().filesDir, "ders_programi_ref")

    private fun updateScheduleFileStatus() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        val fileName = prefs.getString(KEY_SCHEDULE_FILE_NAME, null)
        if (fileName != null && scheduleRefFile().exists()) {
            scheduleFileStatusText.text = "Yüklü dosya: $fileName"
            viewScheduleFileButton.isEnabled = true
        } else {
            scheduleFileStatusText.text = "Henüz dosya yüklenmedi"
            viewScheduleFileButton.isEnabled = false
        }
    }

    private fun openScheduleFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf"))
        }
        startActivityForResult(Intent.createChooser(intent, "Ders programı resmi veya PDF seç"), PICK_SCHEDULE_FILE)
    }

    private fun saveScheduleFile(fileUri: Uri) {
        try {
            val contentResolver = requireContext().contentResolver
            val mimeType = contentResolver.getType(fileUri) ?: "application/octet-stream"

            var displayName = "ders_programi"
            contentResolver.query(fileUri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    displayName = cursor.getString(nameIndex) ?: displayName
                }
            }

            contentResolver.openInputStream(fileUri)?.use { input ->
                scheduleRefFile().outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val prefs = requireContext().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
            prefs.edit()
                .putString(KEY_SCHEDULE_FILE_NAME, displayName)
                .putString(KEY_SCHEDULE_FILE_MIME, mimeType)
                .apply()

            updateScheduleFileStatus()
            Toast.makeText(requireContext(), "Ders programı dosyası kaydedildi: $displayName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Dosya kaydedilemedi: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun openSavedScheduleFile() {
        val file = scheduleRefFile()
        if (!file.exists()) {
            Toast.makeText(requireContext(), "Önce bir dosya yükleyin", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = requireContext().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        val mimeType = prefs.getString(KEY_SCHEDULE_FILE_MIME, "*/*") ?: "*/*"

        try {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(Intent.createChooser(intent, "Dosyayı aç"))
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Dosya açılamadı: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeScheduleData() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Insert time slots
                val existingSlots = repository.getAllTimeSlots()
                existingSlots.collect { slots ->
                    if (slots.isEmpty()) {
                        ScheduleInitializer.getTimeSlots().forEach {
                            repository.insertTimeSlot(it)
                        }
                    }
                }

                // Insert schedule entries
                val existingSchedule = repository.getAllScheduleEntries()
                existingSchedule.collect { schedule ->
                    if (schedule.isEmpty()) {
                        ScheduleInitializer.getScheduleEntries().forEach {
                            repository.insertScheduleEntry(it)
                        }
                    }
                }

                statusTextView.text = "Ders programı yüklendi"
            } catch (e: Exception) {
                statusTextView.text = "Hata: ${e.message}"
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        }
        startActivityForResult(Intent.createChooser(intent, "Excel dosyası seç"), PICK_EXCEL_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_EXCEL_FILE && resultCode == Activity.RESULT_OK) {
            val fileUri: Uri? = data?.data
            fileUri?.let {
                importExcelFile(it)
            }
        } else if (requestCode == PICK_SCHEDULE_FILE && resultCode == Activity.RESULT_OK) {
            val fileUri: Uri? = data?.data
            fileUri?.let {
                saveScheduleFile(it)
            }
        }
    }

    private fun importExcelFile(fileUri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                statusTextView.text = "Excel dosyası yükleniyor..."

                val excelImporter = ExcelImporter()
                val result = excelImporter.importExcel(requireContext(), fileUri)

                when (result) {
                    is ImportResult.Success -> {
                        try {
                            // Save weekly plans and learning outcomes to database
                            result.weeklyPlans.forEach { weeklyPlan ->
                                repository.insertWeeklyPlan(weeklyPlan)
                            }
                            result.learningOutcomes.forEach { outcome ->
                                repository.insertLearningOutcome(outcome)
                            }

                            var statusMessage = "Excel başarıyla yüklendi! ${result.learningOutcomes.size} kazanım eklendi"
                            if (result.warnings.isNotEmpty()) {
                                statusMessage += "\n⚠ ${result.warnings.size} uyarı"
                            }
                            statusTextView.text = statusMessage
                            Toast.makeText(
                                requireContext(),
                                "Excel dosyası başarıyla yüklendi",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            statusTextView.text = "Veritabanı kayıt hatası: ${e.message}"
                            Toast.makeText(
                                requireContext(),
                                "Veritabanı kayıt hatası: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is ImportResult.Error -> {
                        statusTextView.text = "Hata: ${result.message}"
                        Toast.makeText(
                            requireContext(),
                            "Hata: ${result.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                statusTextView.text = "Hata: ${e.message}"
                Toast.makeText(
                    requireContext(),
                    "Hata: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
