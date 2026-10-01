#!/usr/bin/env python3
"""
Linux only: opens the result file in WPS Spreadsheets at the end of an execution (also when it was
stopped). Called by com.ams.report.ReportOpener, which waits until the test run has ended; on Windows
and macOS (or Linux without WPS) ReportOpener opens the file with the default program instead.

No key presses are ever sent. WPS does not reload a file that changed on disk, so when a WPS window
that THIS script opened earlier still shows the file, that window is closed (without saving) and the
new file is opened. WPS windows the user opened themselves are never closed; if such a window shows the
file, the script only prints a note.

Usage: python3 wps_open.py /path/to/testcases_result.xlsx
Exit code 0 = opened, 1 = not opened (see the printed message).
"""
import os
import signal
import subprocess
import sys
import time

from Xlib import X, display

WPS_LAUNCHER = "/usr/bin/et"
WPS_PROGRAMS = ("/opt/kingsoft/wps-office/office6/et", "/opt/kingsoft/wps-office/office6/wpsoffice")
# Remembers the WPS processes this script started (next to this script's working folder)
PID_FILE = os.path.join(os.getcwd(), "results", "wps_opened_by_tests.txt")


def prop(d, window, name):
    try:
        value = window.get_full_property(d.intern_atom(name), X.AnyPropertyType)
        return value.value if value else None
    except Exception:
        return None


def title(d, window):
    name = prop(d, window, "_NET_WM_NAME") or prop(d, window, "WM_NAME") or b""
    return name.decode("utf-8", "replace") if isinstance(name, bytes) else str(name)


def wps_shows(file_name):
    """True when a WPS window title contains the file name."""
    try:
        d = display.Display()
    except Exception:
        return False
    for wid in prop(d, d.screen().root, "_NET_CLIENT_LIST") or []:
        window = d.create_resource_object("window", wid)
        wm_class = prop(d, window, "WM_CLASS") or b""
        wm_class = wm_class.decode("utf-8", "replace").lower() if isinstance(wm_class, bytes) else str(wm_class).lower()
        if "wps" in wm_class and file_name in title(d, window):
            return True
    return False


def wps_pids():
    pids = set()
    for pid in os.listdir("/proc"):
        if pid.isdigit():
            try:
                if os.readlink("/proc/%s/exe" % pid) in WPS_PROGRAMS:
                    pids.add(int(pid))
            except OSError:
                pass
    return pids


def wait_for(condition, seconds):
    end = time.time() + seconds
    while time.time() < end:
        if condition():
            return True
        time.sleep(0.25)
    return condition()


def close_windows_opened_by_tests():
    """Stops the WPS processes this script started earlier (nothing is saved)."""
    if not os.path.exists(PID_FILE):
        return
    with open(PID_FILE) as f:
        ours = {int(line) for line in f.read().split() if line.isdigit()}
    for pid in ours & wps_pids():
        try:
            os.kill(pid, signal.SIGTERM)
        except OSError:
            pass
    wait_for(lambda: not (ours & wps_pids()), 10)
    os.remove(PID_FILE)


def main(path):
    file_name = os.path.basename(path)
    if wps_shows(file_name):
        close_windows_opened_by_tests()
        if not wait_for(lambda: not wps_shows(file_name), 5):
            print("WPS still shows an earlier " + file_name + " (opened by you); close it and open the file again to see the new results")
            return 1

    lock = os.path.join(os.path.dirname(path), ".~" + file_name)
    if os.path.exists(lock):
        os.remove(lock)  # left over from a closed WPS window
    before = wps_pids()
    subprocess.Popen([WPS_LAUNCHER, path], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, start_new_session=True)
    opened = wait_for(lambda: os.path.exists(lock), 30)

    # Remember the WPS processes started for this file, so the next run can close them
    started = wps_pids() - before
    if started:
        os.makedirs(os.path.dirname(PID_FILE), exist_ok=True)
        with open(PID_FILE, "w") as f:
            f.write("\n".join(str(pid) for pid in sorted(started)))
    return 0 if opened else 1


if __name__ == "__main__":
    sys.exit(main(os.path.abspath(sys.argv[1])))
