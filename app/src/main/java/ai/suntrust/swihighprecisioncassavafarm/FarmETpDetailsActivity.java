package ai.suntrust.swihighprecisioncassavafarm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Float.parseFloat;

//import com.google.android.material.textfield.TextInputEditText;
//import fr.ganfra.materialspinner.MaterialSpinner;
//import fr.erictruong.materialedittext.MaterialEditText;
//import android.widget.MaterialEditText;

public class FarmETpDetailsActivity extends AppCompatActivity {
    Context context = this;

    static final int GET_FARM_LOCATION_REQUEST = 101;
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 1889;
    private static final int GET_CAMERA_PERMISSION_CODE = 101;

    String app_dir;


    Farm farm = new Farm();
    String id;

    int valve1_check = 0;
    int valve2_check = 0;
    int rain_check = 0;

    ImageView imageview_animated_drip;
    ImageView imageview_camera;
    ImageView imageview_farm_picture;

    TextView textview_title;
    TextView textview_description;
    MaterialEditText textview_location;
    TextView textview_map;
    MaterialEditText edittext_sampling_time;
    MaterialEditText edittext_watering_scheme;

    TextView textview_drip_details;
    MaterialEditText edittext_drip_flowrate;
    MaterialEditText edittext_drip_interval;
    MaterialEditText edittext_tape_interval;
    MaterialEditText edittext_total_drip_per_rai;
    MaterialEditText edittext_total_flowrate_per_rai;

    MaterialBetterSpinner spinner_soil_type;
    ArrayAdapter spinner_soil_adapter;

    TextView textview_mainpump_details;
    MaterialEditText textview_mainpump_description;
    //MaterialEditText textview_mainpump_id;
    MaterialEditText textview_mainpump_API_key;
    MaterialBetterSpinner spinner_mainpump;
    ArrayAdapter spinner_mainpump_adapter;
    //Switch   switch_mainpump;

    TextView textview_rain_details;
    //TextView textview_rain_id;
    MaterialEditText textview_rain_API_key;
    MaterialEditText textview_rain_description;
    TextView textview_rain_value;
    MaterialEditText textview_rain_value2;
    MaterialBetterSpinner spinner_rain;
    ArrayAdapter spinner_rain_adapter;

    //Switch sw_valve11, sw_valve12, sw_valve13, sw_valve14, sw_valve15, sw_valve16;
    //Switch sw_valve21, sw_valve22, sw_valve23, sw_valve24, sw_valve25, sw_valve26;
    //Switch sw_valve1[] = new Switch[6];
    //Switch sw_valve2[] = new Switch[6];

    TextView textview_valve1_detail, textview_valve2_detail;
    MaterialBetterSpinner spinner_valve1;
    ArrayAdapter spinner_valve1_adapter;
    MaterialBetterSpinner spinner_valve2;
    ArrayAdapter spinner_valve2_adapter;
    //TextView textview_valve1_id, textview_valve2_id;
    MaterialEditText textview_valve1_description, textview_valve2_description;
    MaterialEditText textview_valve1_write_api_key, textview_valve2_write_api_key;

    ProgressBar progressBar_loading;

    Typeface app_typeFace;

    /**** soil ****/
    ArrayList<Soil> mSoil = new ArrayList<Soil>();
    ArrayList<String> mSoil_Type = new ArrayList<String>();
    /**************/

    /**** mainpump ****/
    //ArrayList<Mainpump> mMainpump = new ArrayList<Mainpump>();
    Map<String, Mainpump> mapOfMainpump = new HashMap<String, Mainpump>();
    ArrayList<String> mMainpump = new ArrayList<String>();
    /******************/

    /**** sensor ****/
    Map<String, Sensor> mapOfSensor = new HashMap<String, Sensor>();
    ArrayList<String> mrainSensor = new ArrayList<String>();
    /****************/

    /**** valve ****/
    Map<String, Valve> mapOfValve = new HashMap<String, Valve>();
    ArrayList<String> mValve1 = new ArrayList<String>();
    ArrayList<String> mValve2 = new ArrayList<String>();
    /****************/

    APIInterface apiInterface;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mFirebaseFarmRef;
    DatabaseReference mFirebaseSoilRef;
    DatabaseReference mFirebaseMainpumpRef;
    DatabaseReference mFirebaseSensorRef;
    DatabaseReference mFirebaseValveRef;

