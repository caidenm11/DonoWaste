package com.example.donowaste

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.example.donowaste.data.DonationsRepository
import com.example.donowaste.data.UsersRepository
import com.example.donowaste.models.UserProfile
import com.example.donowaste.ui.RoleSelectionScreen
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

    // We create the launcher here at the Activity level
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val idToken = account.idToken!!
                // Pass the token to the Composable to handle Firebase sign-in
                onGoogleSignInSuccess?.invoke(idToken)
            } catch (e: ApiException) {
                Log.w("Auth", "Google sign in failed", e)
            }
        }
    }

    // Callback to trigger the sign-in flow from Compose
    private var onGoogleSignInRequest: (() -> Unit)? = null
    // Callback to pass the result token back to Compose
    private var onGoogleSignInSuccess: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define the sign-in request logic
        onGoogleSignInRequest = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

        setContent {
            MaterialTheme {
                // Pass the request function into our main navigator
                AppNavigator(onRequestGoogleSignIn = { onGoogleSignInRequest?.invoke() })
            }
        }
    }

    // This is a bridge to allow the Activity result to be handled within Compose's scope
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

    // This handles the token from Google and signs into Firebase
    mainActivity?.SetSignInSuccessHandler { idToken ->
        scope.launch {
            isLoading = true
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
                // The currentUser state change will trigger the LaunchedEffect below
            } catch (e: Exception) {
                Log.e("AppNavigator", "Firebase sign-in failed", e)
                isLoading = false
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
                // Not signed in, show the login button
                LoginScreen(onSignInClick = onRequestGoogleSignIn)
            }
            userProfile == null -> {
                // Signed in, but profile is loading
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
                        } finally {
                            isLoading = false
                        }
                    }
                }
            }
            else -> {
                MainAppContent(userProfile = userProfile!!)
            }
        }
    }
}

@Composable
fun LoginScreen(onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to DonoWaste", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onSignInClick, modifier = Modifier.fillMaxWidth()) {
            Text("Sign in with Google")
        }
    }
}


@Composable
fun MainAppContent(userProfile: UserProfile, donationsRepo: DonationsRepository = DonationsRepository()) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome, ${userProfile.displayName}!", style = MaterialTheme.typography.titleLarge)
        Text("Your role: ${userProfile.role}", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(32.dp))

        if (userProfile.role == "donor") {
            Button(onClick = {
                scope.launch {
                    donationsRepo.createDonation(title = "Test Donation", category = "Test")
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