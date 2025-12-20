package nl.tue.hci.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import nl.tue.hci.core.model.UserRole
import nl.tue.hci.core.R


@Preview
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        val viewModel = remember { LoginViewModel() }
        
        // 设置 isSigningUp 为 true 以显示密码字段和角色选择器
        LaunchedEffect(Unit) {
            viewModel.enableSignUp()
            viewModel.selectRole(UserRole.DINER)
        }
        
        LoginScreen(
            onRoleSelected = {},
            modifier = Modifier.background(color = Color.White),
            viewModel = viewModel
        )
    }
}

@Composable
fun LoginScreen(
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation events
    LaunchedEffect(uiState.navigationEvent) {
        when (val event = uiState.navigationEvent) {
            is LoginNavigationEvent.NavigateToRoleSelection -> {
                // TODO: Navigate to role selection screen or handle email login
                // For now, you can trigger role selection callback
                // onRoleSelected(UserRole.DINER) // Example
                viewModel.consumeNavigationEvent()
            }
            is LoginNavigationEvent.NavigateWithGoogle -> {
                // TODO: Handle Google login navigation
                // onRoleSelected(UserRole.DINER) // Example
                viewModel.consumeNavigationEvent()
            }
            is LoginNavigationEvent.NavigateWithApple -> {
                // TODO: Handle Apple login navigation
                // onRoleSelected(UserRole.DINER) // Example
                viewModel.consumeNavigationEvent()
            }
            is LoginNavigationEvent.Consumed -> {
                // Event already consumed, do nothing
            }
            null -> {
                // No navigation event
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
//            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Welcome Title
            Text(
                text = "BesteChef",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                color = colorResource(R.color.text_primary),
            )

            Text(
                text = "Match your ... (TODO)",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp),
                color = colorResource(R.color.text_primary),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message display
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // Email input field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                placeholder = { 
                    Text(
                        text = "email@domain.com",
                        color = colorResource(R.color.text_secondary)
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = colorResource(R.color.outline_light),
                    unfocusedBorderColor = colorResource(R.color.outline_light),
                    errorBorderColor = MaterialTheme.colorScheme.error,
                ),
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = uiState.errorMessage != null
            )
            
            // Password input fields (only shown when signing up)
            if (uiState.isSigningUp) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Password field
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    placeholder = {
                        Text(
                            text = "password",
                            color = colorResource(R.color.text_secondary)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = colorResource(R.color.outline_light),
                        unfocusedBorderColor = colorResource(R.color.outline_light),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                    ),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    isError = uiState.errorMessage != null,
                    visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (uiState.passwordVisible) "Hide password" else "Show password",
                                tint = colorResource(R.color.text_secondary)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(1.dp))

                // Confirm password field
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = { viewModel.updateConfirmPassword(it) },
                    placeholder = {
                        Text(
                            text = "confirm password...",
                            color = colorResource(R.color.text_secondary)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = colorResource(R.color.outline_light),
                        unfocusedBorderColor = colorResource(R.color.outline_light),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                    ),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    isError = uiState.errorMessage != null,
                    visualTransformation = if (uiState.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                            Icon(
                                imageVector = if (uiState.confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (uiState.confirmPasswordVisible) "Hide password" else "Show password",
                                tint = colorResource(R.color.text_secondary)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Role selector
                RoleSelector(
                    selectedRole = uiState.selectedRole,
                    onRoleSelected = { viewModel.selectRole(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continue button (mint green)
            Button(
                onClick = { viewModel.onContinueClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.diner_primary_color)
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colorResource(R.color.text_primary),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Continue",
                        color = colorResource(R.color.text_primary),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider with "or"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colorResource(R.color.outline_light)
                )
                Text(
                    text = "or",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colorResource(R.color.outline_light),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google login button
            SocialLoginButton(
                onClick = { viewModel.onGoogleLoginClick() },
                text = "Continue with Google",
                icon = "G",
                iconColor = Color(0xFF4285F4),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Apple login button
            SocialLoginButton(
                onClick = { viewModel.onAppleLoginClick() },
                text = "Continue with Apple",
                icon = "🍎",
                iconColor = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )
        }

        // Legal disclaimer at the bottom
        LegalDisclaimer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

@Composable
fun SocialLoginButton(
    onClick: () -> Unit,
    text: String,
    icon: String,
    iconColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = colorResource(R.color.button_grey),
            contentColor = colorResource(R.color.text_primary),
        ),
        border = null,
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon == "G") {
                // Google logo - colorful "G"
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4285F4), // Blue
                                    Color(0xFF34A853), // Green
                                    Color(0xFFFBBC05), // Yellow
                                    Color(0xFFEA4335)  // Red
                                )
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // Apple logo
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun RoleSelector(
    selectedRole: UserRole?,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .background(
                color = colorResource(R.color.button_grey),
                shape = RoundedCornerShape(20.dp)
            ),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Chef option
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    color = if (selectedRole == UserRole.CHEF) {
                        colorResource(R.color.chef_primary_color)
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onRoleSelected(UserRole.CHEF) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Chef",
                color = if (selectedRole == UserRole.CHEF) {
                    Color.White
                } else {
                    colorResource(R.color.text_secondary)
                },
                fontSize = 16.sp,
                fontWeight = if (selectedRole == UserRole.CHEF) FontWeight.Medium else FontWeight.Normal
            )
        }

        // Diner option
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    color = if (selectedRole == UserRole.DINER) {
                        colorResource(R.color.diner_primary_color)
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onRoleSelected(UserRole.DINER) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Diner",
                color = if (selectedRole == UserRole.DINER) {
                    Color.White
                } else {
                    colorResource(R.color.text_secondary)
                },
                fontSize = 16.sp,
                fontWeight = if (selectedRole == UserRole.DINER) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

@Composable
fun LegalDisclaimer(
    modifier: Modifier = Modifier
) {
    val annotatedText = buildAnnotatedString {
        append("By clicking continue, you agree to our ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Terms of Service")
        }
        append(" and ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Privacy Policy")
        }
    }

    Text(
        text = annotatedText,
//        onClick = { offset ->
//            // Handle click on Terms of Service or Privacy Policy
//            // You can add navigation logic here
//        },
        style = MaterialTheme.typography.bodySmall.copy(
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        ),
        modifier = modifier.padding(horizontal = 32.dp)
    )
}


