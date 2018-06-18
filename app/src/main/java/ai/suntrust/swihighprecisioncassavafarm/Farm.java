package ai.suntrust.swihighprecisioncassavafarm;

import java.util.ArrayList;
import java.util.List;

public class Farm {
    String id;
    String title;
    String description;
    String latitude;
    String longitude;
    String starting_date;
    int watering_scheme;

    List<WateringSchedule> watering_schedule = new ArrayList<WateringSchedule>();

    boolean need_watering;

    String plant_id;
    String plant_title;
    String soil_id;
    String soil_title;

    String mainpump_id;
    String humidity_sensor_id;
    String humidity_critical_point;
    String rain_sensor_id;
    String valve_1_id;
    String valve_2_id;

    /***** ETp *****/
    float drip_flowrate;
    float drip_interval;
    float tape_interval;
    int   total_drip_per_rai;
    float total_flowrate_per_rai;
    /***************/

    String linegroup_token;

    String uid;

    int     sampling_time;
    boolean is_public;
    boolean activated;
    String created_at;

    String image_path;

    Farm()
    {

    }

    public String getID(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getLatitude(){
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public String getHumidityCriticalPoint(){
        return humidity_critical_point;
    }

    public String getValve1ID(){
        return valve_1_id;
    }

    public String getValve2ID(){
        return valve_2_id;
    }

    public int getWateringScheme(){
        return watering_scheme;
    }

    public String getSoilTitle(){
        return soil_title;
    }

    public String getPlantTitle(){
        return plant_title;
    }

    public String getStartingDate(){
        return starting_date;
    }

    public String getMainpumpID(){
        return mainpump_id;
    }

    public String getHumidityID(){
        return humidity_sensor_id;
    }

    public String getRainID() {return rain_sensor_id; }

    public float getDripFlowrate() { return drip_flowrate; }
    public float getDripInterval() { return drip_interval; }
    public float getTapeInterval() { return tape_interval; }
    public int   getTotalDripPerRai() { return total_drip_per_rai; }
    public float getTotalFlowratePerRai() { return  total_flowrate_per_rai; }

    public String getCreatedAt(){
        return created_at;
    }

    public boolean getNeedWatering(){ return need_watering;}

    public boolean isActivated(){ return activated;}

    public int getSamplingTime(){ return sampling_time;}

    public String getImagePath(){ return image_path;}
}
