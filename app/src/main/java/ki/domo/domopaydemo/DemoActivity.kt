package ki.domo.domopaydemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_demo.*
import java.text.DecimalFormat


/**
 * Example of Activity using Domo Pay solution
 */
class DemoActivity : AppCompatActivity() {


    companion object {


        // TAGs
        private val TAG = DemoActivity::class.java.simpleName


        // Foramtting decimal
        private var DEFAULT_DECIMALFORMAT = DecimalFormat("#.##")

        // Request codes
        private const val DOMOPAY_REQUEST_CODE = 1


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        init()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult $requestCode $resultCode $data")
        when (requestCode) {
            DOMOPAY_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(this, "Paiement réussi !!", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(this, "Paiement annulé...", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun init() {
        // ActionBar setup
        setSupportActionBar(domo_toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val burgersListener = View.OnFocusChangeListener { _, _ ->
            domo_burger_text_total.text = getString(
                R.string.domo_price,
                DEFAULT_DECIMALFORMAT.format(
                    calculateItemTotal(domo_burger_text_quantity, domo_burger_text_price)
                )
            )
            refreshTotal()
        }
        domo_burger_text_quantity.onFocusChangeListener = burgersListener
        domo_burger_text_price.onFocusChangeListener = burgersListener

        val friesListener = View.OnFocusChangeListener { _, _ ->
            domo_fries_text_total.text = getString(
                R.string.domo_price,
                DEFAULT_DECIMALFORMAT.format(
                    calculateItemTotal(domo_fries_text_quantity, domo_fries_text_price)
                )
            )
            refreshTotal()
        }
        domo_fries_text_quantity.onFocusChangeListener = friesListener
        domo_fries_text_price.onFocusChangeListener = friesListener

        val cokesListener = View.OnFocusChangeListener { _, _ ->
            domo_coke_text_total.text = getString(
                R.string.domo_price,
                DEFAULT_DECIMALFORMAT.format(
                    calculateItemTotal(domo_coke_text_quantity, domo_coke_text_price)
                )
            )
            refreshTotal()
        }
        domo_coke_text_quantity.onFocusChangeListener = cokesListener
        domo_coke_text_price.onFocusChangeListener = cokesListener

        domo_button_valid.setOnClickListener { validOrder() }

    }


    private fun validOrder() {

        val uri = Uri.parse("pay:Toto")
        val intent = Intent("ki.domopay.intent.action.PAY", uri)
        intent.putExtra("description", "Toto")
        intent.putExtra("amount", (calculateTotal()*100).toString())
        intent.putExtra("currency", "EUR")
        intent.putExtra("clientKey", "heytom-00000")

        createJSonDetails().let {
            intent.putExtra("details", it)
        }

        startActivityForResult(intent, DOMOPAY_REQUEST_CODE)

    }

    private fun refreshTotal() {
        domo_text_total.text = getString(
            R.string.domo_total, DEFAULT_DECIMALFORMAT.format(calculateTotal())
        )
    }

    private fun createJSonDetails(): String {
        val burgersQuantity = extractQuantity(domo_burger_text_quantity)
        val burgerAmount = extractPrice(domo_burger_text_price)
        val friesQuantity = extractQuantity(domo_fries_text_quantity)
        val friesAmount = extractPrice(domo_fries_text_price)
        val cokesQuantity = extractQuantity(domo_coke_text_quantity)
        val cokeAmount = extractPrice(domo_coke_text_price)
        // Open array
        var finalJson = "["
        detailJson("burgers", burgersQuantity, burgerAmount)?.let { detailJson ->
            finalJson += detailJson
        }
        detailJson("frites", friesQuantity, friesAmount)?.let { detailJson ->
            // Check for separator
            if (finalJson.length > 1) {
                finalJson += ", "
            }
            finalJson += detailJson
        }
        detailJson("cocas", cokesQuantity, cokeAmount)?.let { detailJson ->
            // Check for separator
            if (finalJson.length > 1) {
                finalJson += ", "
            }
            finalJson += detailJson
        }
        // Close array
        finalJson += "]"
        return finalJson
    }

    /**
     * Create Json for a detail
     */
    private fun detailJson(label: String, quantity: Int, amount: Float): String? {
        if (quantity > 0 && amount > 0) {
            return "{ \"label\":\"$label\", \"quantity\":\"$quantity\", \"amount\":\"${amount*100}\" }"
        }
        return null
    }


    /* *** Amount calculation *** */


    private fun calculateTotal(): Float {
        return calculateItemTotal(
            domo_burger_text_quantity, domo_burger_text_price
        ) + calculateItemTotal(
            domo_fries_text_quantity, domo_fries_text_price
        ) + calculateItemTotal(domo_coke_text_quantity, domo_coke_text_price)
    }

    private fun extractQuantity(view: View): Int {
        if (view is EditText) {
            view.text.toString().let {
                if (it.isNotEmpty()) {
                    return it.toInt()
                }
            }
        }
        return 0
    }

    private fun extractPrice(view: View): Float {
        if (view is EditText) {
            view.text.toString().let {
                if (it.isNotEmpty()) {
                    return it.toFloat()
                }
            }
        }
        return 0F
    }

    private fun calculateItemTotal(quantityView: View, priceView: View): Float {
        return extractQuantity(quantityView) * extractPrice(priceView)
    }

}
