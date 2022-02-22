package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/** Represents a Blob.
 * @author Neel Dhoundiyal **/
public class Blob implements Serializable {

    /** Stores the name of the file. **/
    private String _name;

    /** Stores the hash code. **/
    private String _hashCodeBlob;

    /** String content of the file. **/
    private String _content;

    /** Stores the byte stream values in an array. **/
    private byte[] _contentStream;

    /** Blob constructor.
     * @param pathname  : Reps the pathname of the incoming file.**/
    public Blob(String pathname) {
        File f = new File(pathname);
        _name = pathname;
        _content = Utils.readContentsAsString(f);
        _contentStream = Utils.readContents(f);
        _hashCodeBlob =  "b" + Utils.sha1(hashList());

    }

    /** Returns the list that will be used to generate the SHA-1 code. **/
    private ArrayList<Object> hashList() {
        ArrayList<Object> l = new ArrayList<>();
        l.add(_name);
        l.add(_content);
        l.add(_contentStream);
        return l;
    }

    /** Returns the name of the blob. **/
    public String getName() {
        return _name;
    }

    /** Returns the hashCode of the blob. **/
    public String getHashCodeBlob() {
        return _hashCodeBlob;
    }

    /** Returns the content of the blob. **/
    public String getContent() {
        return _content;
    }

    /** Returns the content stream of the blob. **/
    public byte[] getContentStream() {
        return _contentStream;
    }


}
