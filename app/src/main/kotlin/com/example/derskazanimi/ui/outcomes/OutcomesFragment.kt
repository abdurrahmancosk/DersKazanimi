package com.example.derskazanimi.ui.outcomes

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.derskazanimi.R
import com.example.derskazanimi.data.AppDatabase
import com.example.derskazanimi.data.repository.DersRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OutcomesFragment : Fragment(R.layout.fragment_outcomes) {

    private lateinit var repository: DersRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var classSpinner: Spinner

    private val classOptions = listOf("Tümü", "9-C", "11-A", "12-B", "HZ-A", "HZ-B", "HZ-C")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        repository = DersRepository(database)

        recyclerView = view.findViewById(R.id.outcomesRecyclerView)
        classSpinner = view.findViewById(R.id.outcomeClassSpinner)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = OutcomesAdapter(emptyList())

        setupSpinner()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            classOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        classSpinner.adapter = adapter

        classSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadOutcomes(if (position == 0) null else classOptions[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun loadOutcomes(className: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val weeklyPlans = repository.getAllWeeklyPlans().first()
                val allOutcomes = repository.getAllLearningOutcomes().first()
                val plansById = weeklyPlans.associateBy { it.id }

                val filtered = if (className == null) allOutcomes else allOutcomes.filter { it.className == className }

                val items = filtered
                    .mapNotNull { outcome ->
                        val plan = plansById[outcome.weeklyPlanId] ?: return@mapNotNull null
                        OutcomeItemDisplay(
                            dateRange = "${plan.startDate} / ${plan.endDate}",
                            className = outcome.className,
                            kazanim = outcome.kazanim
                        ) to plan.startDate
                    }
                    .sortedBy { it.second }
                    .map { it.first }

                recyclerView.adapter = OutcomesAdapter(items)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
