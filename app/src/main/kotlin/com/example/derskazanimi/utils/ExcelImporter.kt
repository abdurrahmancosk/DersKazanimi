package com.example.derskazanimi.utils

import android.content.Context
import android.net.Uri
import com.example.derskazanimi.data.entity.LearningOutcome
import com.example.derskazanimi.data.entity.WeeklyPlan
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExcelImporter {

    fun importExcel(context: Context, fileUri: Uri): ImportResult {
        return try {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            inputStream?.use { stream ->
                val workbook = WorkbookFactory.create(stream)
                parseWorkbook(workbook)
            } ?: ImportResult.Error("Dosya açılamadı")
        } catch (e: Exception) {
            ImportResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }

    private fun parseWorkbook(workbook: org.apache.poi.ss.usermodel.Workbook): ImportResult {
        val weeklyPlans = mutableListOf<WeeklyPlan>()
        val learningOutcomes = mutableListOf<LearningOutcome>()
        val errors = mutableListOf<String>()

        // Iterate through all sheets (each sheet can be a different class or section)
        for (sheetIndex in 0 until workbook.numberOfSheets) {
            try {
                val sheet = workbook.getSheetAt(sheetIndex)
                val sheetName = sheet.sheetName

                // Parse each row to extract week number and learning outcomes
                for (rowIndex in 1 until sheet.physicalNumberOfRows) {
                    try {
                        val row = sheet.getRow(rowIndex) ?: continue

                        // Column B: Hafta Tarihleri (e.g., "26 Eylül - 02 Ekim")
                        val dateRangeCell = row.getCell(1)
                        val dateRange = dateRangeCell?.stringCellValue ?: continue

                        if (dateRange.isBlank()) continue

                        // Column F: Kazanım (Learning Outcome)
                        val kazanimCell = row.getCell(5)
                        val kazanim = kazanimCell?.stringCellValue ?: continue

                        if (kazanim.isBlank()) continue

                        // Extract week number from row index (column A)
                        val weekNumberCell = row.getCell(0)
                        val weekNumber = try {
                            val cellValue = weekNumberCell?.numericCellValue
                            if (cellValue != null && cellValue > 0) {
                                cellValue.toInt()
                            } else {
                                rowIndex / 2
                            }
                        } catch (e: Exception) {
                            rowIndex / 2
                        }

                        if (weekNumber <= 0) {
                            errors.add("Geçersiz hafta numarası: Satır $rowIndex")
                            continue
                        }

                        // Parse date range to get start and end dates
                        val (startDate, endDate) = parseDateRange(dateRange)

                        if (startDate.isEmpty() || endDate.isEmpty()) {
                            errors.add("Geçersiz tarih formatı: $dateRange (Satır $rowIndex)")
                            continue
                        }

                        // Check if weekly plan already exists
                        val existingPlan = weeklyPlans.find { it.weekNumber == weekNumber }
                        val weeklyPlanId = if (existingPlan == null) {
                            val newPlan = WeeklyPlan(
                                weekNumber = weekNumber,
                                startDate = startDate,
                                endDate = endDate
                            )
                            weeklyPlans.add(newPlan)
                            weeklyPlans.size
                        } else {
                            weeklyPlans.indexOf(existingPlan) + 1
                        }

                        // Extract class name from sheet name (e.g., "9-C", "12-B")
                        val className = extractClassName(sheetName)

                        if (className.isEmpty()) {
                            errors.add("Sınıf adı bulunamadı: $sheetName")
                            continue
                        }

                        try {
                            val outcome = LearningOutcome(
                                className = className,
                                weeklyPlanId = weeklyPlanId,
                                subject = "Matematik",
                                kazanim = kazanim.trim()
                            )
                            learningOutcomes.add(outcome)
                        } catch (e: Exception) {
                            errors.add("Kazanım kaydı başarısız: ${e.message} (Satır $rowIndex)")
                        }
                    } catch (e: Exception) {
                        errors.add("Satır işleme hatası $rowIndex: ${e.message}")
                        continue
                    }
                }
            } catch (e: Exception) {
                errors.add("Sayfa işleme hatası: ${e.message}")
            }
        }

        return if (learningOutcomes.isNotEmpty()) {
            ImportResult.Success(weeklyPlans, learningOutcomes, errors)
        } else {
            ImportResult.Error("Hiç kazanım yüklenemedi. Hatalar: ${errors.joinToString(", ")}")
        }
    }

    private fun parseDateRange(dateRange: String): Pair<String, String> {
        return try {
            // Format: "26 Eylül - 02 Ekim" or similar
            val parts = dateRange.split("-").map { it.trim() }
            if (parts.size >= 2) {
                val startDateStr = parts[0]
                val endDateStr = parts[1]

                // Convert to yyyy-MM-dd format
                val startDate = convertToDateFormat(startDateStr)
                val endDate = convertToDateFormat(endDateStr)
                Pair(startDate, endDate)
            } else {
                Pair("2026-09-01", "2026-09-07")
            }
        } catch (e: Exception) {
            Pair("2026-09-01", "2026-09-07")
        }
    }

    private fun convertToDateFormat(dateStr: String): String {
        return try {
            // Parse Turkish month names
            val turkishMonths = mapOf(
                "Ocak" to "01", "Şubat" to "02", "Mart" to "03", "Nisan" to "04",
                "Mayıs" to "05", "Haziran" to "06", "Temmuz" to "07", "Ağustos" to "08",
                "Eylül" to "09", "Ekim" to "10", "Kasım" to "11", "Aralık" to "12"
            )

            val tokens = dateStr.split("\\s+".toRegex()).filter { it.isNotEmpty() }
            if (tokens.size >= 2) {
                val day = tokens[0].filter { it.isDigit() }.padStart(2, '0')
                val month = turkishMonths[tokens[1]] ?: ""

                if (day.isNotEmpty() && month.isNotEmpty()) {
                    val dayInt = day.toIntOrNull() ?: 1
                    if (dayInt in 1..31) {
                        val year = "2026"
                        "$year-$month-$day"
                    } else {
                        ""
                    }
                } else {
                    ""
                }
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun extractClassName(sheetName: String): String {
        // Extract class name like "9-C", "10-B", "11-A", "12-B" from sheet name
        val regex = "([9-9]|10|11|12)\\s*[-/]\\s*([A-C])".toRegex(RegexOption.IGNORE_CASE)
        val matchResult = regex.find(sheetName)
        return if (matchResult != null) {
            "${matchResult.groupValues[1]}-${matchResult.groupValues[2].uppercase()}"
        } else {
            "" // Return empty if class name not found
        }
    }
}

sealed class ImportResult {
    data class Success(
        val weeklyPlans: List<WeeklyPlan>,
        val learningOutcomes: List<LearningOutcome>,
        val warnings: List<String> = emptyList()
    ) : ImportResult()

    data class Error(val message: String) : ImportResult()
}
