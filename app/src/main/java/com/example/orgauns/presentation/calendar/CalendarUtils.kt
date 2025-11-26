package com.example.orgauns.presentation.calendar

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

/**
 * Utilidades para trabajar con el calendario
 */
object CalendarUtils {

    /**
     * Obtiene los días a mostrar en el grid del calendario para un mes dado
     * Incluye días del mes anterior y siguiente para completar las semanas
     */
    fun getDaysInMonth(yearMonth: YearMonth): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()

        // Primer día del mes
        val firstDayOfMonth = yearMonth.atDay(1)
        // Último día del mes
        val lastDayOfMonth = yearMonth.atEndOfMonth()

        // Día de la semana del primer día (1 = Lunes, 7 = Domingo)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value

        // Agregar días del mes anterior para completar la primera semana
        val previousMonth = yearMonth.minusMonths(1)
        val daysInPreviousMonth = previousMonth.lengthOfMonth()
        for (i in firstDayOfWeek - 1 downTo 1) {
            days.add(
                CalendarDay(
                    date = previousMonth.atDay(daysInPreviousMonth - i + 1),
                    isCurrentMonth = false
                )
            )
        }

        // Agregar todos los días del mes actual
        for (day in 1..lastDayOfMonth.dayOfMonth) {
            days.add(
                CalendarDay(
                    date = yearMonth.atDay(day),
                    isCurrentMonth = true
                )
            )
        }

        // Agregar días del mes siguiente para completar la última semana
        val remainingDays = 7 - (days.size % 7)
        if (remainingDays < 7) {
            val nextMonth = yearMonth.plusMonths(1)
            for (day in 1..remainingDays) {
                days.add(
                    CalendarDay(
                        date = nextMonth.atDay(day),
                        isCurrentMonth = false
                    )
                )
            }
        }

        return days
    }

    /**
     * Obtiene los 7 días de la semana que contiene la fecha seleccionada
     * Empieza en Lunes y termina en Domingo
     */
    fun getDaysInWeek(selectedDate: LocalDate): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()

        // Encontrar el lunes de la semana
        val dayOfWeek = selectedDate.dayOfWeek.value // 1 = Lunes, 7 = Domingo
        val monday = selectedDate.minusDays((dayOfWeek - 1).toLong())

        // Agregar 7 días desde el lunes
        for (i in 0..6) {
            val date = monday.plusDays(i.toLong())
            days.add(
                CalendarDay(
                    date = date,
                    isCurrentMonth = date.month == selectedDate.month
                )
            )
        }

        return days
    }

    /**
     * Convierte epoch millis a LocalDate en zona local
     */
    fun epochMillisToLocalDate(epochMillis: Long): LocalDate {
        return java.time.Instant.ofEpochMilli(epochMillis)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
    }

    /**
     * Obtiene los días de la semana en español
     */
    fun getDaysOfWeekHeaders(): List<String> {
        return listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    }

    /**
     * Formatea YearMonth a String legible
     */
    fun formatYearMonth(yearMonth: YearMonth): String {
        val month = yearMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        return "${month.capitalize()} ${yearMonth.year}"
    }
}

/**
 * Representa un día en el calendario
 */
data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean
)

/**
 * Extension para capitalizar primera letra
 */
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

