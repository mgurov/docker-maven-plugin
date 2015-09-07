package org.jolokia.docker.maven.util;

public class WaitResult {
    public final WaitUtil.WaitStatus result;
    public final long waitedMs;

    public WaitResult(WaitUtil.WaitStatus result, long waitedMs) {
        this.result = result;
        this.waitedMs = waitedMs;
    }

    public boolean isOk() {
        return result == WaitUtil.WaitStatus.positive;
    }
}
