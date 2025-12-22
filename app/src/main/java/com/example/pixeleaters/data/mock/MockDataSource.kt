package com.example.pixeleaters.data.mock

import com.example.pixeleaters.data.model.ApiResponse
import com.example.pixeleaters.data.model.Contact
import kotlinx.coroutines.delay
import kotlin.random.Random

class MockDataSource {

    private suspend fun simulateNetworkDelay(minDelay: Int = 500, maxDelay: Int = 2000) {
        val delayTime = Random.nextInt(minDelay, maxDelay + 1).toLong()
        delay(delayTime)
    }

    private fun shouldSimulateError(): Boolean = Random.nextDouble() < 0.1

    private val allMockContacts by lazy { generateMockContacts(20) }

    private fun generateMockContacts(count: Int): List<Contact> {
        val firstNames = listOf(
            "Иван", "Алексей", "Дмитрий", "Сергей", "Андрей",
            "Мария", "Анна", "Екатерина", "Ольга", "Наталья",
            "Михаил", "Александр", "Артем", "Владимир", "Павел"
        )

        val lastNames = listOf(
            "Иванов", "Петров", "Сидоров", "Кузнецов", "Смирнов",
            "Попов", "Васильев", "Федоров", "Михайлов", "Алексеев"
        )

        val workplaces = listOf(
            "Google", "Яндекс", "VK", "Тинькофф", "Сбер",
            "Ростелеком", "МТС", "Билайн", "Газпром", "Лукойл",
            null, null, null
        )

        val domains = listOf("gmail.com", "yandex.ru", "mail.ru", "outlook.com")

        return (1..count).map { index ->
            val firstName = firstNames.random()
            val lastName = lastNames.random()
            val emailDomain = domains.random()

            Contact(
                id = index.toLong(),
                firstName = firstName,
                lastName = lastName,
                phoneNumber = generatePhoneNumber(),
                email = if (Random.nextBoolean()) "${firstName.lowercase()}.${lastName.lowercase()}@$emailDomain" else null,
                telegram = if (Random.nextBoolean()) "@${firstName.lowercase()}${lastName.first().lowercase()}" else null,
                address = if (Random.nextBoolean()) generateAddress() else null,
                workplace = workplaces.random(),
                categories = generateCategories(),
                profileImageUrl = "https://picsum.photos/200/200?random=$index",
                isFavorite = Random.nextBoolean(),
                createdAt = System.currentTimeMillis() - Random.nextLong(0, 30L * 24 * 60 * 60 * 1000)
            )
        }
    }

    private fun generatePhoneNumber(): String {
        val prefix = listOf("+7", "+375", "+380", "+48").random()
        val number = (1000000000..9999999999).random().toString()
        return "$prefix $number"
    }

    private fun generateAddress(): String {
        val streets = listOf("ул. Ленина", "пр. Мира", "ул. Пушкина", "ул. Гагарина", "бул. Непокоренных")
        return "${streets.random()}, д. ${(1..200).random()}, кв. ${(1..100).random()}"
    }

    private fun generateCategories(): List<String> {
        val allCategories = listOf("Работа", "Друг", "Семья", "Учеба", "Коллега", "Клиент")
        return allCategories
            .shuffled()
            .take(Random.nextInt(1, 4))
            .distinct()
    }

    suspend fun getAllContacts(): ApiResponse<List<Contact>> {
        simulateNetworkDelay()

        return if (shouldSimulateError()) {
            ApiResponse(
                data = emptyList(),
                success = false,
                message = "Ошибка сервера: Не удалось загрузить контакты"
            )
        } else {
            ApiResponse(
                data = allMockContacts
            )
        }
    }

