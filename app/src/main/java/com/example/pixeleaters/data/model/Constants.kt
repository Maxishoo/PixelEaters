package com.example.pixeleaters.data.model

/**
 * Модель напоминания.
 * Используется для отображения в списке и на экране деталей.
 * Все текстовые поля предназначены для подстановки из ресурсов (strings.xml),
 * но передаются как строки — локализация и форматирование должны управляться выше.
 */
data class Constants(
    val id: String,
    val title: String,
    val date: String,        // Формат: "25.11.2025"
    val time: String,        // Формат: "09:13"
    val description: String,
    val context: String? = null, // Дополнительный контекст (опционально)
    val tag: String? = null      // Например: "#коллега", "#личное"
)