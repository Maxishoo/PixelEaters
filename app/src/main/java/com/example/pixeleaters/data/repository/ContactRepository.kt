package com.example.pixeleaters.data.repository

import com.example.pixeleaters.data.model.Contact
import com.example.pixeleaters.data.model.Reminder
import kotlinx.coroutines.delay

class ContactRepository {

    companion object {
        private const val LOADING_DELAY_MS = 2000L
    }

    private val mockContacts = listOf(
        Contact(
            id = 10,
            name = "Михаил Козлов",
            tags = listOf("друг", "универ")
        ),
        Contact(
            id = 11,
            name = "Анна Петрова",
            tags = listOf("коллега", "проект")
        ),
        Contact(
            id = 12,
            name = "Виктор Смирнов",
            tags = listOf("работа", "начальник")
        ),
        Contact(
            id = 13,
            name = "Елена Волкова",
            tags = listOf("друг", "школа")
        ),
        Contact(
            id = 14,
            name = "Артем Новиков",
            tags = listOf("коллега", "работа")
        ),
        Contact(
            id = 15,
            name = "Ольга Морозова",
            tags = listOf("семья", "сестра")
        ),
        Contact(
            id = 16,
            name = "Николай Федоров",
            tags = listOf("спорт", "футбол")
        ),
        Contact(
            id = 17,
            name = "Мария Соколова",
            tags = listOf("коллега", "HR")
        ),
        Contact(
            id = 18,
            name = "Павел Лебедев",
            tags = listOf("друг", "детство")
        ),
        Contact(
            id = 19,
            name = "Татьяна Козлова",
            tags = listOf("работа", "бухгалтерия")
        ),
        Contact(
            id = 20,
            name = "Игорь Попов",
            tags = listOf("сосед", "дом")
        ),
        Contact(
            id = 1,
            name = "Иванов Иван",
            tags = listOf("коллега", "работа")
        ),
        Contact(
            id = 2,
            name = "Петров Дмитрий",
            tags = listOf("коллега", "работа")
        ),
        Contact(
            id = 3,
            name = "Орлов Сергей",
            tags = listOf("коллега", "работа")
        ),
        Contact(
            id = 4,
            name = "Ложкин Коля",
            tags = listOf("коллега", "работа")
        ),
        Contact(
            id = 5,
            name = "Иван(работа)",
            tags = listOf("коллега", "работа")
        ),
        Contact(
            id = 6,
            name = "Сидоров Саня",
            tags = listOf("коллега", "работа")
        ),
        Contact(
            id = 7,
            name = "Александр Иванович",
            tags = listOf("коллега", "работа")
        ),
        Contact(
            id = 8,
            name = "Денис(шахматы)",
            tags = listOf("коллега", "работа")
        ),
        Contact(
            id = 9,
            name = "Семен",
            tags = listOf("коллега", "работа")
        )
    )

    private val mockReminders = listOf(
        Reminder(
            id = 1,
            tag = "коллега",
            title = "День рождения Ивана",
            date = "25.11.2025",
            time = "09:13",
            shortDescription = "Не забудьте поздравить друга с праздником",
            description = "мой друг со школы с которым я проводил очень много времени.",
            context = "Последний раз общались 2 месяца назад, обсуждали идею открытия бизнеса"
        ),
        Reminder(
            id = 2,
            tag = "коллега",
            title = "День рождения Елена",
            date = "05.12.2025",
            time = "11:00",
            shortDescription = "Не забудьте поздравить коллегу с праздником",
            description = "Коллега из отдела маркетинга",
            context = "Работаем над совместным проектом"
        ),
        Reminder(
            id = 3,
            tag = "коллега",
            title = "Обсуждение проекта",
            date = "05.12.2025",
            time = "11:00",
            shortDescription = "Не забудьте поздравить коллегу с праздником",
            description = "Встреча по проекту",
            context = "Обсудить план на следующий квартал"
        ),
        Reminder(
            id = 4,
            tag = "коллега",
            title = "Проект Олег",
            date = "05.12.2025",
            time = "11:00",
            shortDescription = "Не забудьте поздравить коллегу с праздником",
            description = "Проект с Олегом",
            context = "Завершить до конца месяца"
        ),
        Reminder(
            id = 5,
            tag = "коллега",
            title = "Посмотреть кухню Полина",
            date = "05.12.2025",
            time = "11:00",
            shortDescription = "Не забудьте поздравить коллегу с праздником",
            description = "Помочь Полине выбрать кухню",
            context = "Встреча в выходные"
        ),
        Reminder(
            id = 6,
            tag = "коллега",
            title = "Поделить деньги с Петром",
            date = "05.12.2025",
            time = "11:00",
            shortDescription = "Не забудьте поздравить коллегу с праздником",
            description = "Разделить расходы за поездку",
            context = "После командировки"
        )
    )

    suspend fun getContacts(): Result<List<Contact>> {
        delay(LOADING_DELAY_MS)
        return Result.success(mockContacts)
    }

    suspend fun getReminders(): Result<List<Reminder>> {
        delay(LOADING_DELAY_MS)
        return Result.success(mockReminders)
    }

    suspend fun getReminderById(id: Int): Result<Reminder?> {
        delay(LOADING_DELAY_MS)
        return Result.success(mockReminders.find { it.id == id })
    }

    suspend fun searchContacts(query: String): Result<List<Contact>> {
        delay(LOADING_DELAY_MS)
        val filtered = if (query.isBlank()) {
            mockContacts
        } else {
            mockContacts.filter { contact ->
                contact.name.contains(query, ignoreCase = true) ||
                        contact.tags.any { it.contains(query, ignoreCase = true) }
            }
        }
        return Result.success(filtered)
    }

    suspend fun searchReminders(query: String): Result<List<Reminder>> {
        delay(LOADING_DELAY_MS)
        val filtered = if (query.isBlank()) {
            mockReminders
        } else {
            mockReminders.filter { reminder ->
                reminder.title.contains(query, ignoreCase = true) ||
                        reminder.tag.contains(query, ignoreCase = true) ||
                        reminder.shortDescription.contains(query, ignoreCase = true)
            }
        }
        return Result.success(filtered)
    }
}
