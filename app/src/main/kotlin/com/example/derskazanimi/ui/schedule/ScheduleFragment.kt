package com.example.derskazanimi.ui.schedule

import android.os.Bundle
import android.view.View
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.derskazanimi.R
import com.example.derskazanimi.data.AppDatabase
import com.example.derskazanimi.data.entity.TimeSlot
import com.example.derskazanimi.data.repository.DersRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ScheduleFragment : Fragment(R.layout.fragment_schedule) {

    private lateinit var repository: DersRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var daySpinner: Spinner
    private lateinit var dutyDatesRecyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter

    // Calendar.DAY_OF_WEEK convention: 1=Pazar .. 7=Cumartesi
    private val dayNames = mapOf(
        2 to "Pazartesi",
        3 to "Salı",
        4 to "Çarşamba",
        5 to "Perşembe",
        6 to "Cuma",
        7 to "Cumartesi"
    )

    private val spinnerDays = listOf(2, 3, 4, 5, 6, 7) // "Tüm Hafta" position 0'da ayrı tutulur

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        repository = DersRepository(database)

        recyclerView = view.findViewById(R.id.scheduleRecyclerView)
        daySpinner = view.findViewById(R.id.daySpinner)
        dutyDatesRecyclerView = view.findViewById(R.id.dutyDatesRecyclerView)

        setupSpinner()
        setupRecyclerView()
        loadDutyDates()
    }

    private fun loadDutyDates() {
        dutyDatesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val allDuties = repository.getAllDutyDates().first()
                val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                val upcoming = allDuties.filter { it.date >= todayDate }

                val displayItems = upcoming.map { duty ->
                    "${formatTurkishDate(duty.date)} - ${duty.note}"
                }

                dutyDatesRecyclerView.adapter = DutyDateAdapter(displayItems)
                dutyDatesRecyclerView.visibility = if (displayItems.isEmpty()) View.GONE else View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun formatTurkishDate(isoDate: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("d MMMM yyyy", Locale("tr"))
            val date = parser.parse(isoDate)
            if (date != null) formatter.format(date) else isoDate
        } catch (e: Exception) {
            isoDate
        }
    }

    private fun setupSpinner() {
        val spinnerItems = listOf("Tüm Hafta") + spinnerDays.map { dayNames[it] ?: "Bilinmeyen Gün" }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerItems
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = adapter

        // Bugünü varsayılan seçili gün yap (Pazar/Cumartesi'de "Tüm Hafta" kalır)
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val defaultPosition = spinnerDays.indexOf(currentDay)
        if (defaultPosition >= 0) {
            daySpinner.setSelection(defaultPosition + 1)
        }

        daySpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    loadWholeWeekSchedule()
                } else {
                    loadScheduleForDay(spinnerDays[position - 1])
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        scheduleAdapter = ScheduleAdapter(emptyList())
        recyclerView.adapter = scheduleAdapter
    }

    private fun minutesBetween(start: String, end: String): Int {
        return try {
            val (sh, sm) = start.split(":").map { it.toInt() }
            val (eh, em) = end.split(":").map { it.toInt() }
            (eh * 60 + em) - (sh * 60 + sm)
        } catch (e: Exception) {
            0
        }
    }

    private fun buildDayItems(
        dayLabel: String,
        timeSlots: List<TimeSlot>,
        entriesForDay: List<com.example.derskazanimi.data.entity.ScheduleEntry>
    ): List<ScheduleItemDisplay> {
        val items = mutableListOf<ScheduleItemDisplay>()
        val sortedSlots = timeSlots.sortedBy { it.startTime }
        var previousEndTime: String? = null

        for (slot in sortedSlots) {
            if (previousEndTime != null && previousEndTime != slot.startTime) {
                val gap = minutesBetween(previousEndTime, slot.startTime)
                if (gap >= 30) {
                    items.add(
                        ScheduleItemDisplay(
                            dayName = dayLabel,
                            className = "Öğle Arası",
                            timeSlot = "$previousEndTime - ${slot.startTime}"
                        )
                    )
                }
            }

            val entry = entriesForDay.find { it.timeSlotId == slot.id }
            items.add(
                ScheduleItemDisplay(
                    dayName = dayLabel,
                    className = entry?.className ?: "Boş",
                    timeSlot = "${slot.startTime} - ${slot.endTime}"
                )
            )
            previousEndTime = slot.endTime
        }

        return items
    }

    private fun loadScheduleForDay(dayOfWeek: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                repository.getScheduleByDay(dayOfWeek).collectLatest { scheduleEntries ->
                    repository.getAllTimeSlots().collectLatest { timeSlots ->
                        val dayLabel = dayNames[dayOfWeek] ?: "Bilinmeyen Gün"
                        val items = buildDayItems(dayLabel, timeSlots, scheduleEntries)
                        scheduleAdapter = ScheduleAdapter(items)
                        recyclerView.adapter = scheduleAdapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadWholeWeekSchedule() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                repository.getAllScheduleEntries().collectLatest { scheduleEntries ->
                    repository.getAllTimeSlots().collectLatest { timeSlots ->
                        val items = mutableListOf<ScheduleItemDisplay>()
                        for (day in spinnerDays) {
                            val dayLabel = dayNames[day] ?: "Bilinmeyen Gün"
                            val entriesForDay = scheduleEntries.filter { it.dayOfWeek == day }
                            items.addAll(buildDayItems(dayLabel, timeSlots, entriesForDay))
                        }
                        scheduleAdapter = ScheduleAdapter(items)
                        recyclerView.adapter = scheduleAdapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
