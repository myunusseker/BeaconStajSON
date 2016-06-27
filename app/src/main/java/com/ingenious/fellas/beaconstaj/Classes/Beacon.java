package com.ingenious.fellas.beaconstaj.Classes;

public class Beacon {
    private String name;
    private String address;
    private int rssi;
    private double accuracy;
    private String distance;
    private int id;
    public static final int TXPOWER = -55;
    private boolean isNear;

    public Beacon(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Beacon(String name, String address, int rssi) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
        this.setAccuracy();
        this.setDistance();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi)
    {
        this.rssi = rssi;
        this.setAccuracy();
        this.setDistance();
    }

    private void setAccuracy() {
        double result = -1;
        if (rssi == 0) {
            result = -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi*1.0/TXPOWER;
        if (ratio < 1.0) {
            result = Math.pow(ratio,10);
        }
        else {
            result =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
        }
        accuracy = result;
    }

    private void setDistance() {
        String result="";
        if (accuracy == -1.0) {
            result = "Unknown";
        } else if (accuracy < 1) {
            result = "Immediate";
        } else if (accuracy < 3) {
            result = "Near";
        } else {
            result = "Far";
        }
        distance = result;
    }

    public String getDistance()
    {
        return distance;
    }

    public double getAccuracy()
    {
        return accuracy;
    }

    public boolean getIsNear() {
        return isNear;
    }

    public void setIsNear(boolean near) {
        isNear = near;
    }
}
