package com.example.glanceexample.glance

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

// Объект создается один раз, поэтому цена и ее изменение общие для приложения и виджета.
object PriceDataRepo {
    // Биржевой тикер, который будет показан в виджете.
    var ticker = "GOOGL"

    // Предыдущая цена нужна для вычисления изменения в процентах.
    private var previousPrice = 0f
    var change = 0

    // StateFlow хранит последнее значение цены и сообщает о нем подписчикам.
    private val _currentPrice = MutableStateFlow(0f)

    // Наружу отдаем только интерфейс чтения, чтобы менять цену можно было через update().
    val currentPrice: StateFlow<Float> get() = _currentPrice

    fun update() {
        // Сохраняем цену до обновления, затем генерируем новое демонстрационное значение.
        previousPrice = currentPrice.value
        _currentPrice.value = Random.nextInt(20, 35) + Random.nextFloat()

        // Сравниваем новую цену с предыдущей и сохраняем процентное изменение целым числом.
        change = ((_currentPrice.value - previousPrice) / previousPrice * 100).toInt()
    }
}
