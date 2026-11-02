package com.example.glanceexample.glance

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Приемник связывает события Android AppWidgetManager с нашим Glance-виджетом.
class StockAppWidgetReceiver : GlanceAppWidgetReceiver() {

    // Этот экземпляр будет использоваться системой для показа и обновления виджета.
    override val glanceAppWidget = StockAppWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // При первом добавлении виджета загружаем начальную демонстрационную цену
        // в фоновом потоке, чтобы не выполнять эту работу на главном потоке.
        CoroutineScope(Dispatchers.IO).launch {
            PriceDataRepo.update()
        }
    }
}
