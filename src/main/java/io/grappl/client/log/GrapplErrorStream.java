package io.grappl.client.log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class GrapplErrorStream extends PrintStream {

    private GrapplLog theLog;

    public GrapplErrorStream(GrapplLog log, OutputStream out) {
        super(out);
        theLog = log;
    }

    @Override
    public void print(String s) {
        theLog.log(s);
    }

    @Override
    public void println(String s) {
        System.out.println("ech " + s);
    }

    @Override
    public void write(int b) {}

    @Override
    public void write(byte[] buf, int off, int len) {}

    @Override
    public void write(byte[] b) throws IOException {}
}
