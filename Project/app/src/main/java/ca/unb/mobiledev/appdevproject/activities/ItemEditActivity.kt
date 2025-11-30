package ca.unb.mobiledev.appdevproject.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ca.unb.mobiledev.appdevproject.R

class ItemEditActivity : AppCompatActivity() {
    private lateinit var itemNameTextView : TextView
    private lateinit var itemIDTextView : TextView
    private lateinit var damagedCheckbox : CheckBox
    private lateinit var descExitText : EditText
    private var itemID: Long = 0
    private lateinit var itemName : String
    private var upc : Long = 0
    private var damaged : Boolean = false
    private lateinit var description : String
    private lateinit var saveButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        itemNameTextView = findViewById(R.id.itemName)
        itemIDTextView = findViewById(R.id.itemID)
        damagedCheckbox = findViewById(R.id.damaged)
        descExitText = findViewById(R.id.description)

        itemID = intent.getLongExtra("itemID", 0)
        itemName = intent.getStringExtra("itemName") ?: ""
        upc = intent.getLongExtra("upc", 0)
        damaged = intent.getBooleanExtra("damaged", false)
        description = intent.getStringExtra("description") ?: ""

        updateItemView()

        descExitText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                description = s.toString()
            }
        })

        damagedCheckbox.tag = true
        damagedCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(damagedCheckbox.tag == true) {
                damaged = damagedCheckbox.isChecked
            }
        }

        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            val intent = Intent()
            Log.d("Item Edit", "sending $itemID")
            intent.putExtra("itemID", itemID)
            intent.putExtra("upc", upc)
            intent.putExtra("damaged", damaged)
            intent.putExtra("description", description)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    fun updateItemView() {
        damagedCheckbox.tag = false
        damagedCheckbox.isChecked = damaged
        damagedCheckbox.tag = true
        descExitText.setText(description)
        itemNameTextView.text = itemName
        itemIDTextView.text = upc.toString()
    }
}