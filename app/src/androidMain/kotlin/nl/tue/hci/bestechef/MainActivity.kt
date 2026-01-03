package nl.tue.hci.bestechef

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import nl.tue.hci.core.data.PlatformContext

class MainActivity : ComponentActivity() {
    private var navigateToOrders = false
    private var navigateToChat = false
    private var chatCustomerName: String? = null
    
    // Request permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission is granted, notifications can be shown
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        PlatformContext.context = applicationContext
        
        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        // Check if we should navigate from notification
        val navigateTo = intent?.getStringExtra("navigate_to")
        navigateToOrders = navigateTo == "orders"
        navigateToChat = navigateTo == "chat"
        chatCustomerName = intent?.getStringExtra("customer_name")

        setContent {
            BesteChefApp(
                initialNavigateToOrders = navigateToOrders,
                initialNavigateToChat = navigateToChat,
                initialChatCustomerName = chatCustomerName
            )
        }
    }
}
