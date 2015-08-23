package org.jolokia.docker.maven.config;

import org.jolokia.docker.maven.util.EnvUtil;

import java.util.*;

public class Arguments {

    private String shell;

    private List<String> exec;

    private List<String> execInlined = new ArrayList<>();

    public Arguments() {
        this(null);
    }

    public Arguments(String shell) {
        this.shell = shell;
    }

    public void set(String shell) {
        setShell(shell);
    }

    public void setShell(String shell) {
        this.shell = shell;
    }

    public String getShell() {
        return shell;
    }

    public void setExec(List<String> exec) {
        this.exec = exec;
    }

    public void setArg(String arg) {
        this.execInlined.add(arg);
    }

    public List<String> getExec() {
        return exec == null ? execInlined : exec;
    }

    public void validate() throws IllegalArgumentException {
        int valueSources = 0;
        if (shell != null) {
            valueSources ++;
        }
        if (exec != null && !exec.isEmpty()) {
            valueSources ++;
        }
        if (!execInlined.isEmpty()) {
            valueSources ++;
        }

        if (valueSources != 1){
            throw new IllegalArgumentException("Argument conflict: either shell or args should be specified and only in one form.");
        }
    }

    public List<String> asStrings() {
        if (shell != null) {
            return Arrays.asList(EnvUtil.splitOnSpaceWithEscape(shell));
        }
        if (exec != null) {
            return Collections.unmodifiableList(exec);
        }
        return Collections.unmodifiableList(execInlined);
    }

    public static class Builder {
        private String shell;
        private List<String> params;

        public static Builder get(){
            return new Builder();
        }

        public Builder withShell(String shell){
            this.shell = shell;
            return this;
        }

        public Builder withParam(String param){
            if (params == null) {
                params = new ArrayList<>();
            }
            this.params.add(param);
            return this;
        }

        public Arguments build(){
            Arguments a = new Arguments();
            a.setShell(shell);
            a.setExec(params);
            return a;
        }
    }
}
