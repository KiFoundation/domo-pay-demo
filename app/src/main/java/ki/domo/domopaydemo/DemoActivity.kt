package ki.domo.domopaydemo

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Example of Activity using Domo Pay solution
 */
class DemoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

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
                    calculateItemTotal(domo_burger_text_quantity, domo_burger_text_price)
                )
                domo_text_total.text = getString(
                    R.string.domo_total, calculateTotal()
                )
            }
        }
        domo_burger_text_quantity.setOnFocusChangeListener(burgersListener)
        domo_burger_text_price.setOnFocusChangeListener(burgersListener)

        val friesListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                domo_fries_text_total.text = getString(
                    R.string.domo_price,
                    calculateItemTotal(domo_fries_text_quantity, domo_fries_text_price)
                )
                domo_text_total.text = getString(
                    R.string.domo_total, calculateTotal()
                )
            }
        }
        domo_fries_text_quantity.setOnFocusChangeListener(friesListener)
        domo_fries_text_price.setOnFocusChangeListener(friesListener)

        val cokesListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                domo_coke_text_total.text = getString(
                    R.string.domo_price,
                    calculateItemTotal(domo_coke_text_quantity, domo_coke_text_price)
                )
                domo_text_total.text = getString(
                    R.string.domo_total, calculateTotal()
                )
            }
        }
        domo_coke_text_quantity.setOnFocusChangeListener(cokesListener)
        domo_coke_text_price.setOnFocusChangeListener(cokesListener)

        domo_button_valid.setOnClickListener({ validOrder() })

    }


    private fun validOrder() {
        // TODO
    }


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
