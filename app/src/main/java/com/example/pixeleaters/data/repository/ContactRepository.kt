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
            title = "Иванов Иван — созвон по релизу",
            date = "25.11.2025",
            time = "09:30",
            shortDescription = "Согласовать статус задач и риски",
            description = "Попросить Ивана уточнить дедлайны по своим задачам и подтвердить, что баги P0 закрыты. Обсудить план выкладки и кто на созвоне поддержки.",
            context = "Последний раз обсуждали релиз вчера. Иван обещал прислать список блокеров."
        ),
        Reminder(
            id = 2,
            tag = "работа",
            title = "Анна Петрова — согласовать макеты",
            date = "25.11.2025",
            time = "12:00",
            shortDescription = "Показать итоговые отступы/шрифты и получить ок",
            description = "Отправить Анне скриншоты экранов и уточнить спорные места: размеры иконок в top bar, поведение поиска и теги.",
            context = "Анна из дизайна/продукта. Просила всё 1-в-1 как в Figma."
        ),
        Reminder(
            id = 3,
            tag = "друг",
            title = "Михаил Козлов — вернуть книгу",
            date = "26.11.2025",
            time = "18:30",
            shortDescription = "Договориться где пересечься и отдать",
            description = "Написать Мише и предложить встретиться у метро/в кофейне. Книга: «Clean Architecture».",
            context = "Брали книгу на прошлой неделе. Миша просил вернуть до выходных."
        ),
        Reminder(
            id = 4,
            tag = "коллега",
            title = "Петров Дмитрий — напомнить про документы",
            date = "27.11.2025",
            time = "11:00",
            shortDescription = "Уточнить, что всё отправил в бухгалтерию",
            description = "Спросить у Димы, отправил ли он акт/счёт и нужно ли помочь с формой. Если нет — подсказать, куда именно загрузить.",
            context = "Дима говорил, что “позже скинет”, но дедлайн сегодня."
        ),
        Reminder(
            id = 5,
            tag = "семья",
            title = "Ольга Морозова — поздравить с днём рождения",
            date = "27.11.2025",
            time = "20:30",
            shortDescription = "Короткий звонок и договориться о встрече",
            description = "Позвонить Ольге, поздравить и уточнить, когда удобно заехать/встретиться. Подумать над подарком (сертификат/цветы).",
            context = "Ольга — сестра. В прошлый раз не получилось встретиться из-за работы."
        ),
        Reminder(
            id = 6,
            tag = "друг",
            title = "Денис (шахматы) — подтвердить игру",
            date = "28.11.2025",
            time = "18:40",
            shortDescription = "Уточнить время и место",
            description = "Написать Денису: играем ли сегодня, во сколько и где. Если отмена — перенести на выходные.",
            context = "Денис обычно отвечает вечером. В прошлый раз переносили из-за дождя."
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
