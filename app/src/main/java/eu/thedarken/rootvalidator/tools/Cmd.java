package eu.thedarken.rootvalidator.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import eu.thedarken.rootvalidator.BuildConfig;

public class Cmd {
    public static final int INIT = 99;
    public static final int OK = 0;
    public static final int PROBLEM = 1;

    public static final int COMMAND_PERMISSION_PROBLEM = 126;
    public static final int COMMAND_FAILED = 127;
    public static final int INTERRUPTED = 130;
    private int exitcode = INIT;
    private Integer timeout = 0;
    private Executor exe;
    private final ArrayList<String> commands = new ArrayList<>();
    private ArrayList<String> output = new ArrayList<>();
    private ArrayList<String> errors = new ArrayList<>();
    private long SHELLDELAY = 100;
    private boolean DEBUG = BuildConfig.DEBUG;
    private final String TAG = Cmd.class.getName();
    private boolean useExit = true;
    private String mRuntimeExec = "sh";
    private List<String> mRaw;

    public void execute() {
        exe = new Executor(commands);
        if (DEBUG) {
            Log.d(TAG, "SHELLDELAY:" + SHELLDELAY);
        }
        exe.start();
        try {
            if (timeout == 0) {
                exe.join();
            } else {
                Log.i(TAG, "timeout is " + timeout);
                exe.join(timeout);
            }
            if (output == null)
                output = new ArrayList<>();
            if (errors == null)
                errors = new ArrayList<>();
        } catch (InterruptedException e) {
            exe.interrupt();
            Thread.currentThread().interrupt();
        }
    }

    private class Executor extends Thread {
        private final String TAG = Executor.class.getName();
        private Process q = null;
        private ArrayList<String> commands = new ArrayList<>();
        private int exitcode = INIT;
        private StreamHarvester error_harvester;
        private StreamHarvester output_harvester;

        public Executor(ArrayList<String> commands) {
            this.commands = commands;
        }

        @Override
        public void run() {
            try {
                if (mRaw != null)
                    mRaw.add("### START ###");
                if (SHELLDELAY > 0)
                    Thread.sleep(SHELLDELAY);
                if (mRaw != null)
                    mRaw.add("Process:" + mRuntimeExec);
                q = Runtime.getRuntime().exec(mRuntimeExec);
                OutputStreamWriter os = new OutputStreamWriter(q.getOutputStream());
                // loop commands
                for (String s : commands) {
                    os.write(s + "\n");
                    os.flush();
                    if (DEBUG)
                        Log.d(TAG, s);
                }
                if (useExit)
                    os.write("exit\n");
                os.flush();
                os.close();

                error_harvester = new StreamHarvester(q.getErrorStream(), "Error");
                output_harvester = new StreamHarvester(q.getInputStream(), "Output");

                error_harvester.start();
                output_harvester.start();

                exitcode = q.waitFor();

                error_harvester.join();
                output_harvester.join();

                if (DEBUG)
                    Log.d(this.TAG, "Exitcode: " + exitcode);
            } catch (InterruptedException interrupt) {
                if (DEBUG)
                    Log.w(this.TAG, "Interrupted!");
                exitcode = INTERRUPTED;
                return;
            } catch (IOException e) {
                if (DEBUG)
                    Log.w(this.TAG, "IOException, command failed? not found?");
                exitcode = COMMAND_FAILED;
            } finally {
                if (q != null)
                    q.destroy();
                if (output_harvester != null)
                    Cmd.this.output = output_harvester.getHarvest();
                if (error_harvester != null)
                    Cmd.this.errors = error_harvester.getHarvest();
                Cmd.this.exitcode = this.exitcode;

                if (mRaw != null) {
                    mRaw.addAll(this.commands);
                    mRaw.add("Exitcode:" + Cmd.this.exitcode);
                    mRaw.addAll(Cmd.this.output);
                    mRaw.addAll(Cmd.this.errors);
                    mRaw.add("### END ###");
                    mRaw.add(" ");
                }
            }
        }
    }

    public void setRaw(List<String> raw) {
        mRaw = raw;
    }

    public String getRuntimeExec() {
        return mRuntimeExec;
    }

    public void setRuntimeExec(String runtimeExec) {
        mRuntimeExec = runtimeExec;
    }

    public void addCommand(String c) {
        commands.add(c);
    }

    public void clearCommands() {
        this.commands.clear();
    }

    public void setTimeout(int ms) {
        timeout = ms;
    }

    public void useExit(boolean useExit) {
        this.useExit = useExit;
    }

    public void setShellDelay(long ms) {
        SHELLDELAY = ms;
    }

    public int getCommandCount() {
        return this.commands.size();
    }

    public ArrayList<String> getOutput() {
        return output;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public int getExitCode() {
        return exitcode;
    }

    class StreamHarvester extends Thread {
        private final String TAG = StreamHarvester.class.getName();
        private InputStream is;
        private ArrayList<String> output = new ArrayList<String>();
        private String type;

        StreamHarvester(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    output.add(line);
                    if (DEBUG)
                        Log.v(this.TAG, type + ":" + output.size() + ":" + line);
                }
                br.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        public ArrayList<String> getHarvest() {
            return output;
        }
    }
}
