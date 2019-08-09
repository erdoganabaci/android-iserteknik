package webfirmam.app.iserTeknik;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private CustomWebViewClient webViewClient;
    private String Url = "https://webfirmam.net/";
    ProgressDialog mProgressDialog;
    Handler handler = new Handler();
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

       /* OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();*/
      /*  OneSignal.startInit(this)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
*/

        OneSignal.startInit(this)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Yükleniyor...");

        webViewClient = new CustomWebViewClient();
        webView = findViewById(R.id.webview);//webview mızı xml anasayfa.xml deki webview bağlıyoruz
        webView.getSettings().setBuiltInZoomControls(true); //zoom yapılmasına izin verir
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient); //oluşturduğumuz webViewClient objesini webViewımıza set ediyoruz
        webView.loadUrl(Url);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(final String userId, String registrationId) {
                System.out.println("userID: " + userId);
                UUID uuidDb = UUID.randomUUID();

                UUID uuid = UUID.randomUUID();
                final String uuidString = uuid.toString();
                final String uuidDbString = uuidDb.toString();

                //DatabaseReference newReference = database.getReference("PlayerIDs");
                DatabaseReference newReference = database.getReference("PlayerIDs");
                newReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ArrayList<String> playerIDsFromServer = new ArrayList<>();
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            //String myPlayerId = ds.child("mykey").getValue(String.class).toString();
                           // System.out.println("geldi degerler :" + ds.getValue());
                            //HashMap<String, Object> value = (HashMap<String, Object>) ds.getValue();
                            //HashMap<String, Object> hashmap1 =(HashMap<String, Object>)  value.get("post");
                            //System.out.println("ücretsiz "+ hashmap1.get(hashmap1.keySet()));
                            //System.out.println("ücret :" + hashmap1.keySet());
                            // HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();

                           // System.out.println("değerlerim gelmis "+hashMap.get("post"));
                            //HashMap<String, String> hashMap1  =  (HashMap<String, String>) hashMap.get("post");
                            //System.out.println("my url " + hashMap1 );
                            //HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                            //String currentPlayerID = hashMap.get("mykey");
                            // String currentPlayerID = hashMap.get("playerID");
                            //HashMap<String, String> hashMapId = currentPlayerID
                            //HashMap<String, String> newHashMap = currentPlayerID["mykey"]

                            //playerIDsFromServer.add(currentPlayerID);

                            HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                            String currentPlayerID = hashMap.get("playerID");

                            playerIDsFromServer.add(currentPlayerID);
                        }
                        //Eğer o kullanıcı yoksa ekle aynı telefon idleri eklemek 2 defa göndermiş olucak.
                        if (!playerIDsFromServer.contains(userId)) {
                            databaseReference.child("PlayerIDs").child(uuidString).child("playerID").setValue(userId);
                           // databaseReference.child("PlayerIDs").child(uuidDbString).child("post").child(uuidString).child("playerID").setValue(userId);
                           // databaseReference.child("PlayerIDs").child("mykey").child("post").child(uuidString).child("playerID").setValue(userId);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    public void adminPanelFab(View view){
        //intent koycan
        startActivity(new Intent(this,LoginActivity.class));
    }

    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        String myurl;

        @Override
        public void notificationReceived(OSNotification notification) {

            JSONObject data = notification.payload.additionalData;

            String notificationID = notification.payload.notificationID;
            String launchUrl = notification.payload.launchURL; // update docs launchUrl
            myurl = data.optString("myurl", null);
            String title = notification.payload.title;
            String body = notification.payload.body;
            String smallIcon = notification.payload.smallIcon;
            String largeIcon = notification.payload.largeIcon;
            String bigPicture = notification.payload.bigPicture;
            String smallIconAccentColor = notification.payload.smallIconAccentColor;
            String sound = notification.payload.sound;
            String ledColor = notification.payload.ledColor;
            int lockScreenVisibility = notification.payload.lockScreenVisibility;
            String groupKey = notification.payload.groupKey;
            String groupMessage = notification.payload.groupMessage;
            String fromProjectNumber = notification.payload.fromProjectNumber;
            String rawPayload = notification.payload.rawPayload;

            String customKey;

            Log.i("OneSignalExample", "NotificationID received: " + notificationID);

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }
            //Intent intent = new Intent(getApplicationContext(), SendPushNotification.class);
            //intent.putExtra("dataUrl",myurl);

            //startActivity(intent);
        }
    }

    public void secondFab(View view){
        startActivity(new Intent(this,SendPushNotification.class));
    }

