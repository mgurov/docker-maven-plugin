package org.jolokia.docker.maven.util;

public class WaitResult {
    public final boolean ok;
    public final long waitedMs;

    public WaitResult(boolean ok, long waitedMs) {
        this.ok = ok;
        this.waitedMs = waitedMs;
    }
}
