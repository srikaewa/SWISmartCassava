package ai.suntrust.swihighprecisioncassavafarm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.aakira.compoundicontextview.CompoundIconTextView;
import com.github.abdularis.buttonprogress.DownloadButtonProgress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyDialogCompat;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FarmETpControlCenterActivity extends AppCompatActivity {
    Context context = this;

    APIInterface apiInterface;

    private View mContentView;

    private TextView textview_farm_title;
    private TextView textview_farm_description;
    private ImageView imageview_farm_setting;
    private ImageView imageview_farm_alert;
    private CompoundIconTextView textview_rain_status;
    private CompoundIconTextView textview_today_watering;
    private TextView    textview_farm;
    private DownloadButtonProgress switch_farm;
    private TextView    textview_mainpump;
    private DownloadButtonProgress switch_mainpump;
    private DownloadButtonProgress sw_valve11, sw_valve12, sw_valve13, sw_valve14, sw_valve15, sw_valve16;
    private DownloadButtonProgress sw_valve21, sw_valve22, sw_valve23, sw_valve24, sw_valve25, sw_valve26;
    private Chronometer  cm_valve11, cm_valve12, cm_valve13, cm_valve14, cm_valve15, cm_valve16;
    private Chronometer  cm_valve21, cm_valve22, cm_valve23, cm_valve24, cm_valve25, cm_valve26;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mFirebaseFarmRef;
    DatabaseReference mFirebaseMainpumpRef;
    DatabaseReference mFirebaseSensorRef;
    DatabaseReference mFirebaseValveRef;

    LovelyStandardDialog dialog_reset_timer;

    Farm farm = new Farm();

    /**** mainpump ****/
    //ArrayList<Mainpump> mMainpump = new ArrayList<Mainpump>();
    Map<String, Mainpump> mapOfMainpump = new HashMap<String, Mainpump>();
    /******************/

    /**** sensor ****/
    Map<String, Sensor> mapOfSensor = new HashMap<String, Sensor>();
    /****************/

    /**** valve ****/
    Map<String, Valve> mapOfValve = new HashMap<String, Valve>();
    /****************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_farm_etp_control_center);

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String textServerAddress = sharedPref.getString(SettingActivity.KEY_PREF_SERVER_ADDRESS, "DEFAULT");

        getSupportActionBar().hide();

        mContentView = findViewById(R.id.fullscreen_content);

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.button_farm_configuration).setOnTouchListener(mDelayHideTouchListener);
        apiInterface = APIClient.getClient(textServerAddress).create(APIInterface.class);

        //progressBar_loading = findViewById(R.id.progressBar_loading);
        imageview_farm_setting = findViewById(R.id.imageview_farm_setting);
        imageview_farm_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageview_farm_alert = findViewById(R.id.imageview_farm_alert);
        textview_farm_title = findViewById(R.id.farm_title);
        textview_farm_description = findViewById(R.id.farm_description);
        textview_rain_status = findViewById(R.id.textview_rain_status);
        textview_today_watering = findViewById(R.id.textview_today_watering);
        textview_rain_status = findViewById(R.id.textview_rain_status);

        switch_farm = findViewById(R.id.switch_farm);
        textview_farm = findViewById(R.id.textview_farm);

        switch_mainpump = findViewById(R.id.switch_mainpump);
        textview_mainpump = findViewById(R.id.textview_mainpump);
        //switch_mainpump.setTypeface(ResourcesCompat.getFont(context, R.font.superspace_regular));
        sw_valve11 = findViewById(R.id.switch11);
        //sw_valve1[0].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve12 = findViewById(R.id.switch12);
        //sw_valve1[1].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve13 = findViewById(R.id.switch13);
        //sw_valve1[2].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve14 = findViewById(R.id.switch14);
        //sw_valve1[3].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve15 = findViewById(R.id.switch15);
        //sw_valve1[4].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve16 = findViewById(R.id.switch16);
        //sw_valve1[5].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        cm_valve11 = findViewById(R.id.timer11);
        //cm_valve1[0].setFormat("เวลาเปิดวาล์ว %s");
        cm_valve12 = findViewById(R.id.timer12);
        cm_valve13 = findViewById(R.id.timer13);
        cm_valve14 = findViewById(R.id.timer14);
        cm_valve15 = findViewById(R.id.timer15);
        cm_valve16 = findViewById(R.id.timer16);

        cm_valve21 = findViewById(R.id.timer21);
        //cm_valve1[0].setFormat("เวลาเปิดวาล์ว %s");
        cm_valve22 = findViewById(R.id.timer22);
        cm_valve23 = findViewById(R.id.timer23);
        cm_valve24 = findViewById(R.id.timer24);
        cm_valve25 = findViewById(R.id.timer25);
        cm_valve26 = findViewById(R.id.timer26);

        sw_valve21 = findViewById(R.id.switch21);
        //sw_valve2[0].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve22 = findViewById(R.id.switch22);
        //sw_valve2[1].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve23 = findViewById(R.id.switch23);
        //sw_valve2[2].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve24 = findViewById(R.id.switch24);
        //sw_valve2[3].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve25 = findViewById(R.id.switch25);
        //sw_valve2[4].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve26 = findViewById(R.id.switch26);
        //sw_valve2[5].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));

        dialog_reset_timer = new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                .setTopColor(getResources().getColor(R.color.colorAccent))
                .setIcon(R.drawable.ic_timer_white_24dp);

        Glide.with(context)
                .asGif()
                .load(R.drawable.animated_alarm)
                .into(imageview_farm_alert);
        initialFarmData();
        initiateDatabase();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    void initialFarmData()
    {
        farm.id = getIntent().getExtras().getString("id");
        farm.mainpump_id = getIntent().getExtras().getString("mainpump_id");
        farm.rain_sensor_id = getIntent().getExtras().getString("rain_id");
        farm.valve_1_id = getIntent().getExtras().getString("valve_1_id");
        farm.valve_2_id = getIntent().getExtras().getString("valve_2_id");
    }

    void initiateDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseFarmRef = mFirebaseDatabase.getReference("farm").child(farm.id);
        mFirebaseFarmRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                farm.need_watering = ds.child("need_watering").getValue(String.class).equals("true");
                farm.title = ds.child("title").getValue(String.class);
                farm.description = ds.child("description").getValue(String.class);
                farm.latitude = ds.child("latitude").getValue(String.class);
                farm.longitude = ds.child("longitude").getValue(String.class);
                farm.starting_date = ds.child("starting_date").getValue(String.class);
                farm.watering_scheme = Integer.parseInt(ds.child("watering_scheme").getValue(String.class));

                farm.plant_id = ds.child("plant_id").getValue(String.class);
                farm.plant_title = ds.child("plant_title").getValue(String.class);
                farm.soil_id = ds.child("soil_id").getValue(String.class);
                farm.soil_title = ds.child("soil_title").getValue(String.class);

                farm.mainpump_id = ds.child("mainpump_id").getValue(String.class);
                farm.rain_sensor_id = ds.child("rain_sensor_id").getValue(String.class);
                farm.valve_1_id = ds.child("valve_1_id").getValue(String.class);
                farm.valve_2_id = ds.child("valve_2_id").getValue(String.class);
                farm.activated = ds.child("activated").getValue(String.class).equals("true");
                farm.watering_schedule.clear();
                for(DataSnapshot ws: ds.child("watering_schedule").getChildren())
                {
                    WateringSchedule w = new WateringSchedule();
                    //Log.d("WS", ws.toString());
                    w.current_date = ws.child("current_date").getValue(String.class);
                    w.next_date = ws.child("next_date").getValue(String.class);
                    w.days = ws.child("days").getValue(Integer.class);
                    w.hours = ws.child("hours").getValue(Integer.class);
                    w.mins = ws.child("mins").getValue(Integer.class);
                    w.total_mins = ws.child("total_mins").getValue(Integer.class);
                    farm.watering_schedule.add(w);
                    //Log.d("WS", farm.watering_schedule.get(0).current_date);
                }
                initialFarmUI();
                final Farm f = farm;
                switch_farm.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
                    @Override
                    public void onIdleButtonClick(View view) {
                        switch_farm.setIndeterminate();
                        Call<String> call = apiInterface.activateFarm1(f.getID());
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                //progressBar_loading.setVisibility(View.GONE);
                                //Log.d("Mainpump", response.code() + "");
                                if(response.body().equals("1000")){
                                    //enableAllValve();
                                    //turnonPumpUI();
                                    Snackbar.make(findViewById(android.R.id.content), "เปิดระบบฟาร์มเรียบร้อย...", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }else {
                                    //switch_mainpump.setChecked(false);
                                    Snackbar.make(findViewById(android.R.id.content), "เปิดระบบฟาร์มไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                                updateFarmUI();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                //progressBar_loading.setVisibility(View.GONE);
                                updateFarmUI();
                                Log.d("Farm", t.getMessage());
                                Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อระบบฟาร์มผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelButtonClick(View view) {

                    }

                    @Override
                    public void onFinishButtonClick(View view) {
                        switch_farm.setIndeterminate();
                        Call<String> call = apiInterface.deactivateFarm1(f.id);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                //Log.d("Mainpump", response.code() + "");
                                if(response.body().equals("1100")){
                                    //enableAllValve();
                                    //turnonPumpUI();
                                    Snackbar.make(findViewById(android.R.id.content), "ปิดระบบฟาร์มเรียบร้อย...", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }else if(response.body().equals("1101")){
                                    Snackbar.make(findViewById(android.R.id.content), "ปิดระบบฟาร์มไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                                updateFarmUI();
                                //else{
                                //    updateFarmUI();
                                //    Snackbar.make(findViewById(android.R.id.content), "[1102] เกิดข้อผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                //            .setAction("Action", null).show();
                                //}
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d("Farm", t.getMessage());
                                updateFarmUI();
                                Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อระบบฟาร์มผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFirebaseMainpumpRef = mFirebaseDatabase.getReference("mainpump");
        mFirebaseMainpumpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapOfMainpump.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Mainpump mainpump = new Mainpump();
                    mainpump.id = ds.getKey();
                    mainpump.created_at = ds.child("created_at").getValue(String.class);
                    mainpump.description = ds.child("description").getValue(String.class);
                    mainpump.entry_id = ds.child("entry_id").getValue(Integer.class);
                    mainpump.field1 = ds.child("field1").getValue(String.class);
                    mainpump.field2 = ds.child("field2").getValue(String.class);
                    mainpump.field3 = ds.child("field3").getValue(String.class);
                    mainpump.field4 = ds.child("field4").getValue(String.class);
                    mainpump.field5 = ds.child("field5").getValue(String.class);
                    mainpump.field6 = ds.child("field6").getValue(String.class);
                    mainpump.field7 = ds.child("field7").getValue(String.class);
                    mainpump.field8 = ds.child("field8").getValue(String.class);
                    mainpump.write_api_key = ds.child("write_api_key").getValue(String.class);
                    mainpump.data_created_at = ds.child("data_created_at").getValue(String.class);
                    mainpump.last_updated = ds.child("last_updated").getValue(String.class);
                    //mMainpump.add(mainpump);
                    mapOfMainpump.put(mainpump.id, mainpump);
                }
                final Mainpump m = mapOfMainpump.get(farm.mainpump_id);
                updateMainpumpUI();

                switch_mainpump.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
                    @Override
                    public void onIdleButtonClick(View view) {
                        switch_mainpump.setIndeterminate();
                        Call<String> call = apiInterface.turnonMainpump(m.id, m.write_api_key, "1");
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                //progressBar_loading.setVisibility(View.GONE);
                                //Log.d("Mainpump", response.code() + "");
                                if(response.body().equals("2000")){
                                    //enableAllValve();
                                    //turnonPumpUI();
                                    Snackbar.make(findViewById(android.R.id.content), "เปิดปั๊มน้ำหลักเรียบร้อย...", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }else {
                                    updateMainpumpUI();
                                    //switch_mainpump.setChecked(false);
                                    Snackbar.make(findViewById(android.R.id.content), "เปิดปั๊มน้ำหลักไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                //progressBar_loading.setVisibility(View.GONE);
                                updateMainpumpUI();
                                Log.d("Mainpump", t.getMessage());
                                Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อปั๊มน้ำหลักผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelButtonClick(View view) {

                    }

                    @Override
                    public void onFinishButtonClick(View view) {
                        switch_mainpump.setIndeterminate();
                        Call<String> call = apiInterface.turnoffMainpump(m.id, m.write_api_key, "1");
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                //progressBar_loading.setVisibility(View.GONE);
                                //Log.d("Mainpump", response.code() + "");
                                if(response.body().equals("2200")){
                                    //enableAllValve();
                                    //turnonPumpUI();
                                    Snackbar.make(findViewById(android.R.id.content), "ปิดปั๊มน้ำหลักเรียบร้อย...", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }else if(response.body().equals("2001")){
                                    updateMainpumpUI();
                                    Snackbar.make(findViewById(android.R.id.content), "ปิดปั๊มน้ำหลักไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d("Mainpump", t.getMessage());
                                updateMainpumpUI();
                                Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อปั๊มน้ำหลักผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFirebaseValveRef = mFirebaseDatabase.getReference("valve");
        mFirebaseValveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapOfValve.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Valve valve = new Valve();
                    valve.id = ds.getKey();
                    valve.created_at = ds.child("created_at").getValue(String.class);
                    valve.description = ds.child("description").getValue(String.class);
                    if(ds.child("entry_id").getValue(Integer.class) != null)
                        valve.entry_id = ds.child("entry_id").getValue(Integer.class);
                    else
                        valve.entry_id = -1;
                    valve.field1 = ds.child("field1").getValue(String.class);
                    valve.field2 = ds.child("field2").getValue(String.class);
                    valve.field3 = ds.child("field3").getValue(String.class);
                    valve.field4 = ds.child("field4").getValue(String.class);
                    valve.field5 = ds.child("field5").getValue(String.class);
                    valve.field6 = ds.child("field6").getValue(String.class);
                    valve.field7 = ds.child("field7").getValue(String.class);
                    valve.field8 = ds.child("field8").getValue(String.class);
                    valve.lastTimeOn1 = ds.child("last_datetime_on1").getValue(String.class);
                    valve.lastTimeOn2 = ds.child("last_datetime_on2").getValue(String.class);
                    valve.lastTimeOn3 = ds.child("last_datetime_on3").getValue(String.class);
                    valve.lastTimeOn4 = ds.child("last_datetime_on4").getValue(String.class);
                    valve.lastTimeOn5 = ds.child("last_datetime_on5").getValue(String.class);
                    valve.lastTimeOn6 = ds.child("last_datetime_on6").getValue(String.class);
                    valve.lastTimeOn7 = ds.child("last_datetime_on7").getValue(String.class);
                    valve.lastTimeOn8 = ds.child("last_datetime_on8").getValue(String.class);
                    if(ds.child("time_on1").getValue(Integer.class) != null)
                        valve.time_on1 = ds.child("time_on1").getValue(Integer.class);
                    else
                        valve.time_on1 = -1;
                    if(ds.child("time_on2").getValue(Integer.class) != null)
                        valve.time_on2 = ds.child("time_on2").getValue(Integer.class);
                    else
                        valve.time_on2 = -1;
                    if(ds.child("time_on3").getValue(Integer.class) != null)
                        valve.time_on3 = ds.child("time_on3").getValue(Integer.class);
                    else
                        valve.time_on3 = -1;
                    if(ds.child("time_on4").getValue(Integer.class) != null)
                        valve.time_on4 = ds.child("time_on4").getValue(Integer.class);
                    else
                        valve.time_on4 = -1;
                    if(ds.child("time_on5").getValue(Integer.class) != null)
                        valve.time_on5 = ds.child("time_on5").getValue(Integer.class);
                    else
                        valve.time_on5 = -1;
                    if(ds.child("time_on6").getValue(Integer.class) != null)
                        valve.time_on6 = ds.child("time_on6").getValue(Integer.class);
                    else
                        valve.time_on6 = -1;
                    if(ds.child("time_on7").getValue(Integer.class) != null)
                        valve.time_on7 = ds.child("time_on7").getValue(Integer.class);
                    else
                        valve.time_on7 = -1;
                    if(ds.child("time_on8").getValue(Integer.class) != null)
                        valve.time_on8 = ds.child("time_on8").getValue(Integer.class);
                    else
                        valve.time_on8 = -1;
                    valve.write_api_key = ds.child("write_api_key").getValue(String.class);
                    valve.data_created_at = ds.child("data_created_at").getValue(String.class);
                    valve.last_updated = ds.child("last_updated").getValue(String.class);
                    //mMainpump.add(mainpump);
                    mapOfValve.put(valve.id, valve);
                }

                updateValveUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cm_valve11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));

                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #1")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v1 = mapOfValve.get(farm.valve_1_id);
                                Call<String> call = apiInterface.resetValveTimer(v1.id, "1");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #1 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #1 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #1 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                .setNegativeButton(android.R.string.no, null)
                .show();
            }
        });

        sw_valve11.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnonValve(v1.id, v1.write_api_key, "1");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            //cm_valve11.start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #1 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #1 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #1 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnoffValve(v1.id, v1.write_api_key, "1");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #1 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #1 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #1 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        cm_valve12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));
                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #2")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v1 = mapOfValve.get(farm.valve_1_id);
                                Call<String> call = apiInterface.resetValveTimer(v1.id, "2");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #2 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #2 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #2 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        sw_valve12.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnonValve(v1.id, v1.write_api_key, "2");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            //cm_valve11.start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #2 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #2 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #2 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnoffValve(v1.id, v1.write_api_key, "2");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #2 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #2 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #2 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        cm_valve13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));
                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #3")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v1 = mapOfValve.get(farm.valve_1_id);
                                Call<String> call = apiInterface.resetValveTimer(v1.id, "3");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #3 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #3 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #3 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        sw_valve13.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnonValve(v1.id, v1.write_api_key, "3");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            //cm_valve11.start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #3 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #3 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #3 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnoffValve(v1.id, v1.write_api_key, "3");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #3 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #3 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #3 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        cm_valve14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));
                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #4")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v1 = mapOfValve.get(farm.valve_1_id);
                                Call<String> call = apiInterface.resetValveTimer(v1.id, "4");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #4 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #4 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #4 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        sw_valve14.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnonValve(v1.id, v1.write_api_key, "4");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            //cm_valve11.start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #4 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #4 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #4 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnoffValve(v1.id, v1.write_api_key, "4");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #4 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #4 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #4 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        cm_valve15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));
                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #5")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v1 = mapOfValve.get(farm.valve_1_id);
                                Call<String> call = apiInterface.resetValveTimer(v1.id, "5");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #5 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #5 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #5 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        sw_valve15.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnonValve(v1.id, v1.write_api_key, "5");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            //cm_valve11.start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #5 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #5 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #5 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnoffValve(v1.id, v1.write_api_key, "5");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #5 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #5 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #5 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        cm_valve16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));
                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #6")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v1 = mapOfValve.get(farm.valve_1_id);
                                Call<String> call = apiInterface.resetValveTimer(v1.id, "6");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #6 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #6 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #6 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

        sw_valve16.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnonValve(v1.id, v1.write_api_key, "6");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            //cm_valve11.start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #6 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #6 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #6 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v1 = mapOfValve.get(farm.valve_1_id);
                Call<String> call = apiInterface.turnoffValve(v1.id, v1.write_api_key, "6");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #6 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #6 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #6 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });


        cm_valve21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));
                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #7")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v2 = mapOfValve.get(farm.valve_2_id);
                                Call<String> call = apiInterface.resetValveTimer(v2.id, "1");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #7 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #7 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #7 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        sw_valve21.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);
                Call<String> call = apiInterface.turnonValve(v2.id, v2.write_api_key, "1");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #7 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #7 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #7 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);

                Call<String> call = apiInterface.turnoffValve(v2.id, v2.write_api_key, "1");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            //cm_valve1[valve_number].stop();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #7 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #7 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #7 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        cm_valve22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));
                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #8")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v2 = mapOfValve.get(farm.valve_2_id);
                                Call<String> call = apiInterface.resetValveTimer(v2.id, "2");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #8 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #8 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #8 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        sw_valve22.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);
                Call<String> call = apiInterface.turnonValve(v2.id, v2.write_api_key, "2");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #8 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #8 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #8 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);

                Call<String> call = apiInterface.turnoffValve(v2.id, v2.write_api_key, "2");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            //cm_valve1[valve_number].stop();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #8 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #8 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #8 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        cm_valve23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));
                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #9")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v2 = mapOfValve.get(farm.valve_2_id);
                                Call<String> call = apiInterface.resetValveTimer(v2.id, "3");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #9 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #9 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #9 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        sw_valve23.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);
                Call<String> call = apiInterface.turnonValve(v2.id, v2.write_api_key, "3");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #9 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #9 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #9 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);

                Call<String> call = apiInterface.turnoffValve(v2.id, v2.write_api_key, "3");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            //cm_valve1[valve_number].stop();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #9 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #9 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #9 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        cm_valve24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));
                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #10")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v2 = mapOfValve.get(farm.valve_2_id);
                                Call<String> call = apiInterface.resetValveTimer(v2.id, "4");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #10 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #10 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #10 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        sw_valve24.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);
                Call<String> call = apiInterface.turnonValve(v2.id, v2.write_api_key, "4");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #10 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #10 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #10 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);

                Call<String> call = apiInterface.turnoffValve(v2.id, v2.write_api_key, "4");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            //cm_valve1[valve_number].stop();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #10 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #10 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #10 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        cm_valve25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));
                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #11")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v2 = mapOfValve.get(farm.valve_2_id);
                                Call<String> call = apiInterface.resetValveTimer(v2.id, "5");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #11 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #11 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #11 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        sw_valve25.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);
                Call<String> call = apiInterface.turnonValve(v2.id, v2.write_api_key, "5");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #11 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #11 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #11 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);

                Call<String> call = apiInterface.turnoffValve(v2.id, v2.write_api_key, "5");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            //cm_valve1[valve_number].stop();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #11 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #11 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #11 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        cm_valve26.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chronometer cm = (Chronometer) findViewById(v.getId());
                //String dstring = getResources().getResourceName(v.getId());
                //final int valve_number = getValveNumber2(dstring.substring(dstring.length()-7));
                dialog_reset_timer.setTitle("รีเซ็ตเวลาของวาล์ว #12")
                        .setMessage("ค่าของเวลาจะเป็น 0 เพื่อเริ่มต้นจับเวลาการเปิดปิดวาล์วน้ำใหม่")
                        .setPositiveButton(android.R.string.ok, LovelyDialogCompat.wrap(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Valve v2 = mapOfValve.get(farm.valve_2_id);
                                Call<String> call = apiInterface.resetValveTimer(v2.id, "6");
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        if (response.body().equals("400")) {
                                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                                            //cm_valve1[valve_number].start();
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #12 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            Snackbar.make(findViewById(android.R.id.content), "รีเซ็ตเวลาวาล์ว #12 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        //updateValveUI();
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #12 เพื่อรีเซ็ตเวลาไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }))
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        sw_valve26.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);
                Call<String> call = apiInterface.turnonValve(v2.id, v2.write_api_key, "6");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        //Log.d("TurnOnValve", response.body() + "");
                        if (response.body().equals("300")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.VISIBLE);
                            //cm_valve1[valve_number].start();
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #12 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #12 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #12 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }

            @Override
            public void onCancelButtonClick(View view) {

            }

            @Override
            public void onFinishButtonClick(View view) {
                DownloadButtonProgress dbutton = (DownloadButtonProgress)findViewById(view.getId());
                dbutton.setIndeterminate();
                //String dstring = getResources().getResourceName(dbutton.getId());
                //Log.d("ValveNumber", "Valve = " + dstring.substring(dstring.length()-8));
                //final int valve_number = getValveNumber(dstring.substring(dstring.length()-8));
                Valve v2 = mapOfValve.get(farm.valve_2_id);

                Call<String> call = apiInterface.turnoffValve(v2.id, v2.write_api_key, "6");
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body().equals("320")) {
                            //cm_row1[(int)(valve_number/2)].setVisibility(View.GONE);
                            //cm_valve1[valve_number].stop();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #12 เรียบร้อย...", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            updateValveUI();
                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #12 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        updateValveUI();
                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #12 ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });
            }
        });

        mFirebaseSensorRef = mFirebaseDatabase.getReference("sensor");
        mFirebaseSensorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//mMainpump.clear();
                mapOfSensor.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Sensor sensor = new Sensor();
                    sensor.id = ds.getKey();
                    sensor.created_at = ds.child("created_at").getValue(String.class);
                    sensor.description = ds.child("description").getValue(String.class);
                    if(ds.child("entry_id").getValue(Integer.class) != null)
                        sensor.entry_id = ds.child("entry_id").getValue(Integer.class);
                    else
                        sensor.entry_id = -1;
                    sensor.field1 = ds.child("field1").getValue(String.class);
                    sensor.field2 = ds.child("field2").getValue(String.class);
                    sensor.field3 = ds.child("field3").getValue(String.class);
                    sensor.field4 = ds.child("field4").getValue(String.class);
                    sensor.field5 = ds.child("field5").getValue(String.class);
                    sensor.field6 = ds.child("field6").getValue(String.class);
                    sensor.field7 = ds.child("field7").getValue(String.class);
                    sensor.field8 = ds.child("field8").getValue(String.class);
                    sensor.read_api_key = ds.child("read_api_key").getValue(String.class);
                    sensor.data_created_at = ds.child("data_created_at").getValue(String.class);
                    sensor.last_updated = ds.child("last_updated").getValue(String.class);
                    //mMainpump.add(mainpump);
                    mapOfSensor.put(sensor.id, sensor);
                }
                updateRainUI();
                //updateFarmUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    void updateRainUI()
    {
        // currently support only 1 sensor
        Sensor sensor = mapOfSensor.get(farm.rain_sensor_id);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        LocalDate ld = fmt.parseLocalDate(sensor.created_at);
        LocalDate now = LocalDate.now();
        int dbetween = Days.daysBetween(ld, now).getDays();
        if(dbetween == 0) {
            textview_rain_status.setText("ปริมาณน้ำฝนในแปลงล่าสุดวันนี้ " + Math.round(Float.parseFloat(sensor.field1)) + " มม.");
        }
        else if(dbetween == 1)
        {
            textview_rain_status.setText("ปริมาณน้ำฝนในแปลงล่าสุดเมื่อวานนี้ " + Math.round(Float.parseFloat(sensor.field1)) + " มม.");
        }else
        {
            textview_rain_status.setText("ปริมาณน้ำฝนในแปลงล่าสุดเมื่อ " + dbetween + " วันที่แล้ว " + Math.round(Float.parseFloat(sensor.field1)) + " มม.");
        }

    }

    public boolean doesObjectContainField(Object object, String fieldName) {
        Class<?> objectClass = object.getClass();
        for (Field field : objectClass.getFields()) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    void updateValveUI()
    {
        Valve v = mapOfValve.get(farm.valve_1_id);
        //Log.d("ValveStatus", "field1 = " + v.field1);
        if(v.field1 != null )
        {
            if(v.field1.equals("1")){
                sw_valve11.setFinish();
                cm_valve11.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn1());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime())/1000;
                //Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => "+ time_now.toString() +", seconds diff => " + Long.toString(diff));
                cm_valve11.setBase(SystemClock.elapsedRealtime() - (diff*1000 + v.getTimeOn1()*60000));
                cm_valve11.start();
            }
            else{
                cm_valve11.stop();
                sw_valve11.setIdle();
                cm_valve11.setBase(SystemClock.elapsedRealtime() - v.getTimeOn1()*60000);
                cm_valve11.setClickable(true);
            }
        }
        else
        {
            sw_valve11.setIdle();
        }

        if(v.field2 != null )
        {
            if(v.field2.equals("1")){
                sw_valve12.setFinish();
                cm_valve12.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn2());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime())/1000;
                //Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => "+ time_now.toString() +", seconds diff => " + Long.toString(diff));
                cm_valve12.setBase(SystemClock.elapsedRealtime() - (diff*1000 + v.getTimeOn2()*60000));
                cm_valve12.start();
            }
            else{
                cm_valve12.stop();
                sw_valve12.setIdle();
                cm_valve12.setBase(SystemClock.elapsedRealtime() - v.getTimeOn2()*60000);
                cm_valve12.setClickable(true);
            }
        }
        else
        {
            sw_valve12.setIdle();
        }

        if(v.field3 != null )
        {
            if(v.field3.equals("1")){
                sw_valve13.setFinish();
                cm_valve13.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn3());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime())/1000;
                //Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => "+ time_now.toString() +", seconds diff => " + Long.toString(diff));
                cm_valve13.setBase(SystemClock.elapsedRealtime() - (diff*1000 + v.getTimeOn3()*60000));
                cm_valve13.start();
            }
            else{
                cm_valve13.stop();
                sw_valve13.setIdle();
                cm_valve13.setBase(SystemClock.elapsedRealtime() - v.getTimeOn3()*60000);
                cm_valve13.setClickable(true);
            }
        }
        else
        {
            sw_valve13.setIdle();
        }

        if(v.field4 != null )
        {
            if(v.field4.equals("1")){
                sw_valve14.setFinish();
                cm_valve14.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn4());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime())/1000;
                //Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => "+ time_now.toString() +", seconds diff => " + Long.toString(diff));
                cm_valve14.setBase(SystemClock.elapsedRealtime() - (diff*1000 + v.getTimeOn4()*60000));
                cm_valve14.start();
            }
            else{
                cm_valve14.stop();
                sw_valve14.setIdle();
                cm_valve14.setBase(SystemClock.elapsedRealtime() - v.getTimeOn4()*60000);
                cm_valve14.setClickable(true);
            }
        }
        else
        {
            sw_valve14.setIdle();
        }

        if(v.field5 != null )
        {
            if(v.field5.equals("1")){
                sw_valve15.setFinish();
                cm_valve15.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn5());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime())/1000;
                //Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => "+ time_now.toString() +", seconds diff => " + Long.toString(diff));
                cm_valve15.setBase(SystemClock.elapsedRealtime() - (diff*1000 + v.getTimeOn5()*60000));
                cm_valve15.start();
            }
            else{
                cm_valve15.stop();
                sw_valve15.setIdle();
                cm_valve15.setBase(SystemClock.elapsedRealtime() - v.getTimeOn5()*60000);
                cm_valve15.setClickable(true);
            }
        }
        else
        {
            sw_valve15.setIdle();
        }

        if(v.field6 != null )
        {
            if(v.field6.equals("1")){
                sw_valve16.setFinish();
                cm_valve16.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn6());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime())/1000;
                //Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => "+ time_now.toString() +", seconds diff => " + Long.toString(diff));
                cm_valve16.setBase(SystemClock.elapsedRealtime() - (diff*1000 + v.getTimeOn6()*60000));
                cm_valve16.start();
            }
            else{
                cm_valve16.stop();
                sw_valve16.setIdle();
                cm_valve16.setBase(SystemClock.elapsedRealtime() - v.getTimeOn6()*60000);
                cm_valve16.setClickable(true);
            }
        }
        else
        {
            sw_valve16.setIdle();
        }

        v = mapOfValve.get(farm.valve_2_id);
        if(v.field1 != null )
        {
            if (v.field1.equals("1")) {
                sw_valve21.setFinish();
                cm_valve21.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn1());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime()) / 1000;
                Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => " + time_now.toString() + ", seconds diff => " + Long.toString(diff));
                cm_valve21.setBase(SystemClock.elapsedRealtime() - (diff * 1000 + v.getTimeOn1() * 60000));
                cm_valve21.start();
            } else {
                cm_valve21.stop();
                sw_valve21.setIdle();
                cm_valve21.setBase(SystemClock.elapsedRealtime() - v.getTimeOn1() * 60000);
                cm_valve21.setClickable(true);
            }
        }
        else
        {
            sw_valve21.setIdle();
        }
        if(v.field2 != null )
        {
            if (v.field2.equals("1")) {
                sw_valve22.setFinish();
                cm_valve22.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn2());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime()) / 1000;
                Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => " + time_now.toString() + ", seconds diff => " + Long.toString(diff));
                cm_valve22.setBase(SystemClock.elapsedRealtime() - (diff * 1000 + v.getTimeOn2() * 60000));
                cm_valve22.start();
            } else {
                cm_valve22.stop();
                sw_valve22.setIdle();
                cm_valve22.setBase(SystemClock.elapsedRealtime() - v.getTimeOn2() * 60000);
                cm_valve22.setClickable(true);
            }
        }
        else
        {
            sw_valve22.setIdle();
        }
        if(v.field3 != null )
        {
            if (v.field3.equals("1")) {
                sw_valve23.setFinish();
                cm_valve23.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn3());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime()) / 1000;
                Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => " + time_now.toString() + ", seconds diff => " + Long.toString(diff));
                cm_valve23.setBase(SystemClock.elapsedRealtime() - (diff * 1000 + v.getTimeOn3() * 60000));
                cm_valve23.start();
            } else {
                cm_valve23.stop();
                sw_valve23.setIdle();
                cm_valve23.setBase(SystemClock.elapsedRealtime() - v.getTimeOn3() * 60000);
                cm_valve23.setClickable(true);
            }
        }
        else
        {
            sw_valve23.setIdle();
        }
        if(v.field4 != null )
        {
            if (v.field4.equals("1")) {
                sw_valve24.setFinish();
                cm_valve24.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn4());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime()) / 1000;
                Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => " + time_now.toString() + ", seconds diff => " + Long.toString(diff));
                cm_valve24.setBase(SystemClock.elapsedRealtime() - (diff * 1000 + v.getTimeOn4() * 60000));
                cm_valve24.start();
            } else {
                cm_valve24.stop();
                sw_valve24.setIdle();
                cm_valve24.setBase(SystemClock.elapsedRealtime() - v.getTimeOn4() * 60000);
                cm_valve24.setClickable(true);
            }
        }
        else
        {
            sw_valve24.setIdle();
        }
        if(v.field5 != null )
        {
            if (v.field5.equals("1")) {
                sw_valve25.setFinish();
                cm_valve25.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn5());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime()) / 1000;
                Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => " + time_now.toString() + ", seconds diff => " + Long.toString(diff));
                cm_valve25.setBase(SystemClock.elapsedRealtime() - (diff * 1000 + v.getTimeOn5() * 60000));
                cm_valve25.start();
            } else {
                cm_valve25.stop();
                sw_valve25.setIdle();
                cm_valve25.setBase(SystemClock.elapsedRealtime() - v.getTimeOn5() * 60000);
                cm_valve25.setClickable(true);
            }
        }
        else
        {
            sw_valve25.setIdle();
        }
        if(v.field6 != null )
        {
            if (v.field6.equals("1")) {
                sw_valve26.setFinish();
                cm_valve26.setClickable(false);
                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date valveLastTimeOn = new Date();
                Date time_now = new Date();
                try {
                    valveLastTimeOn = df1.parse(v.getLastTimeOn6());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = (time_now.getTime() - valveLastTimeOn.getTime()) / 1000;
                Log.d("TimeDiff", "Last on => " + valveLastTimeOn.toString() + " now => " + time_now.toString() + ", seconds diff => " + Long.toString(diff));
                cm_valve26.setBase(SystemClock.elapsedRealtime() - (diff * 1000 + v.getTimeOn6() * 60000));
                cm_valve26.start();
            } else {
                cm_valve26.stop();
                sw_valve26.setIdle();
                cm_valve26.setBase(SystemClock.elapsedRealtime() - v.getTimeOn6() * 60000);
                cm_valve26.setClickable(true);
            }
        }
        else
        {
            sw_valve26.setIdle();
        }
    }

    void updateFarmUI()
    {
        Sensor sensor = mapOfSensor.get(farm.rain_sensor_id);
        updateTodayWatering();
        if(farm.isActivated())
        {
            switch_farm.setFinish();
            textview_farm.setText(getResources().getString(R.string.farm_activated));
            activateOnUI();
        }
        else
        {
            switch_farm.setIdle();
            textview_farm.setText(getResources().getString(R.string.farm_deactivated));
            activateOffUI();
        }
    }

    void updateTodayWatering()
    {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();
        for(int i=0;i < farm.watering_schedule.size();i++)
        {
            LocalDate wdate = fmt.parseLocalDate(farm.watering_schedule.get(i).current_date);
            int dbetween = Days.daysBetween(now, wdate).getDays();
            Log.d("WateringDate", "WDate = " + wdate.toString() + ", now = " + now.toString() + ", days diff = " + dbetween);
            if(dbetween == 0)
            {
                textview_today_watering.setText("ตารางให้น้ำวันนี้ " + farm.watering_schedule.get(i).hours + " ชั่วโมง " + farm.watering_schedule.get(i).mins + " นาที");
                break;
            }
            else if(dbetween > 0)
            {
                textview_today_watering.setText("ตารางให้น้ำครั้งต่อไปอีก " + dbetween + " วัน");
                break;
            }
        }
    }

    void activateOnUI()
    {
        switch_mainpump.setEnabled(true);
        sw_valve11.setEnabled(true);
        sw_valve12.setEnabled(true);
        sw_valve13.setEnabled(true);
        sw_valve14.setEnabled(true);
        sw_valve15.setEnabled(true);
        sw_valve16.setEnabled(true);
        sw_valve21.setEnabled(true);
        sw_valve22.setEnabled(true);
        sw_valve23.setEnabled(true);
        sw_valve24.setEnabled(true);
        sw_valve25.setEnabled(true);
        sw_valve26.setEnabled(true);
        cm_valve11.setEnabled(true);
        cm_valve12.setEnabled(true);
        cm_valve13.setEnabled(true);
        cm_valve14.setEnabled(true);
        cm_valve15.setEnabled(true);
        cm_valve16.setEnabled(true);
        cm_valve21.setEnabled(true);
        cm_valve22.setEnabled(true);
        cm_valve23.setEnabled(true);
        cm_valve24.setEnabled(true);
        cm_valve25.setEnabled(true);
        cm_valve26.setEnabled(true);
    }

    void activateOffUI()
    {
        switch_mainpump.setEnabled(false);
        sw_valve11.setEnabled(false);
        sw_valve12.setEnabled(false);
        sw_valve13.setEnabled(false);
        sw_valve14.setEnabled(false);
        sw_valve15.setEnabled(false);
        sw_valve16.setEnabled(false);
        sw_valve21.setEnabled(false);
        sw_valve22.setEnabled(false);
        sw_valve23.setEnabled(false);
        sw_valve24.setEnabled(false);
        sw_valve25.setEnabled(false);
        sw_valve26.setEnabled(false);
        cm_valve11.setEnabled(false);
        cm_valve12.setEnabled(false);
        cm_valve13.setEnabled(false);
        cm_valve14.setEnabled(false);
        cm_valve15.setEnabled(false);
        cm_valve16.setEnabled(false);
        cm_valve21.setEnabled(false);
        cm_valve22.setEnabled(false);
        cm_valve23.setEnabled(false);
        cm_valve24.setEnabled(false);
        cm_valve25.setEnabled(false);
        cm_valve26.setEnabled(false);
    }

    void updateMainpumpUI()
    {
        Mainpump m = mapOfMainpump.get(farm.mainpump_id);
        if(m.field1.equals("1"))
        {
            switch_mainpump.setFinish();
            textview_mainpump.setText(getResources().getString(R.string.main_pump_on));
        }
        else
        {
            switch_mainpump.setIdle();
            textview_mainpump.setText(getResources().getString(R.string.main_pump_off));
        }
    }

    void initialFarmUI()
    {
        textview_farm_title.setText(farm.getTitle());
        textview_farm_description.setText(farm.getDescription());
        updateFarmUI();
    }

}


