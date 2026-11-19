package com.example.derskazanimi.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.derskazanimi.R
import com.example.derskazanimi.data.AppDatabase
import com.example.derskazanimi.data.repository.DersRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var repository: DersRepository
    private lateinit var currentClassTextView: TextView
    private lateinit var currentOutcomeTextView: TextView
    private lateinit var timeStatusTextView: TextView
    private lateinit var timeDisplayTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        repository = DersRepository(database)

        // Initialize views
        currentClassTextView = view.findViewById(R.id.currentClassText)
        currentOutcomeTextView = view.findViewById(R.id.currentOutcomeText)
        timeStatusTextView = view.findViewById(R.id.timeStatusText)
        timeDisplayTextView = view.findViewById(R.id.timeDisplayText)

        // Start checking current time and display lesson info
        startTimeCheck()
    }

    private fun startTimeCheck() {
        viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                try {
                    updateLessonInfo()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // Update every minute
                delay(60000)
            }
        }
    }

    private suspend fun updateLessonInfo() {
        try {
            val calendar = Calendar.getInstance()
            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            val currentWeekNumber = calendar.get(Calendar.WEEK_OF_YEAR)

            // Display current time
            timeDisplayTextView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.time)

            // Get all time slots
            repository.getAllTimeSlots().collectLatest { timeSlots ->
                // Find current time slot
                var currentTimeSlot: Int? = null
                var nextTimeSlot: Int? = null

                for (slot in timeSlots) {
                    if (isTimeBetween(currentTime, slot.startTime, slot.endTime)) {
                        currentTimeSlot = slot.id
                        break
                    } else if (isTimeAfter(currentTime, slot.endTime)) {
                        nextTimeSlot = slot.id
                    }
                }

                // Get schedule and learning outcomes
                if (currentTimeSlot != null) {
                    // During lesson time
                    repository.getScheduleByDayAndTimeSlot(currentDayOfWeek, currentTimeSlot)
                        .collectLatest { scheduleEntries ->
                            if (scheduleEntries.isNotEmpty()) {
                                val className = scheduleEntries[0].className
                                currentClassTextView.text = "Sınıf: $className"
                                timeStatusTextView.text = "Ders Saati Aktif ✓"

                                // Get learning outcomes for this class this week
                                repository.getLearningOutcomeByClassAndWeek(className, currentWeekNumber)
                                    .collectLatest { outcomes ->
                                        if (outcomes.isNotEmpty()) {
                                            val outcomeText = outcomes.take(3).map { it.kazanim }
                                                .joinToString("\n• ")
                                            currentOutcomeTextView.text = "• $outcomeText"
                                        } else {
                                            currentOutcomeTextView.text = "Bu hafta kazanımları yükleniyor..."
                                        }
                                    }
                            } else {
                                currentClassTextView.text = "Sınıf: Bilgi yok"
                                currentOutcomeTextView.text = "Kazanım: Bilgi yok"
                            }
                        }
                } else if (nextTimeSlot != null) {
                    // Break time - show next lesson
                    repository.getScheduleByDayAndTimeSlot(currentDayOfWeek, nextTimeSlot)
                        .collectLatest { scheduleEntries ->
                            if (scheduleEntries.isNotEmpty()) {
                                val nextClassName = scheduleEntries[0].className
                                currentClassTextView.text = "Sonraki Ders: $nextClassName"
                                currentOutcomeTextView.text = "Tenefüs süresi"
                                timeStatusTextView.text = "Tenefüs ⏸"
                            }
                        }
                } else {
                    currentClassTextView.text = "Ders Saati: Dışında"
                    currentOutcomeTextView.text = "Ders programı dışı zaman"
                    timeStatusTextView.text = "Ders Yok"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            currentOutcomeTextView.text = "Hata: ${e.message}"
        }
    }

    private fun isTimeBetween(current: String, start: String, end: String): Boolean {
        return current >= start && current < end
    }

    private fun isTimeAfter(current: String, time: String): Boolean {
        return current >= time
    }
}
