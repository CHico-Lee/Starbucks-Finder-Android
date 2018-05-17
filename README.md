<img src="app/src/main/res/mipmap-xhdpi/ic_launcher.png" alt="Starbucks Finder icon" title="Starbucks Finder" align="right" height="96" width="96"/>

Starbucks Finder - Android
====================================

An Android app to list all nearby Starbucks.
Click on the list will open the Starbucks location on a map.
The app will search nearby Starbucks with current GPS position by default, click the top-right icon to change the search location.

Screenshot
--------------
![Alt text](docs/starbucks_list.jpg?raw=true "Starbucks on List")
![Alt text](docs/starbucks_map.jpg?raw=true "Starbucks on Map")


Implementation
------------

- Get the last GPS location for Lat and long using Google Play services location APIs (LocationServices).
- using URL Class to download JSON data from Places API Web Service.
- Using JSONObject Class to parse JSON data.
- List nearby Starbucks using RecyclerView with a LinearLayoutManager.
- Using Maps Android API to plot the location on a map.
- Using Places API for Android to get location position search by text.


Requirements
------------

- Native Android application written entirely in Kotlin.
- App should show a list of the local Starbucks, using Google or any other API.
- App should allow user to click on a Starbucks and load a map.
- App should allow returning back to the Starbucks list from the map.
- App should use native (not 3rd party) networking functionality written in Kotlin.
- App should not crash under any situation.
- App should have proper commenting and code structure.
- App should look nice and polished.

Pre-requisites
--------------

- Android SDK 27+
- Android Gradle Plugin 3.0
- Maps Android API
- Places API for Android
- Places API Web Service


Required Permission
--------------

- GPS location access
- Internet access

Steps to run
--------------
Simply install and run **Starbucks_Finder_v1.0.apk**

--or--

To run on Android Studio:
Get API Keys from Google API Console with SHA-1 certificate fingerprint.
https://console.developers.google.com/
- API Key for "Places API Web Service"
- API Key for "Maps Android API" and "Places API for Android"

Paste API keys into **app\src\debug\res\values\google_maps_api.xml**
```
<!--google_maps_api.xml-->
<resources>
    <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">
        Android Maps and Places API KEY HERE
    </string>
    <string name="google_places_web_key" templateMergeStrategy="preserve" translatable="false">
        Places Web Service API KEY HERE
    </string>
</resources>
```



