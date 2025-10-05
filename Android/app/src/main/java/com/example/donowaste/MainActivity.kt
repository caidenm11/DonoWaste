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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.donowaste.data.DonationsRepository
import com.example.donowaste.data.UsersRepository
import com.example.donowaste.models.UserProfile
import com.example.donowaste.navigation.AppNavigation
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

@Composable
fun AppNavigator(
    usersRepo: UsersRepository = UsersRepository(),
    onRequestGoogleSignIn: () -> Unit
) {
    val mainActivity = (LocalContext.current as? MainActivity)
    val auth = Firebase.auth
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
            isLoading = true
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
            } catch (e: Exception) {
                Log.e("AppNavigator", "Firebase sign-in failed", e)
                isLoading = false
            }
        }
    }

    // This effect refetches the profile whenever the user changes (signs in/out)
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            isLoading = true
            try {
                userProfile = usersRepo.ensureAndGetUserProfile()
            } catch (e: Exception) {
                Log.e("AppNavigator", "Failed to get user profile", e)
            } finally {
                isLoading = false
            }
        } else {
            userProfile = null
            isLoading = false
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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
                            // Refetch profile to get the new role
                            userProfile = usersRepo.ensureAndGetUserProfile()
                        } catch (e: Exception) {
                            Log.e("AppNavigator", "Failed to update role", e)
                        } finally {
                            isLoading = false
                        }
                    }
                }
            }
            else -> {
                // Pass the user profile and a callback to handle role switching
                MainAppContent(
                    userProfile = userProfile!!,
                    onSwitchRole = {
                        scope.launch {
                            isLoading = true
                            try {
                                val newRole = if (userProfile!!.role == "donor") "donatee" else "donor"
                                usersRepo.updateUserRole(newRole)
                                // Re-fetch the profile to update the UI with the new role
                                userProfile = usersRepo.ensureAndGetUserProfile()
                            } catch (e: Exception) {
                                Log.e("AppNavigator", "Failed to switch role", e)
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

@Composable
fun LoginScreen(
    onGoogleSignInClick: () -> Unit,
    onEmailSignIn: (String, String) -> Unit,
    onEmailCreateAccount: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
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

// MainAppContent now needs the onSwitchRole callback
@Composable
fun MainAppContent(
    userProfile: UserProfile,
    onSwitchRole: () -> Unit,
    donationsRepo: DonationsRepository = DonationsRepository()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome, ${userProfile.displayName}!", style = MaterialTheme.typography.titleLarge)
        Text("Your role: ${userProfile.role}", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))

        // This button now uses the hoisted callback
        Button(onClick = onSwitchRole) {
            Text(text = "Switch to ${if (userProfile.role == "donor") "Donatee" else "Donor"}")
        }
        
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                // Sign out from Firebase
                Firebase.auth.signOut()

                // Also sign out from Google to allow account switching
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut()
            },
            colors = ButtonDefaults.outlinedButtonColors()
        ) {
            Text("Sign Out")
        }

        Spacer(Modifier.height(32.dp))

        if (userProfile.role == "donor") {
            Button(onClick = {
                scope.launch {
                    donationsRepo.createDonation(title = "Test Donation", category = "Test")
                    // Note: This creates the donation but doesn't show it.
                    // We need to build the donation feed screen to see the result.
                    Log.d("MainAppContent", "Donation creation attempted. Check Firestore.")
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Create a Donation")
            }
        }

        if (userProfile.role == "donatee") {
            Text("Donations will appear here for you to claim.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
