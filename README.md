# domo-pay-demo

Domo payment framework documentation





1 / Installation


Domo pay application should be installed in order to use this framework.
Please install it on your device before.

An apk could be provided if needed.


2 / Configuration


In order to secure your payments you should provide clientkey, package name and app signature to the API.

The client key will be provided by Ki fundation and will be mapped with your package id and app signature.
 
You can obtain signature with keytool for example.



2 / Intent



Using Domo Payment is very simple.

You only need to start an activity with extra parameters and wait for the result.

Uri format will be "pay:" and action is "ki.domopay.intent.action.PAY"



3 / Parameters


Give bundle extras to your intent :


clientKey: Very important for security check. It will be checked will your packagename and app signature to the API.
amount: Total amount (Be careful in centimes)
currency: Short code currency
description: Description of the order
details (optional): Json with order content


4 / Details JSon

The JSon should be an array of Json object with this parameters :
label: Name of content
quantity: Number of unit
amount: Unit price (in cents)

Example :

[{"label":"burgers","amount":"750","quantity":2},{"label":"frites","amount":"200","quantity":1},{"label":"cocas","amount":"250","quantity":1}]


5 / Kotlin code example

Create the intent

val intent = Intent("ki.domopay.intent.action.PAY", Uri.parse("pay:"))
intent.putExtra("description", "Pay")
intent.putExtra("amount", "1000")
intent.putExtra("currency", "EUR")
intent.putExtra("clientKey", "heytom-00000")
startActivityForResult(intent, 1)


Get the resultcode

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
	super.onActivityResult(requestCode, resultCode, data)
	when (requestCode) {
		1 -> {
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











