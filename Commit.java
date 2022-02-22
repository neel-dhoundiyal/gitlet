package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


/** Represents a Commit in a Gitlet repo.
 * @author Neel Dhoundiyal **/
public class Commit implements Serializable {

    /** Stores the mapping between file names and blob references. **/
    private HashMap<String, Blob> _blobs;

    /** Stores the log message of the commit. **/
    private String _logMessage;

    /** Stores the time stamp of the commit. **/
    private String _timeStamp;

    /** Stores the parent hash code. **/
    private String _parent;

    /** Stores the second(merge) parent hash code. **/
    private String _parentMerge;

    /** Boolean value that indicates whether a merge will happen. **/
    private boolean _merge;

    /** Stores the hash code of the commit. **/
    private String _hashCodeCommit;

    /** The current branch that the commit object is on. **/
    private String _branch;

    /** Initial Commit 0. **/
    public Commit() {
        _logMessage = "initial commit";
        _branch = "master";
        _timeStamp = "Wed Dec 31 16:00:00 1969 -0800";
        _parent = null;
        _parentMerge = null;
        _merge = false;
        ArrayList<Object> initial = new ArrayList<>();
        initial.add(_timeStamp);
        initial.add(_logMessage);
        _hashCodeCommit = "c" + Utils.sha1(initial);
        _blobs = new HashMap<>();
    }

    /** Commit Constructor.
     * @param p : Reps the parent.
     * @param m : Reps the log message.
     * @param b : Reps the branch.
     * @param  blobs : Reps the blobs for the commit.**/
    public Commit(String m,  String p, String b,
                  HashMap<String, Blob> blobs) {
        _logMessage = m;
        _timeStamp = timeStampNow();
        _parent = p;
        _parentMerge = null;
        _branch = b;
        _blobs = blobs;
        _merge = false;
        _hashCodeCommit = "c" + Utils.sha1(hashList());

    }

    /** Commit Constructor.
     * @param p : Reps the parent.
     * @param p2 : Reps the second parent.
     * @param m : Reps the log message.
     * @param b : Reps the branch.
     * @param  blobs : Reps the blobs for the commit.**/
    public Commit(String m,  String p, String p2, String b,
                  HashMap<String, Blob> blobs) {
        _logMessage = m;
        _timeStamp = timeStampNow();
        _parent = p;
        _branch = b;
        _blobs = blobs;
        _merge = true;
        _parentMerge = p2;
        ArrayList<Object>  x = hashList();
        x.add(_parentMerge);
        _hashCodeCommit = "c" + Utils.sha1(x);

    }

    /** Returns the list that will be used to generate the SHA-1 code. **/
    private ArrayList<Object> hashList() {
        ArrayList<Object> l = new ArrayList<>();
        l.add(_logMessage);
        l.add(_branch);
        l.add(_timeStamp);
        return l;
    }

    /** Returns the current time stamp. **/
    private String timeStampNow() {
        ZonedDateTime current = ZonedDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.
                                   ofPattern("EEE MMM dd HH:mm:ss yyyy");
        String strDate = current.format(format) + " -0800";
        return strDate;
    }

    /** Returns the log message. **/
    String getLogMessage() {
        return _logMessage;
    }

    /** Returns the time stamp. **/
    String getTimeStamp() {
        return _timeStamp;
    }

    /** Returns the hash code of the parent. **/
    String getParent() {
        return _parent;
    }

    /** Returns the hash code of the parent. **/
    String getParentMerge() {
        return _parentMerge;
    }

    /** Returns the current branch of the commit. **/
    String getBranch() {
        return _branch;
    }

    /** Returns all the blobs of the commit. **/
    HashMap<String, Blob> getBlobs() {
        return _blobs;
    }

    /** Returns the hash code of the commit. **/
    String getHashCodeCommit() {
        return _hashCodeCommit;
    }

    /** Returns if it is a merge commit. **/
    boolean getMerge() {
        return _merge;
    }


}
