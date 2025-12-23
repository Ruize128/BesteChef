package nl.tue.hci.feature.diner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@Composable
fun SearchResultScreenPreview() {
    SearchResultsScreen(
        modifier = Modifier
    )
}


@Composable
fun SearchResultsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    // Static data
    val searchLocation = "Eindhoven"
    val searchDate = "12-12-2025"
    val searchGuests = "6 guests"
    
    val chefs = listOf(
        ChefResult(
            name = "Chef Marius van Vlaanderen",
            rating = 5.0f,
            reviewCount = 2,
            eventCount = 13,
            canTravel = true,
            availableOnDate = true,
            quote = "Enhancing classic flavors with a touch of style",
            imageColor = Color(0xFFB2E5D4) // Light mint green
        ),
        ChefResult(
            name = "Chef Example Two",
            rating = 4.8f,
            reviewCount = 5,
            eventCount = 20,
            canTravel = true,
            availableOnDate = true,
            quote = "Creating memorable culinary experiences",
            imageColor = Color(0xFFFFD4B2) // Light orange/peach
        ),
        ChefResult(
            name = "Chef Example Three",
            rating = 4.2f,
            reviewCount = 12,
            eventCount = 30,
            canTravel = false,
            availableOnDate = true,
            quote = "Creating memorable culinary experiences",
            imageColor = Color(0xFFB2E5D4),
        )
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Title with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search Result",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Search parameters row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(20.dp)   // TODO: color
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Eindhoven",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }

            HorizontalDivider(
                modifier = Modifier.size(width = 1.dp, height = 16.dp)
                    .background(Color.DarkGray)
                    .align(Alignment.CenterVertically)
            )

            Box(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "12-12-2025",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }

            HorizontalDivider(
                modifier = Modifier.size(width = 1.dp, height = 16.dp)
                    .background(Color.DarkGray)
                    .align(Alignment.CenterVertically)
            )

            Box(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "6 guests",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
        
        // Action buttons row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                text = "Filter",
                icon = Icons.Default.Home,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                text = "Sort",
                icon = Icons.Default.Favorite,
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                text = "Search text",
                icon = Icons.Default.Search,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Chef results list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(chefs) { chef ->
                ChefResultCard(chef = chef)
            }
        }
    }
}

