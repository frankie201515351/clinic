package com.clinic.dentalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.clinic.dentalapp.data.ClinicRepository
import com.clinic.dentalapp.data.Session
import com.clinic.dentalapp.ui.navigation.MainNav
import com.clinic.dentalapp.ui.screens.LoginScreen
import com.clinic.dentalapp.ui.theme.DentalClinicTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ClinicRepository(applicationContext)

        setContent {
            // فرض اتجاه من اليمين لليسار على كامل التطبيق
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                DentalClinicTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AppRoot(repository)
                    }
                }
            }
        }
    }
}

@Composable
fun AppRoot(repository: ClinicRepository) {
    var loggedIn by remember { mutableStateOf(false) }
    var ready by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        repository.ensureDefaultUsers()
        ready = true
    }

    if (!ready) return

    if (loggedIn) {
        MainNav(repository = repository, onLogout = { loggedIn = false })
    } else {
        LoginScreen(repository = repository, onLoginSuccess = { loggedIn = true })
    }
}
