package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TimeSeriesPoint
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.PrimaryPurple
import java.util.Locale

@Composable
fun InteractiveEarningsChart(
    points: List<TimeSeriesPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = PrimaryPurple,
    accentColor: Color = AccentGreen
) {
    var selectedIndex by remember(points) { mutableStateOf<Int?>(null) }

    if (points.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No earnings recorded for this timeframe",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
        return
    }

    val maxEarnings = remember(points) {
        val max = points.maxOfOrNull { it.earnings } ?: 100.0
        if (max <= 0.0) 100.0 else max * 1.15
    }

    val selectedPoint = selectedIndex?.let { points.getOrNull(it) } ?: points.lastOrNull()
    val fillPath = remember { Path() }
    val linePath = remember { Path() }

    Column(modifier = modifier.fillMaxWidth()) {
        // Selected metric header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = selectedPoint?.let { "${it.label} Performance" } ?: "Overview",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$${String.format(Locale.US, "%.2f", selectedPoint?.earnings ?: 0.0)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (selectedPoint != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${selectedPoint.salesCount} Units Sold",
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Chart Canvas
        val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        val n = points.size

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(points) {
                        detectTapGestures { offset ->
                            val sectionWidth = size.width / (n.coerceAtLeast(1))
                            val idx = (offset.x / sectionWidth).toInt().coerceIn(0, n - 1)
                            selectedIndex = idx
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val horizontalStep = if (n > 1) width / (n - 1) else width

                // Horizontal subtle grid lines
                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = height * (i.toFloat() / gridLines)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                if (n == 0) return@Canvas

                fillPath.reset()
                linePath.reset()

                val xCoords = FloatArray(n)
                val yCoords = FloatArray(n)

                for (idx in 0 until n) {
                    val x = if (n > 1) idx * horizontalStep else width / 2f
                    val yFraction = (points[idx].earnings / maxEarnings).toFloat().coerceIn(0f, 1f)
                    val y = height - (yFraction * (height - 24f)) - 12f
                    xCoords[idx] = x
                    yCoords[idx] = y
                }

                // Construct fill path
                fillPath.moveTo(xCoords[0], height)
                fillPath.lineTo(xCoords[0], yCoords[0])
                for (i in 1 until n) {
                    val prevX = xCoords[i - 1]
                    val prevY = yCoords[i - 1]
                    val currX = xCoords[i]
                    val currY = yCoords[i]
                    val midX = (prevX + currX) / 2
                    fillPath.cubicTo(midX, prevY, midX, currY, currX, currY)
                }
                fillPath.lineTo(xCoords[n - 1], height)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = 0.35f),
                            lineColor.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = height
                    )
                )

                // Construct line path
                linePath.moveTo(xCoords[0], yCoords[0])
                for (i in 1 until n) {
                    val prevX = xCoords[i - 1]
                    val prevY = yCoords[i - 1]
                    val currX = xCoords[i]
                    val currY = yCoords[i]
                    val midX = (prevX + currX) / 2
                    linePath.cubicTo(midX, prevY, midX, currY, currX, currY)
                }

                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw circles
                val activeIdx = selectedIndex ?: (n - 1)
                for (i in 0 until n) {
                    val isSelected = i == activeIdx
                    val cx = xCoords[i]
                    val cy = yCoords[i]

                    if (isSelected) {
                        drawLine(
                            color = accentColor.copy(alpha = 0.4f),
                            start = Offset(cx, 0f),
                            end = Offset(cx, height),
                            strokeWidth = 1.5.dp.toPx()
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 7.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                        drawCircle(
                            color = accentColor,
                            radius = 5.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    } else {
                        drawCircle(
                            color = Color.White,
                            radius = 4.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                        drawCircle(
                            color = lineColor,
                            radius = 3.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
        }

        // Labels row under the chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEachIndexed { idx, point ->
                val isSelected = (selectedIndex ?: (n - 1)) == idx
                Text(
                    text = point.label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
