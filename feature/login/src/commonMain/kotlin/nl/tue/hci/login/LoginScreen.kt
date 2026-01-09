package nl.tue.hci.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.graphics.painter.Painter
// Preview removed for multiplatform compatibility
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.onFocusChanged
import nl.tue.hci.core.model.UserRole
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.icons.rememberIconPainter
import nl.tue.hci.core.ui.PlatformBackHandler


// @androidx.compose.ui.tooling.preview.Preview - removed for multiplatform
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        val coroutineScope = rememberCoroutineScope()
        val stateHolder = remember { LoginStateHolder(coroutineScope) }
        
        LaunchedEffect(Unit) {
            stateHolder.enableSignIn()
            stateHolder.selectRole(UserRole.DINER)
        }
        
        LoginScreen(
            onLogin = {},
            modifier = Modifier,
            stateHolder = stateHolder
        )
    }
}

@Composable
fun LoginScreen(
    onLogin: (UserRole) -> Unit,
    modifier: Modifier = Modifier,
    stateHolder: LoginStateHolder? = null
) {
    val colors = BesteChefThemeColors.current()
    val coroutineScope = rememberCoroutineScope()
    val loginStateHolder = stateHolder ?: remember { LoginStateHolder(coroutineScope) }
    val uiState by loginStateHolder.uiState.collectAsState()

    // Handle back button - go back to sign in mode when in register mode
    PlatformBackHandler(
        enabled = uiState.isSigningUp,
        onBack = {
            loginStateHolder.resetToSignIn()
        }
    )

    // Handle navigation events
    LaunchedEffect(uiState.navigationEvent) {
        when (val event = uiState.navigationEvent) {
            is LoginNavigationEvent.NavigateToRoleSelection -> {
                // TODO: Navigate to role selection screen or handle email login
                loginStateHolder.consumeNavigationEvent()
            }
            is LoginNavigationEvent.NavigateToDinerMainPage -> {
                // Jump to Diner Main Page directly (UI prototype - no validation)
                onLogin(event.role)
                loginStateHolder.consumeNavigationEvent()
            }
            is LoginNavigationEvent.NavigateToChefMainPage -> {
                // Jump to Diner Main Page directly (UI prototype - no validation)
                onLogin(event.role)
                loginStateHolder.consumeNavigationEvent()
            }
            is LoginNavigationEvent.NavigateWithGoogle -> {
                // TODO: Handle Google login navigation
                loginStateHolder.consumeNavigationEvent()
            }
            is LoginNavigationEvent.NavigateWithApple -> {
                // TODO: Handle Apple login navigation
                loginStateHolder.consumeNavigationEvent()
            }
            is LoginNavigationEvent.Consumed -> {
                // Event already consumed, do nothing
            }
            null -> {
                // No navigation event
            }
        }
    }

    val typography = BesteChefThemeTypography.current()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
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
            style = typography.sectionTitle,
                modifier = Modifier.padding(bottom = 8.dp),
                color = colors.textPrimary,
        )
        
        Text(
            text = "Match your ... (TODO)",
            style = typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp),
                color = colors.textPrimary,
        )
        
        Spacer(modifier = Modifier.height(16.dp))

            // Error message display
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = colors.error,
                    style = typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // Email input field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { loginStateHolder.updateEmail(it) },
                placeholder = { 
                    Text(
                        text = "email@domain.com",
                        color = colors.textSecondary
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused) {
                            // user left the email input: trigger mode check
                            loginStateHolder.onEmailFocusLost()
                        }
                    }
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface,
                    focusedBorderColor = colors.outline,
                    unfocusedBorderColor = colors.outline,
                    errorBorderColor = colors.error,
                ),
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = uiState.errorMessage != null
            )
            
            // Password input fields (only shown when signing up)
            if (uiState.isSigningUp) {
                Spacer(modifier = Modifier.height(16.dp))

                ConfirmPasswordInput(
                    stateHolder = loginStateHolder
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Role selector
                RoleSelector(
                    selectedRole = uiState.selectedRole,
                    onRoleSelected = { loginStateHolder.selectRole(it) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            } else if (uiState.isSigningIn) {
                // when account exists, input the password
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { loginStateHolder.updatePassword(it) },
                    placeholder = {
                        Text(
                            text = "password",
                            color = colors.textSecondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedBorderColor = colors.outline,
                        unfocusedBorderColor = colors.outline,
                        errorBorderColor = colors.error,
                    ),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    isError = uiState.errorMessage != null,
                    visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { loginStateHolder.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (uiState.passwordVisible) "Hide password" else "Show password",
                                tint = colors.textSecondary
                            )
                        }
                    }
                )
            }

        Spacer(modifier = Modifier.height(16.dp))

            // Continue button - changes color based on selected role
        Button(
                onClick = { loginStateHolder.onContinueClick() },
            modifier = Modifier
                .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = when (uiState.selectedRole) {
                    UserRole.CHEF -> colors.chefPrimary
                    UserRole.DINER -> colors.dinerPrimary
                    else -> colors.dinerPrimary // Default to diner if no role selected
                }
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colors.textPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Continue",
                        color = colors.textPrimary,
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
                    color = colors.outline
                )
                Text(
                    text = "or",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = colors.textSecondary,
                    fontSize = 14.sp
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colors.outline,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google login button
            SocialLoginButton(
                onClick = { loginStateHolder.onGoogleLoginClick() },
                text = "Continue with Google",
                iconPainter = rememberIconPainter("google_logo"),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Apple login button
            SocialLoginButton(
                onClick = { loginStateHolder.onAppleLoginClick() },
                text = "Continue with Apple",
                iconPainter = rememberIconPainter("apple_logo"),
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
    iconPainter: Painter?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = BesteChefThemeColors.current()
    
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = colors.surfaceContainer,
            contentColor = colors.textPrimary,
        ),
        border = null,
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconPainter != null) {
                Image(
                    painter = iconPainter,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary
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
    val colors = BesteChefThemeColors.current()
    
    Row(
        modifier = modifier
            .height(40.dp)
            .background(
                color = colors.surfaceContainer,
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
                        colors.chefPrimary
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
                    colors.textOnPrimary
                } else {
                    colors.textSecondary
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
                        colors.dinerPrimary
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
                    colors.textOnPrimary
                } else {
                    colors.textSecondary
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
    val colors = BesteChefThemeColors.current()
    
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
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        ),
        modifier = modifier.padding(horizontal = 32.dp)
    )
}


@Composable
fun ConfirmPasswordInput(
    stateHolder: LoginStateHolder
) {
    val colors = BesteChefThemeColors.current()
    val uiState by stateHolder.uiState.collectAsState()

    // Password field
    OutlinedTextField(
        value = uiState.password,
        onValueChange = { stateHolder.updatePassword(it) },
        placeholder = {
            Text(
                text = "password",
                color = colors.textSecondary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            focusedBorderColor = colors.outline,
            unfocusedBorderColor = colors.outline,
            errorBorderColor = colors.error,
        ),
        singleLine = true,
        enabled = !uiState.isLoading,
        isError = uiState.errorMessage != null,
        visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { stateHolder.togglePasswordVisibility() }) {
                Icon(
                    imageVector = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (uiState.passwordVisible) "Hide password" else "Show password",
                    tint = colors.textSecondary
                )
            }
        }
    )

    Spacer(modifier = Modifier.height(1.dp))

    // Confirm password field
    OutlinedTextField(
        value = uiState.confirmPassword,
        onValueChange = { stateHolder.updateConfirmPassword(it) },
        placeholder = {
            Text(
                text = "confirm password...",
                color = colors.textSecondary
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            focusedBorderColor = colors.outline,
            unfocusedBorderColor = colors.outline,
            errorBorderColor = colors.error,
        ),
        singleLine = true,
        enabled = !uiState.isLoading,
        isError = uiState.errorMessage != null,
        visualTransformation = if (uiState.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { stateHolder.toggleConfirmPasswordVisibility() }) {
                Icon(
                    imageVector = if (uiState.confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (uiState.confirmPasswordVisible) "Hide password" else "Show password",
                    tint = colors.textSecondary
                )
            }
        }
    )
}