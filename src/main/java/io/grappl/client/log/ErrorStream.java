package io.grappl.client.log;

import io.grappl.client.Application;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ErrorStream extends PrintStream {

    public ErrorStream(OutputStream out) {
        super(out);
    }

    @Override
    public void print(String s) {
        Application.getClientLog().log(s);
    }

    @Override
    public void write(int b) {
    }

    @Override
    public void write(byte[] buf, int off, int len) {

    }

    @Override
    public void write(byte[] b) throws IOException {

    }

    @Override
    public void println(String s) {
        System.out.println("ech " + s);
    }
}