    void initiateDatabase(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseFarmRef = mFirebaseDatabase.getReference("farm").child(id);
        mFirebaseFarmRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                String _id;
                //Log.d("FarmChild",dataSnapshot.child("title").getValue(String.class));
                farm.id = id;
                //Log.d("FarmChild",ds.child("description").getValue(String.class));
                farm.need_watering = ds.child("need_watering").getValue(String.class).equals(true);
                farm.title = ds.child("title").getValue(String.class);
                farm.description = ds.child("description").getValue(String.class);
                farm.latitude = ds.child("latitude").getValue(String.class);
                farm.longitude = ds.child("longitude").getValue(String.class);
                farm.starting_date = ds.child("starting_date").getValue(String.class);
                farm.watering_scheme = Integer.parseInt(ds.child("watering_scheme").getValue(String.class));
                farm.sampling_time = Integer.parseInt(ds.child("sampling_time").getValue(String.class));

                farm.plant_id = ds.child("plant_id").getValue(String.class);
                farm.plant_title = ds.child("plant_title").getValue(String.class);
                farm.soil_id = ds.child("soil_id").getValue(String.class);
                farm.soil_title = ds.child("soil_title").getValue(String.class);

                farm.mainpump_id = ds.child("mainpump_id").getValue(String.class);
                farm.rain_sensor_id = ds.child("rain_sensor_id").getValue(String.class);
                farm.valve_1_id = ds.child("valve_1_id").getValue(String.class);
                farm.valve_2_id = ds.child("valve_2_id").getValue(String.class);

                farm.drip_flowrate = Float.parseFloat(ds.child("drip_flowrate").getValue(String.class));
                farm.drip_interval = Float.parseFloat(ds.child("drip_interval").getValue(String.class));
                farm.tape_interval = Float.parseFloat(ds.child("tape_interval").getValue(String.class));
                farm.total_drip_per_rai = ds.child("total_drip_per_rai").getValue(Integer.class);
                farm.total_flowrate_per_rai = ds.child("total_flowrate_per_rai").getValue(Float.class);

                initialFarmUI();
                /*for(DataSnapshot ds: dataSnapshot.getChildren()){
                    _id = ds.getKey();
                    if(_id.equals(id)){

                    }
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFirebaseSoilRef = mFirebaseDatabase.getReference("soil");
        mFirebaseSoilRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSoil.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Soil soil = new Soil();
                    soil.id = ds.getKey();
                    soil.title_eng = ds.child("title_eng").getValue(String.class);
                    soil.title_thai = ds.child("title_thai").getValue(String.class);
                    soil.water_allowance = Integer.valueOf(ds.child("water_allowance").getValue(String.class));
                    soil.water_holding_capacity = Float.valueOf(ds.child("water_holding_capacity").getValue(String.class));
                    mSoil.add(soil);
                }
                spinner_soil_type = findViewById(R.id.spinner_soilType);
                for(int i = 0; i<mSoil.size(); i++)
                {
                    mSoil_Type.add(mSoil.get(i).title_thai);
                }
                spinner_soil_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, mSoil_Type){
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/superspace_regular.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));
                        return v;
                    }


                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);

                        Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/superspace_regular.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));

                        //v.setBackgroundColor(Color.GREEN);

                        return v;
                    }
                };
                spinner_soil_type.setAdapter(spinner_soil_adapter);
                spinner_soil_type.setTypeface(app_typeFace);
                spinner_soil_type.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));
                spinner_soil_type.setText(farm.getSoilTitle());
                initialSoilUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mFirebaseMainpumpRef = mFirebaseDatabase.getReference("mainpump");
        mFirebaseMainpumpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //mMainpump.clear();
                mapOfMainpump.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
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

                // currently support only 1 mainpump
                final Mainpump m = mapOfMainpump.get(farm.mainpump_id);
                textview_mainpump_API_key.setText(m.write_api_key);
                textview_mainpump_description.setText(m.description);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                SimpleDateFormat ft =
                        new SimpleDateFormat ("E yyyy.MM.dd '@' HH:mm:ss");
                Date date = new Date();
                try {
                    date = formatter.parse(m.created_at.replaceAll("Z$", "+0000"));
                    //Log.d("CreatedAt", "Date ==> " + ft.format(date) + " with time zone ==> " + "time zone : " + TimeZone.getDefault().getID());
                }catch(ParseException e){
                    e.printStackTrace();
                }

                String [] mainpumpList = new String [] {"test", "test2", "test3"};


                for (Map.Entry<String, Mainpump> entry : mapOfMainpump.entrySet())
                {
                    //Log.d("ValveSpinner", "" + entry.getKey().toString());
                    mMainpump.add(entry.getKey() + " - " + entry.getValue().getDescription());
                    //mMainpump.add(entry.getKey());
                }

                //R.layout.simple_dropdown_item_1line

                spinner_mainpump_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line , mMainpump){
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);

                        Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/superspace_regular.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));
                        return v;
                    }


                    public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                        View v =super.getDropDownView(position, convertView, parent);

                        Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/superspace_regular.ttf");
                        ((TextView) v).setTypeface(externalFont);
                        ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));

                        //v.setBackgroundColor(Color.GREEN);

                        return v;
                    }
                };


                spinner_mainpump = (MaterialBetterSpinner) findViewById(R.id.spinner_mainpump);
                spinner_mainpump.setAdapter(spinner_mainpump_adapter);
                //spinner_mainpump.setSelection(spinner_mainpump_adapter.getPosition(farm.mainpump_id + " - " + mapOfMainpump.get(farm.mainpump_id).getDescription()));
                spinner_mainpump.setTypeface(app_typeFace);
                spinner_mainpump.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));
                spinner_mainpump.setText(mapOfMainpump.get(farm.mainpump_id).getID());

                spinner_mainpump.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("SpinnerMainpump", "Selected => " + position);
                    }
                });

                /*switch_mainpump.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar_loading.setVisibility(View.VISIBLE);
                        if(switch_mainpump.isChecked())
                        {
                            Call<String> call = apiInterface.turnonMainpump(m.id, m.write_api_key, "1");
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    progressBar_loading.setVisibility(View.GONE);
                                    //Log.d("Mainpump", response.code() + "");
                                    if(response.body().equals("2000")){
                                        enableAllValve();
                                        Snackbar.make(findViewById(android.R.id.content), "เปิดปั๊มน้ำหลักเรียบร้อย...", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }else if(response.body().equals("2001")){
                                        switch_mainpump.setChecked(false);
                                        Snackbar.make(findViewById(android.R.id.content), "[2001] เปิดปั๊มน้ำหลักไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }else{
                                        switch_mainpump.setChecked(false);
                                        Snackbar.make(findViewById(android.R.id.content), "[2002] เกิดข้อผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    switch_mainpump.setChecked(false);
                                    progressBar_loading.setVisibility(View.GONE);
                                    Log.d("Mainpump", t.getMessage());
                                    Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อปั๊มน้ำหลักผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                        else
                        {
                            Call<String> call = apiInterface.turnoffMainpump(m.id, m.write_api_key, "1");
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    progressBar_loading.setVisibility(View.GONE);
                                    if(response.body().equals("2200")){
                                        disableAllValve();
                                        //Log.d("Mainpump", response.code() + "");
                                        Snackbar.make(findViewById(android.R.id.content), "ปิดปั๊มน้ำหลักเรียบร้อย...", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }else if(response.body().equals("2201")){
                                        switch_mainpump.setChecked(true);
                                        Snackbar.make(findViewById(android.R.id.content), "ปิดปั๊มน้ำหลักไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }else{
                                        switch_mainpump.setChecked(true);
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อปั๊มน้ำหลักผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    progressBar_loading.setVisibility(View.GONE);
                                    switch_mainpump.setChecked(true);
                                    Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อปั๊มน้ำหลักผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            });
                        }
                    }
                });*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                    sensor.entry_id = ds.child("entry_id").getValue(Integer.class);
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

                initialrainUI();
                updaterainUI();

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
                    valve.entry_id = ds.child("entry_id").getValue(Integer.class);
                    valve.field1 = ds.child("field1").getValue(String.class);
                    valve.field2 = ds.child("field2").getValue(String.class);
                    valve.field3 = ds.child("field3").getValue(String.class);
                    valve.field4 = ds.child("field4").getValue(String.class);
                    valve.field5 = ds.child("field5").getValue(String.class);
                    valve.field6 = ds.child("field6").getValue(String.class);
                    valve.field7 = ds.child("field7").getValue(String.class);
                    valve.field8 = ds.child("field8").getValue(String.class);
                    valve.time_on1 = ds.child("time_on1").getValue(Integer.class);
                    valve.time_on2 = ds.child("time_on2").getValue(Integer.class);
                    valve.time_on3 = ds.child("time_on3").getValue(Integer.class);
                    valve.time_on4 = ds.child("time_on4").getValue(Integer.class);
                    valve.time_on5 = ds.child("time_on5").getValue(Integer.class);
                    valve.time_on6 = ds.child("time_on6").getValue(Integer.class);
                    valve.time_on7 = ds.child("time_on7").getValue(Integer.class);
                    valve.time_on8 = ds.child("time_on8").getValue(Integer.class);
                    valve.write_api_key = ds.child("write_api_key").getValue(String.class);
                    valve.data_created_at = ds.child("data_created_at").getValue(String.class);
                    valve.last_updated = ds.child("last_updated").getValue(String.class);
                    //mMainpump.add(mainpump);
                    mapOfValve.put(valve.id, valve);
                }

                initialValveUI();
                // currently support 2-zone valves
                final Valve v1 = mapOfValve.get(farm.valve_1_id);
                textview_valve1_description.setText(v1.description);
                textview_valve1_write_api_key.setText(v1.write_api_key);
                final Valve v2 = mapOfValve.get(farm.valve_2_id);
                textview_valve2_description.setText(v2.description);
                textview_valve2_write_api_key.setText(v2.write_api_key);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                SimpleDateFormat ft =
                        new SimpleDateFormat ("E yyyy.MM.dd '@' HH:mm:ss");
                Date date = new Date();
                try {
                    date = formatter.parse(v1.created_at.replaceAll("Z$", "+0000"));
                    //Log.d("CreatedAt", "Date ==> " + ft.format(date) + " with time zone ==> " + "time zone : " + TimeZone.getDefault().getID());
                }catch(ParseException e){
                    e.printStackTrace();
                }

                /* for(int i=0; i < sw_valve1.length; i++) {
                    sw_valve1[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Switch valve = (Switch) findViewById(v.getId());
                            final int valve_number = getValveNumber(valve.getText().toString());
                            progressBar_loading.setVisibility(View.VISIBLE);
                            if (sw_valve1[valve_number].isChecked()) {
                                Call<String> call = apiInterface.turnonValve(v1.id, v1.write_api_key, Integer.toString(valve_number+1));
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        progressBar_loading.setVisibility(View.GONE);
                                        if (response.body().equals("300")) {
                                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #" + (valve_number+1) + " เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else if (response.body().equals("301")) {
                                            sw_valve1[valve_number].setChecked(false);
                                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #" + (valve_number+1) + " ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            sw_valve1[valve_number].setChecked(false);
                                            Snackbar.make(findViewById(android.R.id.content), "เกิดข้อผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        sw_valve1[valve_number].setChecked(false);
                                        progressBar_loading.setVisibility(View.GONE);
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #" + (valve_number+1) + " ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            } else {
                                Call<String> call = apiInterface.turnoffValve(v1.id, v1.write_api_key, Integer.toString(valve_number+1));
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        progressBar_loading.setVisibility(View.GONE);
                                        if (response.body().equals("320")) {
                                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #" + (valve_number+1) + " เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else if (response.body().equals("321")) {
                                            sw_valve1[valve_number].setChecked(true);
                                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #" + (valve_number+1) + " ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            sw_valve1[valve_number].setChecked(true);
                                            Snackbar.make(findViewById(android.R.id.content), "เกิดข้อผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        sw_valve1[valve_number].setChecked(true);
                                        progressBar_loading.setVisibility(View.GONE);
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #" + (valve_number+1) + " ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }
                    });
                }

                for(int i=0; i < sw_valve2.length; i++) {
                    sw_valve2[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Switch valve = (Switch) findViewById(v.getId());
                            final int valve_number = getValveNumber(valve.getText().toString());
                            progressBar_loading.setVisibility(View.VISIBLE);
                            if (sw_valve2[valve_number-6].isChecked()) {
                                Call<String> call = apiInterface.turnonValve(v2.id, v2.write_api_key, Integer.toString(valve_number-5));
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        //Log.d("TurnOnValve", response.body() + "");
                                        progressBar_loading.setVisibility(View.GONE);
                                        if (response.body().equals("300")) {
                                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #" + (valve_number+1) + " เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else if (response.body().equals("301")) {
                                            sw_valve1[valve_number-6].setChecked(false);
                                            Snackbar.make(findViewById(android.R.id.content), "เปิดวาล์ว #" + (valve_number+1) + " ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            sw_valve1[valve_number-6].setChecked(false);
                                            Snackbar.make(findViewById(android.R.id.content), "เกิดข้อผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        sw_valve1[valve_number-6].setChecked(false);
                                        progressBar_loading.setVisibility(View.GONE);
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #" + (valve_number+1) + " ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            } else {
                                Call<String> call = apiInterface.turnoffValve(v2.id, v2.write_api_key, Integer.toString(valve_number-5));
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        progressBar_loading.setVisibility(View.GONE);
                                        if (response.body().equals("320")) {
                                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #" + (valve_number+1) + " เรียบร้อย...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else if (response.body().equals("321")) {
                                            sw_valve1[valve_number-6].setChecked(true);
                                            Snackbar.make(findViewById(android.R.id.content), "ปิดวาล์ว #" + (valve_number+1) + " ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        } else {
                                            sw_valve1[valve_number-6].setChecked(true);
                                            Snackbar.make(findViewById(android.R.id.content), "เกิดข้อผิดพลาด!!!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        sw_valve1[valve_number-6].setChecked(true);
                                        progressBar_loading.setVisibility(View.GONE);
                                        Snackbar.make(findViewById(android.R.id.content), "เชื่อมต่อวาล์ว #" + (valve_number+1) + " ไม่สำเร็จ!!!", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                });
                            }
                        }
                    });
                }

                Mainpump m = mapOfMainpump.get(farm.mainpump_id);
                switch(m.field1)
                {
                    case "-1":  switch_mainpump.setChecked(false);
                        switch_mainpump.setEnabled(true);
                        switch_mainpump.setText("สถานะ (" + ft.format(date) + ")");
                        disableAllValve();
                        break;
                    case "1" :  switch_mainpump.setChecked(true);
                        switch_mainpump.setEnabled(true);
                        switch_mainpump.setText("สถานะ (" + ft.format(date) + ")");
                        enableAllValve();
                        break;
                    default  :  switch_mainpump.setEnabled(false);
                        switch_mainpump.setText("N/A");
                        break;
                } */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    int getValveNumber(String valveText)
    {
        int vn = 0;
        switch(valveText){
            case "วาล์ว #1":
               vn = 0;
               break;
            case "วาล์ว #2":
                vn = 1;
                break;
            case "วาล์ว #3":
                vn = 2;
                break;
            case "วาล์ว #4":
                vn = 3;
                break;
            case "วาล์ว #5":
                vn = 4;
                break;
            case "วาล์ว #6":
                vn = 5;
                break;
            case "วาล์ว #7":
                vn = 6;
                break;
            case "วาล์ว #8":
                vn = 7;
                break;
            case "วาล์ว #9":
                vn = 8;
                break;
            case "วาล์ว #10":
                vn = 9;
                break;
            case "วาล์ว #11":
                vn = 10;
                break;
            case "วาล์ว #12":
                vn = 11;
                break;
        }
        return vn;
    }

    void initialrainUI()
    {
        // currently support only 1 sensor


        spinner_rain = findViewById(R.id.spinner_rain);
        spinner_rain.setTypeface(app_typeFace);
        spinner_rain.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));

