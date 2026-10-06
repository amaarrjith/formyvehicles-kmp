package org.example.project

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
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
import formyvehiclesai.shared.generated.resources.img_car_swift
import formyvehiclesai.shared.generated.resources.img_bike_glamour
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.delay

sealed interface VehicleStatusUiState {
    object Loading : VehicleStatusUiState
    data class Success(val statuses: List<VehicleStatus>) : VehicleStatusUiState
    data class Error(val message: String) : VehicleStatusUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleStatusScreen(
    onBackClick: () -> Unit,
    regNumber: String? = null,
    modifier: Modifier = Modifier,
    viewModel: VehicleStatusViewModel = org.koin.compose.koinInject()
) {
    LaunchedEffect(regNumber) {
        viewModel.loadVehiclesAndStatus(regNumber)
    }

    val expandedDates = remember { mutableStateMapOf<String, Boolean>() }
    val todayDateString = viewModel.todayDateString
    val todayDays = getEpochDays(todayDateString)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppBackButton {
                    onBackClick()
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "My Vehicle Status",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            // Results selector dropdown & Filter badge row
            val rotationAngle by animateFloatAsState(
                targetValue = if (viewModel.isChooseVehicleSheetOpen) 180f else 0f,
                animationSpec = tween(durationMillis = 250)
            )
            val selectedOption = viewModel.selectedOption

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Selector
                Column(
                    modifier = Modifier
                        .clickable { viewModel.openChooseVehicleSheet() }
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Result Showing for",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedOption.title}\n(${selectedOption.subtitle.ifBlank { "${viewModel.userVehicles.size} Vehicles" }})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(17.dp))
                        Canvas(
                            modifier = Modifier
                                .size(14.dp)
                                .rotate(rotationAngle)
                        ) {
                            val path = Path().apply {
                                moveTo(size.width * 0.2f, size.height * 0.38f)
                                lineTo(size.width * 0.5f, size.height * 0.68f)
                                lineTo(size.width * 0.8f, size.height * 0.38f)
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFF64748B),
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }

                // Filter Pill Button matching Reference Image 1
//                Row(
//                    modifier = Modifier
//                        .height(38.dp)
//                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
//                        .background(Color.White, RoundedCornerShape(20.dp))
//                        .clickable { /* Toggle filter */ }
//                        .padding(horizontal = 14.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Canvas(modifier = Modifier.size(16.dp)) {
//                        drawLine(Color(0xFF1E293B), Offset(0f, size.height * 0.25f), Offset(size.width, size.height * 0.25f), strokeWidth = 1.8.dp.toPx())
//                        drawLine(Color(0xFF1E293B), Offset(0f, size.height * 0.75f), Offset(size.width, size.height * 0.75f), strokeWidth = 1.8.dp.toPx())
//                        drawCircle(Color(0xFF1E293B), radius = 2.5.dp.toPx(), center = Offset(size.width * 0.35f, size.height * 0.25f))
//                        drawCircle(Color(0xFF1E293B), radius = 2.5.dp.toPx(), center = Offset(size.width * 0.65f, size.height * 0.75f))
//                    }
//                    Spacer(modifier = Modifier.width(6.dp))
//                    Text(
//                        text = "Filter",
//                        color = Color(0xFF1E293B),
//                        fontSize = 14.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//
//                    if (viewModel.activeFilterCount > 0) {
//                        Spacer(modifier = Modifier.width(6.dp))
//                        Box(
//                            modifier = Modifier
//                                .size(20.dp)
//                                .background(Color(0xFF6338F6), CircleShape),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = viewModel.activeFilterCount.toString(),
//                                color = Color.White,
//                                fontSize = 11.sp,
//                                fontWeight = FontWeight.Bold,
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                    }
//                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (val state = viewModel.uiState) {
                is VehicleStatusUiState.Loading -> {
                    AppLoader(message = "Generating status timeline...")
                }
                is VehicleStatusUiState.Success -> {
                    if (viewModel.statusList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No status available", fontSize = 16.sp, color = Color.Gray)
                        }
                    } else {
                        // Group statuses by date
                        val groupedByDate = remember(viewModel.statusList) {
                            viewModel.statusList.groupBy { it.date }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            val dates = groupedByDate.keys.toList()
                            dates.forEachIndexed { dateIndex, date ->
                                val dateStatuses = groupedByDate[date] ?: emptyList()
                                val currentMonth = getMonthHeader(date)
                                val prevDate = if (dateIndex > 0) dates[dateIndex - 1] else null
                                val showHeader = dateIndex == 0 || (prevDate != null && getMonthHeader(prevDate) != currentMonth)

                                item(key = "header_$date") {
                                    if (showHeader) {
                                        MonthHeaderDivider(monthName = currentMonth)
                                    }
                                }

                                val isExpanded = expandedDates[date] ?: false
                                val visibleStatuses = if (isExpanded || dateStatuses.size <= 1) dateStatuses else listOf(dateStatuses.first())

                                itemsIndexed(visibleStatuses, key = { _, status -> status.id }) { index, status ->
                                    val itemDays = getEpochDays(status.date)
                                    val diff = itemDays - todayDays
                                    val isDone = diff < 0
                                    val daysOffsetString = if (isDone) "Done" else "${diff} Days"
                                    
                                    TimelineEventRow(
                                        status = status,
                                        isDone = isDone,
                                        daysOffset = daysOffsetString,
                                        isLast = dateIndex == dates.lastIndex && index == visibleStatuses.lastIndex,
                                        onStatusClick = {
                                            viewModel.selectedStatusDetail = status
                                        }
                                    )
                                }

                                // If day has 2 or more statuses, show "Show All (N)" button
                                if (dateStatuses.size > 1) {
                                    item(key = "expand_$date") {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 138.dp, bottom = 12.dp)
                                                .clickable {
                                                    expandedDates[date] = !isExpanded
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isExpanded) "- Hide Details" else "Show All (${dateStatuses.size})",
                                                color = Color(0xFF6366F1),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is VehicleStatusUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red, fontSize = 16.sp)
                    }
                }
            }
        }

        // Floating Action Button
        if (viewModel.uiState is VehicleStatusUiState.Success) {
            FloatingActionButton(
                onClick = { viewModel.isAddStatusSheetOpen = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .offset(y = (-64).dp),
                shape = CircleShape,
                containerColor = Color(0xFF6366F1),
                contentColor = Color.White
            ) {
                Canvas(modifier = Modifier.size(20.dp)) {
                    drawLine(
                        color = Color.White,
                        start = Offset(size.width * 0.5f, 0f),
                        end = Offset(size.width * 0.5f, size.height),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, size.height * 0.5f),
                        end = Offset(size.width, size.height * 0.5f),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // "Choose Vehicle" Modal Bottom Sheet matching user screenshot
        if (viewModel.isChooseVehicleSheetOpen) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.isChooseVehicleSheetOpen = false },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Choose Vehicle",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Option 1: All Vehicle
                        val isAllSelected = viewModel.tempSelectedVehicleFilter == "All"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.tempSelectedVehicleFilter = "All" },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                width = if (isAllSelected) 1.8.dp else 1.dp,
                                color = if (isAllSelected) Color(0xFF6366F1) else Color(0xFFE2E8F0)
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAllSelected) Color(0xFFF5F3FF) else Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left Radio / Checkmark ic_filter
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            if (isAllSelected) Color(0xFF6366F1) else Color.Transparent,
                                            CircleShape
                                        )
                                        .border(
                                            width = if (isAllSelected) 0.dp else 1.5.dp,
                                            color = if (isAllSelected) Color.Transparent else Color(0xFF94A3B8),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isAllSelected) {
                                        Canvas(modifier = Modifier.size(12.dp)) {
                                            val checkPath = Path().apply {
                                                moveTo(size.width * 0.2f, size.height * 0.5f)
                                                lineTo(size.width * 0.45f, size.height * 0.75f)
                                                lineTo(size.width * 0.85f, size.height * 0.25f)
                                            }
                                            drawPath(
                                                path = checkPath,
                                                color = Color.White,
                                                style = Stroke(
                                                    width = 2.5.dp.toPx(),
                                                    cap = StrokeCap.Round,
                                                    join = StrokeJoin.Round
                                                )
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "All Vehicle",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = "Default",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Shows All Vehicle status in one Timeline",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        // Option 2...N: Vehicles added in DB
                        viewModel.userVehicles.forEach { vehicle ->
                            val isVehicleSelected = viewModel.tempSelectedVehicleFilter == vehicle.registrationNumber
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.tempSelectedVehicleFilter = vehicle.registrationNumber },
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    width = if (isVehicleSelected) 1.8.dp else 1.dp,
                                    color = if (isVehicleSelected) Color(0xFF6366F1) else Color(0xFFE2E8F0)
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isVehicleSelected) Color(0xFFF5F3FF) else Color.White
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Left Selection Radio
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                if (isVehicleSelected) Color(0xFF6366F1) else Color.Transparent,
                                                CircleShape
                                            )
                                            .border(
                                                width = if (isVehicleSelected) 0.dp else 1.5.dp,
                                                color = if (isVehicleSelected) Color.Transparent else Color(0xFF94A3B8),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isVehicleSelected) {
                                            Canvas(modifier = Modifier.size(12.dp)) {
                                                val checkPath = Path().apply {
                                                    moveTo(size.width * 0.2f, size.height * 0.5f)
                                                    lineTo(size.width * 0.45f, size.height * 0.75f)
                                                    lineTo(size.width * 0.85f, size.height * 0.25f)
                                                }
                                                drawPath(
                                                    path = checkPath,
                                                    color = Color.White,
                                                    style = Stroke(
                                                        width = 2.5.dp.toPx(),
                                                        cap = StrokeCap.Round,
                                                        join = StrokeJoin.Round
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = vehicle.registrationNumber,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${vehicle.brand} ${vehicle.model}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = vehicle.brand,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = Color(0xFF64748B)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${vehicle.year} ${vehicle.fuelType} ${vehicle.gearType}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Vehicle Image Thumbnail
                                    Image(
                                        painter = painterResource(
                                            if (vehicle.vehicleType.contains("Bike", ignoreCase = true) || vehicle.vehicleType.contains("Two", ignoreCase = true)) Res.drawable.img_bike_glamour else Res.drawable.img_car_swift
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(90.dp, 60.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE2E8F0))
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom Action Row matching screenshot: < Back and Continue
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable { viewModel.isChooseVehicleSheetOpen = false }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Canvas(modifier = Modifier.size(16.dp)) {
                                val arrow = Path().apply {
                                    moveTo(size.width * 0.65f, size.height * 0.2f)
                                    lineTo(size.width * 0.35f, size.height * 0.5f)
                                    lineTo(size.width * 0.65f, size.height * 0.8f)
                                }
                                drawPath(
                                    path = arrow,
                                    color = Color(0xFF1E293B),
                                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Back",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Button(
                            onClick = { viewModel.applyChooseVehicleSelection() },
                            modifier = Modifier
                                .height(52.dp)
                                .width(160.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Continue",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Status Detail Modal Bottom Sheet when user taps any status card
        val selectedStatus = viewModel.selectedStatusDetail
        if (selectedStatus != null) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.selectedStatusDetail = null },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 28.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    when (selectedStatus.type) {
                                        "Service" -> Color(0xFFEEF2FF)
                                        "Alert" -> Color(0xFFFEF2F2)
                                        "Insurance" -> Color(0xFFECFDF5)
                                        else -> Color(0xFFF1F5F9)
                                    },
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = selectedStatus.type,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (selectedStatus.type) {
                                    "Service" -> Color(0xFF6366F1)
                                    "Alert" -> Color(0xFFEF4444)
                                    "Insurance" -> Color(0xFF10B981)
                                    else -> Color(0xFF64748B)
                                }
                            )
                        }

                        Text(
                            text = selectedStatus.date,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = selectedStatus.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Associated Vehicle: ${selectedStatus.vehicleName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6366F1)
                    )

                    if (selectedStatus.cycle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Recurrence: ${selectedStatus.cycle}",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569)
                        )
                    }

                    if (selectedStatus.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = selectedStatus.description,
                            fontSize = 15.sp,
                            color = Color(0xFF475569)
                        )
                    }

                    if (!selectedStatus.alertTime.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Reminder: ${selectedStatus.alertTime}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.selectedStatusDetail = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6366F1),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Close Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // "Add New Info" Modal Bottom Sheet
        if (viewModel.isAddStatusSheetOpen) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.isAddStatusSheetOpen = false },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.88f)
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Add New Info",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title Input
                        AppTextField(
                            value = viewModel.statusTitle,
                            onValueChange = { viewModel.statusTitle = it },
                            title = "Title",
                            isMandatory = false,
                            isSecure = false,
                            placeholder = "Ex. My Car Loan - ₹ 12000"
                        )

                        // Vehicle Dropdown
                        AppDropdown(
                            value = viewModel.vehicleAssociation,
                            onValueChange = { viewModel.vehicleAssociation = it },
                            title = "Vehicle",
                            options = viewModel.availableVehicleOptions,
                            isMandatory = false,
                            placeholder = "All Vehicles"
                        )

                        // Type Dropdown
                        AppDropdown(
                            value = viewModel.statusType,
                            onValueChange = { viewModel.statusType = it },
                            title = "Type",
                            options = listOf("Alert", "Service", "Challan", "PUC", "Insurance", "Toll"),
                            isMandatory = false,
                            placeholder = "Alert"
                        )

                        // Info Date (Date for which info is being added)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Date",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = viewModel.infoDate,
                                onValueChange = { viewModel.infoDate = it },
                                readOnly = true,
                                enabled = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.isInfoDatePickerOpen = true },
                                placeholder = { Text(text = "Select Info Date", color = Color(0xFF94A3B8)) },
                                trailingIcon = {
                                    IconButton(onClick = { viewModel.isInfoDatePickerOpen = true }) {
                                        Canvas(modifier = Modifier.size(20.dp)) {
                                            val rectPath = Path().apply {
                                                moveTo(size.width * 0.15f, size.height * 0.25f)
                                                lineTo(size.width * 0.85f, size.height * 0.25f)
                                                lineTo(size.width * 0.85f, size.height * 0.85f)
                                                lineTo(size.width * 0.15f, size.height * 0.85f)
                                                close()
                                            }
                                            drawPath(
                                                path = rectPath,
                                                color = Color(0xFF64748B),
                                                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                            )
                                            drawLine(Color(0xFF64748B), Offset(size.width * 0.15f, size.height * 0.45f), Offset(size.width * 0.85f, size.height * 0.45f), strokeWidth = 1.5.dp.toPx())
                                            drawLine(Color(0xFF64748B), Offset(size.width * 0.35f, size.height * 0.15f), Offset(size.width * 0.35f, size.height * 0.3f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
                                            drawLine(Color(0xFF64748B), Offset(size.width * 0.65f, size.height * 0.15f), Offset(size.width * 0.65f, size.height * 0.3f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6366F1),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B)
                                )
                            )
                        }

                        // Alert Date & Time Field with Date & Time Picker Triggers
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Alert Date & Time (Optional)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF475569)
                                )
                                if (viewModel.alertDate.isNotBlank()) {
                                    Text(
                                        text = "Clear Alert",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444),
                                        modifier = Modifier.clickable { viewModel.clearAlertDateTime() }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = viewModel.alertDateTimeDisplay,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.isAlertDatePickerOpen = true },
                                placeholder = { Text(text = "Ex. 20 Jan 2025 - 10:00 am", color = Color(0xFF94A3B8)) },
                                trailingIcon = {
                                    IconButton(onClick = { viewModel.isAlertDatePickerOpen = true }) {
                                        Canvas(modifier = Modifier.size(20.dp)) {
                                            val rectPath = Path().apply {
                                                moveTo(size.width * 0.15f, size.height * 0.25f)
                                                lineTo(size.width * 0.85f, size.height * 0.25f)
                                                lineTo(size.width * 0.85f, size.height * 0.85f)
                                                lineTo(size.width * 0.15f, size.height * 0.85f)
                                                close()
                                            }
                                            drawPath(
                                                path = rectPath,
                                                color = Color(0xFF64748B),
                                                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                            )
                                            drawLine(Color(0xFF64748B), Offset(size.width * 0.15f, size.height * 0.45f), Offset(size.width * 0.85f, size.height * 0.45f), strokeWidth = 1.5.dp.toPx())
                                            drawLine(Color(0xFF64748B), Offset(size.width * 0.35f, size.height * 0.15f), Offset(size.width * 0.35f, size.height * 0.3f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
                                            drawLine(Color(0xFF64748B), Offset(size.width * 0.65f, size.height * 0.15f), Offset(size.width * 0.65f, size.height * 0.3f), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6366F1),
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B)
                                )
                            )
                        }

                        // Cycle Pill Selection (Initial selection is "Ones")
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Cycle",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                viewModel.cycleOptions.forEach { cycle ->
                                    val isSelected = cycle == viewModel.selectedCycle
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(if (isSelected) Color(0xFF6366F1) else Color(0xFFF1F5F9))
                                            .clickable { viewModel.selectedCycle = cycle }
                                            .padding(horizontal = 16.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cycle,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) Color.White else Color(0xFF1E293B)
                                        )
                                    }
                                }
                            }
                        }

                        // Dynamic Info Preview Banner (ONLY shown when alertDate is chosen!)
                        val message = viewModel.dynamicInfoMessage
                        if (message != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = message,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE2E8F0))
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom Action Row matching screenshot: < Back and Create
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable { viewModel.isAddStatusSheetOpen = false }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Canvas(modifier = Modifier.size(16.dp)) {
                                val arrow = Path().apply {
                                    moveTo(size.width * 0.65f, size.height * 0.2f)
                                    lineTo(size.width * 0.35f, size.height * 0.5f)
                                    lineTo(size.width * 0.65f, size.height * 0.8f)
                                }
                                drawPath(
                                    path = arrow,
                                    color = Color(0xFF1E293B),
                                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Back",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        Button(
                            onClick = { viewModel.submitStatus() },
                            modifier = Modifier
                                .height(52.dp)
                                .width(160.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Create",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Info Date Picker Dialog
        if (viewModel.isInfoDatePickerOpen) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { viewModel.isInfoDatePickerOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedMillis = datePickerState.selectedDateMillis
                            if (selectedMillis != null) {
                                val epochDays = (selectedMillis / (1000 * 60 * 60 * 24)).toInt()
                                viewModel.infoDate = dateStringFromEpochDays(epochDays)
                            }
                            viewModel.isInfoDatePickerOpen = false
                        }
                    ) {
                        Text("OK", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.isInfoDatePickerOpen = false }) {
                        Text("Cancel", color = Color(0xFF64748B))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Alert Date Picker Dialog
        if (viewModel.isAlertDatePickerOpen) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { viewModel.isAlertDatePickerOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedMillis = datePickerState.selectedDateMillis
                            if (selectedMillis != null) {
                                val epochDays = (selectedMillis / (1000 * 60 * 60 * 24)).toInt()
                                viewModel.alertDate = dateStringFromEpochDays(epochDays)
                                viewModel.isAlertTimePickerOpen = true
                            }
                            viewModel.isAlertDatePickerOpen = false
                        }
                    ) {
                        Text("OK", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.isAlertDatePickerOpen = false }) {
                        Text("Cancel", color = Color(0xFF64748B))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Alert Time Picker Dialog
        if (viewModel.isAlertTimePickerOpen) {
            val timePickerState = rememberTimePickerState(initialHour = 10, initialMinute = 0, is24Hour = false)
            AlertDialog(
                onDismissRequest = { viewModel.isAlertTimePickerOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val hour = timePickerState.hour
                            val minute = timePickerState.minute
                            val amPm = if (hour >= 12) "PM" else "AM"
                            val displayHour = if (hour % 12 == 0) 12 else hour % 12
                            val displayMinute = minute.toString().padStart(2, '0')
                            viewModel.alertTime = "$displayHour:$displayMinute $amPm"
                            viewModel.isAlertTimePickerOpen = false
                        }
                    ) {
                        Text("OK", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.isAlertTimePickerOpen = false }) {
                        Text("Cancel", color = Color(0xFF64748B))
                    }
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }

        BaseToastHost(viewModel = viewModel)
    }
}

/**
 * Centered Month Separator line matching mockup (e.g. February 2025)
 */
@Composable
fun MonthHeaderDivider(
    monthName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE2E8F0))
        )
        
        Box(
            modifier = Modifier
                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = monthName,
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Standard timeline row representing checkups, services, alerts, and loan items.
 */
@Composable
fun TimelineEventRow(
    status: VehicleStatus,
    isDone: Boolean,
    daysOffset: String,
    isLast: Boolean,
    onStatusClick: (VehicleStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onStatusClick(status) }
    ) {
        // Date block (Left)
        Box(
            modifier = Modifier
                .width(82.dp)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            val dateParts = status.date.split(" ")
            val displayDate = if (dateParts.size >= 2) "${dateParts[0]} ${dateParts[1]}" else status.date
            Text(
                text = displayDate,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }

        // Timeline connector (Middle)
        Box(
            modifier = Modifier
                .width(48.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(Color(0xFFCBD5E1))
            )
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(12.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, Color(0xFFCBD5E1), CircleShape)
            )
        }

        // Relative Offset & Details (Right)
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .padding(top = 2.dp)
            ) {
                if (isDone) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(12.dp)) {
                            val path = Path().apply {
                                moveTo(size.width * 0.15f, size.height * 0.5f)
                                lineTo(size.width * 0.45f, size.height * 0.8f)
                                lineTo(size.width * 0.85f, size.height * 0.2f)
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFF2ECC71),
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Done",
                            color = Color(0xFF2ECC71),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = daysOffset,
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = status.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                if (status.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = status.description,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
