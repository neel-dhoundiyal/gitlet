package gitlet;


import java.io.ObjectInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Collection;


/** Represents a Gitlet repo.
 * @author Neel Dhoundiyal**/
public class Gitlet implements Serializable {

    /** Contains the hash code of the head of the commits.**/
    private String _head;

    /** All the files that have been currently staged.**/
    private ArrayList<Blob> _staged;

    /** All the files that are being currently
     * tracked in the working directory.**/
    private HashMap<String, Blob> _trackedDir;

    /** All the files that are being currently
     * tracked not in the working directory.**/
    private HashMap<String, Blob> _trackedNDir;

    /** All the files that are not tracked.**/
    private HashMap<String, Blob> _removed;

    /** All the commits in the Gitlet repo.**/
    private HashMap<String, Commit> _commits;

    /** Current branch.**/
    private String _currentBranch;

    /** All blobs involved.**/
    private HashMap<String, Blob>  _allBlobs;

    /** Maps the commit hashcode to the its branch.*/
    private HashMap<String, String> _branch;

    /** Names of the blobs that are currently staged.*/
    private ArrayList<String> _blobNamesStaged;

    /** Maps the start of the branch to the commit.*/
    private HashMap<String, Commit> _branchStart;

    /** Untracked files.*/
    private HashMap<String, Blob> _untracked;

    /** Checks if a remove happens.*/
    private boolean _removechk;

    /** Maps a branch to its split commit.*/
    private HashMap<String, Commit> _branchSplit;

    /** Merge Parent Hash.**/
    private String _mergeParentHash;



    /** Gitlet Constructor.*/
    public Gitlet() {
        File filepath = Utils.join(System.getProperty("user.dir"),
                ".gitlet");
        if (filepath.exists()) {
            try {
                File k = new File(".gitlet/info");
                FileInputStream f = new FileInputStream(k);
                ObjectInputStream input = new ObjectInputStream(f);
                Gitlet obj = (Gitlet) input.readObject();
                input.close();
                copy(obj);
            } catch (ClassNotFoundException | IOException exception) {
                System.out.println(exception);
            }

        } else {
            _head = null;
            _currentBranch = null;
            _staged = new ArrayList<>();
            _trackedDir = new HashMap<>();
            _trackedNDir = new HashMap<>();
            _removed = new HashMap<>();
            _commits = new HashMap<>();
            _allBlobs = new HashMap<>();
            _branch = new HashMap<>();
            _blobNamesStaged = new ArrayList<>();
            _branchStart = new HashMap<>();
            _untracked = new HashMap<>();
            _removechk = false;
            _branchSplit = new HashMap<>();
            _mergeParentHash = null;
        }
    }

    /** Copies all the information of another
     * Gitlet object into the current one.
     * @param x : Gitlet Object*/
    public void copy(Gitlet x) {
        this._commits = x._commits;
        this._removed = x._removed;
        this._staged = x._staged;
        this._head = x._head;
        this._trackedDir = x._trackedDir;
        this._trackedNDir = x._trackedNDir;
        this._currentBranch = x._currentBranch;
        this._allBlobs = x._allBlobs;
        this._branch = x._branch;
        this._blobNamesStaged = x._blobNamesStaged;
        this._branchStart = x._branchStart;
        this._untracked = x._untracked;
        this._removechk = x._removechk;
        this._branchSplit = x._branchSplit;
        this._mergeParentHash = x._mergeParentHash;
    }

