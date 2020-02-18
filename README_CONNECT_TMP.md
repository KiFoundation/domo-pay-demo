# Domo-Connect

Domo connect provides you simple ways to enhance Domo experience into your application.

You will "connect" your app with your Domo device and/or using a simple system payment (called **DomoPay**).

Domo connect is easy to use, it use only basic Android principle

## Domo connect infos

With Domo Connect you will have access to some data.

You can collect data in different ways :
- When your application is started
- Receive a broadcast

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

## Data from broadcast receiver (COMING SOON)



## "Domo Connect" **data**

"Domo Connect" will give you a jSon structured like this :

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
