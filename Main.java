package gitlet;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.util.Arrays;

/** Gitlet Main.
 *  @author Neel Dhoundiyal
 *  Collaborators: Abdullah Khan
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        if (args.length == 0 || args == null) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        ArrayList<String> commands = new ArrayList<String>();
        commands.addAll(Arrays.asList(args));
        Gitlet g = new Gitlet();
        g.run(commands);
        Utils.writeContents(new File(".gitlet/info"),
                Utils.serialize(g));
    }

}