        for (Map.Entry<String, Sensor> entry : mapOfSensor.entrySet())
        {
            //Log.d("ValveSpinner", "" + entry.getKey().toString());
            //mrainSensor.add(entry.getKey() + " - " + entry.getValue().getDescription());
            mrainSensor.add(entry.getKey());
        }

        spinner_rain_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, mrainSensor){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/superspace_regular.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));

                return v;
            }


            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/superspace_regular.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));

                //v.setBackgroundColor(Color.GREEN);

                return v;
            }
        };
        spinner_rain.setAdapter(spinner_rain_adapter);
        spinner_rain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("SpinnerValve", "get => " + parent.getSelectedItem().toString().substring(0, 6));
                if(++rain_check > 1) {
                    Call<String> call = apiInterface.setRainChannel(farm.getID(), parent.getSelectedItem().toString().substring(0, 6));
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            //progressBar_loading.setVisibility(View.GONE);
                            //Log.d("Mainpump", response.code() + "");
                            //Log.d("SamplingTime", "code = " + response.code() + ", message = " + response.body());
                            //Log.d("SamplingTime", "Oh yeah! => ");
                            if (response.body().equals("5600")) {
                                //enableAllValve();
                                //turnonPumpUI();
                                updaterainUI();
                                Snackbar.make(findViewById(android.R.id.content), "เปลี่ยนหมายเลขเซ็นเซอร์น้ำฝนเรียบร้อย", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            } else {
                                //switch_mainpump.setChecked(false);
                                Snackbar.make(findViewById(android.R.id.content), "เปลี่ยนหมายเลขเซ็นเซอร์น้ำฝนไม่สำเร็จ!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            //progressBar_loading.setVisibility(View.GONE);
                            //Log.d("SamplingTime", t.getMessage());
                            Snackbar.make(findViewById(android.R.id.content), t.getMessage(), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void updaterainUI()
    {
        Sensor m = mapOfSensor.get(farm.rain_sensor_id);

        spinner_rain.setText(farm.rain_sensor_id);
        textview_rain_description.setText(m.getDescription());
        textview_rain_API_key.setText(m.getReadAPIKey());

        textview_rain_API_key.setText(m.read_api_key);
        textview_rain_description.setText(m.description);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SimpleDateFormat ft =
                new SimpleDateFormat ("E yyyy.MM.dd '@' HH:mm:ss");
        Date date = new Date();
        try {
            date = formatter.parse(m.created_at.replaceAll("Z$", "+0000"));
            //Log.d("CreatedAt", "Date ==> " + ft.format(date) + " with time zone ==> " + "time zone : " + TimeZone.getDefault().getID());
        }catch(ParseException e){
            e.printStackTrace();
        }

        textview_rain_value.setText("ปริมาณน้ำฝนในแปลงล่าสุด " + m.field1 + " มม.");
        textview_rain_value2.setText(m.field1 + " มม. (" + ft.format(date) + ")");
    }

    void initialFarmData()
    {
        //String id;
        id = getIntent().getExtras().getString("id");
    }

    void initialSoilUI(){

    }

    void initialFarmUI(){
        setTitle(farm.title);

        String file_path = getPreference(context, "farmImagePath");
        Uri farmImageURI = Uri.fromFile(new File(file_path));
        Glide.with(this).load(farmImageURI).into(imageview_farm_picture);

        textview_title = (TextView) findViewById(R.id.farm_title);
        textview_title.setText(farm.title);
        textview_description = (TextView) findViewById(R.id.farm_description);
        textview_description.setText(farm.description);

        edittext_watering_scheme = findViewById(R.id.textview_waterScheme);
        edittext_watering_scheme.setTypeface(app_typeFace);
        String [] watering_scheme_string = getResources().getStringArray(R.array.watering_scheme_array);
        switch(farm.watering_scheme)
        {
            case 1:
                edittext_watering_scheme.setText(watering_scheme_string[0]);
                break;
            case 2:
                edittext_watering_scheme.setText(watering_scheme_string[1]);
                break;
        }

        textview_location = findViewById(R.id.textview_farmLocation);
        textview_location.setTypeface(app_typeFace);
        textview_location.setText(farm.latitude.substring(0,12) + " , " + farm.longitude.substring(0,12));


        edittext_drip_flowrate = findViewById(R.id.edittext_drip_flowrate);
        edittext_drip_flowrate.setTypeface(app_typeFace);
        edittext_drip_flowrate.setText(Float.toString(farm.getDripFlowrate()));
        edittext_drip_interval = findViewById(R.id.edittext_drip_interval);
        edittext_drip_interval.setTypeface(app_typeFace);
        edittext_drip_interval.setText(Float.toString(farm.getDripInterval()));
        edittext_tape_interval = findViewById(R.id.edittext_tape_interval);
        edittext_tape_interval.setTypeface(app_typeFace);
        edittext_tape_interval.setText(Float.toString(farm.getTapeInterval()));
        edittext_total_drip_per_rai = findViewById(R.id.edittext_total_drip_per_rai);
        edittext_total_drip_per_rai.setTypeface(app_typeFace);
        edittext_total_drip_per_rai.setText(Float.toString(farm.getTotalDripPerRai()));
        edittext_total_flowrate_per_rai = findViewById(R.id.edittext_total_flowrate_per_rai);
        edittext_total_flowrate_per_rai.setTypeface(app_typeFace);
        edittext_total_flowrate_per_rai.setText(Float.toString(farm.getTotalFlowratePerRai()));

        textview_drip_details = findViewById(R.id.textview_drip_system_details);
        textview_drip_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow tableRow_drip = findViewById(R.id.tableRow_drip_flowrate);
                if(tableRow_drip.getVisibility() == View.GONE)
                {
                    tableRow_drip.setVisibility(View.VISIBLE);
                    tableRow_drip = findViewById(R.id.tableRow_drip_interval);
                    tableRow_drip.setVisibility(View.VISIBLE);
                    tableRow_drip = findViewById(R.id.tableRow_tape_interval);
                    tableRow_drip.setVisibility(View.VISIBLE);
                    tableRow_drip = findViewById(R.id.tableRow_total_drip_per_rai);
                    tableRow_drip.setVisibility(View.VISIBLE);
                    tableRow_drip = findViewById(R.id.tableRow_total_flowrate_per_rai);
                    tableRow_drip.setVisibility(View.VISIBLE);
                    textview_drip_details.setText(R.string.close);
                }
                else
                {
                    tableRow_drip.setVisibility(View.GONE);
                    tableRow_drip = findViewById(R.id.tableRow_drip_interval);
                    tableRow_drip.setVisibility(View.GONE);
                    tableRow_drip = findViewById(R.id.tableRow_tape_interval);
                    tableRow_drip.setVisibility(View.GONE);
                    tableRow_drip = findViewById(R.id.tableRow_total_drip_per_rai);
                    tableRow_drip.setVisibility(View.GONE);
                    tableRow_drip = findViewById(R.id.tableRow_total_flowrate_per_rai);
                    tableRow_drip.setVisibility(View.GONE);
                    textview_drip_details.setText(R.string.details);
                }
            }
        });

        edittext_sampling_time = findViewById(R.id.edittext_farm_sampling_time);
        edittext_sampling_time.setTypeface(app_typeFace);
        edittext_sampling_time.setText(farm.getSamplingTime()+"");


        //textview_location.set(farm.latitude.substring(0, 6) + " , " + farm.longitude.substring(0, 6));


        //textview_mainpump_id = findViewById(R.id.edittext_mainPump_ChannelId);
        //textview_mainpump_id.setText(farm.mainpump_id);
        textview_mainpump_API_key = findViewById(R.id.edittext_mainPump_APIKey);
        textview_mainpump_API_key.setTypeface(app_typeFace);
        textview_mainpump_description = findViewById(R.id.edittext_mainPump_description);
        textview_mainpump_description.setTypeface(app_typeFace);
        //switch_mainpump = (Switch) findViewById(R.id.switch_mainpump);
        //switch_mainpump.setTypeface(ResourcesCompat.getFont(context, R.font.superspace_regular));

        textview_valve1_write_api_key = findViewById(R.id.edittext_valve1_API_key);
        textview_valve1_write_api_key.setTypeface(app_typeFace);
        textview_valve1_description = findViewById(R.id.edittext_valve1_description);
        textview_valve1_description.setTypeface(app_typeFace);

        //edittext_sampling_time = findViewById(R.id.edittext_farm_sampling_time);
        edittext_sampling_time.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Log.d("SamplingTime", event.toString());
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    EditText sedit_text = findViewById(v.getId());
                    Call<String> call = apiInterface.setFarmSamplingTime(farm.getID(), sedit_text.getText().toString());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            //progressBar_loading.setVisibility(View.GONE);
                            //Log.d("Mainpump", response.code() + "");
                            //Log.d("SamplingTime", "code = " + response.code() + ", message = " + response.body());
                            //Log.d("SamplingTime", "Oh yeah! => ");
                            if(response.body().equals("5100")){
                                //enableAllValve();
                                //turnonPumpUI();
                                Snackbar.make(findViewById(android.R.id.content), "ตั้งค่าเวลาตรวจสอบเซ็นเซอร์เรียบร้อย", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }else {
                                //switch_mainpump.setChecked(false);
                                Snackbar.make(findViewById(android.R.id.content), "ตั้งค่าเวลาตรวจสอบเซ็นเซอร์ไม่สำเร็จ!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            //progressBar_loading.setVisibility(View.GONE);
                            //Log.d("SamplingTime", t.getMessage());
                            Snackbar.make(findViewById(android.R.id.content), t.getMessage(), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    return true;
                }
                return false;
            }
        });

        textview_mainpump_details = (TextView) findViewById(R.id.main_pump_details);
        textview_mainpump_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow tableRow_mainPump = findViewById(R.id.tableRow_mainPump1);
                if(tableRow_mainPump.getVisibility() == View.GONE)
                {
                    tableRow_mainPump.setVisibility(View.VISIBLE);
                    tableRow_mainPump = findViewById(R.id.tableRow_mainPump_description);
                    tableRow_mainPump.setVisibility(View.VISIBLE);
                    tableRow_mainPump = findViewById(R.id.tableRow_mainPump2);
                    tableRow_mainPump.setVisibility(View.VISIBLE);
                    //tableRow_mainPump = findViewById(R.id.tableRow_mainpump_switch);
                    //tableRow_mainPump.setVisibility(View.VISIBLE);
                    textview_mainpump_details.setText(R.string.close);
                }
                else
                {
                    tableRow_mainPump.setVisibility(View.GONE);
                    tableRow_mainPump = findViewById(R.id.tableRow_mainPump_description);
                    tableRow_mainPump.setVisibility(View.GONE);
                    tableRow_mainPump = findViewById(R.id.tableRow_mainPump2);
                    tableRow_mainPump.setVisibility(View.GONE);
                    //tableRow_mainPump = findViewById(R.id.tableRow_mainpump_switch);
                    //tableRow_mainPump.setVisibility(View.GONE);
                    textview_mainpump_details.setText(R.string.details);
                }
            }
        });


        /*textview_rain_id = (TextView) findViewById(R.id.edittext_rain_sensor_ChannelId);
        textview_rain_id.setText(farm.rain_sensor_id);*/
        //spinner_soil_type.setTypeface(app_typeFace);
        //spinner_soil_type.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));

        textview_rain_API_key = findViewById(R.id.edittext_rain_sensor_APIKey);
        textview_mainpump_API_key.setTypeface(app_typeFace);

        textview_rain_description = findViewById(R.id.edittext_rain_sensor_description);
        textview_rain_description.setTypeface(app_typeFace);

        textview_rain_value = findViewById(R.id.textview_rain_sensor_value);

        textview_rain_value2 = findViewById(R.id.edittext_rain_sensor_value);
        textview_rain_value2.setTypeface(app_typeFace);

        textview_rain_details = (TextView) findViewById(R.id.textview_rain_sensor_details);
        textview_rain_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow tableRow_rainSensor = findViewById(R.id.tableRow_sensor_description);
                if(tableRow_rainSensor.getVisibility() == View.GONE)
                {
                    tableRow_rainSensor.setVisibility(View.VISIBLE);
                    tableRow_rainSensor = findViewById(R.id.tableRow_rainSensor1);
                    tableRow_rainSensor.setVisibility(View.VISIBLE);
                    tableRow_rainSensor = findViewById(R.id.tableRow_rainSensor2);
                    tableRow_rainSensor.setVisibility(View.VISIBLE);
                    tableRow_rainSensor = findViewById(R.id.tableRow_rainSensor3);
                    tableRow_rainSensor.setVisibility(View.VISIBLE);
                    textview_rain_details.setText(R.string.close);
                }
                else
                {
                    tableRow_rainSensor.setVisibility(View.GONE);
                    tableRow_rainSensor = findViewById(R.id.tableRow_rainSensor1);
                    tableRow_rainSensor.setVisibility(View.GONE);
                    tableRow_rainSensor = findViewById(R.id.tableRow_rainSensor2);
                    tableRow_rainSensor.setVisibility(View.GONE);
                    tableRow_rainSensor = findViewById(R.id.tableRow_rainSensor3);
                    tableRow_rainSensor.setVisibility(View.GONE);
                    textview_rain_details.setText(R.string.details);
                }
            }
        });

        /*textview_valve2_id = (TextView) findViewById(R.id.edittext_valve2_ChannelId);
        textview_valve2_id.setText(farm.valve_2_id);*/
        textview_valve2_write_api_key = findViewById(R.id.edittext_valve2_API_key);
        textview_valve2_write_api_key.setTypeface(app_typeFace);
        textview_valve2_description = findViewById(R.id.edittext_valve2_description);
        textview_valve2_description.setTypeface(app_typeFace);

        /*
        sw_valve1[0] = (Switch) findViewById(R.id.switch11);
        sw_valve1[0].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve1[1] = (Switch) findViewById(R.id.switch12);
        sw_valve1[1].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve1[2] = (Switch) findViewById(R.id.switch13);
        sw_valve1[2].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve1[3] = (Switch) findViewById(R.id.switch14);
        sw_valve1[3].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve1[4] = (Switch) findViewById(R.id.switch15);
        sw_valve1[4].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve1[5] = (Switch) findViewById(R.id.switch16);
        sw_valve1[5].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));

        sw_valve2[0] = (Switch) findViewById(R.id.switch21);
        sw_valve2[0].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve2[1] = (Switch) findViewById(R.id.switch22);
        sw_valve2[1].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve2[2] = (Switch) findViewById(R.id.switch23);
        sw_valve2[2].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve2[3] = (Switch) findViewById(R.id.switch24);
        sw_valve2[3].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve2[4] = (Switch) findViewById(R.id.switch25);
        sw_valve2[4].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular));
        sw_valve2[5] = (Switch) findViewById(R.id.switch26);
        sw_valve2[5].setTypeface(ResourcesCompat.getFont(this, R.font.superspace_regular)); */

        textview_valve1_detail = (TextView) findViewById(R.id.textview_valve1_details);
        textview_valve1_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow tableRow_switch = findViewById(R.id.tableRow_valve1_description);
                if(tableRow_switch.getVisibility() == View.GONE)
                {
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = findViewById(R.id.tableRow_valve1_channelID);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_valve1_API_key);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    /*tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch11);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch12);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch13);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch14);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch15);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch16);
                    tableRow_switch.setVisibility(View.VISIBLE);*/

                    tableRow_switch = findViewById(R.id.tableRow_valve2_description);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = findViewById(R.id.tableRow_valve2_channelID);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_valve2_API_key);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    /*tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch21);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch22);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch23);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch24);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch25);
                    tableRow_switch.setVisibility(View.VISIBLE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch26);
                    tableRow_switch.setVisibility(View.VISIBLE);*/
                    textview_valve1_detail.setText(R.string.close);
                }
                else
                {
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = findViewById(R.id.tableRow_valve1_channelID);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_valve1_API_key);
                    tableRow_switch.setVisibility(View.GONE);
                    /*tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch11);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch12);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch13);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch14);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch15);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch16);
                    tableRow_switch.setVisibility(View.GONE);*/
                    tableRow_switch = findViewById(R.id.tableRow_valve2_description);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = findViewById(R.id.tableRow_valve2_channelID);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_valve2_API_key);
                    tableRow_switch.setVisibility(View.GONE);
                    /*tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch21);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch22);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch23);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch24);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch25);
                    tableRow_switch.setVisibility(View.GONE);
                    tableRow_switch = (TableRow) findViewById(R.id.tableRow_switch26);
                    tableRow_switch.setVisibility(View.GONE);*/
                    textview_valve1_detail.setText(R.string.details);
                }
            }
        });

        textview_map = (TextView) findViewById(R.id.textview_map);
        textview_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(FarmETpDetailsActivity.this, MapActivity.class);
                intent.putExtra("latitude", farm.getLatitude());
                intent.putExtra("longitude", farm.getLongitude());
                startActivityForResult(intent, GET_FARM_LOCATION_REQUEST);
            }
        });

        if(farm.need_watering){
            imageview_animated_drip.setVisibility(View.VISIBLE);
        }else
        {
            imageview_animated_drip.setVisibility(View.INVISIBLE);
        }

    }

    void initialValveUI()
    {

        /*textview_valve1_id = (TextView) findViewById(R.id.edittext_valve1_ChannelId);
        textview_valve1_id.setText(farm.valve_1_id);*/
        spinner_valve1 = findViewById(R.id.spinner_valve1);
        /*for(int i = 0; i < mapOfValve.size(); i++)
        {
            mValve1.add(mapOfValve.get(i).getID());
        }*/
        for (Map.Entry<String, Valve> entry : mapOfValve.entrySet())
        {
            //Log.d("ValveSpinner", "" + entry.getKey().toString());
            mValve1.add(entry.getKey() + " - " + entry.getValue().getDescription());
        }

        spinner_valve1_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, mValve1){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/superspace_regular.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_font_size));

                return v;
            }


            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/superspace_regular.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_font_size));

                //v.setBackgroundColor(Color.GREEN);

                return v;
            }
        };
        spinner_valve1.setAdapter(spinner_valve1_adapter);
        spinner_valve1.setText(farm.valve_1_id);
        spinner_valve1.setTypeface(app_typeFace);
        spinner_valve1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));
        spinner_valve1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("SpinnerValve", "get => " + parent.getSelectedItem().toString().substring(0, 6));
                if(++valve1_check > 1) {
                    Call<String> call = apiInterface.setValve1Channel(farm.getID(), parent.getSelectedItem().toString().substring(0, 6));
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            //progressBar_loading.setVisibility(View.GONE);
                            //Log.d("Mainpump", response.code() + "");
                            //Log.d("SamplingTime", "code = " + response.code() + ", message = " + response.body());
                            //Log.d("SamplingTime", "Oh yeah! => ");
                            if (response.body().equals("5200")) {
                                //enableAllValve();
                                //turnonPumpUI();
                                textview_valve1_description.setText(mapOfValve.get(farm.valve_1_id).description);
                                textview_valve1_write_api_key.setText(mapOfValve.get(farm.valve_1_id).write_api_key);
                                Snackbar.make(findViewById(android.R.id.content), "เปลี่ยนหมายเลขวาล์ว #1 เรียบร้อย", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                valve1_check = 0;
                            } else {
                                //switch_mainpump.setChecked(false);
                                Snackbar.make(findViewById(android.R.id.content), "เปลี่ยนหมายเลขวาล์ว #1 ไม่สำเร็จ!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            //progressBar_loading.setVisibility(View.GONE);
                            //Log.d("SamplingTime", t.getMessage());
                            Snackbar.make(findViewById(android.R.id.content), t.getMessage(), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_valve2 = findViewById(R.id.spinner_valve2);
        for (Map.Entry<String, Valve> entry : mapOfValve.entrySet())
        {
            mValve2.add(entry.getKey() + " - " + entry.getValue().getDescription());
        }

        spinner_valve2_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, mValve2){
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/superspace_regular.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_font_size));

                return v;
            }


            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);

                Typeface externalFont=Typeface.createFromAsset(getAssets(), "font/superspace_regular.ttf");
                ((TextView) v).setTypeface(externalFont);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_font_size));

                //v.setBackgroundColor(Color.GREEN);

                return v;
            }
        };
        spinner_valve2.setAdapter(spinner_valve2_adapter);
        spinner_valve2.setTypeface(app_typeFace);
        spinner_valve2.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.normal_font_size));
        spinner_valve2.setText(farm.valve_2_id);
        spinner_valve2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Log.d("SpinnerValve", "get => " + parent.getSelectedItem().toString().substring(0, 6));
                if(++valve2_check > 1) {
                    Call<String> call = apiInterface.setValve2Channel(farm.getID(), parent.getSelectedItem().toString().substring(0, 6));
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            //progressBar_loading.setVisibility(View.GONE);
                            //Log.d("Mainpump", response.code() + "");
                            //Log.d("SamplingTime", "code = " + response.code() + ", message = " + response.body());
                            //Log.d("SamplingTime", "Oh yeah! => ");
                            if (response.body().equals("5300")) {
                                //enableAllValve();
                                //turnonPumpUI();
                                textview_valve2_description.setText(mapOfValve.get(farm.valve_2_id).description);
                                textview_valve2_write_api_key.setText(mapOfValve.get(farm.valve_2_id).write_api_key);
                                Snackbar.make(findViewById(android.R.id.content), "เปลี่ยนหมายเลขวาล์ว #2 เรียบร้อย", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                valve2_check = 0;
                            } else {
                                //switch_mainpump.setChecked(false);
                                Snackbar.make(findViewById(android.R.id.content), "เปลี่ยนหมายเลขวาล์ว #2 ไม่สำเร็จ!", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            //progressBar_loading.setVisibility(View.GONE);
                            //Log.d("SamplingTime", t.getMessage());
                            Snackbar.make(findViewById(android.R.id.content), t.getMessage(), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_FARM_LOCATION_REQUEST && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                farm.latitude = extras.getString("latitude");
                farm.longitude = extras.getString("longitude");
            }
        }else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            String path = saveImage(photo);
            setPreference(context, path, "farmImagePath");
            imageview_farm_picture.setImageBitmap(photo);
        }else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK){
            if (data != null) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        String path = saveImage(bitmap);
                        setPreference(context, path, "farmImagePath");
                        imageview_farm_picture.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //setPreference(context, picturePath, "imagePath");
            }
        }
    }


    boolean setPreference(Context c, String value, String key) {
        String prefName = farm.getID() + "_picture";
        SharedPreferences settings = getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    String getPreference(Context c, String key) {
        String prefName = farm.getID() + "_picture";
        SharedPreferences settings = getSharedPreferences(prefName, 0);
        String value = settings.getString(key, "");
        return value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_etp_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // read setting
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String textServerAddress = sharedPref.getString(SettingActivity.KEY_PREF_SERVER_ADDRESS, "DEFAULT");

        app_typeFace = Typeface.createFromAsset(getAssets(),"font/superspace_regular.ttf");

        initialFarmData();
        initiateDatabase();

        apiInterface = APIClient.getClient(textServerAddress).create(APIInterface.class);

        progressBar_loading = (ProgressBar) findViewById(R.id.progressBar_loading);

        imageview_animated_drip = (ImageView) findViewById(R.id.animated_drip);

        imageview_farm_picture = findViewById(R.id.farm_picture);
        imageview_camera = findViewById(R.id.imageview_camera);
        imageview_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        Glide.with(context)
                .asGif()
                .load(R.drawable.animated_alarm)
                .into(imageview_animated_drip);
        PackageManager m = getPackageManager();
        app_dir = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(app_dir, 0);
            app_dir = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("yourtag", "Error Package name not found ", e);
        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("เลือกแหล่งรูปภาพ");
        String[] pictureDialogItems = {
                "เลือกรูปจาก gallery ในเครื่อง",
                "ถ่ายภาพใหม่จากกล้อง" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    private void takePhotoFromCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    GET_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        //File imDir = new File(Environment.getExternalStorageDirectory() + app_dir);
        File imDir = new File(app_dir + "/farm_pictures");
        // have the object build the directory structure, if needed.
        if (!imDir.exists()) {
            imDir.mkdirs();
        }

        try {
            File f = new File(imDir, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(findViewById(android.R.id.content), "Camera permission granted...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent cameraIntent = new
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Camera permission denied...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
        return super.onOptionsItemSelected(item);
    }
}
