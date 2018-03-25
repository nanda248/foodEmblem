package com.example.jiongyi.foodemblem.room;

import java.util.Date;

/**
 * Created by JiongYi on 19/3/2018.
 */

public class CustomerReservation {
    private int paxseated;
    private int pax;
    private String reservationdate;
    private String reservationtime;
    private String tableNo;
    private int seatCapacity;
    private String status;
    private String restname;

    public CustomerReservation(){

    }
    public CustomerReservation(int paxseated, int pax, String reservationdate, String tableNo, int seatCapacity, String status, String restname, String reservationtime){
        this.setPaxseated(paxseated);
        this.setPax(pax);
        this.setReservationdate(reservationdate);
        this.setTableNo(tableNo);
        this.setSeatCapacity(seatCapacity);
        this.setStatus(status);
        this.setRestname(restname);
        this.setReservationtime(reservationtime);
    }

    public int getPaxseated() {
        return paxseated;
    }

    public void setPaxseated(int paxseated) {
        this.paxseated = paxseated;
    }

    public int getPax() {
        return pax;
    }

    public void setPax(int pax) {
        this.pax = pax;
    }

    public String getReservationdate() {
        return reservationdate;
    }

    public void setReservationdate(String reservationdate) {
        this.reservationdate = reservationdate;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public int getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(int seatCapacity) {
        this.seatCapacity = seatCapacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRestname() {
        return restname;
    }

    public void setRestname(String restname) {
        this.restname = restname;
    }


    public String getReservationtime() {
        return reservationtime;
    }

    public void setReservationtime(String reservationtime) {
        this.reservationtime = reservationtime;
    }
}
