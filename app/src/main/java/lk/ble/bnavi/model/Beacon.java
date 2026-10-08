package lk.ble.bnavi.model;

import java.util.Objects;

public class Beacon {
    private String address;
    private String name;
    private String uuid;
    private int rssi;
    private int txPower;
    private double distance;

    public Beacon(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Beacon)) return false;

        Beacon beacon = (Beacon) o;

        if (!address.equals(beacon.address)) return false;
        return Objects.equals(name, beacon.name);
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + uuid.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Beacon{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", rssi=" + rssi +
                ", txPower=" + txPower +
                ", distance=" + distance +
                '}';
    }
}
