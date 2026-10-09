
package org.example.project
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import formyvehiclesai.shared.generated.resources.Res
import formyvehiclesai.shared.generated.resources._404
import formyvehiclesai.shared.generated.resources.error
import formyvehiclesai.shared.generated.resources.ic_car_error
import formyvehiclesai.shared.generated.resources.rotate_right
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppErrorScreen(
    errorMessage: String,
    action: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_car_error),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = errorMessage,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 24.dp),
                color = Color.Red,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(Res.drawable.rotate_right),
                contentDescription = "Try again",
                modifier = Modifier
                    .size(20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        action()
                    }
            )

        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AppErrorScreenPreview(

) {
    AppErrorScreen(
        errorMessage = "Internal Server Error"
    ) {

    }
}