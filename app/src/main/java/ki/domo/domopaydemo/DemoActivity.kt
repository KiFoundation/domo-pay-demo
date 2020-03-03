package ki.domo.domopaydemo

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_demo.*
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*


/**
 * Example of Activity using Domo Pay solution
 *
 * Domo Pay is based on intent and waiting for the resultcode
 */
class DemoActivity : AppCompatActivity() {


    companion object {


        // TAGs
        private val TAG = DemoActivity::class.java.simpleName


        // Client key provided by "Domo"
        private const val DOMO_CLIENTKEY = "<YOUR_DOMO_CLIENT_KEY_HERE>"


        // Formatting decimal
        private var DEFAULT_DECIMALFORMAT = DecimalFormat("#.##")

        // Request codes
        private const val DOMOPAY_REQUEST_CODE = 1

    }


    // Broadcast receiver for Domo connect
    private var receiver = DomoConnectReceiver()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        init()
        registerReceiver(
            receiver, IntentFilter("ki.domoconnect.intent.action.DOMOCONNECT_RESPONSE")
        )
    }

    override fun onResume() {
        super.onResume()
        refreshTotal()
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    /**
     * The status of the payment is transmit by the requestcode/resultcode
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult $requestCode $resultCode $data")
        when (requestCode) {
            DOMOPAY_REQUEST_CODE -> {
                when (resultCode) {
                    // Payment is finished correctly
                    Activity.RESULT_OK -> {
                        Log.d(TAG, "Payment success !!")
                        Toast.makeText(this, "Payment success !!", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Log.d(TAG, "Payment canceled...")
                        // Activity.RESULT_CANCELED -> The payment has been canceled or is not finished
                        Toast.makeText(this, "Payment canceled...", Toast.LENGTH_LONG).show()
                    }
                }
                data?.extras?.let { extras ->
                    extras.get("uuid")?.let {
                        Log.d(TAG, "Charge uuid : $it")
                        Toast.makeText(this, "Charge uuid : $it", Toast.LENGTH_LONG).show()
                    }
                    extras.get("externalOrderID")?.let {
                        Log.d(TAG, "Demo order uuid : $it")
                        Toast.makeText(this, "Demo order uuid : $it", Toast.LENGTH_LONG).show()
                    }
                    extras.get("domo_connect")?.let {
                        Log.d(TAG, "Demo connect ! json: $it")
                        Toast.makeText(this, "Demo connect ! json: $it", Toast.LENGTH_LONG).show()
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

        // Refresh values on focus change on edittext
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

        // Valid order on button's click
        domo_button_valid.setOnClickListener { validOrder() }

        // Extract extras data from "DomoConnect"
        intent?.extras?.let { extras ->
            extras.getString("domo_connect")?.let { json ->
                Log.d(TAG, "domo_connect json: $json")
                extractDomoInfos(json)
            }
        } ?: let {
            Log.d(TAG, "sendBroadcast")
            sendDomoConnectBroadcast()

        }


    }


    /**
     * Valid order -> start Domo-Pay activity
     */
    private fun validOrder() {

        // Create intent to start Domo Pay activity
        val uri = Uri.parse("pay:")
        val intent = Intent("ki.domopay.intent.action.PAY", uri)

        // Payment parameters
        intent.putExtra("description", "My food!")
        intent.putExtra("amount", (calculateTotal() * 100).toString())
        intent.putExtra("currency", "EUR")
        intent.putExtra("clientKey", DOMO_CLIENTKEY)
        intent.putExtra("externalOrderID", UUID.randomUUID().toString())

        // If you choose to transmit the details you have to create a JSON like this
        createJSonDetails().let {
            intent.putExtra("details", it)
        }

        // Starting Domo Pay activity
        startActivityForResult(intent, DOMOPAY_REQUEST_CODE)

    }

    private fun refreshTotal() {
        domo_text_total.text = getString(
            R.string.domo_total, DEFAULT_DECIMALFORMAT.format(calculateTotal())
        )
    }

    /**
     * Construct intent for Domo broadcast
     */
    private fun sendDomoConnectBroadcast() {
        // Create intent with Domo action
        val intent = Intent("ki.domoconnect.intent.action.DOMOCONNECT_REQUEST").also {
            // Nedded for Domo security ?
            it.putExtra("ki.domoconnect.intent.extra.CLIENTKEY", DOMO_CLIENTKEY)
            // Needed for Android >= 8
            it.component = ComponentName(
                "ki.domo.domolauncher", "ki.domo.domolauncher.receiver.DomoConnectReceiver"
            )
            // Needed if your Broadcast is declared in the MANIFEST
//            it.putExtra("ki.domoconnect.intent.extra.PACKAGENAME", packageName)
//            it.putExtra(
//                "ki.domoconnect.intent.extra.COMPONENT",
//                "ki.domo.domopaydemo.DemoActivity\$DomoConnectReceiver"
//            )
        }
        // Send broadcast to Domo
        sendBroadcast(intent)
    }

    /**
     * Create the JSON specified for the details
     */
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
        detailJson("fries", friesQuantity, friesAmount)?.let { detailJson ->
            // Check for separator
            if (finalJson.length > 1) {
                finalJson += ", "
            }
            finalJson += detailJson
        }
        detailJson("cokes", cokesQuantity, cokeAmount)?.let { detailJson ->
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
            return "{ \"label\":\"$label\", \"quantity\":\"$quantity\", \"amount\":\"${amount * 100}\" }"
        }
        return null
    }

    /**
     * Parse JSon and extract address
     */
    private fun extractDomoInfos(json: String?) {
        json?.let {
            val unit = JSONObject(json).getJSONObject("unit")
            unit.let {
                val address = it.getString("address")
                val zipcode = it.getString("city")
                val city = it.getString("city")
                domo_text_address.text =
                    getString(R.string.domo_address, "$address, $zipcode $city")
            }
        }
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


    /**
     * Domo connect receiver
     */
    inner class DomoConnectReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "onReceive $intent")
            // Check domo action
            intent.action?.let {action ->
                if (action == "ki.domoconnect.intent.action.DOMOCONNECT_RESPONSE") {
                    // Extract data
                    val json = intent.getStringExtra("ki.domoconnect.intent.extra.DOMO_CONNECT")
                    val error = intent.getStringExtra("ki.domoconnect.intent.extra.ERROR")
                    Log.d(TAG, "Domo connect json: $json")
                    // Check json
                    json?.let {
                        extractDomoInfos(it)
                    }
                    // Check error
                    error?.let {
                        Toast.makeText(this@DemoActivity, error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}
