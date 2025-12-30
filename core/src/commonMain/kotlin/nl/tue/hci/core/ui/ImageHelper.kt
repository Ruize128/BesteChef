package nl.tue.hci.core.ui

/**
 * Helper function to get image resource name from menu item title.
 * Returns null if no image is available for this item.
 */
fun getImageNameFromTitle(title: String): String? {
    return when {
        title.contains("Yuzu", ignoreCase = true) || title.contains("yuzu", ignoreCase = true) -> "yuzu_mousse"
        title.contains("Grilled Mackerel", ignoreCase = true) || title.contains("Mackerel", ignoreCase = true) -> "grilled_mackerel_with_miso"
        title.contains("Seared seabass", ignoreCase = true) || title.contains("seabass", ignoreCase = true) -> "seared_seabass"
        title.contains("5-course Omakase", ignoreCase = true) || title.contains("Omakase", ignoreCase = true) -> "omakase_5_course"
        title.contains("Wagyu", ignoreCase = true) || title.contains("wagyu", ignoreCase = true) -> "wagyu_beef_steak"
        title.contains("Sushi", ignoreCase = true) || title.contains("sushi", ignoreCase = true) -> "sushi_platter"
        title.contains("Caesar", ignoreCase = true) || title.contains("caesar", ignoreCase = true) -> "caesar_salad"
        else -> null
    }
}

/**
 * Get all image names for a carousel (for items with multiple images).
 * Returns a list of image names, or null if no images available.
 */
fun getCarouselImageNames(title: String): List<String>? {
    return when {
        title.contains("Grilled Mackerel", ignoreCase = true) || title.contains("Mackerel", ignoreCase = true) -> {
            listOf("grilled_mackerel_with_miso", "grilled_mackerel_with_miso_2", "grilled_mackerel_with_miso_3")
        }
        else -> {
            val singleImage = getImageNameFromTitle(title)
            if (singleImage != null) listOf(singleImage) else null
        }
    }
}

/**
 * Get image resource name for a chef by name.
 * Returns null if no image is available for this chef.
 */
fun getChefImageName(chefName: String): String? {
    return when {
        chefName.contains("Marius", ignoreCase = true) || chefName.contains("ichiraku", ignoreCase = true) -> "ichiraku_menu_cover"
        chefName.contains("Verstappen", ignoreCase = true) || chefName.contains("middle eastern", ignoreCase = true) -> "middle_eastern_cuisine"
        else -> "middle_eastern_cuisine_2"
    }
}

/**
 * Get all image names for a chef carousel.
 * Returns a list of image names, or null if no images available.
 */
fun getChefCarouselImageNames(chefName: String): List<String>? {
    return when {
        chefName.contains("Marius", ignoreCase = true) || chefName.contains("ichiraku", ignoreCase = true) -> {
            listOf("ichiraku_menu_cover", "ichiraku_menu_cover_2", "ichiraku_menu_cover_3")
        }
        else -> {
            val singleImage = getChefImageName(chefName)
            if (singleImage != null) listOf(singleImage) else null
        }
    }
}

/**
 * Get avatar image name for a person by name.
 * Returns null if no image is available.
 * 
 * Note: For chat screens:
 * - In diner chat: "ME" = diner (sophie), "DH" = chef (ichiraku)
 * - In chef chat: "ME" = chef (ichiraku), "DH" = customer (sophie)
 * 
 * The mapping handles both cases by checking context or using alternative identifiers.
 */
fun getAvatarImageName(name: String, isChefContext: Boolean = false): String? {
    return when {
        name.contains("Sophie", ignoreCase = true) -> "sophie"
        name.contains("ichiraku", ignoreCase = true) -> "ichiraku"
        // For chat avatars: context determines mapping
        name == "ME" && !isChefContext -> "sophie" // Diner chat: ME = diner
        name == "ME" && isChefContext -> "ichiraku" // Chef chat: ME = chef
        name == "DH" && !isChefContext -> "ichiraku" // Diner chat: DH = chef
        name == "DH" && isChefContext -> "sophie" // Chef chat: DH = customer
        else -> null
    }
}

