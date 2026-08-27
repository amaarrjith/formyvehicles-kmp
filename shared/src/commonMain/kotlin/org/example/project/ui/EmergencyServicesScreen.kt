package org.example.project.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import formyvehiclesai.shared.generated.resources.Res
import formyvehiclesai.shared.generated.resources.eme_accident
import formyvehiclesai.shared.generated.resources.eme_break
import formyvehiclesai.shared.generated.resources.eme_elec
import formyvehiclesai.shared.generated.resources.eme_mech
import formyvehiclesai.shared.generated.resources.eme_medical
import formyvehiclesai.shared.generated.resources.eme_tyre
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

data class EmergencyItem(
    val id: String,
    val title: String,
    val backgroundColor: Color,
    val imageRes: DrawableResource? = null,
    val iconDrawer: (@Composable () -> Unit)? = null
)

@Composable
fun EmergencyServicesScreen(
    onCategoryClick: ((EmergencyItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        EmergencyItem("tyre", "Tyre Works", Color(0xFFF0EEFF), imageRes = Res.drawable.eme_tyre, iconDrawer = { TyreIcon() }),
        EmergencyItem("mechanical", "Mechanical", Color(0xFFF0EEFF), imageRes = Res.drawable.eme_mech, iconDrawer = { MechanicalIcon() }),
        EmergencyItem("medical", "Medical", Color(0xFFFEEAE6), imageRes = Res.drawable.eme_medical, iconDrawer = { MedicalIcon() }),
        EmergencyItem("electrical", "Electrical", Color(0xFFDCF7EC), imageRes = Res.drawable.eme_elec, iconDrawer = { ElectricalIcon() }),
        EmergencyItem("breakdown", "Breakdown", Color(0xFFDCF7EC), imageRes = Res.drawable.eme_break, iconDrawer = { BreakdownIcon() }),
        EmergencyItem("accident", "Accident", Color(0xFFFEEAE6), imageRes = Res.drawable.eme_accident, iconDrawer = { AccidentIcon() }),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFC))
            .statusBarsPadding()
            .padding(top = 10.dp)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Emergency Services",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Select a service for instant 24/7 roadside help",
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 3x3 GRID LAYOUT
        val chunkedItems = items.chunked(3)
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            chunkedItems.forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowItems.forEach { item ->
                        Box(modifier = Modifier.weight(1f)) {
                            EmergencyCard(
                                item = item,
                                onClick = { onCategoryClick?.invoke(item) }
                            )
                        }
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun EmergencyCard(
    item: EmergencyItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.92f)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = item.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageRes != null) {
                    Image(
                        painter = painterResource(item.imageRes),
                        contentDescription = item.title,
                        modifier = Modifier.size(54.dp)
                    )
                } else if (item.iconDrawer != null) {
                    item.iconDrawer.invoke()
                }
            }

            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2C3545),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

// VECTOR CANVAS DRAWERS
@Composable
fun TyreIcon() {
    Canvas(modifier = Modifier.size(48.dp)) {
        val darkColor = Color(0xFF2C3545)
        drawCircle(color = darkColor, radius = size.width * 0.35f, center = Offset(size.width * 0.38f, size.height * 0.45f), style = Stroke(width = 4.dp.toPx()))
        drawCircle(color = darkColor, radius = size.width * 0.14f, center = Offset(size.width * 0.38f, size.height * 0.45f), style = Stroke(width = 3.dp.toPx()))
        drawRoundRect(color = darkColor, topLeft = Offset(size.width * 0.65f, size.height * 0.15f), size = Size(size.width * 0.22f, size.height * 0.7f), cornerRadius = CornerRadius(6.dp.toPx()))
    }
}

