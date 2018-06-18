package ai.suntrust.swihighprecisioncassavafarm;

public class Soil {
    String id;
    String title_eng;
    String title_thai;
    int water_allowance;
    float water_holding_capacity;

    Soil(){

    }

    String getID(){
        return id;
    }

    String getTitleEng(){
        return title_eng;
    }

    String getTitleThai(){
        return title_thai;
    }

    int getWaterAllowance(){
        return water_allowance;
    }

    float getWaterHoldingCapacity(){
        return water_holding_capacity;
    }
}
