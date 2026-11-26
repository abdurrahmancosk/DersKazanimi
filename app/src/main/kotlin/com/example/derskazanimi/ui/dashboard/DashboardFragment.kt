package com.example.derskazanimi.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.derskazanimi.R
import com.example.derskazanimi.data.AppDatabase
import com.example.derskazanimi.data.entity.ScheduleEntry
import com.example.derskazanimi.data.entity.TimeSlot
import com.example.derskazanimi.data.repository.DersRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
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

    private val dayNames = mapOf(
        1 to "Pazar",
        2 to "Pazartesi",
        3 to "Salı",
        4 to "Çarşamba",
        5 to "Perşembe",
        6 to "Cuma",
        7 to "Cumartesi"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        repository = DersRepository(database)

        currentClassTextView = view.findViewById(R.id.currentClassText)
        currentOutcomeTextView = view.findViewById(R.id.currentOutcomeText)
        timeStatusTextView = view.findViewById(R.id.timeStatusText)
        timeDisplayTextView = view.findViewById(R.id.timeDisplayText)

        startTimeCheck()
    }

    private fun startTimeCheck() {
        viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                try {
                    updateLessonInfo()
                } catch (e: Exception) {
                    e.printStackTrace()
                    currentOutcomeTextView.text = "Hata: ${e.message}"
                }
                delay(60000)
            }
        }
    }

    private suspend fun updateLessonInfo() {
        val calendar = Calendar.getInstance()
        timeDisplayTextView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.time)

        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Pazar..7=Cumartesi
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

        val timeSlots = repository.getAllTimeSlots().first()
        val allEntries = repository.getAllScheduleEntries().first()

        if (timeSlots.isEmpty()) {
            timeStatusTextView.text = "Ders programı yükleniyor..."
            currentClassTextView.text = ""
            currentOutcomeTextView.text = ""
            return
        }

        val slotsById = timeSlots.associateBy { it.id }

        // Şu an aktif bir ders var mı?
        val activeSlot = timeSlots.firstOrNull { isTimeBetween(currentTime, it.startTime, it.endTime) }
        val activeEntry = activeSlot?.let { slot ->
            allEntries.firstOrNull { it.dayOfWeek == currentDayOfWeek && it.timeSlotId == slot.id }
        }

        // Sıradaki dersi bul: bugün kalan saatlerden başlayıp, gerekirse sonraki günlere bak
        val next = findNextLesson(currentDayOfWeek, currentTime, allEntries, slotsById)

        if (activeEntry != null && activeSlot != null) {
            timeStatusTextView.text = "Ders Saati Aktif ✓"
            currentClassTextView.text = "Şu anki ders: ${activeEntry.className} (${activeSlot.startTime}-${activeSlot.endTime})"
        } else {
            timeStatusTextView.text = "Tenefüs / Ders Saati Dışı"
            currentClassTextView.text = ""
        }

        if (next != null) {
            val (entry, slot, dayOffset) = next
            val dayLabel = if (dayOffset == 0) "Bugün" else dayNames[entry.dayOfWeek] ?: "Bilinmeyen Gün"
            val nextLine = "Sonraki Ders: $dayLabel ${slot.startTime} - Sınıf ${entry.className}"

            currentClassTextView.text = if (currentClassTextView.text.isNullOrBlank()) {
                nextLine
            } else {
                "${currentClassTextView.text}\n$nextLine"
            }

            val weekDate = if (dayOffset == 0) {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            } else {
                val futureCalendar = calendar.clone() as Calendar
                futureCalendar.add(Calendar.DAY_OF_YEAR, dayOffset)
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(futureCalendar.time)
            }

            val weeklyPlan = repository.getWeeklyPlanForDate(weekDate).first()
            if (weeklyPlan != null) {
                val outcomes = repository.getLearningOutcomeByClassAndWeek(entry.className, weeklyPlan.id).first()
                currentOutcomeTextView.text = if (outcomes.isNotEmpty()) {
                    "Deftere yazılacak kazanım (${entry.className}):\n• " +
                        outcomes.map { it.kazanim }.joinToString("\n• ")
                } else {
                    "Bu hafta için ${entry.className} sınıfına ait kazanım bulunamadı.\nAyarlar sekmesinden Excel yıllık planını yükleyin."
                }
            } else {
                currentOutcomeTextView.text = "Bu haftaya ait plan bulunamadı.\nAyarlar sekmesinden Excel yıllık planını yükleyin."
            }
        } else {
            if (currentClassTextView.text.isNullOrBlank()) {
                currentClassTextView.text = "Önümüzdeki günlerde planlı ders bulunamadı"
            }
            currentOutcomeTextView.text = "Kazanım bilgisi yok"
        }
    }

    /**
     * Bugünden başlayarak (saat filtresiyle), sonraki 7 gün içinde kronolojik olarak
     * ilk karşılaşılan ders kaydını döner: (ScheduleEntry, TimeSlot, dayOffset)
     */
    private fun findNextLesson(
        currentDayOfWeek: Int,
        currentTime: String,
        allEntries: List<ScheduleEntry>,
        slotsById: Map<Int, TimeSlot>
    ): Triple<ScheduleEntry, TimeSlot, Int>? {
        for (offset in 0..6) {
            val dayToCheck = ((currentDayOfWeek - 1 + offset) % 7) + 1

            val candidates = allEntries
                .filter { it.dayOfWeek == dayToCheck }
                .mapNotNull { entry -> slotsById[entry.timeSlotId]?.let { entry to it } }
                .filter { (_, slot) -> offset > 0 || slot.startTime > currentTime }
                .sortedBy { (_, slot) -> slot.startTime }

            if (candidates.isNotEmpty()) {
                val (entry, slot) = candidates.first()
                return Triple(entry, slot, offset)
            }
        }
        return null
    }

    private fun isTimeBetween(current: String, start: String, end: String): Boolean {
        return current >= start && current < end
    }
}
