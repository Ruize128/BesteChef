package nl.tue.hci.login

import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.model.UserRole
import nl.tue.hci.core.R


@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onRoleSelected = {},
        modifier = Modifier.background(color = Color.White)
    )
}

@Composable
fun LoginScreen(
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Welcome Title
        Text(
            text = "BesteChef",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Match your ... (TODO)",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        val emailAddress = TextFieldValue;
        // Input the account (e-mail address)
//        var email by rememberSavable { mutableStateOf("") }
        EmailInputField(
            email = "",
            onEmailChange = {},

        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                ,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.diner_primary_color)

            )
        ) {
            Text(
                text = "Continue",
                color = colorResource(R.color.text_primary)
            )
        }




        Spacer(modifier = Modifier.height(16.dp))
        
//        Button(
//            onClick = { onRoleSelected(UserRole.CHEF) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.primary
//            )
//        ) {
//            Text(
//                text = "I am a Chef",
//                style = MaterialTheme.typography.titleMedium
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedButton(
//            onClick = { onRoleSelected(UserRole.DINER) },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp)
//        ) {
//            Text(
//                text = "I am a Diner",
//                style = MaterialTheme.typography.titleMedium
//            )
//        }
    }
}


@Composable
fun EmailInputField(
    email: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "email",
    onDone: () -> Unit = {}
) {
//    val focusManager
    OutlinedTextField(
        value = email,
        placeholder = { Text(text = "example@domaim.com", color = Color.Gray) },
        onValueChange = onEmailChange,
        label = { Text(label) },
        singleLine = true,
//        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
    )
}

