/*
 * Copyright 2026 Williams Suarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.example.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.data.model.BeverageType

interface AppStrings {
    val languageCode: String

    // Bottom Navigation
    val tabToday: String
    val tabStats: String
    val tabReminders: String

    // Today Screen
    val todayTitle: String
    val remindersTooltip: String
    val quickHydrate: String
    val ofGoal: String
    val remainingTarget: String
    val goalCompleted: String
    val mlToGo: String
    val extraSurplus: String
    val glassesLeft: String
    val streakBadge: String
    val todaysLogTitle: String
    val totalIntakePrefix: String
    val emptyLogTitle: String
    val emptyLogSubtitle: String
    val customVolumeButton: String
    val logCustomActionFab: String
    val addedSnackbar: String
    val removedSnackbar: String
    val undoLabel: String

    // Presets
    fun presetLabel(amountMl: Int, originalDesc: String): String

    // Beverage Types
    fun beverageName(type: BeverageType): String
    fun beverageDescription(type: BeverageType): String

    // Beverage Selector Dialog
    val beverageSelectorTitle: String
    val beverageSelectorSubtitle: String
    val hydroFactorLabel: String
    val doneButton: String

    // Custom Log Dialog
    val customLogTitle: String
    val logCustomDrinkTitle: String get() = customLogTitle
    val changeBeverage: String
    val changeBeverageButton: String get() = changeBeverage
    val effectiveHydrationDesc: String
    val effectiveHydrationLabel: String get() = effectiveHydrationDesc
    val quickAdjustLabel: String
    val volumeLabel: String
    val noteOptionalLabel: String
    val notePlaceholder: String
    val cancelButton: String
    val logDrinkButton: String
    fun logAmountButton(amountMl: Int): String = String.format(logDrinkButton, amountMl)
    fun approxEffectiveLabel(amountMl: Int): String
    val customLabel: String
    val specifyMlLabel: String

    // Stats Screen
    val statsTitle: String
    val statsSubtitle: String
    val weeklyOverviewTitle: String
    val targetPrefix: String
    val metricDailyAverage: String
    val metricGoalRate: String
    val daysMetFormat: String
    val metricStreak: String
    val streakKeepGoing: String
    val streakStartToday: String
    val metricWeeklyTotal: String
    val beverageDistributionTitle: String
    val beverageDistributionSubtitle: String
    val noBeveragesLogged: String
    val tipTitle: String
    val tipBody: String
    val todayLabel: String
    fun streakDaysFormat(days: Int): String
    fun totalWeeklyFormat(totalMl: Int): String
    fun dayLabel(day: String): String

    // Reminders & Settings Screen
    val settingsTitle: String
    val settingsSubtitle: String
    val languageSectionTitle: String
    val languageSectionSubtitle: String
    val languageOptionEn: String
    val languageOptionEs: String
    val notificationPermissionNeeded: String
    val notificationPermissionBody: String
    val grantPermissionButton: String
    val pushRemindersTitle: String
    val pushRemindersActive: String
    val pushRemindersDisabled: String
    val sendTestNotification: String
    val testNotificationSent: String
    val reminderIntervalTitle: String
    val reminderIntervalSubtitle: String
    val activeHoursTitle: String
    val activeHoursSubtitle: String
    val wakeUpStartTime: String
    val bedtimeEndTime: String
    val smartRemindersTitle: String
    val smartRemindersSubtitle: String
    val dailyTargetTitle: String
    val openCalculatorButton: String
    val resetHistoryTitle: String
    val resetHistorySubtitle: String
    val resetConfirmTitle: String
    val resetConfirmBody: String
    val clearEverythingButton: String
    val allDataResetSnackbar: String

    // Interval Chip Labels
    fun intervalLabel(minutes: Int): String

    // Goal Calculator Dialog
    val calculatorTitle: String
    val calculatorSubtitle: String
    val bodyWeightLabel: String
    val activityLevelLabel: String
    val activityLow: String
    val activityModerate: String
    val activityHigh: String
    val climateLabel: String
    val climateMild: String
    val climateWarm: String
    val climateHot: String
    val recommendedTargetLabel: String
    fun applyGoalButton(amountMl: Int): String

    // Notification Strings
    val notificationChannelName: String
    val notificationChannelDesc: String
    val notifAction250: String
    val notifAction500: String
    val notifTapToLogTip: String
    val notifTestTitle: String
    val notifGoalReachedTitle: String
    val notifAlmostThereTitle: String
    val notifHalfwayTitle: String
    val notifTimeForBreakTitle: String
    fun notifMessage(todayIntake: Int, goal: Int, percent: Int, remaining: Int, isTest: Boolean): String
    fun notifConfirmTitle(amountAdded: Int): String
    fun notifConfirmMessage(todayIntake: Int, goal: Int, percent: Int): String
}

object EnglishStrings : AppStrings {
    override val languageCode: String = "en"

    override val tabToday: String = "Today"
    override val tabStats: String = "Stats"
    override val tabReminders: String = "Reminders"

    override val todayTitle: String = "Daily Hydration"
    override val remindersTooltip: String = "Reminders"
    override val quickHydrate: String = "Quick Hydrate"
    override val ofGoal: String = "of %d ml goal"
    override val remainingTarget: String = "Remaining Target"
    override val goalCompleted: String = "Goal Completed!"
    override val mlToGo: String = "%d ml to go"
    override val extraSurplus: String = "+%d ml extra surplus"
    override val glassesLeft: String = "~%d glasses left"
    override val streakBadge: String = "%d Day Streak!"
    override val todaysLogTitle: String = "Today's Intake Log (%d)"
    override val totalIntakePrefix: String = "Total: %d ml"
    override val emptyLogTitle: String = "No drinks logged yet today"
    override val emptyLogSubtitle: String = "Start your day with a fresh glass of water! Tap any preset above."
    override val customVolumeButton: String = "+ Custom Volume"
    override val logCustomActionFab: String = "Log Water Intake"
    override val addedSnackbar: String = "Added +%d ml %s 💧"
    override val removedSnackbar: String = "Removed entry (%d ml)"
    override val undoLabel: String = "Undo"

    override fun presetLabel(amountMl: Int, originalDesc: String): String = "$amountMl ml $originalDesc"

    override fun beverageName(type: BeverageType): String = when (type) {
        BeverageType.WATER -> "Pure Water"
        BeverageType.LEMON_WATER -> "Lemon Water"
        BeverageType.HERBAL_TEA -> "Herbal Tea"
        BeverageType.ELECTROLYTE -> "Electrolytes"
        BeverageType.TEA -> "Green/Black Tea"
        BeverageType.COFFEE -> "Coffee"
        BeverageType.JUICE -> "Fresh Juice"
        BeverageType.SPARKLING -> "Sparkling Water"
    }

    override fun beverageDescription(type: BeverageType): String = when (type) {
        BeverageType.WATER -> "100% hydration"
        BeverageType.LEMON_WATER -> "Refreshing citrus hydration"
        BeverageType.HERBAL_TEA -> "Caffeine-free soothing infusion"
        BeverageType.ELECTROLYTE -> "Enhanced rehydration"
        BeverageType.TEA -> "Antioxidant rich"
        BeverageType.COFFEE -> "Mild diuretic effect"
        BeverageType.JUICE -> "Vitamins & natural sugars"
        BeverageType.SPARKLING -> "Crisp effervescence"
    }

    override val beverageSelectorTitle: String = "Select Beverage Type"
    override val beverageSelectorSubtitle: String = "Different drinks have distinct hydration efficiency ratings."
    override val hydroFactorLabel: String = "%d%% hydro factor"
    override val doneButton: String = "Done"

    override val customLogTitle: String = "Log Custom Drink"
    override val changeBeverage: String = "Change"
    override val effectiveHydrationDesc: String = "%d%% effective hydration"
    override val quickAdjustLabel: String = "Quick Adjust Volume"
    override val volumeLabel: String = "Volume (ml)"
    override val noteOptionalLabel: String = "Note (optional)"
    override val notePlaceholder: String = "Add a note (e.g. After morning run, With lunch)"
    override val cancelButton: String = "Cancel"
    override val logDrinkButton: String = "Log %d ml"
    override fun approxEffectiveLabel(amountMl: Int): String = "≈ $amountMl ml effective"
    override val customLabel: String = "Custom"
    override val specifyMlLabel: String = "Specify ml"

    override val statsTitle: String = "Hydration Analytics"
    override val statsSubtitle: String = "Past 7 days performance and fluid distribution"
    override val weeklyOverviewTitle: String = "Weekly Intake Overview"
    override val targetPrefix: String = "Target: %d ml"
    override val metricDailyAverage: String = "Daily Average"
    override val metricGoalRate: String = "Goal Rate"
    override val daysMetFormat: String = "%d of 7 days met"
    override val metricStreak: String = "Current Streak"
    override val streakKeepGoing: String = "Keep it going!"
    override val streakStartToday: String = "Start today!"
    override val metricWeeklyTotal: String = "7-Day Total"
    override val beverageDistributionTitle: String = "Beverage Distribution"
    override val beverageDistributionSubtitle: String = "Breakdown of fluids logged across all entries"
    override val noBeveragesLogged: String = "No beverages logged yet."
    override val tipTitle: String = "Hydration Habit Tip"
    override val tipBody: String = "Drinking 300-500ml of water right after waking up jumpstarts your metabolism and restores fluids lost overnight. Use regular reminder intervals to maintain steady energy throughout your day!"
    override val todayLabel: String = "Today"
    override fun streakDaysFormat(days: Int): String = "$days ${if (days == 1) "Day" else "Days"}"
    override fun totalWeeklyFormat(totalMl: Int): String = "$totalMl ml total"
    override fun dayLabel(day: String): String = if (day == "Today") todayLabel else day

    override val settingsTitle: String = "Reminders & Settings"
    override val settingsSubtitle: String = "Configure notifications, reminder intervals, language, and targets"
    override val languageSectionTitle: String = "Language / Idioma"
    override val languageSectionSubtitle: String = "Switch between English and Spanish"
    override val languageOptionEn: String = "English"
    override val languageOptionEs: String = "Español"
    override val notificationPermissionNeeded: String = "Notification Permission Needed"
    override val notificationPermissionBody: String = "To receive periodic hydration reminders and quick-drink alerts, please grant notification access."
    override val grantPermissionButton: String = "Grant Permission"
    override val pushRemindersTitle: String = "Push Reminders"
    override val pushRemindersActive: String = "Active • Alerts scheduled"
    override val pushRemindersDisabled: String = "Disabled"
    override val sendTestNotification: String = "Send Test Push Notification"
    override val testNotificationSent: String = "Test Notification Sent! 🔔"
    override val reminderIntervalTitle: String = "Reminder Interval"
    override val reminderIntervalSubtitle: String = "How frequently would you like to be prompted?"
    override val activeHoursTitle: String = "Active Notification Hours"
    override val activeHoursSubtitle: String = "No reminders will disturb you outside this window"
    override val wakeUpStartTime: String = "Wake-up / Start Time"
    override val bedtimeEndTime: String = "Bedtime / End Time"
    override val smartRemindersTitle: String = "Smart Reminder Mode"
    override val smartRemindersSubtitle: String = "Only notify if your daily hydration target is not yet fulfilled"
    override val dailyTargetTitle: String = "Daily Intake Target"
    override val openCalculatorButton: String = "Calculate Ideal Target (Weight & Activity)"
    override val resetHistoryTitle: String = "Reset History"
    override val resetHistorySubtitle: String = "Clear all logged drink records"
    override val resetConfirmTitle: String = "Reset All Intake Data?"
    override val resetConfirmBody: String = "This will permanently clear all logged drinks and reset your history. Are you sure?"
    override val clearEverythingButton: String = "Clear Everything"
    override val allDataResetSnackbar: String = "All intake data reset."

    override fun intervalLabel(minutes: Int): String = when (minutes) {
        30 -> "Every 30m"
        45 -> "Every 45m"
        60 -> "Every 1h"
        90 -> "Every 1.5h"
        120 -> "Every 2h"
        180 -> "Every 3h"
        240 -> "Every 4h"
        else -> "Every ${minutes}m"
    }

    override val calculatorTitle: String = "Hydration Goal Calculator"
    override val calculatorSubtitle: String = "Personalize your daily intake target based on your physical attributes and daily activity level."
    override val bodyWeightLabel: String = "Body Weight"
    override val activityLevelLabel: String = "Activity Level"
    override val activityLow: String = "Low / Desk"
    override val activityModerate: String = "Moderate"
    override val activityHigh: String = "High / Athlete"
    override val climateLabel: String = "Environment Climate"
    override val climateMild: String = "Normal"
    override val climateWarm: String = "Warm"
    override val climateHot: String = "Hot & Humid"
    override val recommendedTargetLabel: String = "Recommended Daily Target"
    override fun applyGoalButton(amountMl: Int): String = "Apply $amountMl ml"

    override val notificationChannelName: String = "Hydration Reminders"
    override val notificationChannelDesc: String = "Periodic push notifications to help you stay hydrated throughout the day"
    override val notifAction250: String = "+250 ml Glass"
    override val notifAction500: String = "+500 ml Bottle"
    override val notifTapToLogTip: String = "Tap a quick button below to log your drink instantly."
    override val notifTestTitle: String = "💧 Hydration Test Notification!"
    override val notifGoalReachedTitle: String = "🎉 Daily Hydration Goal Reached!"
    override val notifAlmostThereTitle: String = "🌊 Almost there! Keep hydrated"
    override val notifHalfwayTitle: String = "💧 Halfway to your daily goal!"
    override val notifTimeForBreakTitle: String = "💧 Time for a fresh water break!"

    override fun notifMessage(todayIntake: Int, goal: Int, percent: Int, remaining: Int, isTest: Boolean): String {
        return when {
            isTest -> "Push notifications are working! Today: $todayIntake / $goal ml ($percent%)"
            percent >= 100 -> "Outstanding work! You've logged $todayIntake ml today. Keep feeling refreshed!"
            else -> "$todayIntake / $goal ml logged ($percent%). Drink $remaining ml more to finish strong!"
        }
    }

    override fun notifConfirmTitle(amountAdded: Int): String = "✅ Added +$amountAdded ml!"
    override fun notifConfirmMessage(todayIntake: Int, goal: Int, percent: Int): String =
        "Today's progress: $todayIntake / $goal ml ($percent%) 💧"
}

object SpanishStrings : AppStrings {
    override val languageCode: String = "es"

    override val tabToday: String = "Hoy"
    override val tabStats: String = "Estadísticas"
    override val tabReminders: String = "Recordatorios"

    override val todayTitle: String = "Hidratación Diaria"
    override val remindersTooltip: String = "Recordatorios"
    override val quickHydrate: String = "Hidratación Rápida"
    override val ofGoal: String = "de meta de %d ml"
    override val remainingTarget: String = "Meta Restante"
    override val goalCompleted: String = "¡Meta Cumplida!"
    override val mlToGo: String = "Faltan %d ml"
    override val extraSurplus: String = "+%d ml extra superados"
    override val glassesLeft: String = "~%d vasos restantes"
    override val streakBadge: String = "¡Racha de %d días!"
    override val todaysLogTitle: String = "Registro de Hoy (%d)"
    override val totalIntakePrefix: String = "Total: %d ml"
    override val emptyLogTitle: String = "No hay bebidas registradas hoy"
    override val emptyLogSubtitle: String = "¡Comienza tu día con un vaso de agua fresca! Toca un botón de arriba."
    override val customVolumeButton: String = "+ Volumen Personalizado"
    override val logCustomActionFab: String = "Registrar Consumo de Agua"
    override val addedSnackbar: String = "Agregado +%d ml %s 💧"
    override val removedSnackbar: String = "Entrada eliminada (%d ml)"
    override val undoLabel: String = "Deshacer"

    override fun presetLabel(amountMl: Int, originalDesc: String): String {
        val esDesc = when (originalDesc.lowercase()) {
            "cup" -> "Taza"
            "glass" -> "Vaso"
            "mug" -> "Tazón"
            "bottle" -> "Botella"
            "flask" -> "Termo"
            else -> originalDesc
        }
        return "$amountMl ml $esDesc"
    }

    override fun beverageName(type: BeverageType): String = when (type) {
        BeverageType.WATER -> "Agua Pura"
        BeverageType.LEMON_WATER -> "Agua con Limón"
        BeverageType.HERBAL_TEA -> "Té de Hierbas"
        BeverageType.ELECTROLYTE -> "Electrolitos"
        BeverageType.TEA -> "Té Verde/Negro"
        BeverageType.COFFEE -> "Café"
        BeverageType.JUICE -> "Jugo Natural"
        BeverageType.SPARKLING -> "Agua con Gas"
    }

    override fun beverageDescription(type: BeverageType): String = when (type) {
        BeverageType.WATER -> "100% hidratación"
        BeverageType.LEMON_WATER -> "Hidratación cítrica refrescante"
        BeverageType.HERBAL_TEA -> "Infusión relajante sin cafeína"
        BeverageType.ELECTROLYTE -> "Rehidratación avanzada con sales"
        BeverageType.TEA -> "Rico en antioxidantes naturales"
        BeverageType.COFFEE -> "Efecto diurético suave"
        BeverageType.JUICE -> "Vitaminas y azúcares naturales"
        BeverageType.SPARKLING -> "Efervescencia fresca y burbujeante"
    }

    override val beverageSelectorTitle: String = "Seleccionar Tipo de Bebida"
    override val beverageSelectorSubtitle: String = "Cada bebida tiene una tasa de hidratación distinta."
    override val hydroFactorLabel: String = "%d%% factor de hidratación"
    override val doneButton: String = "Listo"

    override val customLogTitle: String = "Registrar Bebida Personalizada"
    override val changeBeverage: String = "Cambiar"
    override val effectiveHydrationDesc: String = "%d%% hidratación efectiva"
    override val quickAdjustLabel: String = "Ajuste Rápido de Volumen"
    override val volumeLabel: String = "Volumen (ml)"
    override val noteOptionalLabel: String = "Nota (opcional)"
    override val notePlaceholder: String = "Agregar nota (ej. Después de correr, En el almuerzo)"
    override val cancelButton: String = "Cancelar"
    override val logDrinkButton: String = "Registrar %d ml"
    override fun approxEffectiveLabel(amountMl: Int): String = "≈ $amountMl ml efectivos"
    override val customLabel: String = "Personalizar"
    override val specifyMlLabel: String = "Definir ml"

    override val statsTitle: String = "Análisis de Hidratación"
    override val statsSubtitle: String = "Rendimiento y distribución de los últimos 7 días"
    override val weeklyOverviewTitle: String = "Resumen Semanal de Consumo"
    override val targetPrefix: String = "Meta: %d ml"
    override val metricDailyAverage: String = "Promedio Diario"
    override val metricGoalRate: String = "Tasa de Cumplimiento"
    override val daysMetFormat: String = "%d de 7 días cumplidos"
    override val metricStreak: String = "Racha Actual"
    override val streakKeepGoing: String = "¡Sigue así!"
    override val streakStartToday: String = "¡Empieza hoy!"
    override val metricWeeklyTotal: String = "Total en 7 Días"
    override val beverageDistributionTitle: String = "Distribución de Bebidas"
    override val beverageDistributionSubtitle: String = "Desglose de líquidos registrados en todas las entradas"
    override val noBeveragesLogged: String = "Aún no hay bebidas registradas."
    override val tipTitle: String = "Consejo de Hidratación"
    override val tipBody: String = "Beber entre 300 y 500 ml de agua al levantarte activa tu metabolismo y recupera los líquidos perdidos durante la noche. ¡Usa recordatorios regulares para mantener tu energía constante!"
    override val todayLabel: String = "Hoy"
    override fun streakDaysFormat(days: Int): String = "$days ${if (days == 1) "Día" else "Días"}"
    override fun totalWeeklyFormat(totalMl: Int): String = "$totalMl ml en total"
    override fun dayLabel(day: String): String = when (day) {
        "Today" -> todayLabel
        "Mon" -> "Lun"
        "Tue" -> "Mar"
        "Wed" -> "Mié"
        "Thu" -> "Jue"
        "Fri" -> "Vie"
        "Sat" -> "Sáb"
        "Sun" -> "Dom"
        else -> day
    }

    override val settingsTitle: String = "Recordatorios y Ajustes"
    override val settingsSubtitle: String = "Configura alertas, intervalos de recordatorio, idioma y metas diarias"
    override val languageSectionTitle: String = "Idioma / Language"
    override val languageSectionSubtitle: String = "Alterna entre inglés y español fácilmente"
    override val languageOptionEn: String = "English"
    override val languageOptionEs: String = "Español"
    override val notificationPermissionNeeded: String = "Permiso de Notificaciones Requerido"
    override val notificationPermissionBody: String = "Para recibir recordatorios periódicos y alertas de hidratación rápida, por favor permite el acceso a notificaciones."
    override val grantPermissionButton: String = "Conceder Permiso"
    override val pushRemindersTitle: String = "Recordatorios Push"
    override val pushRemindersActive: String = "Activo • Alertas programadas"
    override val pushRemindersDisabled: String = "Desactivado"
    override val sendTestNotification: String = "Enviar Notificación de Prueba"
    override val testNotificationSent: String = "¡Notificación de prueba enviada! 🔔"
    override val reminderIntervalTitle: String = "Intervalo de Recordatorios"
    override val reminderIntervalSubtitle: String = "¿Con qué frecuencia te gustaría recibir recordatorios?"
    override val activeHoursTitle: String = "Horario Activo de Notificaciones"
    override val activeHoursSubtitle: String = "No recibirás alertas fuera de este horario"
    override val wakeUpStartTime: String = "Hora de Inicio / Despertar"
    override val bedtimeEndTime: String = "Hora de Fin / Dormir"
    override val smartRemindersTitle: String = "Modo de Recordatorio Inteligente"
    override val smartRemindersSubtitle: String = "Notificar únicamente si aún no has alcanzado tu meta diaria"
    override val dailyTargetTitle: String = "Meta Diaria de Consumo"
    override val openCalculatorButton: String = "Calcular Meta Ideal (Peso y Actividad)"
    override val resetHistoryTitle: String = "Reiniciar Historial"
    override val resetHistorySubtitle: String = "Borrar todos los registros de consumo"
    override val resetConfirmTitle: String = "¿Borrar Todos los Datos de Consumo?"
    override val resetConfirmBody: String = "Esto borrará permanentemente todas las bebidas registradas y reiniciará tu historial. ¿Estás seguro?"
    override val clearEverythingButton: String = "Borrar Todo"
    override val allDataResetSnackbar: String = "Todos los datos de consumo han sido reiniciados."

    override fun intervalLabel(minutes: Int): String = when (minutes) {
        30 -> "Cada 30m"
        45 -> "Cada 45m"
        60 -> "Cada 1h"
        90 -> "Cada 1.5h"
        120 -> "Cada 2h"
        180 -> "Cada 3h"
        240 -> "Cada 4h"
        else -> "Cada ${minutes}m"
    }

    override val calculatorTitle: String = "Calculadora de Meta de Hidratación"
    override val calculatorSubtitle: String = "Personaliza tu meta diaria según tus características físicas y nivel de actividad."
    override val bodyWeightLabel: String = "Peso Corporal"
    override val activityLevelLabel: String = "Nivel de Actividad"
    override val activityLow: String = "Bajo / Oficina"
    override val activityModerate: String = "Moderado"
    override val activityHigh: String = "Alto / Atleta"
    override val climateLabel: String = "Clima Ambiental"
    override val climateMild: String = "Normal"
    override val climateWarm: String = "Cálido"
    override val climateHot: String = "Caluroso y Húmedo"
    override val recommendedTargetLabel: String = "Meta Diaria Recomendada"
    override fun applyGoalButton(amountMl: Int): String = "Aplicar $amountMl ml"

    override val notificationChannelName: String = "Recordatorios de Hidratación"
    override val notificationChannelDesc: String = "Notificaciones periódicas para ayudarte a mantenerte hidratado durante todo el día"
    override val notifAction250: String = "+250 ml Vaso"
    override val notifAction500: String = "+500 ml Botella"
    override val notifTapToLogTip: String = "Toca un botón de abajo para registrar tu bebida al instante."
    override val notifTestTitle: String = "💧 ¡Notificación de Prueba de Hidratación!"
    override val notifGoalReachedTitle: String = "🎉 ¡Meta Diaria de Hidratación Alcanzada!"
    override val notifAlmostThereTitle: String = "🌊 ¡Casi listo! Sigue hidratándote"
    override val notifHalfwayTitle: String = "💧 ¡Vas por la mitad de tu meta diaria!"
    override val notifTimeForBreakTitle: String = "💧 ¡Hora de un descanso para beber agua!"

    override fun notifMessage(todayIntake: Int, goal: Int, percent: Int, remaining: Int, isTest: Boolean): String {
        return when {
            isTest -> "¡Las notificaciones funcionan correctamente! Hoy: $todayIntake / $goal ml ($percent%)"
            percent >= 100 -> "¡Excelente trabajo! Has registrado $todayIntake ml hoy. ¡Mantente fresco!"
            else -> "$todayIntake / $goal ml registrados ($percent%). ¡Bebe $remaining ml más para cumplir tu meta!"
        }
    }

    override fun notifConfirmTitle(amountAdded: Int): String = "✅ ¡Se añadieron +$amountAdded ml!"
    override fun notifConfirmMessage(todayIntake: Int, goal: Int, percent: Int): String =
        "Progreso de hoy: $todayIntake / $goal ml ($percent%) 💧"
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { EnglishStrings }
