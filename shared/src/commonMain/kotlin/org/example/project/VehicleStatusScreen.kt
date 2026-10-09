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
import org.example.project.data.model.UserVehicle
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleStatusScreen(
    onBackClick: () -> Unit,
    regNumber: String? = null,
    modifier: Modifier = Modifier,
    viewModel: VehicleStatusViewModel = org.koin.compose.koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(regNumber) {
        viewModel.loadVehiclesAndStatus(regNumber)
    }

    val expandedDates = remember { mutableStateMapOf<String, Boolean>() }
    val todayDateString = viewModel.todayDateString
    val todayDays = getEpochDays(todayDateString)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(16.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.6f, size.height * 0.2f)
                            lineTo(size.width * 0.3f, size.height * 0.5f)
                            lineTo(size.width * 0.6f, size.height * 0.8f)
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFF1E293B),
                            style = Stroke(
                                width = 2.2.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "My Vehicle Status",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            // Results selector dropdown & Filter badge row matching screenshot
            val rotationAngle by animateFloatAsState(
                targetValue = if (uiState.isChooseVehicleSheetOpen) 180f else 0f,
                animationSpec = tween(durationMillis = 250)
            )

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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val currentSelectedFilter = uiState.selectedVehicleFilter
                        val displayText = if (currentSelectedFilter == null) {
                            "All (${uiState.userVehicles.size} Vehicles)"
                        } else {
                            "${currentSelectedFilter.brand.name} ${currentSelectedFilter.vehicleModel.name}"
                        }
                        Text(
                            text = displayText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
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

                // Filter Pill Button matching screenshot
                Row(
                    modifier = Modifier
                        .height(38.dp)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .clickable { viewModel.openChooseVehicleSheet() }
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(15.dp)) {
                        val color = Color(0xFF1E293B)
                        val stroke = 1.6.dp.toPx()
                        // Top line
                        drawLine(color, Offset(0f, size.height * 0.2f), Offset(size.width, size.height * 0.2f), strokeWidth = stroke, cap = StrokeCap.Round)
                        drawCircle(color, radius = 2.dp.toPx(), center = Offset(size.width * 0.7f, size.height * 0.2f))
                        // Middle line
                        drawLine(color, Offset(0f, size.height * 0.5f), Offset(size.width, size.height * 0.5f), strokeWidth = stroke, cap = StrokeCap.Round)
                        drawCircle(color, radius = 2.dp.toPx(), center = Offset(size.width * 0.3f, size.height * 0.5f))
                        // Bottom line
                        drawLine(color, Offset(0f, size.height * 0.8f), Offset(size.width, size.height * 0.8f), strokeWidth = stroke, cap = StrokeCap.Round)
                        drawCircle(color, radius = 2.dp.toPx(), center = Offset(size.width * 0.65f, size.height * 0.8f))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Filter",
                        color = Color(0xFF1E293B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFF5E17EB), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val filterCount = if (uiState.activeFilterCount > 0) uiState.activeFilterCount else (uiState.userVehicles.size.takeIf { it > 0 } ?: 2)
                        Text(
                            text = filterCount.toString(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.isLoading) {
                AppLoader(message = "Generating status timeline...")
            } else if (uiState.errorMessage != null && uiState.statusList.isEmpty()) {
                AppErrorScreen(
                    errorMessage = uiState.errorMessage ?: "Failed To Load Status",
                    action = {
                        viewModel.loadVehiclesAndStatus(regNumber)
                    }
                )
            } else {
                if (uiState.statusList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No status available", fontSize = 16.sp, color = Color.Gray)
                    }
                } else {
                    val sortedStatuses = remember(uiState.statusList) {
                        uiState.statusList.sortedBy { getEpochDays(it.date) }
                    }
                    val groupedByDate = remember(sortedStatuses) {
                        sortedStatuses.groupBy { formatStandardDate(it.date) }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(top = 10.dp, bottom = 96.dp)
                    ) {
                        val dates = groupedByDate.keys.toList()
                        var hasInsertedToday = false
                        var lastMonth = ""

                        dates.forEachIndexed { dateIndex, date ->
                            val dateStatuses = groupedByDate[date] ?: emptyList()
                            val currentMonth = getFullMonthHeader(date)
                            val dateDays = getEpochDays(date)

                            // Month header divider
                            if (currentMonth != lastMonth) {
                                item(key = "header_$date") {
                                    MonthHeaderDivider(monthName = currentMonth)
                                }
                                lastMonth = currentMonth
                            }

                            // Today marker row
                            if (!hasInsertedToday && todayDays <= dateDays) {
                                item(key = "today_marker") {
                                    TodayMarkerRow(dateString = todayDateString)
                                }
                                hasInsertedToday = true
                            }

                            val isExpanded = expandedDates[date] ?: false
                            val diff = dateDays - todayDays
                            val isDone = diff < 0
                            val daysOffsetString = if (isDone) "Done" else "${diff} Days"

                            if (dateStatuses.size > 1 && !isExpanded) {
                                item(key = "group_$date") {
                                    TimelineGroupRow(
                                        date = date,
                                        statuses = dateStatuses,
                                        isDone = isDone,
                                        daysOffset = daysOffsetString,
                                        onToggleExpand = {
                                            expandedDates[date] = true
                                        }
                                    )
                                }
                            } else {
                                itemsIndexed(dateStatuses, key = { _, s -> "status_${s.id}" }) { idx, status ->
                                    TimelineEventRow(
                                        status = status,
                                        displayDate = if (idx == 0) date else "",
                                        isDone = isDone,
                                        daysOffset = if (idx == 0) daysOffsetString else "",
                                        vehicleSubtitle = getVehicleSubtitle(status, uiState.userVehicles, uiState.selectedVehicleFilter),
                                        onStatusClick = {
                                            viewModel.setSelectedStatusDetail(status)
                                        }
                                    )
                                }

                                if (dateStatuses.size > 1 && isExpanded) {
                                    item(key = "collapse_$date") {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 98.dp, bottom = 12.dp)
                                                .clickable { expandedDates[date] = false },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "- Hide Details",
                                                color = Color(0xFF5E17EB),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (!hasInsertedToday) {
                            item(key = "today_marker_end") {
                                TodayMarkerRow(dateString = todayDateString)
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button matching screenshot
        if (!uiState.isLoading) {
            FloatingActionButton(
                onClick = { viewModel.setAddStatusSheetOpen(true) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 44.dp),
                shape = CircleShape,
                containerColor = Color(0xFF5E17EB),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
            ) {
                Canvas(modifier = Modifier.size(20.dp)) {
                    val stroke = 3.dp.toPx()
                    drawLine(
                        color = Color.White,
                        start = Offset(size.width * 0.5f, 0f),
                        end = Offset(size.width * 0.5f, size.height),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, size.height * 0.5f),
                        end = Offset(size.width, size.height * 0.5f),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // "Choose Vehicle" Modal Bottom Sheet matching user screenshot
        if (uiState.isChooseVehicleSheetOpen) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.setChooseVehicleSheetOpen(false) },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                containerColor = Color.White
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
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
                        val isAllSelected = uiState.tempSelectedVehicleFilter == null
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setTempSelectedVehicleFilter(null) },
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
                        uiState.userVehicles.forEach { vehicle ->
                            val isVehicleSelected = uiState.tempSelectedVehicleFilter?.id == vehicle.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setTempSelectedVehicleFilter(vehicle) },
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
                                            text = "${vehicle.brand.name} ${vehicle.vehicleModel.name}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = vehicle.brand.name,
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
                                            if (vehicle.vehicleType.name.contains("Bike", ignoreCase = true) || vehicle.vehicleType.name.contains("Two", ignoreCase = true)) Res.drawable.img_bike_glamour else Res.drawable.img_car_swift
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
                                .clickable { viewModel.setChooseVehicleSheetOpen(false) }
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

                BaseToastHost(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }

        // Status Detail Modal Bottom Sheet when user taps any status card
        val selectedStatus = uiState.selectedStatusDetail
        if (selectedStatus != null) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.setSelectedStatusDetail(null) },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                containerColor = Color.White
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
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
                                    when (selectedStatus.type.name) {
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
                                text = selectedStatus.type.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (selectedStatus.type.name) {
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

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.setSelectedStatusDetail(null) },
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

                BaseToastHost(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }

        // "Add New Info" Modal Bottom Sheet
        if (uiState.isAddStatusSheetOpen) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.setAddStatusSheetOpen(false) },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                containerColor = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.88f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
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
                            value = uiState.statusTitle,
                            onValueChange = { viewModel.onStatusTitleChange(it) },
                            title = "Title",
                            isMandatory = false,
                            isSecure = false,
                            placeholder = "Ex. My Car Loan - ₹ 12000"
                        )

                        // Vehicle Dropdown
                        AppDropdown(
                            value = uiState.vehicleAssociation,
                            onValueChange = { viewModel.onVehicleAssociationChange(it) },
                            title = "Vehicle",
                            options = uiState.availableVehicleOptions,
                            isMandatory = true,
                            placeholder = "Select Vehicle"
                        )

                        // Type Dropdown
                        AppDropdown(
                            selectedItem = uiState.selectedInfoType,
                            onItemSelected = { viewModel.onInfoTypeSelected(it) },
                            title = "Type",
                            options = uiState.infoTypes,
                            getLabel = { it.name },
                            isLoading = uiState.isInfoTypesLoading,
                            isMandatory = true,
                            placeholder = "Select Type"
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
                                value = uiState.infoDate,
                                onValueChange = { viewModel.onInfoDateChange(it) },
                                readOnly = true,
                                enabled = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setInfoDatePickerOpen(true) },
                                placeholder = { Text(text = "Select Info Date", color = Color(0xFF94A3B8)) },
                                trailingIcon = {
                                    IconButton(onClick = { viewModel.setInfoDatePickerOpen(true) }) {
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

                        // Alert Time Input
                        AppTimePicker(
                            value = uiState.alertTime,
                            onTimeSelected = { viewModel.onAlertTimeChange(it) },
                            title = "Alert Time",
                            isMandatory = false,
                            placeholder = "Ex. 09:30 AM"
                        )

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
                                uiState.cycleOptions.forEach { cycle ->
                                    val isSelected = cycle == uiState.selectedCycle
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(if (isSelected) Color(0xFF6366F1) else Color(0xFFF1F5F9))
                                            .clickable { viewModel.onCycleSelected(cycle) }
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
                                .clickable { viewModel.setAddStatusSheetOpen(false) }
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

                BaseToastHost(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }

    // Info Date Picker Dialog
    if (uiState.isInfoDatePickerOpen) {
        val initialMillis = remember(uiState.infoDate) {
            utcMillisFromDateString(uiState.infoDate)
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )
        DatePickerDialog(
            onDismissRequest = { viewModel.setInfoDatePickerOpen(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            viewModel.onInfoDateChange(dateStringFromUtcMillis(selectedMillis))
                        }
                        viewModel.setInfoDatePickerOpen(false)
                    }
                ) {
                    Text("OK", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setInfoDatePickerOpen(false) }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

        BaseToastHost(
            viewModel = viewModel,
            modifier = Modifier.padding(
                vertical = 20.dp
            )
        )
    }
}


val PrimaryPurple = Color(0xFF5E17EB)

/**
 * Centered Month Separator line matching screenshot (e.g. February 2025)
 */
@Composable
fun MonthHeaderDivider(
    monthName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Continuous vertical 8.dp bar in the background
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Box(modifier = Modifier.width(60.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .width(26.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(8.dp)
                        .background(Color(0xFFCBD5E1))
                )
            }
        }

        // Horizontal subtle divider line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(1.dp)
                .background(Color(0xFFE2E8F0))
        )

        // Centered Month Pill Badge
        Box(
            modifier = Modifier
                .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = monthName,
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Today Marker Row matching screenshot:
 * Purple circle with white down-chevron on the timeline bar, followed by formatted today date in purple.
 */
@Composable
fun TodayMarkerRow(
    dateString: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 20.dp)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column: Blank (aligns with 60.dp date column)
        Box(modifier = Modifier.width(60.dp))

        Spacer(modifier = Modifier.width(10.dp))

        // Center Column: Continuous 8.dp bar with centered 26.dp purple circle and white down chevron
        Box(
            modifier = Modifier
                .width(26.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(Color(0xFFCBD5E1))
            )
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(PrimaryPurple, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    val path = Path().apply {
                        moveTo(size.width * 0.25f, size.height * 0.35f)
                        lineTo(size.width * 0.5f, size.height * 0.68f)
                        lineTo(size.width * 0.75f, size.height * 0.35f)
                    }
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right side: Today's date in purple
        Text(
            text = getTodayMarkerText(dateString),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryPurple
        )
    }
}

/**
 * Collapsed multi-event row matching screenshot (e.g. 26 Jan in mockup: "16 Days" on left, "View All [2]" on right)
 */
@Composable
fun TimelineGroupRow(
    date: String,
    statuses: List<VehicleStatus>,
    isDone: Boolean,
    daysOffset: String,
    onToggleExpand: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onToggleExpand() }
            .padding(horizontal = 20.dp)
    ) {
        // Date Block (Left)
        Box(
            modifier = Modifier
                .width(60.dp)
                .padding(top = 8.dp, bottom = 16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = getTimelineDayMonth(date),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Center: continuous 8.dp bar with white 5.dp dot
        Box(
            modifier = Modifier
                .width(26.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(Color(0xFFCBD5E1))
            )
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(5.dp)
                    .background(Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right content: Days on left, "View All [2]" on right
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Days offset
            Box(modifier = Modifier.width(72.dp)) {
                Text(
                    text = daysOffset,
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            // View All [2] (Right-aligned)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View All",
                    color = PrimaryPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(PrimaryPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${statuses.size}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Standard timeline row representing checkups, services, alerts, and loan items matching screenshot:
 * Left Date, Center 8.dp bar + white dot, Right-Left status/days, Right-Right title & vehicle (right-aligned).
 */
@Composable
fun TimelineEventRow(
    status: VehicleStatus,
    displayDate: String,
    isDone: Boolean,
    daysOffset: String,
    vehicleSubtitle: String,
    onStatusClick: (VehicleStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onStatusClick(status) }
            .padding(horizontal = 20.dp)
    ) {
        // Date Block (Left)
        Box(
            modifier = Modifier
                .width(60.dp)
                .padding(top = 8.dp, bottom = 16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            if (displayDate.isNotBlank()) {
                Text(
                    text = getTimelineDayMonth(displayDate),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Center: continuous 8.dp bar with white 5.dp dot
        Box(
            modifier = Modifier
                .width(26.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(Color(0xFFCBD5E1))
            )
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(5.dp)
                    .background(Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right Content: Days/Status on left, Title/Vehicle on right
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Days offset / Status (Left part of right column)
            Box(
                modifier = Modifier.width(72.dp)
            ) {
                if (isDone) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(0xFF16A34A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(9.dp)) {
                                val path = Path().apply {
                                    moveTo(size.width * 0.2f, size.height * 0.5f)
                                    lineTo(size.width * 0.45f, size.height * 0.8f)
                                    lineTo(size.width * 0.85f, size.height * 0.2f)
                                }
                                drawPath(
                                    path = path,
                                    color = Color.White,
                                    style = Stroke(
                                        width = 1.8.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Done",
                            color = Color(0xFF16A34A),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (daysOffset.isNotBlank()) {
                    Text(
                        text = daysOffset,
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Title, Subtitle, Reminder (Right-aligned!)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = status.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.End
                )

                if (vehicleSubtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = vehicleSubtitle,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.End
                    )
                }

                if (!status.reminderTime.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = status.reminderTime,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        ClockReminderIcon()
                    }
                }
            }
        }
    }
}

@Composable
fun ClockReminderIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(12.dp)) {
        val color = Color(0xFF94A3B8)
        val stroke = 1.3.dp.toPx()
        drawCircle(color, radius = size.minDimension * 0.45f, style = Stroke(width = stroke))
        // hour hand
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x, center.y - size.height * 0.25f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        // minute hand
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x + size.width * 0.2f, center.y),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}

fun getVehicleSubtitle(
    status: VehicleStatus,
    userVehicles: List<UserVehicle>,
    selectedFilter: UserVehicle?
): String {
    if (!status.vehicleModel.isNullOrBlank()) return status.vehicleModel
    if (!status.vehicleName.isNullOrBlank()) return status.vehicleName
    if (selectedFilter != null) {
        return "${selectedFilter.brand.name} ${selectedFilter.vehicleModel.name}".trim()
    }
    val titleLower = status.title.lowercase()
    if (titleLower.contains("car") || titleLower.contains("swift")) {
        val car = userVehicles.firstOrNull { it.vehicleType.name.contains("car", ignoreCase = true) || it.vehicleModel.name.contains("swift", ignoreCase = true) }
        if (car != null) return "${car.brand.name} ${car.vehicleModel.name}".trim()
    }
    if (titleLower.contains("bike") || titleLower.contains("glamour")) {
        val bike = userVehicles.firstOrNull { it.vehicleType.name.contains("bike", ignoreCase = true) || it.vehicleType.name.contains("two", ignoreCase = true) || it.vehicleModel.name.contains("glamour", ignoreCase = true) }
        if (bike != null) return "${bike.brand.name} ${bike.vehicleModel.name}".trim()
    }
    val matched = userVehicles.firstOrNull { it.vehicleType.id == status.type.id }
    if (matched != null) {
        return "${matched.brand.name} ${matched.vehicleModel.name}".trim()
    }
    if (userVehicles.isNotEmpty()) {
        val v = userVehicles[kotlin.math.abs(status.id) % userVehicles.size]
        return "${v.brand.name} ${v.vehicleModel.name}".trim()
    }
    return "Swift VXI Hatchback"
}
