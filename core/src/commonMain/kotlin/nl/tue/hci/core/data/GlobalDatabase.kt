package nl.tue.hci.core.data

/**
 * Global in-memory database that can be accessed from anywhere in the app.
 * Data is automatically cleared when the application starts.
 * Uses immutable maps for simple, multiplatform-compatible storage.
 */
object GlobalDatabase {
    
    private var stringData = mapOf<String, String>()
    private var intData = mapOf<String, Int>()
    private var booleanData = mapOf<String, Boolean>()
    private var anyData = mapOf<String, Any>()
    
    // String operations
    fun writeString(key: String, value: String) {
        stringData = stringData.toMutableMap().apply { this[key] = value }
    }
    
    fun readString(key: String): String? {
        return stringData[key]
    }
    
    fun readString(key: String, defaultValue: String): String {
        return readString(key) ?: defaultValue
    }
    
    // Int operations
    fun writeInt(key: String, value: Int) {
        intData = intData.toMutableMap().apply { this[key] = value }
    }
    
    fun readInt(key: String): Int? {
        return intData[key]
    }
    
    fun readInt(key: String, defaultValue: Int): Int {
        return readInt(key) ?: defaultValue
    }
    
    // Boolean operations
    fun writeBoolean(key: String, value: Boolean) {
        booleanData = booleanData.toMutableMap().apply { this[key] = value }
    }
    
    fun readBoolean(key: String): Boolean? {
        return booleanData[key]
    }
    
    fun readBoolean(key: String, defaultValue: Boolean): Boolean {
        return readBoolean(key) ?: defaultValue
    }
    
    // Generic object operations
    fun <T : Any> writeObject(key: String, value: T) {
        anyData = anyData.toMutableMap().apply { this[key] = value }
    }
    
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> readObject(key: String): T? {
        return anyData[key] as? T
    }
    
    // Check if key exists
    fun containsKey(key: String): Boolean {
        return stringData.containsKey(key) ||
                intData.containsKey(key) ||
                booleanData.containsKey(key) ||
                anyData.containsKey(key)
    }
    
    // Remove operations
    fun remove(key: String) {
        stringData = stringData.toMutableMap().apply { remove(key) }
        intData = intData.toMutableMap().apply { remove(key) }
        booleanData = booleanData.toMutableMap().apply { remove(key) }
        anyData = anyData.toMutableMap().apply { remove(key) }
    }
    
    // Clear all data (called on app start)
    fun clear() {
        stringData = emptyMap()
        intData = emptyMap()
        booleanData = emptyMap()
        anyData = emptyMap()
    }
    
    // Get all keys
    fun getAllKeys(): Set<String> {
        val keys = mutableSetOf<String>()
        keys.addAll(stringData.keys)
        keys.addAll(intData.keys)
        keys.addAll(booleanData.keys)
        keys.addAll(anyData.keys)
        return keys
    }
    
    // Get data counts
    fun size(): Int {
        return stringData.size +
                intData.size +
                booleanData.size +
                anyData.size
    }
}
