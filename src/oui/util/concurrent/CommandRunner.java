/*
 * This code is in the public domain and comes with no warranty.
 * http://blog.fivesight.com/prb/space/Call+an+External+Command+from+Java
 */
package oui.util.concurrent;

//import java.io.FileInputStream;
import gov.usgs.cawsc.gui.GuiUtilities;
import java.lang.Process;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.swing.JOptionPane;
import oui.gui.OuiGui;

//import java.io.StringBufferInputStream;
//import java.io.StringReader;

//import EDU.oswego.cs.dl.util.concurrent.BrokenBarrierException;
//import EDU.oswego.cs.dl.util.concurrent.CyclicBarrier;
//import EDU.oswego.cs.dl.util.concurrent.TimeoutException;

public class CommandRunner extends Thread {
    
    private boolean _waitForExit = false;
    private String _command;
    private int _timeout = 0;
    private boolean _destroyOnTimeout = true;
    
    private InputStream _stdin;
    private OutputStream _stdout;
    private OutputStream _stderr;
    
    private static final int BUF = 4096;
    
    private int _xit;
    
    private Throwable _thrownError;
    
    private CyclicBarrier _barrier;
    
    public CommandRunner () {}
    
    public CommandRunner(String command) {
        setCommand(command);
        setDestroyOnTimeout(false);
        setStdErrorStream(System.err);
        setStdOutputStream(System.out);
    }
    
    public static int RUN_SUCCESSFUL = 0;
    public static int RUN_FAILED = 1;
    
    public static int runModel(String arg, String completionMessage) {
        int ret = RUN_SUCCESSFUL;
        CommandRunner cr = new CommandRunner(arg);
        
        try {
            cr.evaluate();
            if (completionMessage != null) {
                JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()), completionMessage, "Run Status", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()), "Run Unsuccessful\n" + e.getMessage(), "Run Status", JOptionPane.ERROR_MESSAGE);
            ret = RUN_FAILED;
        }
        return ret;
    }
    
    public int getExitValue() {
        return _xit;
    }
    
    public void setCommand(String s) {
        _command = s;
    }
    
    public String getCommand() {
        return _command;
    }
    
    public void setInputStream(InputStream is) {
        _stdin = is;
    }
    
    public void setStdOutputStream(OutputStream os) {
        _stdout = os;
    }
    
    public void setStdErrorStream(OutputStream os) {
        _stderr = os;
    }
    
    public void run () {
        try {
        evaluate();
        } catch (IOException e) {
            
        }
    }
    
    public void evaluate() throws IOException {
        Process proc = Runtime.getRuntime().exec(_command);
				
        _barrier = new CyclicBarrier(3 + ((_stdin != null) ? 1 : 0));
				
        PullerThread so =
        new PullerThread("STDOUT", proc.getInputStream(), _stdout);
        so.start();
        
        PullerThread se =
        new PullerThread("STDERR", proc.getErrorStream(), _stderr);
        se.start();
        
        PusherThread si = null;
        if (_stdin != null) {
            si = new PusherThread("STDIN", _stdin, proc.getOutputStream());
            si.start();
        }
        
        boolean _timedout = false;
        long end = System.currentTimeMillis() + _timeout * 1000;
        
        try {
            if (_timeout == 0) {
                _barrier.barrier();
            } else {
                _barrier.attemptBarrier(_timeout * 1000);
            }
        } catch (TimeoutException ex) {
            _timedout = true;
            if (si != null) {
                si.interrupt();
            }
            so.interrupt();
            se.interrupt();
            if (_destroyOnTimeout) {
                proc.destroy();
            }
        } catch (BrokenBarrierException bbe) {
            /* IGNORE */
        } catch (InterruptedException e) {
            /* IGNORE */
        }
        
        _xit = -1;
        
        if (!_timedout) {
            if (_waitForExit) {
                do {
                    try {
                        _xit = proc.exitValue();
                        Thread.sleep(250);
                    } catch (InterruptedException ie) {
                        /* IGNORE */
                    } catch (IllegalThreadStateException iltse) {
                        continue;
                    }
                    break;
                } while (!(_timedout = (System.currentTimeMillis() > end)));
            } else {
                try {
                    _xit = proc.exitValue();
                } catch (IllegalThreadStateException iltse) {
                    _timedout = true;
                }
            }
        }
        
        if (_timedout) {
            if (_destroyOnTimeout) {
                proc.destroy();
            }
        }
    }
    
    public Throwable getThrownError() {
        return _thrownError;
    }
    
    private class PumperThread extends Thread {
        
        private OutputStream _os;
        private InputStream _is;
        
        private volatile boolean _kaput;
        
        private boolean _closeInput;
        
        protected PumperThread(
        String name,
        InputStream is,
        OutputStream os,
        boolean closeInput) {
            super(name);
            _is = is;
            _os = os;
            _closeInput = closeInput;
        }
        
        public void run() {
            _kaput = false;
            try {
                byte[] buf = new byte[BUF];
                int read = 0;
                while (!isInterrupted() && (read = _is.read(buf)) != -1) {
                    if (read == 0)
                        continue;
                    _os.write(buf, 0, read);
                    _os.flush();
                }
            } catch (Throwable t) {
                _thrownError = t;
                return;
            } finally {
                try {
                    if (_closeInput) {
                        _is.close();
                    } else {
                        _os.close();
                    }
                } catch (IOException ioe) {
                    /* IGNORE */
                }
            }
            try {
                _barrier.barrier();
            } catch (InterruptedException ie) {
                /* IGNORE */
            } catch (BrokenBarrierException bbe) {
                /* IGNORE */
            }
        }
    }
    
    private class PusherThread extends PumperThread {
        PusherThread(String name, InputStream is, OutputStream os) {
            super(name, is, os, false);
        }
    }
    
    private class PullerThread extends PumperThread {
        PullerThread(String name, InputStream is, OutputStream os) {
            super(name, is, os, true);
        }
    }
    
    public int getTimeout() {
        return _timeout;
    }
    
    public void setTimeout(int timeout) {
        _timeout = timeout;
    }
    
    public boolean getDestroyOnTimeout() {
        return _destroyOnTimeout;
    }
    
    public void setDestroyOnTimeout(boolean destroyOnTimeout) {
        _destroyOnTimeout = destroyOnTimeout;
    }
    
    public boolean getWaitForExit() {
        return _waitForExit;
    }
    
    public void setWaitForExit(boolean waitForExit) {
        _waitForExit = waitForExit;
    }
    
    public static void main(String[] args) throws Exception {
        CommandRunner cr = new CommandRunner();
        cr.setWaitForExit(true);
        cr.setDestroyOnTimeout(false);
        
//        cr.setCommand("sed -e s/public/private/");
//        cr.setInputStream(new FileInputStream("/home/projects/oui/oui/src/oui/util/concurrent/CommandRunner.java"));
        
//        cr.setCommand("/home/software/firefox/firefox /home/projects/oui/oui/doc/oui/index.html");
        cr.setCommand("/usr/bin/python");

        cr.setStdErrorStream(System.err);
        cr.setStdOutputStream(System.out);
        
        System.out.println ("before");
        cr.start();
//        cr.setInputStream(new StringBufferInputStream ("help()"));
//        cr.setInputStream(new StringBufferInputStream ("help(object)"));
        System.out.println ("after");
        
        System.out.println("output value: " + cr.getExitValue());
        System.out.println("wait for exit: " + cr.getWaitForExit());
    }
}