package com.paul.voting.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paul.voting.ui.screens.home.homeScreen
import com.paul.voting.ui.screens.login.loginscreen
import com.paul.voting.ui.screens.register.registerScreen

@Composable
fun AppNavhost(
    modifier: Modifier=Modifier,
    navController: NavHostController= rememberNavController(),
    startDestination: String= ROUTE_HOME_SCREEN
){
    NavHost(modifier=Modifier, startDestination = startDestination,
        navController=navController){
        composable (ROUTE_HOME_SCREEN) {
            homeScreen(navController)
        }
        composable(ROUTE_REGISTER) {
            registerScreen(navController)
        }
        composable(ROUTE_LOGIN){
            loginscreen(navController)
        }
    }}