/*
    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        String myurl;

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl
            String customKey;
            String openURL = null;
            Object activityToLaunch = secondActivity.class;
            myurl = data.optString("myurl", null);


            if (data != null) {

                customKey = data.optString("customkey", null);
                openURL = data.optString("openURL", null);

                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);

                if (openURL != null)
                    Log.i("OneSignalExample", "openURL to webview with URL value: " + openURL);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken) {
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
                Toast.makeText(getApplicationContext(),"Butona tıklandı",Toast.LENGTH_LONG).show();

                if (result.action.actionID.equals("id1")) {
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
                    activityToLaunch = secondActivity.class;
                } else
                    Log.i("OneSignalExample", "button id called: " + result.action.actionID);
            }
            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
             //Intent intent = new Intent(getApplicationContext(), secondActivity.class);
            Intent intent = new Intent(getApplicationContext(), (Class<?>) activityToLaunch);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("openURL", openURL);
            intent.putExtra("dataUrl",myurl);
            startActivity(intent);

            Log.i("OneSignalExample", "openURL = " + openURL);

            // startActivity(intent);
            if(launchUrl != null){
                intent.putExtra("pushUrl",launchUrl.toString());
                //startActivity(intent);

            }

            // Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
            //   if you are calling startActivity above.

        }
    }
*/


    class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {

            JSONObject data = result.notification.payload.additionalData;
            String customKey;
            String myurl;
            myurl = data.optString("myurl", null);


            if (!myurl.equals("")) {

                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                intent.putExtra("dataUrl",myurl);
                startActivity(intent);

            }else if (myurl.equals("")){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }


        }
    }


    private class CustomWebViewClient extends WebViewClient {

        //Alttaki methodların hepsini kullanmak zorunda deilsiniz
        //Hangisi işinize yarıyorsa onu kullanabilirsiniz.
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) { //Sayfa yüklenirken çalışır
            super.onPageStarted(view, url, favicon);

            if(!mProgressDialog.isShowing())//mProgressDialog açık mı kontrol ediliyor
            {

                    try{
                        //show dialog
                        mProgressDialog.show();//mProgressDialog açık değilse açılıyor yani gösteriliyor ve yükleniyor yazısı çıkıyor

                    }catch (Exception e){
                       e.printStackTrace();
                    }



                handler.postDelayed(new Runnable() {
                    public void run() {
                        mProgressDialog.dismiss();
                    }
                }, 5000);

            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {//sayfamız yüklendiğinde çalışıyor.
            super.onPageFinished(view, url);

            if(mProgressDialog.isShowing()){//mProgressDialog açık mı kontrol açıksa kapat ama sayfa yüklenmesi tamalanırsa
                mProgressDialog.dismiss();//mProgressDialog açıksa kapatılıyor
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Bu method açılan sayfa içinden başka linklere tıklandığında açılmasına yarıyor.
            //Bu methodu override etmez yada edip içini boş bırakırsanız ilk url den açılan sayfa dışında başka sayfaya geçiş yapamaz

            view.loadUrl(url);//yeni tıklanan url i açıyor
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {

            //	if(errorCode !=null){
            //		Timeout
            //	} şeklinde kullanabilirsiniz



        }
    }
    public void onBackPressed() //Android Back Buttonunu Handle ediyoruz. Back butonu bir önceki sayfaya geri dönecek
    {
        if(webView.canGoBack()){//eğer varsa bir önceki sayfaya gidecek
            webView.goBack();
        }else if (doubleBackToExitPressedOnce){//Sayfa yoksa uygulamadan çıkacak
            super.onBackPressed();

            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Çıkmak için 2 kere basınız.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


}
