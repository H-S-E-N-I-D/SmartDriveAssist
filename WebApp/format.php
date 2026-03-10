<?php

use Illuminate\Http\Request;

/*
  |--------------------------------------------------------------------------
  | API Routes
  |--------------------------------------------------------------------------
  |
  | Here is where you can register API routes for your application. These
  | routes are loaded by the RouteServiceProvider within a group which
  | is assigned the "api" middleware group. Enjoy building your API!
  |
 */

Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});

Route::get('/testPush', function() {
    // If the Content-Type and Accept headers are set to 'application/json', 
    // this will return a JSON structure. This will be cleaned up later.
    // PushNotification::app('ShadowCop')
    //              ->to("APA91bEWGIP0ZOoAwBWiuSeOBPPrZ-NzqcfRxLYZ4j47AUcgeF6UiHurcuRXNZz13uUJ5NODaBdlJydnjY1PoESdOl2h7-BilIqlA3RaTZLYMQEWJphNNtYtsWUfZhV6Zj9raGAgzXKDnKnkc3_DQANWmhUjFxm8pg")
    //              ->send('Hello World, i`m a push message');


    define('API_ACCESS_KEY', 'YOUR-API-ACCESS-KEY-GOES-HERE');
    $registrationIds = array($_GET['id']);
// prep the bundle
    $msg = array
        (
        'message' => 'here is a message. message',
        'title' => 'This is a title. title',
        'subtitle' => 'This is a subtitle. subtitle',
        'tickerText' => 'Ticker text here...Ticker text here...Ticker text here',
        'vibrate' => 1,
        'sound' => 1,
        'largeIcon' => 'large_icon',
        'smallIcon' => 'small_icon'
    );
    $fields = array
        (
        'registration_ids' => $registrationIds,
        'data' => $msg
    );

    $headers = array
        (
        'Authorization: key=' . API_ACCESS_KEY,
        'Content-Type: application/json'
    );

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, 'https://android.googleapis.com/gcm/send');
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
    $result = curl_exec($ch);
    curl_close($ch);
    echo $result;
});
