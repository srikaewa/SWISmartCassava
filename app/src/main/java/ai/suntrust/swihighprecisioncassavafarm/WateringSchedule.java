package ai.suntrust.swihighprecisioncassavafarm;

public class WateringSchedule {
    String current_date;
    String next_date;
    int days;
    int hours;
    int mins;
    int total_mins;

    WateringSchedule(){
        this.current_date = "";
        this.next_date = "";
        this.days = 0;
        this.hours = 0;
        this.mins = 0;
        this.total_mins = 0;
    }
}
