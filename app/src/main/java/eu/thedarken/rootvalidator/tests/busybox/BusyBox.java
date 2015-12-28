/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator.tests.busybox;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BusyBox implements Parcelable {
    protected static final String[] APPLETS = {"[", "[[", "acpid", "addgroup", "adduser", "adjtimex", "ar", "arp", "arping", "ash", "awk", "basename", "beep", "blkid",
            "brctl", "bunzip2", "bzcat", "bzip2", "cal", "cat", "catv", "chat", "chattr", "chgrp", "chmod", "chown", "chpasswd", "chpst", "chroot", "chrt",
            "chvt", "cksum", "clear", "cmp", "comm", "cp", "cpio", "crond", "crontab", "cryptpw", "cut", "date", "dc", "dd", "deallocvt", "delgroup",
            "deluser", "depmod", "devmem", "df", "dhcprelay", "diff", "dirname", "dmesg", "dnsd", "dnsdomainname", "dos2unix", "dpkg", "du", "dumpkmap",
            "dumpleases", "echo", "ed", "egrep", "eject", "env", "envdir", "envuidgid", "expand", "expr", "fakeidentd", "false", "fbset", "fbsplash",
            "fdflush", "fdformat", "fdisk", "fgrep", "find", "findfs", "flash_lock", "flash_unlock", "fold", "free", "freeramdisk", "fsck", "fsck.minix",
            "fsync", "ftpd", "ftpget", "ftpput", "fuser", "getopt", "getty", "grep", "gunzip", "gzip", "hd", "hdparm", "head", "hexdump", "hostid", "hostname",
            "httpd", "hush", "hwclock", "id", "ifconfig", "ifdown", "ifenslave", "ifplugd", "ifup", "inetd", "init", "inotifyd", "insmod", "install", "ionice",
            "ip", "ipaddr", "ipcalc", "ipcrm", "ipcs", "iplink", "iproute", "iprule", "iptunnel", "kbd_mode", "kill", "killall", "killall5", "klogd", "last",
            "length", "less", "linux32", "linux64", "linuxrc", "ln", "loadfont", "loadkmap", "logger", "login", "logname", "logread", "losetup", "lpd", "lpq",
            "lpr", "ls", "lsattr", "lsmod", "lzmacat", "lzop", "lzopcat", "makemime", "man", "md5sum", "mdev", "mesg", "microcom", "mkdir", "mkdosfs",
            "mkfifo", "mkfs.minix", "mkfs.vfat", "mknod", "mkpasswd", "mkswap", "mktemp", "modprobe", "more", "mount", "mountpoint", "mt", "mv", "nameif",
            "nc", "netstat", "nice", "nmeter", "nohup", "nslookup", "od", "openvt", "passwd", "patch", "pgrep", "pidof", "ping", "ping6", "pipe_progress",
            "pivot_root", "pkill", "popmaildir", "printenv", "printf", "ps", "pscan", "pwd", "raidautorun", "rdate", "rdev", "readlink", "readprofile",
            "realpath", "reformime", "renice", "reset", "resize", "rm", "rmdir", "rmmod", "route", "rpm", "rpm2cpio", "rtcwake", "run-parts", "runlevel",
            "runsv", "runsvdir", "rx", "script", "scriptreplay", "sed", "sendmail", "seq", "setarch", "setconsole", "setfont", "setkeycodes", "setlogcons",
            "setsid", "setuidgid", "sh", "sha1sum", "sha256sum", "sha512sum", "showkey", "slattach", "sleep", "softlimit", "sort", "split",
            "start-stop-daemon", "stat", "strings", "stty", "su", "sulogin", "sum", "sv", "svlogd", "swapoff", "swapon", "switch_root", "sync", "sysctl",
            "syslogd", "tac", "tail", "tar", "taskset", "tcpsvd", "tee", "telnet", "telnetd", "test", "tftp", "tftpd", "time", "timeout", "top", "touch", "tr",
            "traceroute", "true", "tty", "ttysize", "udhcpc", "udhcpd", "udpsvd", "umount", "uname", "uncompress", "unexpand", "uniq", "unix2dos", "unlzma",
            "unlzop", "unzip", "uptime", "usleep", "uudecode", "uuencode", "vconfig", "vi", "vlock", "volname", "watch", "watchdog", "wc", "wget", "which",
            "who", "whoami", "xargs", "yes", "zcat", "zcip"};
    private final File mPath;
    private final boolean mPrimary;
    boolean mExecutable = true;
    String mVersion;
    String mPermission;
    String mOwner;
    String mGroup;
    private final List<String> mAvailableApplets = new ArrayList<>();
    private final List<String> mMissingApplets = new ArrayList<>(Arrays.asList(APPLETS));

    public BusyBox(File path, boolean primary) {
        this.mPath = path;
        this.mPrimary = primary;
    }

    public boolean isSufficient() {
        return true;
    }

    public boolean isPrimary() {
        return mPrimary;
    }

    public List<String> getMissingApplets() {
        return mMissingApplets;
    }

    public List<String> getAvailableApplets() {
        return mAvailableApplets;
    }

    public String getGroup() {
        return mGroup;
    }

    public String getOwner() {
        return mOwner;
    }

    public String getPermission() {
        return mPermission;
    }

    public boolean isExecutable() {
        return mExecutable;
    }

    public File getPath() {
        return mPath;
    }

    public String getVersion() {
        return mVersion;
    }

    @Override
    public String toString() {
        return "mPath:" + mPath.getAbsolutePath() + " | " +
                "primary:" + mPrimary + " | " +
                "mExecutable:" + mExecutable + " | " +
                "mVersion:" + mVersion + " | " +
                "mPermission:" + mPermission + " | " +
                "mOwner:" + mOwner + " | " +
                "mGroup:" + mGroup + " | " +
                "applets:" + "HAVE-" + mAvailableApplets.size() + " MIA-" + mMissingApplets.size() + " | " +
                "mPath:" + mPath.getAbsolutePath();
    }

    public BusyBox(Parcel in) {
        mPath = new File(in.readString());
        mPrimary = in.readByte() != 0;
        mExecutable = in.readByte() != 0;
        mVersion = in.readString();
        mPermission = in.readString();
        mOwner = in.readString();
        mGroup = in.readString();
        in.readStringList(mAvailableApplets);
        in.readStringList(mMissingApplets);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mPath.getAbsolutePath());
        out.writeByte((byte) (mPrimary ? 1 : 0));
        out.writeByte((byte) (mExecutable ? 1 : 0));
        out.writeString(mVersion);
        out.writeString(mPermission);
        out.writeString(mOwner);
        out.writeString(mGroup);
        out.writeStringList(mAvailableApplets);
        out.writeStringList(mMissingApplets);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<BusyBox> CREATOR = new Creator<BusyBox>() {
        public BusyBox createFromParcel(Parcel in) {
            return new BusyBox(in);
        }

        public BusyBox[] newArray(int size) {
            return new BusyBox[size];
        }
    };
}

