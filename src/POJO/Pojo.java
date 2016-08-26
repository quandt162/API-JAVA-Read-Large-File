package POJO;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;

public class Pojo {
    private long time, id, address;
    public Pojo(long time, long id, long address) {
        this.time = time;
        this.id = id;
        this.address = address;
    }
    public String getTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return calendar.get(Calendar.DATE) + "/" + 
               (calendar.get(Calendar.MONTH) + 1) + "/" +
               calendar.get(Calendar.YEAR) + " (" +
               calendar.get(Calendar.HOUR_OF_DAY) + ":" +
               calendar.get(Calendar.MINUTE) + ")";
    }
    public void setTime(long time) {
        this.time = time;
    }
    public long getImei() {
        return id;
    }
    public void setImei(long id) {
        this.id = id;
    }
    public String getIPAddress() {
        if (address > 0) try {
             return InetAddress.getByName(address+"").getHostAddress();
        } catch (Exception e) { }
        return "";
    }
    public void setIPAddress(String ipa) {
        try {
            byte[] bb = InetAddress.getByName(ipa).getAddress();
            address = ((long)(bb[0] & 0xFF) << 24) +
                 ((long)(bb[1] & 0xFF) << 16) +
                 ((long)(bb[2] & 0xFF) << 8)  +
                 ((long)(bb[3] & 0xFF));
        } catch (Exception e) {
            address = 0l;
        }
    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Time : "+getTime()+ "\timei: "+id + "\tip : "+getIPAddress();
    }
}