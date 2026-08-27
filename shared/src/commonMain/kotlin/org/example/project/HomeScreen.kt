package org.example.project

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import formyvehiclesai.shared.generated.resources.Res
import formyvehiclesai.shared.generated.resources.ic_add_vehicle
import formyvehiclesai.shared.generated.resources.ic_banner_home
import formyvehiclesai.shared.generated.resources.ic_home
import formyvehiclesai.shared.generated.resources.img_car_swift
import formyvehiclesai.shared.generated.resources.img_bike_glamour
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.delay

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val vehicles: List<Vehicle>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddVehicleClick: () -> Unit,
    onVehicleClick: (Vehicle) -> Unit,
    onLogoutClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = org.koin.compose.koinInject()
) {
    // Refresh user data every time this screen appears (handles logout+login with different user)
    LaunchedEffect(Unit) {
        viewModel.refreshUser()
        delay(1000)
        viewModel.uiState = HomeUiState.Success(viewModel.vehicleList)
    }

    AppBackHandler(enabled = true) {
        // Do nothing on back gesture to prevent returning to previous screen
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        when (val state = viewModel.uiState) {
            is HomeUiState.Loading -> {
                AppLoader(message = "Fetching your vehicles...")
            }
            is HomeUiState.Success -> {
                HomeScreenContent(
                    vehicles = state.vehicles,
                    userName = viewModel.userName,
                    onAddVehicleTrigger = { viewModel.isSheetOpen = true },
                    onDeleteVehicle = { vehicle ->
                        viewModel.deleteVehicle(vehicle)
                    },
                    onVehicleClick = onVehicleClick,
                    onProfileClick = onProfileClick,
                    onNotificationClick = onNotificationClick,
                    onViewAllClick = onViewAllClick
                )
            }
            is HomeUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red, fontSize = 16.sp)
                }
            }
        }

        // Modal Bottom Sheet with Drag Handle
        if (viewModel.isSheetOpen) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val focusManager = LocalFocusManager.current

            ModalBottomSheet(
                onDismissRequest = {
                    focusManager.clearFocus()
                    viewModel.isSheetOpen = false
                },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Add New Vehicle",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Scrollable Form inputs
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AppTextField(
                            value = viewModel.regNumber,
                            onValueChange = { viewModel.regNumber = it },
                            title = "Registration Number",
                            isMandatory = true,
                            isSecure = false,
                            placeholder = "Ex. KL 56 X 0000"
                        )

                        AppDropdown(
                            value = viewModel.selectedVehicleType,
                            onValueChange = { viewModel.onVehicleTypeSelected(it) },
                            title = "Vehicle Type",
                            options = viewModel.vehicleTypes,
                            isMandatory = true,
                            placeholder = "Select Vehicle Type"
                        )

                        AppDropdown(
                            value = viewModel.selectedBrand,
                            onValueChange = { viewModel.onBrandSelected(it) },
                            title = "Brand",
                            options = viewModel.availableBrands,
                            isMandatory = true,
                            placeholder = "Select Brand"
                        )

                        AppDropdown(
                            value = viewModel.selectedModel,
                            onValueChange = { viewModel.selectedModel = it },
                            title = "Model",
                            options = viewModel.modelsList,
                            isMandatory = true,
                            placeholder = "Select Model"
                        )

                        AppDropdown(
                            value = viewModel.selectedYear,
                            onValueChange = { viewModel.selectedYear = it },
                            title = "Year",
                            options = (2010..2026).map { it.toString() },
                            isMandatory = true,
                            placeholder = "Select Year"
                        )

                        AppDropdown(
                            value = viewModel.selectedFuelType,
                            onValueChange = { viewModel.selectedFuelType = it },
                            title = "Fuel Type",
                            options = viewModel.fuelTypes,
                            isMandatory = true,
                            placeholder = "Select Fuel Type"
                        )

                        AppDropdown(
                            value = viewModel.selectedGearType,
                            onValueChange = { viewModel.selectedGearType = it },
                            title = "Gear Type",
                            options = viewModel.gearTypes,
                            isMandatory = true,
                            placeholder = "Select Gear Type"
                        )

                        Text(
                            text = if (viewModel.isMoreDetailsVisible) "- Hide Extra Details" else "+ Add More Details",
                            color = Color(0xFF6366F1),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable {
                                    focusManager.clearFocus()
                                    viewModel.isMoreDetailsVisible = !viewModel.isMoreDetailsVisible
                                }
                                .padding(vertical = 4.dp)
                        )

                        if (viewModel.isMoreDetailsVisible) {
                            AppDropdown(
                                value = viewModel.selectedColor,
                                onValueChange = { viewModel.selectedColor = it },
                                title = "Vehicle Color",
                                options = viewModel.vehicleColors,
                                isMandatory = false,
                                placeholder = "Select Color"
                            )

                            AppDropdown(
                                value = viewModel.selectedCategory,
                                onValueChange = { viewModel.selectedCategory = it },
                                title = "Vehicle Category",
                                options = viewModel.vehicleCategories,
                                isMandatory = false,
                                placeholder = "Select Category (Ex. Hatchback)"
                            )

                            AppDropdown(
                                value = viewModel.selectedSeatingCapacity,
                                onValueChange = { viewModel.selectedSeatingCapacity = it },
                                title = "Seating Capacity",
                                options = viewModel.seatingCapacities,
                                isMandatory = false,
                                placeholder = "Select Seating Capacity"
                            )

                            AppTextField(
                                value = viewModel.mileageInput,
                                onValueChange = { viewModel.mileageInput = it },
                                title = "Mileage in KM (Approx)",
                                isMandatory = false,
                                isSecure = false,
                                placeholder = "Ex. 18.5"
                            )

                            AppDropdown(
                                value = viewModel.selectedIsTaxi,
                                onValueChange = { viewModel.selectedIsTaxi = it },
                                title = "Is this a Taxi?",
                                options = listOf("No", "Yes"),
                                isMandatory = false,
                                placeholder = "Select Yes / No"
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Button actions row
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.submitVehicle()
                            },
                            enabled = viewModel.isFormValid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6366F1),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Submit",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        TextButton(
                            onClick = { viewModel.isSheetOpen = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color(0xFF64748B),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    vehicles: List<Vehicle>,
    userName: String,
    onAddVehicleTrigger: () -> Unit,
    onDeleteVehicle: (Vehicle) -> Unit,
    onVehicleClick: (Vehicle) -> Unit,
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFC))
            .statusBarsPadding()
            .padding(top = 10.dp)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hi, Welcome back",
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8) // Slate-400
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = userName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B) // Slate-800
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Notification Bell with Badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadowCircle(Color.White)
                        .clickable { onNotificationClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(22.dp)) {
                        val bellPath = Path().apply {
                            moveTo(size.width * 0.5f, size.height * 0.1f)
                            quadraticTo(size.width * 0.7f, size.height * 0.15f, size.width * 0.75f, size.height * 0.5f)
                            lineTo(size.width * 0.85f, size.height * 0.75f)
                            lineTo(size.width * 0.15f, size.height * 0.75f)
                            lineTo(size.width * 0.25f, size.height * 0.5f)
                            quadraticTo(size.width * 0.3f, size.height * 0.15f, size.width * 0.5f, size.height * 0.1f)
                        }
                        drawPath(
                            path = bellPath,
                            color = Color(0xFF64748B),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = Color(0xFF64748B),
                            startAngle = 0f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = Offset(size.width * 0.4f, size.height * 0.75f),
                            size = androidx.compose.ui.geometry.Size(size.width * 0.2f, size.height * 0.15f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                // Avatar Circle with Initials
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFEEF2F6), CircleShape)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    val initials = remember(userName) {
                        val trimmed = userName.trim()
                        if (trimmed.isEmpty()) "KM"
                        else {
                            val parts = trimmed.split("\\s+".toRegex())
                            if (parts.size == 1) parts[0].take(2).uppercase()
                            else (parts[0].take(1) + parts[1].take(1)).uppercase()
                        }
                    }
                    Text(
                        text = initials,
                        color = Color(0xFF6366F1),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // Premium Home Banner
        Image(
            painter = painterResource(Res.drawable.ic_home),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.FillWidth
        )
        
        Spacer(modifier = Modifier.height(28.dp))

        if (vehicles.isEmpty()) {
            // Use common EmptyStateView
            EmptyStateView(
                icon = {
                    Canvas(modifier = Modifier.size(110.dp)) {
                        // Tree Outline
                        val treePath = Path().apply {
                            moveTo(size.width * 0.22f, size.height * 0.85f)
                            lineTo(size.width * 0.22f, size.height * 0.6f)
                            cubicTo(
                                size.width * 0.05f, size.height * 0.55f,
                                size.width * 0.05f, size.height * 0.35f,
                                size.width * 0.22f, size.height * 0.35f
                            )
                            cubicTo(
                                size.width * 0.25f, size.height * 0.2f,
                                size.width * 0.4f, size.height * 0.2f,
                                size.width * 0.42f, size.height * 0.35f
                            )
                            cubicTo(
                                size.width * 0.55f, size.height * 0.35f,
                                size.width * 0.55f, size.height * 0.55f,
                                size.width * 0.22f, size.height * 0.6f
                            )
                        }
                        drawPath(
                            path = treePath,
                            color = Color(0xFF94A3B8),
                            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Garage Outline
                        val garagePath = Path().apply {
                            moveTo(size.width * 0.45f, size.height * 0.85f)
                            lineTo(size.width * 0.45f, size.height * 0.48f)
                            quadraticTo(size.width * 0.68f, size.height * 0.35f, size.width * 0.9f, size.height * 0.48f)
                            lineTo(size.width * 0.9f, size.height * 0.85f)
                            moveTo(size.width * 0.55f, size.height * 0.85f)
                            lineTo(size.width * 0.55f, size.height * 0.58f)
                            lineTo(size.width * 0.8f, size.height * 0.58f)
                            lineTo(size.width * 0.8f, size.height * 0.85f)
                        }
                        drawPath(
                            path = garagePath,
                            color = Color(0xFF64748B),
                            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        // Base line
                        drawLine(
                            color = Color(0xFF64748B),
                            start = Offset(size.width * 0.1f, size.height * 0.85f),
                            end = Offset(size.width * 0.95f, size.height * 0.85f),
                            strokeWidth = 1.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                },
                title = "No Vehicle Added Yet",
                description = "This app's features work based on your added vehicles.\nPlease add your vehicle details first.",
                modifier = Modifier.weight(1f),
                actionButton = {
                    Button(
                        onClick = onAddVehicleTrigger,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 24.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF6366F1)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF6366F1)
                        )
                    ) {
                        Text(
                            text = "Add Your First Vehicles",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
        } else {
            // Vehicle listing view (Mockup Image 3)
            Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Vehicles",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    
                    Row(
                        modifier = Modifier.clickable { onViewAllClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View All (${vehicles.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6366F1)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Canvas(modifier = Modifier.size(12.dp)) {
                            val arrow = Path().apply {
                                moveTo(size.width * 0.3f, size.height * 0.2f)
                                lineTo(size.width * 0.7f, size.height * 0.5f)
                                lineTo(size.width * 0.3f, size.height * 0.8f)
                            }
                            drawPath(
                                path = arrow,
                                color = Color(0xFF6366F1),
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(vehicles) { vehicle ->
                        VehicleCard(
                            vehicle = vehicle,
                            onDeleteClick = { onDeleteVehicle(vehicle) },
                            onVehicleClick = onVehicleClick
                        )
                    }

                    // Add Vehicle button at the bottom of the list
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAddVehicleTrigger() }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Plus ic_filter
                            Image(
                                painter = painterResource(Res.drawable.ic_add_vehicle),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Add Vehicle",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleCard(
    vehicle: Vehicle,
    onDeleteClick: () -> Unit,
    onVehicleClick: (Vehicle) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVehicleClick(vehicle) },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vehicle Image (Swift or Glamour or general outline)
            val painter = when (vehicle.imageRes) {
                "img_car_swift" -> painterResource(Res.drawable.img_car_swift)
                "img_bike_glamour" -> painterResource(Res.drawable.img_bike_glamour)
                else -> {
                    if (vehicle.vehicleType == "Car") {
                        painterResource(Res.drawable.img_car_swift)
                    } else {
                        painterResource(Res.drawable.img_bike_glamour)
                    }
                }
            }

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(width = 110.dp, height = 75.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Vehicle Text details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vehicle.registrationNumber,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = vehicle.model,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = vehicle.brand,
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8) // Slate-400
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${vehicle.year}  ${vehicle.fuelType}  ${vehicle.gearType}",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8) // Slate-400
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right arrow chevron
            Canvas(modifier = Modifier.size(16.dp)) {
                val arrow = Path().apply {
                    moveTo(size.width * 0.35f, size.height * 0.2f)
                    lineTo(size.width * 0.7f, size.height * 0.5f)
                    lineTo(size.width * 0.35f, size.height * 0.8f)
                }
                drawPath(
                    path = arrow,
                    color = Color(0xFF64748B),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}

