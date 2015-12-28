/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

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

    public static final int COMMAND_NOT_FOUND = 127;
    public static final int INTERRUPTED = 130;
    public static final int OUT_OF_RANGE = 255;
    private int mExitcode = INIT;
    private Integer mTimeout = 0;
    private Executor mExecutor;
    private final ArrayList<String> mCommands = new ArrayList<>();
    private ArrayList<String> mOutput = new ArrayList<>();
    private ArrayList<String> mErrors = new ArrayList<>();
    private long SHELLDELAY = 100;
    private boolean DEBUG = BuildConfig.DEBUG;
    private final String TAG = Cmd.class.getName();
    private boolean mUseExit = true;
    private String mRuntimeExec = "sh";
    private List<String> mRaw;

    public void execute() {
        mExecutor = new Executor(mCommands);
        if (DEBUG) {
            Log.d(TAG, "SHELLDELAY:" + SHELLDELAY);
        }
        mExecutor.start();
        try {
            if (mTimeout == 0) {
                mExecutor.join();
            } else {
                Log.i(TAG, "timeout is " + mTimeout);
                mExecutor.join(mTimeout);
            }
            if (mOutput == null)
                mOutput = new ArrayList<>();
            if (mErrors == null)
                mErrors = new ArrayList<>();
        } catch (InterruptedException e) {
            mExecutor.interrupt();
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
                if (mUseExit)
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
                exitcode = COMMAND_NOT_FOUND;
            } finally {
                if (q != null)
                    q.destroy();
                if (output_harvester != null)
                    Cmd.this.mOutput = output_harvester.getHarvest();
                if (error_harvester != null)
                    Cmd.this.mErrors = error_harvester.getHarvest();
                Cmd.this.mExitcode = this.exitcode;

                if (mRaw != null) {
                    mRaw.addAll(this.commands);
                    mRaw.add("Exitcode:" + Cmd.this.mExitcode);
                    mRaw.addAll(Cmd.this.mOutput);
                    mRaw.addAll(Cmd.this.mErrors);
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
        mCommands.add(c);
    }

    public void clearCommands() {
        this.mCommands.clear();
    }

    public void setTimeout(int ms) {
        mTimeout = ms;
    }

    public void useExit(boolean useExit) {
        this.mUseExit = useExit;
    }

    public void setShellDelay(long ms) {
        SHELLDELAY = ms;
    }

    public int getCommandCount() {
        return this.mCommands.size();
    }

    public ArrayList<String> getOutput() {
        return mOutput;
    }

    public ArrayList<String> getErrors() {
        return mErrors;
    }

    public int getExitCode() {
        return mExitcode;
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
