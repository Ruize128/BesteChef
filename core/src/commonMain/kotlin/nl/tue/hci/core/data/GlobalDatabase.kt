package nl.tue.hci.core.data

/**
 * Global in-memory database that can be accessed from anywhere in the app.
 * Data is automatically cleared when the application starts.
 * Thread-safe operations for concurrent access.
 */
object GlobalDatabase {
    
    private val stringData = mutableMapOf<String, String>()
    private val intData = mutableMapOf<String, Int>()
    private val booleanData = mutableMapOf<String, Boolean>()
    private val anyData = mutableMapOf<String, Any>()
    
    // String operations
    fun writeString(key: String, value: String) {
        synchronized(stringData) {
            stringData[key] = value
        }
    }
    
    fun readString(key: String): String? {
        return synchronized(stringData) {
            stringData[key]
        }
    }
    
    fun readString(key: String, defaultValue: String): String {
        return readString(key) ?: defaultValue
    }
    
    // Int operations
    fun writeInt(key: String, value: Int) {
        synchronized(intData) {
            intData[key] = value
        }
    }
    
    fun readInt(key: String): Int? {
        return synchronized(intData) {
            intData[key]
        }
    }
    
    fun readInt(key: String, defaultValue: Int): Int {
        return readInt(key) ?: defaultValue
    }
    
    // Boolean operations
    fun writeBoolean(key: String, value: Boolean) {
        synchronized(booleanData) {
            booleanData[key] = value
        }
    }
    
    fun readBoolean(key: String): Boolean? {
        return synchronized(booleanData) {
            booleanData[key]
        }
    }
    
    fun readBoolean(key: String, defaultValue: Boolean): Boolean {
        return readBoolean(key) ?: defaultValue
    }
    
    // Generic object operations
    fun <T : Any> writeObject(key: String, value: T) {
        synchronized(anyData) {
            anyData[key] = value
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> readObject(key: String): T? {
        return synchronized(anyData) {
            anyData[key] as? T
        }
    }
    
    // Check if key exists
    fun containsKey(key: String): Boolean {
        return synchronized(stringData) {
            stringData.containsKey(key)
        } || synchronized(intData) {
            intData.containsKey(key)
        } || synchronized(booleanData) {
            booleanData.containsKey(key)
        } || synchronized(anyData) {
            anyData.containsKey(key)
        }
    }
    
    // Remove operations
    fun remove(key: String) {
        synchronized(stringData) {
            stringData.remove(key)
        }
        synchronized(intData) {
            intData.remove(key)
        }
        synchronized(booleanData) {
            booleanData.remove(key)
        }
        synchronized(anyData) {
            anyData.remove(key)
        }
    }
    
    // Clear all data (called on app start)
    fun clear() {
        synchronized(stringData) {
            stringData.clear()
        }
        synchronized(intData) {
            intData.clear()
        }
        synchronized(booleanData) {
            booleanData.clear()
        }
        synchronized(anyData) {
            anyData.clear()
        }
    }
    
    // Get all keys
    fun getAllKeys(): Set<String> {
        val keys = mutableSetOf<String>()
        synchronized(stringData) {
            keys.addAll(stringData.keys)
        }
        synchronized(intData) {
            keys.addAll(intData.keys)
        }
        synchronized(booleanData) {
            keys.addAll(booleanData.keys)
        }
        synchronized(anyData) {
            keys.addAll(anyData.keys)
        }
        return keys
    }
    
    // Get data counts
    fun size(): Int {
        var count = 0
        synchronized(stringData) {
            count += stringData.size
        }
        synchronized(intData) {
            count += intData.size
        }
        synchronized(booleanData) {
            count += booleanData.size
        }
        synchronized(anyData) {
            count += anyData.size
        }
        return count
    }
}