    /** Control center of my Gitlet object.
     * @param commands : List of commands inputted from the terminal.*/
    public void run(ArrayList<String> commands) throws IOException {
        ArrayList<String> funcs = new ArrayList<>();
        String[] x = {"init", "add", "commit", "checkout", "log",
                      "global-log", "merge", "branch", "rm",
                      "rm-branch", "reset", "status", "find"};
        funcs.addAll(Arrays.asList(x));
        String f = commands.get(0);

        if (!funcs.contains(f)) {
            System.out.println("No command with that name exists.");
            System.exit(0);
        }

        if (f.length() == 0) {
            System.out.println("No function has been inputted.");
            System.exit(0);
        }

        String func = commands.remove(0);
        ArrayList<String> operands = commands;

        if (!func.equals("init")
                && !Utils.join(System.getProperty("user.dir"),
                ".gitlet").exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }

        run1(func, operands);

        run2(func, operands);


        if (func.equals("status")) {
            if (operands.size() == 0) {
                status();
            } else {
                System.out.println("Incorrect operands.");
            }
        }

        if (func.equals("rm-branch")) {
            if (operands.size() == 1) {
                String message = operands.get(0);
                rmbranch(message);
            } else {
                System.out.println("Incorrect operands.");
            }
        }

        if (func.equals("rm")) {
            String message = operands.get(0);
            if (operands.size() == 1) {
                rm(message);
            } else {
                System.out.println("Incorrect operands.");
            }
        }

    }

    /** Control center of my Gitlet object.
     * @param operands : List of commands inputted from the terminal.
     * @param func : String.*/
    public void run1(String func,
                     ArrayList<String> operands) throws IOException {

        if (func.equals("init")) {
            if (operands.size() == 0) {
                init();
                return;
            } else {
                System.out.println("Incorrect operands.");
            }
        }

        if (func.equals("add")) {
            if (operands.size() == 1) {
                add(operands.get(0));
            } else {
                System.out.println("Incorrect operands.");
            }
        }

        if (func.equals("commit")) {
            if (operands.size() == 1) {
                String message = operands.get(0);
                commit(message);
            } else {
                System.out.println("Incorrect operands.");
            }
        }

        if (func.equals("checkout")) {
            checkout(operands);
        }

        if (func.equals("merge")) {
            if (operands.size() == 1) {
                String branch = operands.get(0);
                merge(branch);
            } else {
                System.out.println("Incorrect operands.");
            }
        }

    }

    /** Control center of my Gitlet object.
     * @param operands : List of commands inputted from the terminal.
     * @param func : String.*/
    public void run2(String func,
                     ArrayList<String> operands) throws IOException {

        if (func.equals("log")) {
            if (operands.size() == 0) {
                log();
                return;
            } else {
                System.out.println("Incorrect operands.");
            }
        }

        if (func.equals("global-log")) {
            if (operands.size() == 0) {
                globalLog();
            } else {
                System.out.println("Incorrect operands.");
            }
        }

        if (func.equals("find")) {
            if (operands.size() == 1) {
                String message = operands.get(0);
                find(message);
            } else {
                System.out.println("Incorrect operands.");
            }
        }

        if (func.equals("branch")) {
            if (operands.size() == 1) {
                String message = operands.get(0);
                branch(message);
            } else {
                System.out.println("Incorrect operands.");
            }
        }

        if (func.equals("reset")) {
            if (operands.size() == 1) {
                String message = operands.get(0);
                reset(message);
            } else {
                System.out.println("Incorrect operands.");
            }
        }
    }

    /** Initializes a Gitlet repo.*/
    public void init() throws IOException {
        File filepath = Utils.join(System.getProperty("user.dir"), ".gitlet");
        if (!filepath.exists()) {
            filepath.mkdir();
            Utils.join(System.getProperty("user.dir"),
                    ".gitlet/commits").mkdir();
            Utils.join(System.getProperty("user.dir"),
                    ".gitlet/staged").mkdir();
            Utils.join(System.getProperty("user.dir"),
                    ".gitlet/removed").mkdir();
            Commit initial = new Commit();
            _commits.put(initial.getHashCodeCommit(), initial);
            _currentBranch = "master";
            _head = initial.getHashCodeCommit();
            _branch.put(initial.getHashCodeCommit(), initial.getBranch());
            _branchStart.put("master", initial);
            File newfile = Utils.join(System.getProperty("user.dir"),
                    ".gitlet/commits/" + initial.getHashCodeCommit());
            Utils.writeContents(newfile, Utils.serialize(initial));
        } else {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
        }

    }

