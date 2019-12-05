# Domo-Pay

Domo-Pay allows you to collect payments on your Domo application.

## Overview

Collecting payments on your application consists of creating an object, called a `Charge`, used to track and handle all the states of the payment.

Payment is made in four steps:

- you make a **checkout request**, to create a `Charge`;

- you redirect guest-user Domo-Pay interface, to allow him to make the payment;

- you receive a confirmation webhook;

- you check the payment status and deliver the order.

## Collect a payment using Domo-App application

Domo-Pay application allows you to collect payments without making any API calls.
It's very easy to use, it works with a very basic android principle : intent / activity / resultcode

https://developer.android.com/reference/android/app/Activity#StartingActivities

Before starting, verify that Domo-Pay is installed on your device.

### 1. Set up Domo-Pay

First, you need a Third-Party Developer account. Then, you have to [create a new application](https://manager.domo.ki/my-applications/create).

For security check you should provide packageId and app signature of your app.
(use keytool for app signature inprint)

You will then have a `clientKey` and a `clientSecret` client. You need this to collect payments.

### 2. Starting Domo-Pay activity

To make a **checkout request**, you have to start a Domo-Pay Activity.

So create an intent with the uri `pay:` and the action `ki.domopay.intent.action.PAY`

For payment info you have to fill the bundle extras with this paramters :

-   `clientKey`: Your application client key;
-   `amount`: Amount intended to be collected by this Charge. A positive integer representing how much to charge, in cents (e.g., 100 cents to charge $1.00).
-   `currency`: Three-letter ISO currency code, in lowercase. Must be a supported currency (EUR, USD) 
-   `description` (optional): An arbitrary string which you can attach to a Charge object. It is displayed when in the web interface alongside the charge.
-  `details` (optional): A JSON array with the detail of order content. Each row have to contains:
	- `label`: Name of content
	- `quantity`: Number of unit
	- `amount`: Unit price (in cents)

Details JSON example
```
    [
	    {"label":"burgers","amount":"750","quantity":2},
	    {"label":"frites","amount":"200","quantity":1},
	    {"label":"cocas","amount":"250","quantity":1}
	]
```

**Starting activity Kotlin example**

```
    val intent = Intent("ki.domopay.intent.action.PAY", Uri.parse("pay:"))
    intent.putExtra("description", "Pay")
    intent.putExtra("amount", "1000")
    intent.putExtra("currency", "EUR")
    intent.putExtra("clientKey", "heytom-00000")
    startActivityForResult(intent, 1)
```

### 3. Guest user can make the payment

Domo-Pay will create a checkout-request for you, and redirect guest user to a payment interface.

You have nothing to do here.

### 4. Deliver the order

Once the payment has been made, Domo-Pay will give you the result by the resultcode.
`RESULT_OK` show that the payment process si a success. In other case `RESULT_CANCELED` or all other value show that the process has been stopped or failed.

In all cases an intent will be transmit containing the `Charge` uuid, allowing you to check its status on your side.
The uuid could be extract in the result intent with extra name `uuid`
If you did not get any intent and uuid, so the payment is failed before or during the **checkout request**

**Kotlin Example**
```
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Toast.makeText(this, "Payment done !!", Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(this, "Payment canceled...", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
```
