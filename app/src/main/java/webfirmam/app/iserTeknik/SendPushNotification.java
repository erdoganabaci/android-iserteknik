package webfirmam.app.iserTeknik;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SendPushNotification extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    EditText pushTitleText;
    EditText pushContentText;
    EditText pushContentUrl;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_user_logout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_menu){
            //String email = mAuth.getCurrentUser().getEmail();
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            Toast.makeText(SendPushNotification.this,"Çıkış Yapıldı...",Toast.LENGTH_LONG).show();
            startActivity(intent);
        }else if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            NavUtils.navigateUpTo(this,intent);
            //olsada olur return true olmasa da olur
            //return true;
        }else if (item.getItemId() == R.id.kill_app){
            finishAffinity();
            System.exit(0);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_push_notification);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        pushTitleText = findViewById(R.id.pushTitleText);
        pushContentText=findViewById(R.id.pushContentText);
        pushContentUrl=findViewById(R.id.pushContentUrl);

        OneSignal.startInit(getApplicationContext())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        //home geri menu tuşu aktifleştirme önemli!!!!
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = new Intent(this,MyService.class);
        //startService(intent);
        //servis başlatmak için
        //Intent intent = new Intent(this,MyService.class);
        //startService(intent);
        //idleri databaseye mainactivity de ekliyom zaten.
/*
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(final String userId, String registrationId) {
                System.out.println("userID: " + userId);

                UUID uuid = UUID.randomUUID();
                final String uuidString = uuid.toString();

                DatabaseReference newReference = database.getReference("PlayerIDs");
                newReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ArrayList<String> playerIDsFromServer = new ArrayList<>();

                        for (DataSnapshot ds: dataSnapshot.getChildren()) {

                            HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                            String currentPlayerID = hashMap.get("playerID");

                            playerIDsFromServer.add(currentPlayerID);
                        }
                        //Eğer o kullanıcı yoksa ekle aynı telefon idleri eklemek 2 defa göndermiş olucak.
                        if (!playerIDsFromServer.contains(userId)) {
                            databaseReference.child("PlayerIDs").child(uuidString).child("playerID").setValue(userId);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        */
    }
    public void pushNotification(View view){
       /* try {
            OneSignal.postNotification(new JSONObject("{'contents': {'en':'Test Message'}, 'include_player_ids': ['8840e1e8-2e97-47c7-a493-ec68b3cdc453']}"), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        DatabaseReference newReference = database.getReference("PlayerIDs");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();

                    String playerID = hashMap.get("playerID");

                    System.out.println("playerID server:" + playerID);
/*
                    try {
                        //OneSignal.postNotification(new JSONObject("{'headings':{'en':'finalPageTitle'},'contents': {'en':'Everyone'}, 'include_player_ids': ['" + playerID + "']}"), null);
                        //OneSignal.postNotification(new JSONObject("{'headings':{'en':'"+pushTitleText.getText().toString()+"'},'contents': {'en':'"+pushContentText.getText().toString()+"'}, 'include_player_ids': ['" + playerID + "']}"), null);
                        OneSignal.postNotification(new JSONObject("{'headings':{'en':'"+pushTitleText.getText().toString()+"'},'contents': {'en':'"+pushContentText.getText().toString()+"'}, 'data': {'myurl': '"+pushContentUrl.getText().toString()+"'},'include_player_ids': ['" + playerID + "']}"), null);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                    try {
                        OneSignal.postNotification(new JSONObject("{'headings':{'en':'"+pushTitleText.getText().toString()+"'},'contents': {'en':'"+pushContentText.getText().toString()+"'}, 'data': {'myurl': '"+pushContentUrl.getText().toString()+"'},'include_player_ids': ['" + playerID + "']}"),
                        //OneSignal.postNotification(new JSONObject("{'headings':{'en':'"+pushTitleText.getText().toString()+"'},'contents': {'en':'"+pushContentText.getText().toString()+"'}, 'data': {'myurl': '"+pushContentUrl.getText().toString()+"'},'include_player_ids': ['32bc4b8f-2a14-4c0f-bff2-50bc24e20daf']}"),
                                new OneSignal.PostNotificationResponseHandler() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                                        Toast.makeText(SendPushNotification.this,"Bildirim Gönderildi...",Toast.LENGTH_LONG).show();

                                    }

                                    @Override
                                    public void onFailure(JSONObject response) {
                                        Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                                    }
                                });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