@Composable
fun MechanicalIcon() {
    Canvas(modifier = Modifier.size(48.dp)) {
        val darkColor = Color(0xFF2C3545)
        val carBody = Path().apply {
            moveTo(size.width * 0.1f, size.height * 0.6f)
            lineTo(size.width * 0.35f, size.height * 0.35f)
            lineTo(size.width * 0.65f, size.height * 0.35f)
            lineTo(size.width * 0.85f, size.height * 0.6f)
            lineTo(size.width * 0.85f, size.height * 0.75f)
            lineTo(size.width * 0.1f, size.height * 0.75f)
            close()
        }
        drawPath(carBody, color = darkColor, style = Stroke(width = 3.dp.toPx(), join = StrokeJoin.Round))
        drawCircle(color = darkColor, radius = 5.dp.toPx(), center = Offset(size.width * 0.28f, size.height * 0.75f))
        drawCircle(color = darkColor, radius = 5.dp.toPx(), center = Offset(size.width * 0.72f, size.height * 0.75f))
        drawLine(darkColor, Offset(size.width * 0.15f, size.height * 0.6f), Offset(size.width * 0.05f, size.height * 0.3f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun MedicalIcon() {
    Canvas(modifier = Modifier.size(48.dp)) {
        val darkColor = Color(0xFF2C3545)
        val shieldPath = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.1f)
            lineTo(size.width * 0.85f, size.height * 0.22f)
            lineTo(size.width * 0.85f, size.height * 0.55f)
            quadraticTo(size.width * 0.85f, size.height * 0.85f, size.width * 0.5f, size.height * 0.95f)
            quadraticTo(size.width * 0.15f, size.height * 0.85f, size.width * 0.15f, size.height * 0.55f)
            lineTo(size.width * 0.15f, size.height * 0.22f)
            close()
        }
        drawPath(shieldPath, color = darkColor)
        val crossWidth = 6.dp.toPx()
        val crossLen = 16.dp.toPx()
        val cx = size.width * 0.5f
        val cy = size.height * 0.5f
        drawRoundRect(Color.White, topLeft = Offset(cx - crossWidth / 2, cy - crossLen / 2), size = Size(crossWidth, crossLen), cornerRadius = CornerRadius(2.dp.toPx()))
        drawRoundRect(Color.White, topLeft = Offset(cx - crossLen / 2, cy - crossWidth / 2), size = Size(crossLen, crossWidth), cornerRadius = CornerRadius(2.dp.toPx()))
    }
}

@Composable
fun ElectricalIcon() {
    Canvas(modifier = Modifier.size(48.dp)) {
        val darkColor = Color(0xFF2C3545)
        drawRoundRect(color = darkColor, topLeft = Offset(size.width * 0.15f, size.height * 0.35f), size = Size(size.width * 0.7f, size.height * 0.5f), cornerRadius = CornerRadius(4.dp.toPx()))
        drawRect(darkColor, topLeft = Offset(size.width * 0.25f, size.height * 0.25f), size = Size(size.width * 0.15f, size.height * 0.1f))
        drawRect(darkColor, topLeft = Offset(size.width * 0.6f, size.height * 0.25f), size = Size(size.width * 0.15f, size.height * 0.1f))
        val bolt = Path().apply {
            moveTo(size.width * 0.52f, size.height * 0.4f)
            lineTo(size.width * 0.42f, size.height * 0.6f)
            lineTo(size.width * 0.5f, size.height * 0.6f)
            lineTo(size.width * 0.48f, size.height * 0.8f)
            lineTo(size.width * 0.6f, size.height * 0.55f)
            lineTo(size.width * 0.52f, size.height * 0.55f)
            close()
        }
        drawPath(bolt, color = Color.White)
    }
}

@Composable
fun BreakdownIcon() {
    Canvas(modifier = Modifier.size(48.dp)) {
        val darkColor = Color(0xFF2C3545)
        drawRoundRect(darkColor, topLeft = Offset(size.width * 0.45f, size.height * 0.45f), size = Size(size.width * 0.45f, size.height * 0.35f), cornerRadius = CornerRadius(4.dp.toPx()))
        drawLine(darkColor, Offset(size.width * 0.15f, size.height * 0.75f), Offset(size.width * 0.45f, size.height * 0.3f), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
        val hook = Path().apply {
            moveTo(size.width * 0.15f, size.height * 0.75f)
            lineTo(size.width * 0.15f, size.height * 0.6f)
        }
        drawPath(hook, color = darkColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        drawCircle(darkColor, radius = 5.dp.toPx(), center = Offset(size.width * 0.3f, size.height * 0.8f))
        drawCircle(darkColor, radius = 5.dp.toPx(), center = Offset(size.width * 0.75f, size.height * 0.8f))
    }
}

@Composable
fun AccidentIcon() {
    Canvas(modifier = Modifier.size(48.dp)) {
        val darkColor = Color(0xFF2C3545)
        val burst = Path().apply {
            moveTo(size.width * 0.15f, size.height * 0.45f)
            lineTo(size.width * 0.35f, size.height * 0.35f)
            lineTo(size.width * 0.25f, size.height * 0.55f)
            lineTo(size.width * 0.45f, size.height * 0.48f)
            close()
        }
        drawPath(burst, color = darkColor)
        val car = Path().apply {
            moveTo(size.width * 0.4f, size.height * 0.6f)
            lineTo(size.width * 0.55f, size.height * 0.4f)
            lineTo(size.width * 0.85f, size.height * 0.45f)
            lineTo(size.width * 0.95f, size.height * 0.65f)
            lineTo(size.width * 0.95f, size.height * 0.8f)
            lineTo(size.width * 0.4f, size.height * 0.8f)
            close()
        }
        drawPath(car, color = darkColor)
        drawCircle(darkColor, radius = 4.dp.toPx(), center = Offset(size.width * 0.52f, size.height * 0.8f))
        drawCircle(darkColor, radius = 4.dp.toPx(), center = Offset(size.width * 0.85f, size.height * 0.8f))
    }
}

@Composable
fun FuelIcon() {
    Canvas(modifier = Modifier.size(48.dp)) {
        val darkColor = Color(0xFF2C3545)
        drawRoundRect(darkColor, topLeft = Offset(size.width * 0.2f, size.height * 0.25f), size = Size(size.width * 0.45f, size.height * 0.6f), cornerRadius = CornerRadius(4.dp.toPx()))
        drawRoundRect(Color.White, topLeft = Offset(size.width * 0.28f, size.height * 0.35f), size = Size(size.width * 0.29f, size.height * 0.18f), cornerRadius = CornerRadius(2.dp.toPx()))
        val hose = Path().apply {
            moveTo(size.width * 0.65f, size.height * 0.4f)
            lineTo(size.width * 0.8f, size.height * 0.4f)
            lineTo(size.width * 0.8f, size.height * 0.75f)
        }
        drawPath(hose, color = darkColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun LocksmithIcon() {
    Canvas(modifier = Modifier.size(48.dp)) {
        val darkColor = Color(0xFF2C3545)
        drawCircle(darkColor, radius = 10.dp.toPx(), center = Offset(size.width * 0.35f, size.height * 0.45f), style = Stroke(width = 4.dp.toPx()))
        drawLine(darkColor, Offset(size.width * 0.52f, size.height * 0.45f), Offset(size.width * 0.85f, size.height * 0.45f), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
        drawLine(darkColor, Offset(size.width * 0.7f, size.height * 0.45f), Offset(size.width * 0.7f, size.height * 0.62f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
        drawLine(darkColor, Offset(size.width * 0.82f, size.height * 0.45f), Offset(size.width * 0.82f, size.height * 0.62f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Composable
fun TowingIcon() {
    Canvas(modifier = Modifier.size(48.dp)) {
        val darkColor = Color(0xFF2C3545)
        drawArc(
            color = darkColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(size.width * 0.25f, size.height * 0.3f),
            size = Size(size.width * 0.5f, size.height * 0.5f)
        )
        drawRoundRect(darkColor, topLeft = Offset(size.width * 0.15f, size.height * 0.55f), size = Size(size.width * 0.7f, size.height * 0.2f), cornerRadius = CornerRadius(3.dp.toPx()))
    }
}