    suspend fun getContactsPaginated(
        page: Int,
        pageSize: Int = 20
    ): ApiResponse<List<Contact>> {
        simulateNetworkDelay()

        return if (shouldSimulateError()) {
            ApiResponse(
                data = emptyList(),
                success = false,
                message = "Ошибка загрузки страницы $page"
            )
        } else {
            val startIndex = page * pageSize
            val endIndex = minOf(startIndex + pageSize, allMockContacts.size)

            if (startIndex >= allMockContacts.size) {
                ApiResponse(
                    data = emptyList(),
                    success = true,
                    message = "Все контакты загружены"
                )
            } else {
                val pageContacts = allMockContacts.subList(startIndex, endIndex)
                ApiResponse(data = pageContacts)
            }
        }
    }

    suspend fun getContactsInitial(pageSize: Int = 20): ApiResponse<List<Contact>> {
        simulateNetworkDelay(1500, 2500)

        return if (shouldSimulateError()) {
            ApiResponse(
                data = emptyList(),
                success = false,
                message = "Ошибка начальной загрузки"
            )
        } else {
            val initialContacts = allMockContacts.take(pageSize)
            ApiResponse(
                data = initialContacts,
                message = "Загружено ${initialContacts.size} из ${allMockContacts.size} контактов"
            )
        }
    }

    suspend fun getContactById(id: Long): ApiResponse<Contact?> {
        simulateNetworkDelay(800, 1500)

        return if (shouldSimulateError()) {
            ApiResponse(
                data = null,
                success = false,
                message = "Ошибка при получении контакта"
            )
        } else {
            val contact = allMockContacts.firstOrNull { it.id == id }
                ?: allMockContacts.first().copy(id = id)
            ApiResponse(data = contact)
        }
    }

    suspend fun searchContacts(query: String): ApiResponse<List<Contact>> {
        simulateNetworkDelay()

        return if (shouldSimulateError()) {
            ApiResponse(
                data = emptyList(),
                success = false,
                message = "Ошибка поиска"
            )
        } else {
            val filtered = allMockContacts.filter { contact ->
                "${contact.firstName} ${contact.lastName}".contains(query, ignoreCase = true) ||
                        contact.phoneNumber?.contains(query, ignoreCase = true) == true ||
                        contact.email?.contains(query, ignoreCase = true) == true ||
                        contact.workplace?.contains(query, ignoreCase = true) == true
            }
            ApiResponse(data = filtered)
        }
    }

    suspend fun getContactsByCategory(category: String): ApiResponse<List<Contact>> {
        simulateNetworkDelay()

        return if (shouldSimulateError()) {
            ApiResponse(
                data = emptyList(),
                success = false,
                message = "Ошибка фильтрации"
            )
        } else {
            val filtered = allMockContacts.filter { it.categories.contains(category) }
            ApiResponse(data = filtered)
        }
    }

    suspend fun addContact(contact: Contact): ApiResponse<Contact> {
        simulateNetworkDelay(1500, 2500)

        return if (shouldSimulateError()) {
            ApiResponse(
                data = contact,
                success = false,
                message = "Ошибка при создании контакта"
            )
        } else {
            ApiResponse(
                data = contact.copy(id = Random.nextLong(1000, 10000))
            )
        }
    }

    suspend fun updateContact(contact: Contact): ApiResponse<Contact> {
        simulateNetworkDelay(1200, 2000)

        return if (shouldSimulateError()) {
            ApiResponse(
                data = contact,
                success = false,
                message = "Ошибка при обновлении контакта"
            )
        } else {
            ApiResponse(data = contact)
        }
    }

    suspend fun deleteContact(id: Long): ApiResponse<Boolean> {
        simulateNetworkDelay(800, 1800)

        return if (shouldSimulateError()) {
            ApiResponse(
                data = false,
                success = false,
                message = "Ошибка при удалении контакта"
            )
        } else {
            ApiResponse(data = true)
        }
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean): ApiResponse<Boolean> {
        simulateNetworkDelay(500, 1000)

        return if (shouldSimulateError()) {
            ApiResponse(
                data = false,
                success = false,
                message = "Ошибка при обновлении избранного"
            )
        } else {
            ApiResponse(data = true)
        }
    }
}