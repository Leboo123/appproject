package com.paul.voting.ui.screens.register

import com.paul.voting.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.paul.voting.data.AuthViewModel

@Composable
fun registerScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )    {
        Text(
            text = "REGISTER",
            color = Color.Gray,
            fontSize = 40.sp,

            style = MaterialTheme.typography.headlineLarge
        )
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Logo",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Create an account",
            color = Color.Blue,
            fontSize = 16.sp,
        )
        var fullname by remember { mutableStateOf(TextFieldValue("") )}
        var email by remember { mutableStateOf(TextFieldValue("")) }
        var confirmpass by remember { mutableStateOf(TextFieldValue(""))}
        var pass by remember { mutableStateOf(TextFieldValue("") )}

        var context= LocalContext.current

        OutlinedTextField(
            value = fullname,
            onValueChange = { fullname = it },
            label = { Text("Fullname") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),

            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Fullname"
                )
            }

        )


        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("emailaddress") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),

            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "email address"
                )
            }

        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text(" Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),

            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Password"
                )
            }

        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmpass,
            onValueChange = { confirmpass = it },
            label = { Text(" Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            shape = RoundedCornerShape(5.dp),

            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Confirm Password"
                )
            }

        )

        Button(onClick = {
            val myregister= AuthViewModel(navController,context)
            myregister.signup(fullname.text.trim(),email.text.trim(),pass.text.trim(),confirmpass.text.trim())
        }){
            Text(
                text="REGISTER"
            )
        }
        TextButton(onClick = {navController.navigate("login")}) {
            Text(
                text="Already have an account?Login here"
            )

        }

    }
}