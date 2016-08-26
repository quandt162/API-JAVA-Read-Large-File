package API;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

import POJO.Pojo;

import java.nio.channels.FileChannel;
// RandomFileChannel - Joe (c)
/**
RandomFileChannel allows to retrieve a batch of records, set by MAX (default: 499992). Record size
is set by RMAX (default: 24 bytes).
*/
public class RandomFileChannel {
    private FileChannel fcn;
    private long size, curPos;
    private int RMAX = 24; // 24 bytes per record
    private int MAX = 499992; // 24 * 20833 virtual record
    private ByteBuffer bbuf = ByteBuffer.allocateDirect(MAX);
    /**
    Constructor
    @param full_path String, full pathname (or URL)
    @exception Exception thrown by JAVA (file not found, etc.)
    */
    public RandomFileChannel(String full_path) throws Exception {
        if (full_path.indexOf("://") < 0) fcn = (new FileInputStream(full_path)).getChannel();
        else fcn = (new FileInputStream(new File(new URI(full_path)))).getChannel();
        size = fcn.size();
        curPos = 0;
    }
    /**
    close RandomFileChannel.
    */
    public void close() {
        if (fcn != null) try {
            fcn.close(); 
        } catch (Exception ex) { }
        fcn = null;
    }
    /**
    set a batch of records
    @param max int, the max. number of records for a batch
    */
    public void setBatch(int max) {
        MAX = max;
        bbuf = ByteBuffer.allocateDirect(MAX); // renew the cache
    }
    /**
    retrieve the setting batch
    @return int, the max. number of records for a batch
    */
    public int getBatch( ) {
        return MAX;
    }
    /**
    set Record size
    @param rmax int, the max. number of bytes for a record
    */
    public void setSize(int max) {
        RMAX = max;
    }
    /**
    retrieve the settin Record size
    @return int, the max. number of bytes per record
    */
    public int getSize( ) {
        return RMAX;
    }
    /**
    set Staring position
    @param pos long, new cursor position
    @exception Exception if newPos > FileSize or negative
    */
    public void setPosition(long pos) throws Exception {
        if (pos > size) throw new Exception("Too big:"+pos+", max:"+size);
        if (pos < 0) throw new Exception("Cannot be nagative:"+pos);
        curPos = pos;
    }
    /**
    retrieve the current position
    @return the current position.
    */
    public long currentPosition() {
        return curPos;
    }
    /**
    retrieve the next batch of records (up current position)
    @return List of records (Pojo)
    */
    public ArrayList<Pojo>next( ) {
        if (curPos < 0) curPos = 0;
        try {
            long pos = curPos;
            if (pos >= size)  return null;
            curPos += MAX;
            return read(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
     /**
    retrieve the previous batch of records (from current position)
    @return List of records (Pojo)
    */
    public ArrayList<Pojo>previous( ) {
        if (curPos > size) curPos -= MAX;
        try {
            long pos = curPos;
            if (pos < 0) return null;
            curPos -= MAX;
            return read(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // get the chunk...
    private ArrayList<Pojo>read(long pos) throws Exception {
        fcn.position(pos); // set position
        int b = (int)fcn.read(bbuf);
        if (b == -1) return null;
        byte[] bb = new byte[MAX];
        ArrayList<Pojo> listobject = new ArrayList<Pojo>(MAX / RMAX);
        ((ByteBuffer)bbuf.flip()).get(bb, 0, b);
        bbuf.clear();
        for (int l = 0; l < b; l += RMAX) {
             listobject.add(new Pojo(getLong(bb, l), 
                                     getLong(bb, l+8), 
                                     getLong(bb, l+16)
                                    )
                            );
        }
        return listobject;
    }
    //
    private long getLong(byte[] bb, int idx) {
        return (((long)bb[idx]   & 0xFF) << 56) +
               ((long)(bb[idx+1] & 0xFF) << 48) +
               ((long)(bb[idx+2] & 0xFF) << 40) +
               ((long)(bb[idx+3] & 0xFF) << 32) +
               ((long)(bb[idx+4] & 0xFF) << 24) +
               ((long)(bb[idx+5] & 0xFF) << 16) +
               ((long)(bb[idx+6] & 0xFF) << 8)  +
               ((long)(bb[idx+7] & 0xFF));
    }
    public static void main(String[] args) throws Exception {
		RandomFileChannel rfc = new RandomFileChannel("C:\\Users\\quand\\OneDrive\\Documents\\com.ghostteam.voicecall");
		
	}
}
