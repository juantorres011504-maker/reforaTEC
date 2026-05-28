package com.example.reforatec.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reforatec.R
import androidx.compose.foundation.background

@Composable
fun BienvenidaScreen(
    onComencemosClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.imgcomencemos), // pon tu imagen aquí
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.15f))
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {


            Text(
                text = "Crece\nconmigo.",
                modifier = Modifier.padding(top = 72.dp),
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 58.sp,
                textAlign = TextAlign.Start
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onComencemosClick,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.40f),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text(
                        text = "Comencemos",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.3.sp
                    )
                }
            }
        }
    }
}