// app/src/main/java/com/example/proyecto_final/MainActivity.kt
package com.example.proyecto_final

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.core_data.viewmodel.GestorViewModel
import com.example.proyecto_final.navigation.Routes
import com.example.registration.ui.theme.Proyecto_FinalTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private var navControllerRef: NavHostController? = null

    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { t ->
                if (t.isSuccessful) {
                    navControllerRef?.navigate(Routes.ActsList) {
                        popUpTo(Routes.AuthLogin) { inclusive = true }
                    }
                }
            }
        } catch (_: ApiException) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = (application as MyApp).container.gestorVmFactory()
        val vm = ViewModelProvider(this, factory)[GestorViewModel::class.java]

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this, gso)

        val startDestination = Routes.Splash
        val isUserLoggedIn = auth.currentUser != null

        setContent {
            //Estado unico del tema para toda la app (se conserva al navegar)
            var isDarkMode by rememberSaveable { mutableStateOf(false) }

            //Envuelve toda la app con tu tema usando ese estado
            Proyecto_FinalTheme(darkTheme = isDarkMode) {

                //Pinta el fondo con el color del tema para evitar “pantalla blanca”
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    navControllerRef = navController

                    AppNavHost(
                        viewModel = vm,
                        navController = navController,
                        startDestination = startDestination,
                        isUserLoggedIn = isUserLoggedIn,

                        // PASA el estado y el callback a Settings
                        isDarkMode = isDarkMode,
                        onDarkModeChange = { isDarkMode = it },

                        //Login
                        onLogin = { email, pass ->
                            auth.signInWithEmailAndPassword(email, pass)
                                .addOnCompleteListener { res ->
                                    if (res.isSuccessful) {
                                        navController.navigate(Routes.ActsList) {
                                            popUpTo(Routes.AuthLogin) { inclusive = true }
                                        }
                                    }
                                }
                        },

                        //Registro
                        onRegister = { email, pass ->
                            auth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener { res ->
                                    if (res.isSuccessful) {
                                        navController.navigate(Routes.ActsList) {
                                            popUpTo(Routes.AuthLogin) { inclusive = true }
                                        }
                                    }
                                }
                        },

                        //Login con Google
                        onGoogleSignIn = {
                            googleLauncher.launch(googleClient.signInIntent)
                        }
                    )
                }
            }
        }
    }
}
