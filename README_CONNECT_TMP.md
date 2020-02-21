# Domo-Connect

Domo connect provides you simple ways to enhance Domo experience into your application.

You will "connect" your app with your Domo device and/or using a simple system payment (called **DomoPay**).

Domo connect is easy to use, it use only basic Android principle

## Domo Connect infos

With Domo Connect you will have access to some data.

You can collect these data in different ways :
- When your application is started
- Receive a broadcast
- Activity result (COMING SOON ?)

## Data from starting activity

When your app is opened via Domo launcher.
Your activity is started with bundle data.
You can extract "Domo Connect data with the extra key "domo_connect"

Here is an example of code to collect the associated jSon.

```
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    [....]

    val dataJson = intent.getStringExtra("domo_connect")

}
```

## Data from broadcast receiver

Before receving a braodcasst, you have to send an intent to Domo platform.
The intent must followed some rules below.

1 / Set the action.
- action: **ki.domoconnect.intent.action.DOMOCONNECT_REQUEST**

2 / Add an extra containing the CLIENTKEY provided by Domo
- extra: **ki.domoconnect.intent.extra.CLIENTKEY**

3 / For android 8+ you should define component field (class ComponentName) that leads to domo BroadcastReceiver
- package: **ki.domo.domolauncher**
- component: **ki.domo.domolauncher.receiver.DomoConnectReceiver**

**Important**: For step 4, choose A or B (not both !)

4.A / If your BroadcastReceiver is registered via your Manifest like this :
```
<receiver android:name=".receiver.DomoConnectReceiver">
      <intent-filter>
          <action android:name="ki.domoconnect.intent.action.DOMOCONNECT_RESPONSE" />
      </intent-filter>
</receiver>
```
You have to put extra data indicating how DomoConnect can send back the intent (ComponentName part of intent).
- extra: **ki.domoconnect.intent.extra.PACKAGENAME** containing the appid (package name)
- extra: **ki.domoconnect.intent.extra.COMPONENT** containing the absolute class name

**Important** : If you add these extra and your Broadcast is not declared in the Manifest, it is possible that it will never receive the intent !


4.B / If your BroadcastReceiver is registerered in an other way.
By example like this :

```
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    [....]

    registerReceiver(receiver, IntentFilter("ki.domoconnect.intent.action.DOMOCONNECT_RESPONSE"))
}

override fun onDestroy() {
    unregisterReceiver(receiver)
    super.onDestroy()
}
```
No extra is required.


Here is a final example of code for sending a broadcast to Domo Connect
```
// Create intent with Domo action
val intent = Intent("ki.domoconnect.intent.action.DOMOCONNECT_REQUEST").also {
    // Nedded for Domo security ?
    it.putExtra("ki.domoconnect.intent.extra.CLIENTKEY", "YOUR_CLIENT_KEY_HERE")
    // Needed for Android >= 8
    it.component = ComponentName(
      "ki.domo.domolauncher", "ki.domo.domolauncher.receiver.DomoConnectReceiver"
    )
    // Needed only if your BroadcastReceiver is declared in the MANIFEST
    //it.putExtra("ki.domoconnect.intent.extra.PACKAGENAME", packageName)
    //it.putExtra(
    //    "ki.domoconnect.intent.extra.COMPONENT",
    //    "ki.domo.domopaydemo.MyDomoConnectReceiver"
    //)
}
//Finally sending the intent to Domo
sendBroadcast(intent)
```

Once Domo Connect received your broadcast, it will send back to your BroadcastReceiver an intent with the action **ki.domoconnect.intent.action.DOMOCONNECT_RESPONSE** and extras.
- **ki.domoconnect.intent.extra.DOMO_CONNECT** containing the "Domo Connect" JSon
- **ki.domoconnect.intent.extra.ERROR** containing an error message (if something went wrong)

Here is an example of BroadcastReceiver

```
inner class DomoConnectReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive $intent")
        // Extract data
        val json = intent.getStringExtra("ki.domoconnect.intent.extra.DOMO_CONNECT")
        val error = intent.getStringExtra("ki.domoconnect.intent.extra.ERROR")
        Log.d(TAG, "Domo connect json: $json")
        // Check json
        json?.let {

            // TODO insert your json treatment here

        }
        // Check error
        error?.let {
            Toast.makeText(this@DemoActivity, error, Toast.LENGTH_LONG).show()
        }
    }
}
```

## "Domo Connect" **JSon**

"Domo Connect" will give you a JSon structured like this :

```
booking: {
    uuid: string
},
unit: {
    uuid: string,
    name: string,
    address: string
    addressAdditionalDetails: string | null,
    city: string,
    zipCode: string,
    countryCode: string
},
octagon: {
    uuid: string
}
```
-   `booking` contains booking's data. booking's `uuid` is always delivered but other data are delivered according to privacy rules.
-   `octagon` contains current Domo device data
-   `unit` contains data about the location
