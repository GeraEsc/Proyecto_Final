package com.example.registration.Screens

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core_data.viewmodel.GestorViewModel

@Composable
fun LoginRegisterScreen(
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var isLoginMode by rememberSaveable { mutableStateOf(true) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }

    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordErrors by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var confirmError by rememberSaveable { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val passwordFocus = remember { FocusRequester() }
    val confirmFocus = remember { FocusRequester() }

    fun validateEmail(value: String): String? {
        val v = value.trim()
        if (v.isEmpty()) return "El correo no puede estar vacío"
        if (!Patterns.EMAIL_ADDRESS.matcher(v).matches()) return "Formato de correo inválido"
        return null
    }

    fun validatePassword(value: String): List<String> {
        if (value.isEmpty()) return listOf("La contraseña no puede estar vacía")
        val errs = mutableListOf<String>()
        if (value.length < 8) errs += "Mínimo 8 caracteres"
        if (!value.any { it.isUpperCase() }) errs += "Al menos 1 mayúscula"
        if (!value.any { it.isLowerCase() }) errs += "Al menos 1 minúscula"
        if (!value.any { it.isDigit() }) errs += "Al menos 1 dígito"
        val symbols = "!@#\$%^&*()_+-=[]{}|;:'\",.<>/?`~\\"
        if (!value.any { it in symbols }) errs += "Al menos 1 símbolo"
        if (value.any { it.isWhitespace() }) errs += "Sin espacios"
        return errs
    }

    fun validateConfirm(pw: String, conf: String): String? {
        if (conf.isEmpty()) return "Confirma tu contraseña"
        if (pw != conf) return "Las contraseñas no coinciden"
        return null
    }

    fun revalidateAll() {
        emailError = validateEmail(email)
        passwordErrors = validatePassword(password)
        confirmError = if (!isLoginMode) validateConfirm(password, confirm) else null
    }

    // Revalidar al cambiar de modo
    LaunchedEffect(isLoginMode) {
        if (isLoginMode) {
            confirm = ""
            confirmError = null
        }
        revalidateAll()
    }

    // Revalidación en cambios
    LaunchedEffect(email) { emailError = validateEmail(email) }
    LaunchedEffect(password) {
        passwordErrors = validatePassword(password)
        if (!isLoginMode) confirmError = validateConfirm(password, confirm)
    }
    LaunchedEffect(confirm) {
        if (!isLoginMode) confirmError = validateConfirm(password, confirm)
    }

    val canSubmit = remember(emailError, passwordErrors, confirmError, email, password, confirm, isLoginMode, isLoading) {
        if (isLoading) return@remember false
        val baseOk = emailError == null && passwordErrors.isEmpty() && email.isNotBlank() && password.isNotBlank()
        if (isLoginMode) baseOk else baseOk && confirmError == null && confirm.isNotBlank()
    }

    fun submit() {
        revalidateAll()
        if (!canSubmit) return
        val e = email.trim()
        val p = password // no trimeamos pass por si usuario quiere espacios (aunque lo invalidamos arriba)
        if (isLoginMode) onLogin(e, p) else onRegister(e, p)
    }

    Scaffold { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Gestor de Voluntariado",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (isLoginMode) "Iniciar sesión" else "Registrarse",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(Modifier.height(16.dp))

            if (error != null) {
                // Error que venga de tu capa superior (Firebase, etc.)
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(emailError!!, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Usa un correo válido", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocus.requestFocus() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                isError = passwordErrors.isNotEmpty(),
                supportingText = {
                    if (passwordErrors.isNotEmpty()) {
                        Column {
                            passwordErrors.forEach { msg ->
                                Text("• $msg", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    } else {
                        Text(
                            "Debe incluir mayúscula, minúscula, número y símbolo (mín. 8)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(if (showPassword) "Ocultar" else "Ver")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (isLoginMode) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { if (!isLoginMode) confirmFocus.requestFocus() },
                    onDone = {
                        if (isLoginMode) {
                            submit()
                            focusManager.clearFocus()
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocus)
            )
            Spacer(Modifier.height(12.dp))

            // CONFIRM PASSWORD (solo registro)
            if (!isLoginMode) {
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = { Text("Confirmar contraseña") },
                    singleLine = true,
                    isError = confirmError != null,
                    supportingText = {
                        if (confirmError != null) {
                            Text(confirmError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Text(if (showConfirmPassword) "Ocultar" else "Ver")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            submit()
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(confirmFocus)
                )
                Spacer(Modifier.height(12.dp))
            }

            // SUBMIT
            Button(
                onClick = {
                    submit()
                    focusManager.clearFocus()
                },
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (isLoginMode) "Iniciar sesión" else "Registrarme")
            }

            Spacer(Modifier.height(12.dp))

            // Separador "o"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(Modifier.weight(1f))
                Text(
                    "  o  ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge
                )
                HorizontalDivider(Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))

            // GOOGLE
            OutlinedButton(
                onClick = { if (!isLoading) onGoogleSignIn() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Continuar con Google")
            }

            Spacer(Modifier.height(8.dp))

            // CAMBIO DE MODO
            TextButton(onClick = {
                if (!isLoading) {
                    isLoginMode = !isLoginMode
                    // limpiar confirmaciones al cambiar de modo
                    confirm = ""
                    confirmError = null
                }
            }) {
                Text(
                    if (isLoginMode)
                        "¿No tienes cuenta? Regístrate"
                    else
                        "¿Ya tienes cuenta? Inicia sesión"
                )
            }

            if (isLoading) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}


