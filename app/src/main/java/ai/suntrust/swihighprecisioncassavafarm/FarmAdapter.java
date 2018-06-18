package ai.suntrust.swihighprecisioncassavafarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FarmAdapter extends RecyclerView.Adapter<FarmAdapter.MyViewHolder> {

    private ArrayList<Farm> dataFarm;
    private ArrayList<Soil> dataSoil;

    private Activity activity;
    private Context mContext;

    private static final int FARM_DETAIL_REQUEST = 200;
    private static final int FARM_ETP_DETAIL_REQUEST = 201;
    private static final int FARM_CONTROLLER_REQUEST = 300;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textview_title;
        TextView textview_description;
        TextView textview_latitude;
        TextView textview_longitude;
        TextView textview_farm_watering_scheme;
        //TextView textview_details;
        TextView textview_humidity_sensor_value;
        LinearLayout layoutSensorWateringScheme;
        LinearLayout layoutETpWateringScheme;
        TextView textview_controller;
        ImageView imageview_farm_configuration;
        ImageView imageview_farm_picture;

        int watering_scheme;
        String[] watering_scheme_array;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textview_title = (TextView)itemView.findViewById(R.id.farm_title);
            this.textview_description = (TextView) itemView.findViewById(R.id.farm_description);
            //this.textview_latitude = (TextView) itemView.findViewById(R.id.farm_latitude);
            //this.textview_longitude = (TextView) itemView.findViewById(R.id.farm_longitude);
            //this.textview_farm_watering_scheme = (TextView) itemView.findViewById(R.id.farm_watering_scheme);
            //this.textview_humidity_sensor_value = (TextView) itemView.findViewById(R.id.textview_humidity_sensor_value);

            //this.layoutSensorWateringScheme = (LinearLayout)itemView.findViewById(R.id.layout_sensor_watering_scheme);
            //this.layoutETpWateringScheme = (LinearLayout)itemView.findViewById(R.id.layout_etp_watering_scheme);

            //this.watering_scheme_array = itemView.getResources().getStringArray(R.array.watering_scheme_array);
            //this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
            //this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);

            //this.textview_details = (TextView) itemView.findViewById(R.id.farm_details);
            this.imageview_farm_configuration = itemView.findViewById(R.id.imageview_farm_configuration);
            this.textview_controller = (TextView) itemView.findViewById(R.id.farm_controller);
            this.imageview_farm_picture = itemView.findViewById(R.id.farm_picture);
        }
    }

    public FarmAdapter(ArrayList<Farm> farm, Activity activity) {
        this.activity = activity;
        this.dataFarm = farm;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.farm_cardview_item, parent, false);

        view.setOnClickListener(MainActivity.farmOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textview_title = holder.textview_title;
        TextView textview_description = holder.textview_description;
        //TextView textview_latitude = holder.textview_latitude;
        //TextView textview_longitude = holder.textview_longitude;
        //TextView textview_details = holder.textview_details;
        //TextView textview_humidity_sensor_value = holder.textview_humidity_sensor_value;
        //ImageView imageView = holder.imageViewIcon;

        textview_title.setText(dataFarm.get(listPosition).getTitle());
        textview_description.setText(dataFarm.get(listPosition).getDescription());
        //textview_latitude.setText(dataFarm.get(listPosition).getLatitude());
        //textview_longitude.setText(dataFarm.get(listPosition).getLongitude());
        //textview_humidity_sensor_value.setText(dataFarm.get(listPosition).getHumidityCriticalPoint());
        //imageView.setImageResource(dataFarm.get(listPosition).getImage());
        if(dataFarm.get(listPosition).getImagePath()==null || dataFarm.get(listPosition).getImagePath().isEmpty())
            Glide.with(activity).load(R.drawable.cassava_field).into(holder.imageview_farm_picture);
        else
            Glide.with(activity).load(dataFarm.get(listPosition).getImagePath()).into(holder.imageview_farm_picture);


        holder.watering_scheme = dataFarm.get(listPosition).getWateringScheme();
        /*switch(holder.watering_scheme)
        {
            case 1:
                holder.layoutSensorWateringScheme.setVisibility(View.VISIBLE);
                holder.layoutETpWateringScheme.setVisibility(View.GONE);
                holder.textview_farm_watering_scheme.setText(holder.watering_scheme_array[0]);
                break;
            case 2:
                holder.layoutSensorWateringScheme.setVisibility(View.GONE);
                holder.layoutETpWateringScheme.setVisibility(View.VISIBLE);
                holder.textview_farm_watering_scheme.setText(holder.watering_scheme_array[1]);
                break;
        }*/
        ImageView imageview_configuration = holder.imageview_farm_configuration;
        imageview_configuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("FarmDetails","Clicked ==> " + listPosition);
                Intent intent;
                switch(holder.watering_scheme)
                {
                    case 1:
                        //Log.d("FarmAdapter","Starting FarmDetailsActivity!!!!!!!!");
                        intent = new Intent(activity, FarmDetailsActivity.class);
                        intent.putExtra("id", dataFarm.get(listPosition).getID());
                        /*intent.putExtra("title", dataFarm.get(listPosition).getTitle());
                        intent.putExtra("description", dataFarm.get(listPosition).getDescription());
                        intent.putExtra("latitude", dataFarm.get(listPosition).getLatitude());
                        intent.putExtra("longitude", dataFarm.get(listPosition).getLongitude());
                        intent.putExtra("mainpump_id", dataFarm.get(listPosition).getMainpumpID());
                        intent.putExtra("humidity_id", dataFarm.get(listPosition).getHumidityID());
                        intent.putExtra("humidity_critical_point", dataFarm.get(listPosition).getHumidityCriticalPoint());
                        intent.putExtra("valve_1_id", dataFarm.get(listPosition).getValve1ID());
                        intent.putExtra("valve_2_id", dataFarm.get(listPosition).getValve2ID());
                        //intent.putExtra("zone_number", dataFarm.get(listPosition).getZoneNumber());
                        intent.putExtra("watering_scheme", dataFarm.get(listPosition).getWateringScheme());
                        intent.putExtra("soil_title", dataFarm.get(listPosition).getSoilTitle());
                        intent.putExtra("plant_title", dataFarm.get(listPosition).getPlantTitle());
                        intent.putExtra("starting_date", dataFarm.get(listPosition).getStartingDate());
                        intent.putExtra("created_at", dataFarm.get(listPosition).getCreatedAt()); */
                        activity.startActivityForResult(intent, FARM_DETAIL_REQUEST);
                        break;
                    case 2:
                        //Log.d("FarmAdapter","Starting FarmDetailsActivity!!!!!!!!");
                        intent = new Intent(activity, FarmETpDetailsActivity.class);
                        intent.putExtra("id", dataFarm.get(listPosition).getID());
                        /*intent.putExtra("title", dataFarm.get(listPosition).getTitle());
                        intent.putExtra("description", dataFarm.get(listPosition).getDescription());
                        intent.putExtra("latitude", dataFarm.get(listPosition).getLatitude());
                        intent.putExtra("longitude", dataFarm.get(listPosition).getLongitude());
                        intent.putExtra("mainpump_id", dataFarm.get(listPosition).getMainpumpID());
                        intent.putExtra("humidity_id", dataFarm.get(listPosition).getHumidityID());
                        intent.putExtra("humidity_critical_point", dataFarm.get(listPosition).getHumidityCriticalPoint());
                        intent.putExtra("valve_1_id", dataFarm.get(listPosition).getValve1ID());
                        intent.putExtra("valve_2_id", dataFarm.get(listPosition).getValve2ID());
                        //intent.putExtra("zone_number", dataFarm.get(listPosition).getZoneNumber());
                        intent.putExtra("watering_scheme", dataFarm.get(listPosition).getWateringScheme());
                        intent.putExtra("soil_title", dataFarm.get(listPosition).getSoilTitle());
                        intent.putExtra("plant_title", dataFarm.get(listPosition).getPlantTitle());
                        intent.putExtra("starting_date", dataFarm.get(listPosition).getStartingDate());
                        intent.putExtra("created_at", dataFarm.get(listPosition).getCreatedAt()); */
                        activity.startActivityForResult(intent, FARM_ETP_DETAIL_REQUEST);
                        break;
                }

            }
        });
        /*TextView textview_details = holder.textview_details;
        textview_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        TextView textview_controller = holder.textview_controller;
        textview_controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch(holder.watering_scheme) {
                    case 1:
                        intent = new Intent(activity, FarmControlCenterActivity.class);
                        //Log.d("FarmAdapter","Starting FarmDetailsActivity!!!!!!!!");
                        intent.putExtra("id", dataFarm.get(listPosition).getID());
                        intent.putExtra("mainpump_id", dataFarm.get(listPosition).getMainpumpID());
                        intent.putExtra("humidity_id", dataFarm.get(listPosition).getHumidityID());
                        intent.putExtra("valve_1_id", dataFarm.get(listPosition).getValve1ID());
                        intent.putExtra("valve_2_id", dataFarm.get(listPosition).getValve2ID());
                        activity.startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(activity, FarmETpControlCenterActivity.class);
                        //Log.d("FarmAdapter","Starting FarmDetailsActivity!!!!!!!!");
                        intent.putExtra("id", dataFarm.get(listPosition).getID());
                        intent.putExtra("mainpump_id", dataFarm.get(listPosition).getMainpumpID());
                        intent.putExtra("humidity_id", dataFarm.get(listPosition).getHumidityID());
                        intent.putExtra("valve_1_id", dataFarm.get(listPosition).getValve1ID());
                        intent.putExtra("valve_2_id", dataFarm.get(listPosition).getValve2ID());
                        activity.startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataFarm.size();
    }

}