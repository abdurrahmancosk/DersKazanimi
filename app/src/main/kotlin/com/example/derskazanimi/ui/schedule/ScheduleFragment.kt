package com.example.derskazanimi.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.derskazanimi.R
import com.example.derskazanimi.data.AppDatabase
import com.example.derskazanimi.data.repository.DersRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleFragment : Fragment(R.layout.fragment_schedule) {

    private lateinit var repository: DersRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var daySpinner: Spinner
    private lateinit var scheduleAdapter: ScheduleAdapter

    private val dayNames = mapOf(
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

        recyclerView = view.findViewById(R.id.scheduleRecyclerView)
        daySpinner = view.findViewById(R.id.daySpinner)

        setupSpinner()
        setupRecyclerView()
    }

    private fun setupSpinner() {
        val spinnerItems = listOf(
            "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerItems
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = adapter

        // Set current day as default
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
        if (currentDay in 2..7) {
            daySpinner.setSelection(currentDay - 2)
        }

        daySpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDay = position + 2 // Convert to 2-7 range
                loadScheduleForDay(selectedDay)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        scheduleAdapter = ScheduleAdapter(emptyList())
        recyclerView.adapter = scheduleAdapter
    }

    private fun loadScheduleForDay(dayOfWeek: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                repository.getScheduleByDay(dayOfWeek).collectLatest { scheduleEntries ->
                    repository.getAllTimeSlots().collectLatest { timeSlots ->
                        val items = mutableListOf<ScheduleItemDisplay>()

                        for (entry in scheduleEntries) {
                            val timeSlot = timeSlots.find { it.id == entry.timeSlotId }
                            if (timeSlot != null) {
                                items.add(
                                    ScheduleItemDisplay(
                                        dayName = dayNames[dayOfWeek] ?: "Bilinmeyen Gün",
                                        className = entry.className,
                                        timeSlot = "${timeSlot.startTime} - ${timeSlot.endTime}"
                                    )
                                )
                            }
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
