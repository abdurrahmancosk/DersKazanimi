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

    // Yavaş döngüde (veritabanından) hesaplanan ve hızlı döngünün kullandığı durum
    private var countdownTargetMillis: Long? = null
    private var countdownLabel: String = ""
    private var baseStatusText: String = ""
    private var baseClassText: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        repository = DersRepository(database)

        currentClassTextView = view.findViewById(R.id.currentClassText)
        currentOutcomeTextView = view.findViewById(R.id.currentOutcomeText)
        timeStatusTextView = view.findViewById(R.id.timeStatusText)
        timeDisplayTextView = view.findViewById(R.id.timeDisplayText)

        startClockTick()
        startLessonInfoRefresh()
    }

    /** Her saniye çalışır: saat ve geri sayımı günceller (DB'ye gitmez). */
    private fun startClockTick() {
        viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                try {
                    val now = Calendar.getInstance()
                    timeDisplayTextView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now.time)
                    updateCountdownText()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(1000)
            }
        }
    }

    /** 30 saniyede bir çalışır: veritabanından ders/kazanım bilgisini tazeler. */
    private fun startLessonInfoRefresh() {
        viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                try {
                    refreshLessonInfo()
                } catch (e: Exception) {
                    e.printStackTrace()
                    currentOutcomeTextView.text = "Hata: ${e.message}"
                }
                delay(30000)
            }
        }
    }

    private fun updateCountdownText() {
        val target = countdownTargetMillis
        if (target == null) {
            timeStatusTextView.text = baseStatusText
            return
        }
        val remainingSeconds = ((target - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
        val h = remainingSeconds / 3600
        val m = (remainingSeconds % 3600) / 60
        val s = remainingSeconds % 60
        val countdownFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
        timeStatusTextView.text = "$baseStatusText\n$countdownLabel: $countdownFormatted"
    }

    private suspend fun refreshLessonInfo() {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Pazar..7=Cumartesi
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

        val timeSlots = repository.getAllTimeSlots().first()
        val allEntries = repository.getAllScheduleEntries().first()

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val todaysDuty = repository.getDutyDateForDate(todayDate).first()
        val dutyBanner = if (todaysDuty != null) "📌 Bugün ${todaysDuty.note} var!\n" else ""

        if (timeSlots.isEmpty()) {
            baseStatusText = "Ders programı yükleniyor..."
            baseClassText = ""
            countdownTargetMillis = null
            currentClassTextView.text = baseClassText
            currentOutcomeTextView.text = ""
            return
        }

        val slotsById = timeSlots.associateBy { it.id }

        val activeSlot = timeSlots.firstOrNull { isTimeBetween(currentTime, it.startTime, it.endTime) }
        val activeEntry = activeSlot?.let { slot ->
            allEntries.firstOrNull { it.dayOfWeek == currentDayOfWeek && it.timeSlotId == slot.id }
        }

        val next = findNextLesson(currentDayOfWeek, currentTime, allEntries, slotsById)

        if (activeEntry != null && activeSlot != null) {
            baseStatusText = "Ders Saati Aktif ✓"
            baseClassText = "Şu anki ders: ${activeEntry.className} (${activeSlot.startTime}-${activeSlot.endTime})"
            countdownLabel = "Derse kalan bitiş süresi"
            countdownTargetMillis = buildTargetMillis(calendar, 0, activeSlot.endTime)
        } else {
            baseStatusText = "Tenefüs / Ders Saati Dışı"
            baseClassText = ""
            countdownTargetMillis = null
        }

        if (next != null) {
            val (entry, slot, dayOffset) = next
            val dayLabel = if (dayOffset == 0) "Bugün" else dayNames[entry.dayOfWeek] ?: "Bilinmeyen Gün"
            val nextLine = "Sonraki Ders: $dayLabel ${slot.startTime} - Sınıf ${entry.className}"

            baseClassText = if (baseClassText.isBlank()) nextLine else "$baseClassText\n$nextLine"

            if (activeEntry == null) {
                countdownLabel = "Derse başlamasına kalan süre"
                countdownTargetMillis = buildTargetMillis(calendar, dayOffset, slot.startTime)
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
                    "Bu hafta için ${entry.className} sınıfına ait kazanım bulunamadı."
                }
            } else {
                currentOutcomeTextView.text = "Bu haftaya ait plan bulunamadı."
            }
        } else {
            if (baseClassText.isBlank()) {
                baseClassText = "Önümüzdeki günlerde planlı ders bulunamadı"
            }
            currentOutcomeTextView.text = "Kazanım bilgisi yok"
        }

        currentClassTextView.text = dutyBanner + baseClassText
        updateCountdownText()
    }

    /** Verilen (bugünden itibaren dayOffset gün sonraki) "HH:mm" saatinin epoch milis karşılığını döner. */
    private fun buildTargetMillis(baseCalendar: Calendar, dayOffset: Int, time: String): Long {
        val parts = time.split(":")
        val target = baseCalendar.clone() as Calendar
        target.add(Calendar.DAY_OF_YEAR, dayOffset)
        target.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
        target.set(Calendar.MINUTE, parts[1].toInt())
        target.set(Calendar.SECOND, 0)
        target.set(Calendar.MILLISECOND, 0)
        return target.timeInMillis
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
