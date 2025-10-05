import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    println("=== Inventory Manifest Console ===")

    while (true) {
        println(
            """
            |------------------------------
            |Choose an option:
            |1. Add new item
            |2. View items by type
            |3. View all types
            |4. View entire manifest
            |5. Quit
            |------------------------------
            """.trimMargin()
        )
        print("Enter choice: ")

        when (scanner.nextLine().trim()) {
            "1" -> {
                print("Enter item type (e.g., square, circle): ")
                val type = scanner.nextLine().trim()

                print("Enter item number: ")
                val numInput = scanner.nextLine().trim()
                val itemNum = numInput.toIntOrNull() ?: 0

                print("Is the item damaged? (y/n): ")
                val damagedInput = scanner.nextLine().trim().lowercase()
                val damaged = damagedInput == "y"

                val item = InventoryItem(type, itemNum, damaged)
                Manifest.addItem(item)

                println("✅ Added: $item")
            }

            "2" -> {
                print("Enter type to view: ")
                val type = scanner.nextLine().trim()
                val items = Manifest.getItemsByType(type)
                if (items.isEmpty()) {
                    println("No items found for type '$type'.")
                } else {
                    println("Items of type '$type':")
                    items.forEach { println(it) }
                }
            }

            "3" -> {
                val types = Manifest.getAllTypes()
                if (types.isEmpty()) {
                    println("No types in manifest yet.")
                } else {
                    println("All types in manifest:")
                    types.forEach { type ->
                        val items = Manifest.getItemsByType(type)
                        val count = items.size
                        val damagedCount = items.count { it.damaged }
                        println("- $type x$count (damaged: $damagedCount)")
                    }
                }
            }

            "4" -> {
                val all = Manifest.getAllItems()
                if (all.isEmpty()) {
                    println("Manifest is empty.")
                } else {
                    println("Full manifest:")
                    all.forEach { (type, items) ->
                        println("Type: $type")
                        items.forEach { println("  $it") }
                    }
                }
            }

            "5" -> {
                println("Exiting...")
                break
            }

            else -> {
                println("Invalid choice. Please try again.")
            }
        }
        println()
    }
}
