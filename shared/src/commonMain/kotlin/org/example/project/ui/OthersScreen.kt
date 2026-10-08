package org.example.project.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import formyvehiclesai.shared.generated.resources.Res
import formyvehiclesai.shared.generated.resources.banner1
import formyvehiclesai.shared.generated.resources.banner2
import formyvehiclesai.shared.generated.resources.eme_accident
import formyvehiclesai.shared.generated.resources.eme_break
import formyvehiclesai.shared.generated.resources.eme_elec
import formyvehiclesai.shared.generated.resources.eme_mech
import formyvehiclesai.shared.generated.resources.eme_medical
import formyvehiclesai.shared.generated.resources.eme_tyre
import formyvehiclesai.shared.generated.resources.hc_1
import formyvehiclesai.shared.generated.resources.hc_2
import formyvehiclesai.shared.generated.resources.hc_3
import formyvehiclesai.shared.generated.resources.hc_4
import formyvehiclesai.shared.generated.resources.hc_5
import formyvehiclesai.shared.generated.resources.ic_filter
import formyvehiclesai.shared.generated.resources.ic_location
import formyvehiclesai.shared.generated.resources.oth_1
import formyvehiclesai.shared.generated.resources.oth_2
import formyvehiclesai.shared.generated.resources.oth_3
import formyvehiclesai.shared.generated.resources.oth_4
import formyvehiclesai.shared.generated.resources.rm_1
import formyvehiclesai.shared.generated.resources.rm_2
import formyvehiclesai.shared.generated.resources.rm_3
import formyvehiclesai.shared.generated.resources.rm_4
import formyvehiclesai.shared.generated.resources.rm_5
import formyvehiclesai.shared.generated.resources.rm_6
import formyvehiclesai.shared.generated.resources.rm_7
import formyvehiclesai.shared.generated.resources.us_1
import formyvehiclesai.shared.generated.resources.us_2
import formyvehiclesai.shared.generated.resources.us_3
import formyvehiclesai.shared.generated.resources.us_4
import io.ktor.http.ContentType
import org.example.project.AppCarousel
import org.example.project.AppSearchField
import org.example.project.BaseToastHost
import org.example.project.viewmodel.OthersViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.getKoin

