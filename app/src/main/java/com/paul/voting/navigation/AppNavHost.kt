package com.paul.voting.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.paul.voting.ui.screens.dashboard.DashboardScreen
import com.paul.voting.ui.screens.home.homeScreen
import com.paul.voting.ui.screens.login.loginscreen
import com.paul.voting.ui.screens.polls.AddPollScreen
import com.paul.voting.ui.screens.polls.UpdatePollScreen
import com.paul.voting.ui.screens.polls.VotePollScreen
import com.paul.voting.ui.screens.register.RegisterScreen

@RequiresApi(Build.VERSION_CODES.O)
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
            RegisterScreen(navController)
        }
        composable(ROUTE_LOGIN){
            loginscreen(navController)
        }
        composable(ROUTE_DASHBOARD) {
            DashboardScreen(navController)
        }
        composable(ROUTE_ADD_POLL) {
            AddPollScreen(navController = navController)
        }
        composable("vote_poll/{pollId}") { backStackEntry ->
            val pollId = backStackEntry.arguments?.getString("pollId") ?: ""
            VotePollScreen(pollId,navController=navController)
        }
       composable ("update_poll/{pollId}"){
            backStackEntry ->
            val pollId = backStackEntry.arguments?.getString("pollId") ?: ""
            UpdatePollScreen(pollId=pollId,navController=navController)
        }
    }}