    /** Adds files to the Gitlet repo.
     * @param filename : String of the filepath.*/
    private void add(String filename) {
        if (!new File(filename).exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Blob b = new Blob(filename);
        if (_removed.containsKey(b.getName())) {
            _removed.remove(filename);
            Utils.join(System.getProperty("user.dir"),
                    ".gitlet/removed/" + b.getHashCodeBlob()).delete();
            _untracked.remove(filename);
            _trackedDir.put(filename, b);
        } else {
            Commit prev = _commits.get(_head);
            if (addHelperStage(b, prev)) {
                if (!prev.getBlobs().containsKey(filename)) {
                    Utils.writeContents(Utils.join
                            (System.getProperty("user.dir"),
                            ".gitlet/staged" + b.getHashCodeBlob()),
                            Utils.serialize(b));
                    _staged.add(b);
                    _blobNamesStaged.add(b.getName());
                    _trackedDir.put(b.getName(), b);
                } else {
                    if (!prev.getBlobs().get(filename).
                            getHashCodeBlob()
                            .equals(b.getHashCodeBlob())) {
                        Utils.writeContents(Utils.join(
                                System.getProperty("user.dir"),
                                ".gitlet/staged" + b.getHashCodeBlob()),
                                Utils.serialize(b));
                        _staged.add(b);
                        _blobNamesStaged.add(b.getName());
                        _trackedDir.put(b.getName(), b);
                    }
                }
            }
        }
    }


    /** Add Helper function.
     * @return boolean
     * @param b : Blob.
     * @param chk : Commit.**/
    public boolean addHelperStage(Blob b, Commit chk) {
        boolean c = true;
        for (int i = 0; i < _staged.size(); ++i) {
            if (b.getName().equals(_staged.get(i).getName())) {
                if (!b.getHashCodeBlob().
                        equals(_staged.get(i).getHashCodeBlob())) {
                    Utils.join(System.getProperty("user.dir"),
                            ".gitlet/staged"
                                    + _staged.get(i).getHashCodeBlob())
                            .delete();
                    _staged.remove(_staged.get(i));
                    _staged.remove(_staged.get(i).getName());
                    if (!chk.getBlobs().containsKey(b.getName())
                            && !_staged.contains(b)) {
                        Utils.writeContents(Utils.join
                                (System.getProperty("user.dir"),
                                ".gitlet/staged" + b.getHashCodeBlob()),
                                Utils.serialize(b));
                        _staged.add(b);
                        _blobNamesStaged.add(b.getName());
                        _trackedDir.put(b.getName(), b);
                    }
                }
                c = false;
                break;
            }
        }
        return c;
    }

    /** Creates commits.
     * @param log : Log message.**/
    public void commit(String log) throws IOException {
        if (log.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        } else if (_staged.size() == 0 && !_removechk) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        Commit prev = readCommit(_head);
        Commit next = null;
        HashMap<String, Blob> commitBlobs = new HashMap<>();
        if (prev.getLogMessage().equals("initial commit")) {
            for (int i = 0; i < _staged.size(); ++i) {
                commitBlobs.put(_staged.get(i).getName(), _staged.get(i));
            }
            next = new Commit(log, prev.getHashCodeCommit(),
                    _currentBranch, commitBlobs);
        } else {
            next = commitHelper(log, prev);
        }
        Utils.writeContents(Utils.join(System.getProperty("user.dir"),
                ".gitlet/commits/" + next.getHashCodeCommit()),
                             Utils.serialize(next));
        _staged.clear();
        _blobNamesStaged.clear();
        _head = next.getHashCodeCommit();
        _commits.put(next.getHashCodeCommit(), next);
        _currentBranch = next.getBranch();
        _branch.put(next.getHashCodeCommit(),
                next.getBranch());
        _branchStart.put(_currentBranch, next);
        _mergeParentHash = null;
        File dir = Utils.join(System.getProperty("user.dir"),
                ".gitlet/staged/");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                file.delete();

            }
        }
        _removechk = false;
        _removed.clear();

    }

    /** Commit Helper function.
     * @param log : String.
     * @param prev : Commit.
     * @return Commit. **/
    @SuppressWarnings("unchecked")
    public Commit commitHelper(String log, Commit prev) {
        Iterator blobs = prev.getBlobs().entrySet().iterator();
        HashMap<String, Blob> commitBlobs = new HashMap<>();
        while (blobs.hasNext()) {
            Map.Entry<String, Blob> entry = (Map.Entry) blobs.next();
            Blob compare = entry.getValue();
            if (!_removed.containsKey(compare.getName())) {
                if (_blobNamesStaged.contains(compare.getName())) {
                    for (int i = 0; i < _staged.size(); ++i) {
                        if (_staged.get(i).getName().equals
                                (compare.getName())) {
                            commitBlobs.put(_staged.get(i).getName(),
                                    _staged.get(i));
                        }
                    }
                } else {
                    commitBlobs.put(compare.getName(), compare);
                }
            }
        }
        if (_mergeParentHash != null) {
            Commit merge = new Commit(log, prev.getHashCodeCommit(),
                    _mergeParentHash, _currentBranch, commitBlobs);
            return merge;
        }
        Commit next = new Commit(log, prev.getHashCodeCommit(),
                _currentBranch, commitBlobs);
        return next;
    }

    /** Helps in getting previous commits.
     * @return : Commit.
     * @param id : String id.**/
    public Commit readCommit(String id) throws IOException {
        Commit prev = null;
        try {
            FileInputStream f = new FileInputStream(
                    Utils.join(System.getProperty("user.dir"),
                    ".gitlet/commits/" + id));
            ObjectInputStream input = new ObjectInputStream(f);
            prev = (Commit) input.readObject();
            input.close();
        } catch (ClassNotFoundException exp) {
            System.out.println(exp);
            return null;
        }
        return prev;
    }

    /** Remove function.
     * @param name : Name of the file.**/
    public void rm(String name) throws IOException {
        Commit prev = readCommit(_head);
        Blob b = prev.getBlobs().get(name);
        if (!_blobNamesStaged.contains(name)
                && !prev.getBlobs().containsKey(name)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (_blobNamesStaged.contains(name)) {
            _blobNamesStaged.remove(name);
            for (int i = 0; i < _staged.size(); ++i) {
                if (_staged.get(i).getName().equals(name)) {
                    File dir = Utils.join(
                            System.getProperty("user.dir"),
                            ".gitlet/staged/"
                                    + _staged.get(i).getHashCodeBlob());
                    dir.delete();
                    _staged.remove(i);
                    _untracked.put(name, new Blob(name));
                    _trackedDir.remove(name);
                    _removechk = true;
                }
            }
        }

        if (!prev.getLogMessage().equals("initial commit")) {
            if (prev.getBlobs().containsKey(name)) {
                _removed.put(name, b);
                _trackedDir.remove(name);
                Utils.writeContents(Utils.join
                                (System.getProperty("user.dir"),
                        ".gitlet/removed/"
                                + b.getHashCodeBlob()),
                        Utils.serialize(b));
                Utils.join(System.getProperty("user.dir"),
                        b.getName()).delete();
                _removechk = true;
            }
        }

    }

    /** Checkout central function.
     * @param operands : Operands for a checkout.**/
    public void checkout(ArrayList<String> operands) throws IOException {
        if (!_untracked.isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it or add it first.");
            System.exit(0);
        }
        if (operands.size() == 2) {
            checkout1(operands, _head);
        } else if (operands.size() == 3) {
            if (!operands.get(1).equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            checkout1(operands, operands.get(0));
        } else if (operands.size() == 1) {
            checkout3(operands);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /** Checkout for operand sizes 1 and 2.
     * @param operands : Operands.
     * @param id : String id.**/
    public void checkout1(ArrayList<String> operands,
                          String id) throws IOException {
        if (id.length() < 10) {
            boolean chk = false;
            Collection<Commit> commits = _commits.values();
            for (Commit c : commits) {
                if (c.getHashCodeCommit().contains(id)) {
                    chk = true;
                    id = c.getHashCodeCommit();
                    if (c.getBlobs().containsKey(operands.get(2))) {
                        Blob b = c.getBlobs().get(operands.get(2));
                        String content = b.getContent();
                        Utils.join(System.getProperty("user.dir"),
                                operands.get(2)).delete();
                        Utils.writeContents(Utils.join
                                (System.getProperty("user.dir"),
                                        operands.get(2)), content);
                    }
                    return;
                }
            }

        } else if (!_commits.containsKey(id)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        Commit prev = _commits.get(id);
        String name;
        if (id == _head) {
            name = operands.get(1);
        } else {
            name = operands.get(2);
        }
        if (prev.getBlobs().containsKey(name)) {
            Blob b = prev.getBlobs().get(name);
            String content = b.getContent();
            Utils.join(System.getProperty("user.dir"), name).delete();
            Utils.writeContents(Utils.join
                    (System.getProperty("user.dir"), name), content);
        } else {
            System.out.println("File does not exist in that commit.");
        }

    }

    /** Checkout for operand size 3.
     * @param operands : Operands.**/
    @SuppressWarnings("unchecked")
    public void checkout3(ArrayList<String> operands) {
        String branch = operands.get(0);
        if (branch.equals(_currentBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        if (!_branchStart.containsKey(branch)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        Commit chk = _branchStart.get(branch);
        HashMap<String, Blob> newtracked = new HashMap<>();
        if (untracked().size() == 0) {
            Collection<Blob> coll = chk.getBlobs().values();
            for (Blob b :  coll) {
                if (chk.getBlobs().containsKey(b.getName())) {
                    Utils.join(System.getProperty("user.dir"),
                            b.getName()).delete();
                    Blob x = chk.getBlobs().get(b.getName());
                    Utils.writeContents(Utils.join
                            (System.getProperty("user.dir"),
                                    b.getName()), x.getContent());
                }
                newtracked.put(b.getName(), b);
            }
        } else {
            System.out.println("There is an untracked file "
                     + "in the way; "
                    + "delete it or add it first.");
            System.exit(0);
        }

        List<String> filenames = Utils.plainFilenamesIn
                (System.getProperty("user.dir"));
        for (String name : filenames) {
            if (!chk.getBlobs().containsKey(name)) {
                Utils.join(System.getProperty("user.dir"), name).delete();
            }
        }
        _trackedDir.clear();
        _trackedDir.putAll(newtracked);
        _staged.clear();
        File dir = Utils.join(
                System.getProperty("user.dir"), ".gitlet/staged/");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File file : directoryListing) {
                file.delete();

            }
        }
        _currentBranch = branch;
        _head = _branchStart.get(_currentBranch).getHashCodeCommit();
    }

    /** Helper function for checkout3.
     * @param chk : Commit.
     * @param files : List of files.
     * @return boolean.**/
    public boolean checkout3Helper(Commit chk, ArrayList<String> files) {
        boolean c = true;
        for (String file : files) {
            if (!_staged.contains(file)
                    && !_trackedDir.containsKey(file)
                    && !_trackedNDir.containsKey(file)
                    && chk.getBlobs().containsKey(file)) {
                c = false;
                System.out.println("There is an untracked file in the way;"
                        + " delete it or add it first.");
                System.exit(0);
            }
        }
        return c;
    }

    /** Outputs the log.**/
    public void log() throws IOException {
        Commit print = readCommit(_head);
        while (print != null) {
            System.out.println("===");
            System.out.println("commit " + print.getHashCodeCommit());
            System.out.println("Date: " + print.getTimeStamp());
            System.out.print(print.getLogMessage());
            if (print.getLogMessage().equals("initial commit")) {
                break;
            } else {
                System.out.println("\n");
            }
            print = readCommit(print.getParent());
        }
    }


    /** Outputs the global log. **/
    @SuppressWarnings("unchecked")
    public void globalLog() {
        Iterator commits = _commits.entrySet().iterator();
        while (commits.hasNext()) {
            Map.Entry<String, Commit> entry =
                    (Map.Entry<String, Commit>) commits.next();
            System.out.println("===");
            System.out.println("commit " + entry.getKey());
            System.out.println("Date: " + entry.getValue().getTimeStamp());
            System.out.print(entry.getValue().getLogMessage());
            if (!commits.hasNext()) {
                break;
            } else {
                System.out.print("\n");
            }
        }
    }


    /** Finds commits with a particular log message.
     * @param message : String.**/
    @SuppressWarnings("unchecked")
    public void find(String message) {
        boolean chk = false;
        Iterator commits = _commits.entrySet().iterator();
        while (commits.hasNext()) {
            Map.Entry<String, Commit> entry =
                    (Map.Entry<String, Commit>) commits.next();
            if (entry.getValue().getLogMessage().equals(message)) {
                System.out.println(entry.getKey());
                chk = true;
            }
        }
        if (!chk) {
            System.out.println("Found no commit with that message.");
        }
    }

    /** Branch function.
     * @param name : String.**/
    public void branch(String name) throws IOException {
        if (_branchStart.containsKey(name)) {
            System.out.println("A branch with that name already exists.");
        } else {
            Commit head = readCommit(_head);
            _branchStart.put(name, head);
            _branchSplit.put(name, head);
        }
    }

    /** Remove branch function.
     * @param name : String..**/
    public void rmbranch(String name) {
        if (!_branchStart.containsKey(name)) {
            System.out.println("A branch with that name does not exist.");
        } else if (name.equals(_currentBranch)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            _branchStart.remove(name);
        }
    }

    /** Outputs the status.**/
    public void status() throws IOException {
        Map<String, Commit> map = _branchStart;
        Map<String, Commit> sorted = new TreeMap<>();
        sorted.putAll(map);
        System.out.println("=== Branches ===");
        if (!_branchStart.isEmpty()) {
            for (String s : sorted.keySet()) {
                if (s.equals(_currentBranch)) {
                    System.out.println("*" + _currentBranch);
                } else {
                    System.out.println(s);
                }
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        if (!_staged.isEmpty()) {
            for (Blob s : _staged) {
                System.out.println(s.getName());
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        if (!_removed.isEmpty()) {
            for (String s : _removed.keySet()) {
                System.out.println(s);
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String file : untracked()) {
            System.out.println(file);
        }
        System.out.println();
    }


    /** Reset the repo to commit id.
     * @param id : String.**/
    public void reset(String id) throws IOException {
        if (!_commits.containsKey(id)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        if (untracked().size() != 0) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it or add it first.");
        }

        Commit c = readCommit(id);
        for (String s : c.getBlobs().keySet()) {
            if (_trackedDir.containsKey(s) || _staged.contains(s)) {
                ArrayList<String> operands = new ArrayList<>();
                String[] o = {id, "--", s};
                operands.addAll(Arrays.asList(o));
                checkout(operands);
            }
        }
        _staged.clear();
        _currentBranch = c.getBranch();
        _branchStart.put(_currentBranch, c);
        _head = c.getHashCodeCommit();

    }

    /** Returns untracked files in the
     * working directory.
     * @return ArrayList
     * */
    private ArrayList<String> untracked() {
        ArrayList<String> untracked = new ArrayList<>();
        List<String> workingFileNames = Utils.plainFilenamesIn
                (System.getProperty("user.dir"));
        for (String file : workingFileNames) {
            if (!_blobNamesStaged.contains(file)
                    && !_trackedDir.containsKey(file)
                    && !_trackedNDir.containsKey(file)) {
                untracked.add(file);
            }
        }
        return untracked;
    }

    /** Merge branch to the _currentBranch.
     * @param branch : String.**/
    public void merge(String branch) throws IOException {
        if (_staged.size() != 0 || _removed.size() != 0) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        } else if (!_branchSplit.containsKey(branch)) {
            System.out.println(" A branch with that name does not exist.");
            System.exit(0);
        } else if (_branch.equals(_currentBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        } else if (!_untracked.isEmpty() || untracked().size() > 0) {
            System.out.println("There is an untracked file in the way; "
                     + "delete it or add it first.");
            System.exit(0);
        } else {
            if (basicMerge(branch)) {
                System.exit(0);
            } else {
                Commit current = _branchStart.get(_currentBranch);
                Commit b = _branchStart.get(branch);
                Commit split = _branchSplit.get(branch);
                ArrayList<String> files = filenames(current, b, split);
                HashMap<String, Blob> cBlobs = current.getBlobs();
                HashMap<String, Blob> branchBlobs = b.getBlobs();
                HashMap<String, Blob> splitBlobs = split.getBlobs();
                for (String file : files) {
                    if (branchBlobs.containsKey(file)) {
                        if (cBlobs.containsKey(file)
                                && splitBlobs.containsKey(file)) {
                            Blob br = branchBlobs.get(file);
                            Blob cr = cBlobs.get(file);
                            Blob spl = splitBlobs.get(file);
                            if (cr.getContent().equals(spl.getContent())
                                    && !br.getContent().
                                    equals(spl.getContent())) {
                                checkoutstager(b, file);
                            } else if (!cr.getContent().equals(spl.getContent())
                                       && br.getContent().
                                    equals(spl.getContent())) {
                                continue;
                            } else if (br.getContent().
                                    equals(cr.getContent())
                                    && !cr.getContent().
                                    equals(spl.getContent())) {
                                continue;
                            } else if (!br.getContent().equals(cr.getContent())
                                    && !cr.getContent().equals(spl.getContent())
                                    && !br.getContent().
                                    equals(spl.getContent())) {
                                mergeConflict(br, cr);
                                continue;
                            }
                        }
                    }
                }
                _mergeParentHash =  b.getBranch();
                commit("Merged " + branch + " into " + _currentBranch + ".");
            }
        }
    }

    /** Merge branch to the _currentBranch.
     * @param c : Commit
     * @param b : Commit
     * @param s : Commit
     * @return ArrayList**/
    public ArrayList<String> filenames(Commit c, Commit b, Commit s) {
        ArrayList<String> files = new ArrayList<>();
        for (String f : c.getBlobs().keySet()) {
            files.add(f);
        }
        for (String f : b.getBlobs().keySet()) {
            if (!files.contains(f)) {
                files.add(f);
            }
        }
        for (String f : s.getBlobs().keySet()) {
            if (!files.contains(f)) {
                files.add(f);
            }
        }
        return files;

    }

    /** Merge branch to the _currentBranch.
     * @param b : b
     * @param  f : f**/
    public void checkoutstager(Commit b, String f) throws IOException {
        ArrayList<String> operands = new ArrayList<>();
        operands.add(b.getHashCodeCommit());
        operands.add("--");
        operands.add(f);
        checkout(operands);
        add(f);

    }


    /** Merge branch to the _currentBranch.
     * @param branch : String.
     * @return boolean**/
    public boolean basicMerge(String branch) throws IOException {
        Commit current = _branchStart.get(_currentBranch);
        Commit b = _branchStart.get(branch);
        Commit split = _branchSplit.get(branch);
        if (split.getHashCodeCommit().
                equals(b.getHashCodeCommit())) {
            System.out.println("Given branch is an ancestor "
                     + "of the current branch.");
            return true;
        }
        if (split.getHashCodeCommit().equals(current.getHashCodeCommit())) {
            reset(b.getHashCodeCommit());
            System.out.println("Current branch fast-forwarded.");
            return true;
        } else {
            return false;
        }
    }

    /** Builds the file.
     * @param branch : String.
     * @param current : String.
     * @return String**/
    public String fileBuild(String current, String branch) {
        String content = "<<<<<<< HEAD"
                + System.getProperty("line.separator")
                + current
                + "======="
                + System.getProperty("line.separator")
                + branch
                + ">>>>>>>"
                + System.getProperty("line.separator");
        return content;
    }

    /** Merge conflict.
     * @param c : Blob
     * @param b : Blob **/
    public void mergeConflict(Blob c, Blob b) throws IOException {
        if (c == null) {
            String content = fileBuild("", b.getContent());
        } else if (b == null) {
            String content = fileBuild(c.getContent(), "");
        }
        String content = fileBuild(c.getContent(), b.getContent());


        Utils.join(System.getProperty("user.dir"),
                c.getName()).delete();
        Utils.writeContents(Utils.join(System.getProperty("user.dir"),
                c.getName()), content);
        add(c.getName());
        System.out.println("Encountered a merge conflict.");
    }

}



