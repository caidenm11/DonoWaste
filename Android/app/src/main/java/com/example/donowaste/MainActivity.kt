package com.example.donowaste

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.donowaste.data.UsersRepository
import com.example.donowaste.models.UserProfile
import com.example.donowaste.screens.donatee.DonateeHomeScreen
import com.example.donowaste.screens.donatee.DonationsScreen
import com.example.donowaste.screens.donatee.PackageScreen
import com.example.donowaste.screens.donatee.PickupScreen
import com.example.donowaste.screens.donator.CreateItemScreen
import com.example.donowaste.screens.donator.CreatePackageScreen
import com.example.donowaste.screens.donator.DonatorHomeScreen
import com.example.donowaste.screens.donator.RecipientScreen
import com.example.donowaste.ui.RoleSelectionScreen
import com.example.donowaste.ui.theme.DonoWasteTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken!!
                onGoogleSignInSuccess?.invoke(idToken)
            } catch (e: ApiException) {
                Log.w("Auth", "Google sign in failed", e)
            }
        }
    }

    private var onGoogleSignInRequest: (() -> Unit)? = null
    private var onGoogleSignInSuccess: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onGoogleSignInRequest = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

        setContent {
            DonoWasteTheme {
                AppNavigator(onRequestGoogleSignIn = { onGoogleSignInRequest?.invoke() })
            }
        }
    }

    @Composable
    fun SetSignInSuccessHandler(handler: (String) -> Unit) {
        LaunchedEffect(Unit) {
            onGoogleSignInSuccess = handler
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigator(
    onRequestGoogleSignIn: () -> Unit
) {
    val usersRepo = remember { UsersRepository() }
    val mainActivity = (LocalContext.current as? MainActivity)
    val auth = Firebase.auth
    val snackbarHostState = remember { SnackbarHostState() }
    var currentUser by remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    val scope = rememberCoroutineScope()
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    mainActivity?.SetSignInSuccessHandler { idToken ->
        scope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
            } catch (e: Exception) {
                Log.e("AppNavigator", "Firebase sign-in failed", e)
                snackbarHostState.showSnackbar(e.message ?: "Google Sign-In failed.")
            }
        }
    }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            isLoading = true
            try {
                userProfile = usersRepo.ensureAndGetUserProfile()
            } catch (e: Exception) {
                Log.e("AppNavigator", "Failed to get user profile", e)
                snackbarHostState.showSnackbar(e.message ?: "Failed to load profile.")
            } finally {
                isLoading = false
            }
        } else {
            userProfile = null
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                currentUser == null -> {
                    LoginScreen(
                        onGoogleSignInClick = onRequestGoogleSignIn,
                        onEmailSignIn = { email, password ->
                            scope.launch {
                                isLoading = true
                                try {
                                    usersRepo.signInWithEmailPassword(email, password)
                                } catch (e: Exception) {
                                    Log.e("Auth", "Email sign in failed", e)
                                    snackbarHostState.showSnackbar(e.message ?: "Sign in failed.")
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        onEmailCreateAccount = { email, password ->
                            scope.launch {
                                isLoading = true
                                try {
                                    usersRepo.createUserWithEmailPassword(email, password)
                                } catch (e: Exception) {
                                    Log.e("Auth", "Email create account failed", e)
                                    snackbarHostState.showSnackbar(e.message ?: "Registration failed.")
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    )
                }
                userProfile == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Loading Profile...", modifier = Modifier.padding(24.dp))
                    }
                }
                userProfile?.role == "both" -> {
                    RoleSelectionScreen { selectedRole ->
                        scope.launch {
                            isLoading = true
                            try {
                                usersRepo.updateUserRole(selectedRole)
                                userProfile = usersRepo.ensureAndGetUserProfile()
                            } catch (e: Exception) {
                                Log.e("AppNavigator", "Failed to update role", e)
                                snackbarHostState.showSnackbar(e.message ?: "Role selection failed.")
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }
                else -> {
                    MainAppContent(
                        userProfile = userProfile!!,
                        onSwitchRole = {
                            scope.launch {
                                isLoading = true
                                try {
                                    val newRole = if (userProfile!!.role == "donor") "donatee" else "donor"
                                    usersRepo.updateUserRole(newRole)
                                    userProfile = usersRepo.ensureAndGetUserProfile()
                                } catch (e: Exception) {
                                    Log.e("AppNavigator", "Failed to switch role", e)
                                    snackbarHostState.showSnackbar(e.message ?: "Role switch failed.")
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    onGoogleSignInClick: () -> Unit,
    onEmailSignIn: (String, String) -> Unit,
    onEmailCreateAccount: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to DonoWaste", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { onEmailCreateAccount(email, password) }) {
                Text("Create Account")
            }
            Button(onClick = { onEmailSignIn(email, password) }) {
                Text("Sign In")
            }
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = onGoogleSignInClick, modifier = Modifier.fillMaxWidth()) {
            Text("Sign in with Google")
        }
    }
}

@Composable
fun MainAppContent(
    userProfile: UserProfile,
    onSwitchRole: () -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Welcome, ${userProfile.displayName}!", style = MaterialTheme.typography.titleLarge)
                Text("Your role: ${userProfile.role}", style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = onSwitchRole) {
                Text(text = "Switch to ${if (userProfile.role == "donor") "Donatee" else "Donor"}")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    Firebase.auth.signOut()
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    GoogleSignIn.getClient(context, gso).signOut()
                },
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Text("Sign Out")
            }
        }

        Spacer(Modifier.height(16.dp))

        val startDestination = if (userProfile.role == "donor") "donator_home" else "donatee_home"

        NavHost(navController = navController, startDestination = startDestination) {
            // Donatee Flow
            composable("donatee_home") { DonateeHomeScreen(navController) }
            composable("donations_page") { DonationsScreen() }
            composable("pickup_page") { PickupScreen() }
            composable("package_page/{packageId}") { backStackEntry ->
                PackageScreen(
    //                navController = navController,
    //                packageId = backStackEntry.arguments?.getString("packageId")
                )
            }

            // Donator Flow
            composable("donator_home") { DonatorHomeScreen(navController) }
            composable("recipient_page") { RecipientScreen(navController) }
            composable("create_package_page") { CreatePackageScreen(navController) }
            composable("create_item_page") { CreateItemScreen(navController) }
        }
    }
}
