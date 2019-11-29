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
import kotlinx.android.synthetic.main.activity_main.*
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
        setContentView(R.layout.activity_main)

        init()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult $requestCode $resultCode $data")
        when (requestCode) {
            DOMOPAY_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(this, "Paiement en cours...", Toast.LENGTH_LONG).show()
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

        val burgersListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                domo_burger_text_total.text = getString(
                    R.string.domo_price,
                    DEFAULT_DECIMALFORMAT.format(
                        calculateItemTotal(
                            domo_burger_text_quantity,
                            domo_burger_text_price
                        )
                    )
                )
                domo_text_total.text = getString(
                    R.string.domo_total, DEFAULT_DECIMALFORMAT.format(calculateTotal())
                )
            }
        }
        domo_burger_text_quantity.setOnFocusChangeListener(burgersListener)
        domo_burger_text_price.setOnFocusChangeListener(burgersListener)

        val friesListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                domo_fries_text_total.text = getString(
                    R.string.domo_price,
                    DEFAULT_DECIMALFORMAT.format(
                        calculateItemTotal(
                            domo_fries_text_quantity,
                            domo_fries_text_price
                        )
                    )
                )
                domo_text_total.text = getString(
                    R.string.domo_total, DEFAULT_DECIMALFORMAT.format(calculateTotal())
                )
            }
        }
        domo_fries_text_quantity.setOnFocusChangeListener(friesListener)
        domo_fries_text_price.setOnFocusChangeListener(friesListener)

        val cokesListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                domo_coke_text_total.text = getString(
                    R.string.domo_price,
                    DEFAULT_DECIMALFORMAT.format(
                        calculateItemTotal(
                            domo_coke_text_quantity,
                            domo_coke_text_price
                        )
                    )
                )
                domo_text_total.text = getString(
                    R.string.domo_total, DEFAULT_DECIMALFORMAT.format(calculateTotal())
                )
            }
        }
        domo_coke_text_quantity.setOnFocusChangeListener(cokesListener)
        domo_coke_text_price.setOnFocusChangeListener(cokesListener)

        domo_button_valid.setOnClickListener({ validOrder() })

    }


    private fun validOrder() {
        // TODO

        val uri = Uri.parse("pay:Toto")
        val intent = Intent("ki.domopay.intent.action.PAY", uri)
        intent.putExtra("description", "Toto")
        intent.putExtra("amount", calculateTotal().toString())
        intent.putExtra("currency", "EUR")
        intent.putExtra("clientKey", "heytom-00000")

        startActivityForResult(intent, DOMOPAY_REQUEST_CODE)

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
                if (!it.isEmpty()) {
                    return it.toInt()
                }
            }
        }
        return 0
    }

    private fun extractPrice(view: View): Float {
        if (view is EditText) {
            view.text.toString().let {
                if (!it.isEmpty()) {
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
