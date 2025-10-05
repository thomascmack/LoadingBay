fun main() {
    println("=== Testing Manifest ===")

    // Test 1: Add one item
    val item1 = InventoryItem("square", 1, false)
    Manifest.addItem(item1)
    val squareItems = Manifest.getItemsByType("square")
    println("Test 1: squareItems = $squareItems")
    assert(squareItems.size == 1 && squareItems[0] == item1) { "Test 1 failed!" }

    // Test 2: Add multiple items of same type
    val item2 = InventoryItem("square", 2, true)
    Manifest.addItem(item2)
    val squareItems2 = Manifest.getItemsByType("square")
    println("Test 2: squareItems after adding another = $squareItems2")
    assert(squareItems2.size == 2) { "Test 2 failed!" }

    // Test 3: Add item of different type
    val item3 = InventoryItem("circle", 1, false)
    Manifest.addItem(item3)
    val circleItems = Manifest.getItemsByType("circle")
    println("Test 3: circleItems = $circleItems")
    assert(circleItems.size == 1 && circleItems[0] == item3) { "Test 3 failed!" }

    // Test 4: Get all types
    val allTypes = Manifest.getAllTypes()
    println("Test 4: allTypes = $allTypes")
    assert(allTypes.contains("square") && allTypes.contains("circle")) { "Test 4 failed!" }

    // Test 5: Get all items
    val allItems = Manifest.getAllItems()
    println("Test 5: allItems = $allItems")
    assert(allItems.keys.containsAll(listOf("square", "circle"))) { "Test 5 failed!" }

    println("✅ All tests passed!")
}
