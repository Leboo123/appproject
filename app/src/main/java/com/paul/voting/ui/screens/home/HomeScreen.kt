package com.paul.voting.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.paul.voting.navigation.ROUTE_LOGIN
import com.paul.voting.navigation.ROUTE_REGISTER
import org.w3c.dom.Text

@Composable
fun homeScreen(navController: NavHostController) {
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()

            .background(Color.White)
    ){
        Button(onClick = {navController.navigate(ROUTE_REGISTER)},
            modifier= Modifier
                .width((300.dp)),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(Color.Blue)) {
            Text(
                "REGISTER",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold

            )
        }
        Spacer(modifier=Modifier.height(16.dp))
        Button(onClick = {navController.navigate(ROUTE_LOGIN)},
            modifier= Modifier
                .width((300.dp)),
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(Color.Blue)) {
            Text(
                "LOGIN",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}