package com.example.glanceexample.glance

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight as GlanceFontWeight
import com.example.glanceexample.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.Locale

class StockAppWidget : GlanceAppWidget() {

    private var job: Job? = null

    private companion object {
        val smallMode = DpSize(100.dp, 80.dp)
        val mediumMode = DpSize(120.dp, 120.dp)
    }

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(smallMode, mediumMode)
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        if (job == null) {
            job = startUpdateJob(
                Duration.ofSeconds(20).toMillis(),
                context
            )
        }

        provideContent {
            GlanceTheme {
                GlanceContent()
            }
        }
    }

    private fun startUpdateJob(timeInterval: Long, context: Context): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                PriceDataRepo.update()
                StockAppWidget().updateAll(context)
                delay(timeInterval)
            }
        }
    }

    @Composable
    fun GlanceContent() {
        val stateCount by PriceDataRepo.currentPrice.collectAsState()
        val size = LocalSize.current

        when (size) {
            smallMode -> Small(stateCount)
            mediumMode -> Medium(stateCount)
            else -> Small(stateCount)
        }
    }

    @Composable
    private fun Small(stateCount: Float) {
        Column(
            modifier = GlanceModifier
                .clickable { refreshPrice() }
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(8.dp)
        ) {
            StockDisplay(stateCount)
        }
    }

    @Composable
    private fun Medium(stateCount: Float) {
        Column(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            modifier = GlanceModifier
                .clickable { refreshPrice() }
                .fillMaxSize()
                .cornerRadius(15.dp)
                .background(GlanceTheme.colors.background)
                .padding(8.dp)
        ) {
            StockDisplay(stateCount)
            Image(
                provider = ImageProvider(
                    if (PriceDataRepo.change > 0) {
                        R.drawable.up_arrow
                    } else {
                        R.drawable.down_arrow
                    }
                ),
                contentDescription = "Arrow Image",
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(20.dp)
            )
        }
    }

    private fun refreshPrice() {
        PriceDataRepo.update()
    }

    @Composable
    private fun StockDisplay(stateCount: Float) {
        val color = if (PriceDataRepo.change > 0) {
            GlanceTheme.colors.primary
        } else {
            GlanceTheme.colors.error
        }
        val textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = GlanceFontWeight.Bold,
            color = color
        )

        Text(
            text = PriceDataRepo.ticker,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = GlanceFontWeight.Bold
            )
        )
        Text(
            text = String.format(Locale.getDefault(), "%.2f", stateCount),
            style = textStyle
        )
        Text(
            text = "${PriceDataRepo.change} %",
            style = textStyle
        )
    }
}
