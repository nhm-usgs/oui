import os, re
import subprocess
import getpass
from time import gmtime, strftime

GIT = "C:/Program Files (x86)/Git/cmd/git.exe"
JAVAC = "C:/Program Files/Java/jdk1.8.0_131/bin/javac"
VERSION_FILE = "version.properties"

def get_git_revision_hash():
    return subprocess.check_output([GIT, 'rev-parse', 'HEAD'])

def get_git_origin():
    return subprocess.check_output([GIT, 'config', '--get', 'remote.origin.url'])

def get_git_tag():
    tag = subprocess.check_output([GIT, 'tag'])
    if (len(tag) == 0):
        return "\n"
    else:
        return tag

def get_git_commitdate():
    return subprocess.check_output([GIT, 'log', '-1', '--format=%cd'])

def get_javac_version():
    return subprocess.check_output([JAVAC, "-version"], stderr=subprocess.STDOUT).rstrip() + "\n"

def SysInfo():
    values  = {}
    cache   = os.popen2("SYSTEMINFO")
    source  = cache[1].read()
    sysOpts = ["Host Name", "OS Name", "OS Version", "Product ID", "System Manufacturer", "System Model", "System type", "BIOS Version", "Domain", "Windows Directory", "Total Physical Memory", "Available Physical Memory", "Logon Server"]

    for opt in sysOpts:
        values[opt] = [item.strip() for item in re.findall("%s:\w*(.*?)\n" % (opt), source, re.IGNORECASE)][0]
    return values

def get_user_name():
    return getpass.getuser() + "\n"

sha = get_git_revision_hash()
origin = get_git_origin()
tag = get_git_tag()
cdate = get_git_commitdate()
cver = get_javac_version()
values = SysInfo()
username = get_user_name()
time = strftime("%Y-%m-%d", gmtime()) + "\n"

target = open(VERSION_FILE, 'w')
target.write("SHA=" + sha)
target.write("ORIGIN=" + origin)
target.write("TAG=" + tag)
target.write("COMMITDATE=" + cdate)
target.write("COMPILER=" + cver)
target.write("OS_Version=" + values["OS Name"] + " " + values["OS Version"] + " " + values["System Manufacturer"] + " " + values["System Model"] + "\n")
target.write("BUILDER=" + username)
target.write("BUILDDATE=" + time)
target.close()