@Composable
fun OthersScreen(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OthersViewModel = org.koin.compose.koinInject()
) {
    val address = viewModel.userAddress
    val isLoading = viewModel.isLoading
    var searchText by remember { mutableStateOf("") }
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFBFC))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppSearchField(
                modifier = Modifier.weight(1f),
                value = searchText,
                onValueChange = { searchText = it },
                title = null,
                placeholder = "Search services, hotels etc...",
                isSecure = false,
                isMandatory = false
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_filter),
                    contentDescription = "Filter",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_location),
                contentDescription = "Location",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            val locText = if (isLoading) {
                "Fetching location..."
            } else if (address != null) {
                listOfNotNull(address.area, address.district, address.state)
                    .filter { !it.isNullOrBlank() }
                    .joinToString(", ")
            } else {
                "Location Unavailable"
            }
            Text(
                text = locText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B)
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Column(
            Modifier.verticalScroll(rememberScrollState())
        ) {
            val bannerImages = listOf(
                Res.drawable.banner2,
                Res.drawable.banner2
            )
            AppCarousel(
                images = bannerImages,
                height = 160.dp,
                autoScrollDurationMs = 3000L,
                enableAutoScroll = true,
                onImageClick = { index ->
                    println("Carousel image $index clicked")
                }
            )
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Emergency Services",
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier.weight(1f))
                Text(
                    "View All >",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF682BF7)

                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val emergencyItems = remember {
                listOf(
                    EmergencyItem("tyre", "Tyre Works", Color(0xFFF0EEFF), imageRes = Res.drawable.eme_tyre, iconDrawer = { TyreIcon() }),
                    EmergencyItem("mechanical", "Mechanical", Color(0xFFF0EEFF), imageRes = Res.drawable.eme_mech, iconDrawer = { MechanicalIcon() }),
                    EmergencyItem("medical", "Medical", Color(0xFFFEEAE6), imageRes = Res.drawable.eme_medical, iconDrawer = { MedicalIcon() }),
                    EmergencyItem("electrical", "Electrical", Color(0xFFDCF7EC), imageRes = Res.drawable.eme_elec, iconDrawer = { ElectricalIcon() }),
                    EmergencyItem("breakdown", "Breakdown", Color(0xFFDCF7EC), imageRes = Res.drawable.eme_break, iconDrawer = { BreakdownIcon() }),
                    EmergencyItem("accident", "Accident", Color(0xFFFEEAE6), imageRes = Res.drawable.eme_accident, iconDrawer = { AccidentIcon() })
                )
            }

            val chunkedEmergency = remember { emergencyItems.chunked(3) }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                chunkedEmergency.forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowItems.forEach { item ->
                            Box(modifier = Modifier.weight(1f)) {
                                EmergencyCard(
                                    item = item,
                                    onClick = { }
                                )
                            }
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(
                Modifier.height(20.dp)
            )
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(Res.drawable.banner1),
                contentDescription = null
            )
            Spacer(
                Modifier.height(20.dp)
            )
            val vehicleCareItems = listOf(
                HorizontalCardItem("Washing Service", Res.drawable.hc_1),
                HorizontalCardItem("Detailing / Polishing", Res.drawable.hc_2),
                HorizontalCardItem("Ceramic Coating", Res.drawable.hc_3),
                HorizontalCardItem("Body Painting Works", Res.drawable.hc_4),
                HorizontalCardItem("Interior Cleaning", Res.drawable.hc_5)
            )
            HorizontalCardView(
                title = "Vehicle Care Services",
                viewAllCount = 10,
                images = vehicleCareItems
            )
            Spacer(Modifier.height(20.dp))

            val repairItems = listOf(
                HorizontalCardItem("Engine Service", Res.drawable.rm_1),
                HorizontalCardItem("Clutch Service", Res.drawable.rm_2),
                HorizontalCardItem("Electrical Service", Res.drawable.rm_3),
                HorizontalCardItem("AC Repair Service", Res.drawable.rm_4),
                HorizontalCardItem("Oil & Lubrication", Res.drawable.rm_5),
                HorizontalCardItem("Towing Service", Res.drawable.rm_6),
                HorizontalCardItem("Bike Service", Res.drawable.rm_7)
            )
            HorizontalCardView(
                title = "Repair & Maintenance",
                viewAllCount = 12,
                images = repairItems
            )
            Spacer(Modifier.height(20.dp))

            val utilityItems = listOf(
                HorizontalCardItem("PUC Inspection", Res.drawable.us_1),
                HorizontalCardItem("LMV Spare Parts", Res.drawable.us_2),
                HorizontalCardItem("Insurance Services", Res.drawable.us_3),
                HorizontalCardItem("FASTag Services", Res.drawable.us_4)
            )
            HorizontalCardView(
                title = "Utility Services",
                viewAllCount = 9,
                images = utilityItems
            )
            Spacer(Modifier.height(20.dp))

            val othersItems = listOf(
                HorizontalCardItem("Pay Parking Area", Res.drawable.oth_1),
                HorizontalCardItem("Tour & Travels Services", Res.drawable.oth_2),
                HorizontalCardItem("Restaurants Near Me", Res.drawable.oth_3),
                HorizontalCardItem("Pharmacies Near Me", Res.drawable.oth_4)
            )
            HorizontalCardView(
                title = "Others",
                viewAllCount = 9,
                images = othersItems
            )
        }
    }

    BaseToastHost(viewModel = viewModel)
}
}

@Composable
private fun AddressChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            color = Color(0xFF1E293B),
            fontWeight = FontWeight.Bold
        )
    }
}

data class HorizontalCardItem(
    val title: String,
    val image: DrawableResource
)

@Composable
fun HorizontalCardView(
    title: String,
    viewAllCount: Int? = null,
    images: List<HorizontalCardItem>
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                fontSize = 14.sp,
                color = Color(0xFF1E293B),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            val viewAllText = if (viewAllCount != null) "View All ($viewAllCount) >" else "View All >"
            Text(
                text = viewAllText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF682BF7)
            )
        }
        Spacer(
            modifier = Modifier.height(14.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            images.forEach { item ->
                HorizontalScrollCard(
                    item
                )
            }
        }
    }
}

@Composable
fun HorizontalScrollCard(
    item: HorizontalCardItem
) {
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = painterResource(item.image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f)
                        ),
                        startY = 50f
                    )
                )
        )
        Text(
            text = item.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 17.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 10.dp,
                    end = 8.dp,
                    bottom = 10.dp
                )
        )
    }
}