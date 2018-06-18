package ai.suntrust.swihighprecisioncassavafarm;

import android.app.ActionBar;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final Context context = this;

    String god_mode = "000";

    //ProgressDialog dialog_loading;
    LovelyInfoDialog dialog_about_app;
    LovelyProgressDialog dialog_farm_loading;

    /**** firebase authentication ***/
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    /*******************************/

    boolean user_signed_in = false;

    /**** firebase database *****/
    FirebaseDatabase mFirebaseDatabase;
    //DatabaseReference mFirebaseSoilRef;
    DatabaseReference mFirebaseFarmRef;
    //DatabaseReference mFirebaseMainpumpRef;
    //DatabaseReference mFirebaseValveRef;
    //DatabaseReference mFirebaseSensorRef;
    //DatabaseReference mFirebaseSoilPlant;
    /****************************/

    /**** user model ****/
    User mUser = new User();
    ArrayList<Farm> mFarm = new ArrayList<Farm>();
    /********************/


    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    static View.OnClickListener farmOnClickListener;
    private static ArrayList<Farm> data;

    private static final int RC_SIGN_IN = 123;
    private static final String TAG_READ_DATA = "Firebase_database";

    private DrawerLayout mMainDrawerLayout;

    TextView textView_UserEmail;
    TextView textView_UserDisplayName;
    ImageView imageview_UserPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //dialog_loading = ProgressDialog.show(context, "", "Loading farm data...");

        dialog_farm_loading = new LovelyProgressDialog(this);
        dialog_farm_loading.setIcon(R.drawable.ic_cloud_download_white_24dp)
                            .setTitle(R.string.downloading_from_server)
                            .setTopColorRes(R.color.colorAccent)
                            .show();

        setTitle("SWI Smart Cassava");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "สร้างข้อมูลฟาร์มแปลงใหม่", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Log.d("MenuItem","Menu clicked ----> " + item.getItemId());
                        int id = item.getItemId();

                        if (id == R.id.nav_camera) {
                            // Handle the camera action
                        } else if (id == R.id.nav_gallery) {

                        } else if (id == R.id.nav_slideshow) {

                        } else if (id == R.id.nav_manage) {

                        } else if (id == R.id.nav_share) {

                        } else if (id == R.id.nav_send) {

                        } else if (id == R.id.nav_logout){
                            AuthUI.getInstance()
                                    .signOut(context)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        public void onComplete(@NonNull Task<Void> task) {
                                            finish();
                                            mFarm.clear();
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                        drawer.closeDrawers();
                        return false;
                    }
                }
        );

        mMainDrawerLayout = findViewById(R.id.drawer_layout);

        //god_mode = getIntent().getExtras().getString("god_mode");
        god_mode = "311";

        if(god_mode.equals("311")) {
            instantiateUser();
            updateUserProfile();
        }else{
            updateGodProfile();
        }

        instantiateDatabase();

        //if(isUserSignedIn()){
        //    updateUserProfile();
        //    instantiateDatabase();
        //}else{
            /* Firebase Authentication */
            // Choose authentication providers
         //   List<AuthUI.IdpConfig> providers = Arrays.asList(
         //           new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
         //           new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

            // Create and launch sign-in intent

            // only do this once after sign in
        //startActivityForResult(
        //            AuthUI.getInstance()
        //                    .createSignInIntentBuilder()
        //                    .setAvailableProviders(providers)
        //                    .build(),
        //                    RC_SIGN_IN);

            /***************************/
        //}

        /** initialize user element **/
        //View hView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        //textView_UserEmail = (TextView)hView.findViewById(R.id.textview);

        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        // read setting
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String textServerAddress = sharedPref.getString(SettingActivity.KEY_PREF_SERVER_ADDRESS, "DEFAULT");
    }

    /* Firebase Authentication */
    private void instantiateUser(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    private boolean isUserSignedIn(){
        if (mFirebaseUser == null){
            return false;
        }else{
            return true;
        }
    }
    /**************************/
    /*** update user profile after login ***/
    private void updateUserProfile(){
        mUser.setUID(mFirebaseUser.getUid());

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mUser.setEmail(mFirebaseUser.getEmail());
        textView_UserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textview_user_email);
        textView_UserEmail.setText(mUser.getEmail());

        mUser.setDisplayName(mFirebaseUser.getDisplayName());
        textView_UserDisplayName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textview_user_displayname);
        textView_UserDisplayName.setText(mUser.getDisplayName());

        mUser.setPhotoURL(mFirebaseUser.getPhotoUrl().toString());
        imageview_UserPhoto = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageview_user);
        Picasso.get().load(mFirebaseUser.getPhotoUrl()).into(imageview_UserPhoto);

    }

    private void updateGodProfile(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mUser.setEmail("nobody@home.com");
        textView_UserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textview_user_email);
        textView_UserEmail.setText(mUser.getEmail());

        mUser.setDisplayName("Nobody");
        textView_UserDisplayName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textview_user_displayname);
        textView_UserDisplayName.setText(mUser.getDisplayName());
    }
    /***************************************/

    /*** initiate realtime database ***/
    private void instantiateDatabase(){
        //final Context activityContext = this.getApplicationContext();
        /**** get database ****/
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseFarmRef = mFirebaseDatabase.getReference("farm");
        mFirebaseFarmRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //final ProgressDialog dlg = new ProgressDialog(this);
                //dlg.setMessage("Syncing farms...");
                //dlg.setCancelable(false);
                //dlg.show();
                mFarm.clear();
                Log.d("FirebaseDatabase", "Clear farm data!");
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(mUser.getUID().equals(ds.child("uid").getValue(String.class)) || ds.child("public").getValue(String.class).equals("true")) {
                        //Log.d("FirebaseDatabase", "Yes INSIDE");
                        Farm farm = new Farm();
                        farm.id = ds.getKey();
                        farm.title = ds.child("title").getValue(String.class);
                        farm.description = ds.child("description").getValue(String.class);
                        farm.latitude = ds.child("latitude").getValue(String.class);
                        farm.longitude = ds.child("longitude").getValue(String.class);
                        farm.starting_date = ds.child("starting_date").getValue(String.class);
                        farm.watering_scheme = Integer.parseInt(ds.child("watering_scheme").getValue(String.class));
                        String nw = ds.child("need_watering").getValue(String.class);
                        if(nw != null)
                            farm.need_watering = nw.equals("true");

                        farm.plant_id = ds.child("plant_id").getValue(String.class);
                        farm.plant_title = ds.child("plant_title").getValue(String.class);
                        farm.soil_id = ds.child("soil_id").getValue(String.class);
                        farm.soil_title = ds.child("soil_title").getValue(String.class);

                        farm.mainpump_id = ds.child("mainpump_id").getValue(String.class);
                        farm.humidity_sensor_id = ds.child("humidity_sensor_id").getValue(String.class);
                        farm.humidity_critical_point = ds.child("humidity_critical_point").getValue(String.class);
                        farm.valve_1_id = ds.child("valve_1_id").getValue(String.class);
                        farm.valve_2_id = ds.child("valve_2_id").getValue(String.class);

                        farm.linegroup_token = ds.child("linegroup_token").getValue(String.class);

                        farm.uid = ds.child("uid").getValue(String.class);

                        farm.is_public = Boolean.parseBoolean(ds.child("public").getValue(String.class));
                        farm.activated = Boolean.parseBoolean(ds.child("activated").getValue(String.class));
                        farm.created_at = ds.child("created_at").getValue(String.class);
                        String prefName = farm.getID() + "_picture";
                        SharedPreferences settings = getSharedPreferences(prefName, 0);
                        farm.image_path = settings.getString("farmImagePath", "");
                        mFarm.add(farm);

                    }
                };
                if(mFarm.size() > 0) {
                    initialFarmCardView();
                    //dialog_loading.dismiss();
                    dialog_farm_loading.dismiss();

                }
                //dlg.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initialFarmCardView(){
        farmOnClickListener = new FarmOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.farm_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new FarmAdapter(mFarm, this);
        //Log.d("CardView","Number of farm ==> " + mFarm.size());
        recyclerView.setAdapter(adapter);
    }

    private static class FarmOnClickListener implements View.OnClickListener {

        private final Context context;

        private FarmOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            removeItem(v);
        }

        private void removeItem(View v) {
            /*int selectedItemPosition = recyclerView.getChildPosition(v);
            RecyclerView.ViewHolder viewHolder
                    = recyclerView.findViewHolderForPosition(selectedItemPosition);
            TextView textViewName
                    = (TextView) viewHolder.itemView.findViewById(R.id.textViewName);
            String selectedName = (String) textViewName.getText();
            int selectedItemId = -1;
            for (int i = 0; i < MyData.nameArray.length; i++) {
                if (selectedName.equals(MyData.nameArray[i])) {
                    selectedItemId = MyData.id_[i];
                }
            }
            //removedItems.add(selectedItemId);
            data.remove(selectedItemPosition);
            adapter.notifyItemRemoved(selectedItemPosition);*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                updateUserProfile();
                Log.d("FirebaseAuthen","Hello "+mUser.getDisplayName() + "!");
                instantiateDatabase();
            } else {
                // Sign in failed, check response for error code
                // ...
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_about){
            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                dialog_about_app = new LovelyInfoDialog(this);
                dialog_about_app.setTopColorRes(R.color.colorAccent)
                        .setIcon(R.drawable.ic_perm_device_information_white_24dp)
                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                        //.setNotShowAgainOptionEnabled(0)
                        //.setNotShowAgainOptionChecked(true)
                        .setTitle(R.string.app_version)
                        .setMessage(version)
                        .show();

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Log.d("MenuItem","Menu2 clicked ----> " + item.getItemId());

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout){
            mFirebaseAuth.signOut();
            mFarm.clear();
            adapter.notifyDataSetChanged();
            finish();
            //AuthUI.getInstance()
            //        .signOut(this)
            //        .addOnCompleteListener(new OnCompleteListener<Void>() {
            //            public void onComplete(@NonNull Task<Void> task) {
            //                mFarm.clear();
            //                adapter.notifyDataSetChanged();
            //                finish();
            //            }
            //        });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
