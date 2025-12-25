package com.example.pixeleaters.data.repository
import android.content.Context
import com.example.pixeleaters.R
import com.example.pixeleaters.data.model.Constants
import kotlinx.coroutines.delay
/**
 * Репозиторий для получения напоминаний.
 * Имитирует асинхронный вызов к API с задержкой.
 * Использует ресурсы приложения для локализуемых строк.
 */
class ReminderRepository(private val context: Context) {

    private val mockReminders by lazy {
        listOf(
            Constants(
                id = "1",
                title = context.getString(R.string.reminder_title_1),
                date = "25.12.2025",
                time = "09:00",
                description = context.getString(R.string.reminder_desc_1),
                context = context.getString(R.string.reminder_context_1),
                tag = context.getString(R.string.reminder_tag_1)
            ),
            Constants(
                id = "2",
                title = context.getString(R.string.reminder_title_2),
                date = "26.12.2025",
                time = "14:30",
                description = context.getString(R.string.reminder_desc_2),
                context = context.getString(R.string.reminder_context_2),
                tag = context.getString(R.string.reminder_tag_2)
            ),
            Constants(
                id = "3",
                title = context.getString(R.string.reminder_title_3),
                date = "27.12.2025",
                time = "18:00",
                description = context.getString(R.string.reminder_desc_3),
                context = context.getString(R.string.reminder_context_3),
                tag = context.getString(R.string.reminder_tag_3)
            ),
                    Constants(
                    id = "4",
            title = context.getString(R.string.reminder_title_1),
            date = "25.12.2025",
            time = "09:00",
            description = context.getString(R.string.reminder_desc_1),
            context = context.getString(R.string.reminder_context_1),
            tag = context.getString(R.string.reminder_tag_1)
        ),
            Constants(
                id = "5",
                title = context.getString(R.string.reminder_title_1),
                date = "25.12.2025",
                time = "09:00",
                description = context.getString(R.string.reminder_desc_1),
                context = context.getString(R.string.reminder_context_1),
                tag = context.getString(R.string.reminder_tag_1)
            ),
            Constants(
                id = "6",
                title = context.getString(R.string.reminder_title_1),
                date = "25.12.2025",
                time = "09:00",
                description = context.getString(R.string.reminder_desc_1),
                context = context.getString(R.string.reminder_context_1),
                tag = context.getString(R.string.reminder_tag_1)
            ),
            Constants(
                id = "7",
                title = context.getString(R.string.reminder_title_1),
                date = "25.12.2025",
                time = "09:00",
                description = context.getString(R.string.reminder_desc_1),
                context = context.getString(R.string.reminder_context_1),
                tag = context.getString(R.string.reminder_tag_1)
            ),
            Constants(
                id = "8",
                title = context.getString(R.string.reminder_title_1),
                date = "25.12.2025",
                time = "09:00",
                description = context.getString(R.string.reminder_desc_1),
                context = context.getString(R.string.reminder_context_1),
                tag = context.getString(R.string.reminder_tag_1)
            ),
            Constants(
                id = "9",
                title = context.getString(R.string.reminder_title_1),
                date = "25.12.2025",
                time = "09:00",
                description = context.getString(R.string.reminder_desc_1),
                context = context.getString(R.string.reminder_context_1),
                tag = context.getString(R.string.reminder_tag_1)
            ),
            Constants(
                id = "10",
                title = context.getString(R.string.reminder_title_1),
                date = "25.12.2025",
                time = "09:00",
                description = context.getString(R.string.reminder_desc_1),
                context = context.getString(R.string.reminder_context_1),
                tag = context.getString(R.string.reminder_tag_1)
            ),
        )
    }

    /** Асинхронно возвращает список всех напоминаний*/
    suspend fun getReminders(): List<Constants> {
        delay(2000L) // имитация сетевой задержки
        return mockReminders
    }

    /**Асинхронно возвращает напоминание по ID.*/
    suspend fun getReminderById(id: String): Constants? {
        delay(2000L)
        return mockReminders.find { it.id == id }
    }
}