package org.jolokia.docker.maven.config;

/**
 * @author roland
 * @since 12.10.14
 */
public class WaitConfiguration {

    /**
     * @parameter
     */
    private int time;

    /**
     * @parameter
     * @deprecated Use <http><url></url></http> instead
     */
    private String url;

    /**
     * @parameter
     */
    private HttpConfiguration http;

    /**
     * @parameter
     */
    private String log;

    /**
     * @parameter
     */
    private int shutdown;

    /**
     * @parameter
     */
    private String fail;

    public WaitConfiguration() {}

    private WaitConfiguration(Builder builder) {
        this.time = builder.time;
        this.http = new HttpConfiguration(builder.url, builder.method, builder.status);
        this.log = builder.log;
        this.shutdown = builder.shutdown;
    }


    public int getTime() {
        return time;
    }

    public String getUrl() {
        return http != null ? http.getUrl() : url;
    }

    public HttpConfiguration getHttp() {
        return http;
    }

    public String getLog() {
        return log;
    }

    public int getShutdown() {
        return shutdown;
    }

    public String getFail() {
        return fail;
    }

    // =============================================================================

    public static class Builder {
        private int time = 0,shutdown = 0;
        private String url,log,status;
        private String method;

        public Builder time(int time) {
            this.time = time;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder log(String log) {
            this.log = log;
            return this;
        }

        public Builder shutdown(int shutdown) {
            this.shutdown = shutdown;
            return this;
        }

        public WaitConfiguration build() {
            return new WaitConfiguration(this);
        }
    }

    public static class HttpConfiguration {

        /** @parameter */
        private String url;

        /** @parameter */
        private String method;

        /** @parameter */
        private String status;

        public HttpConfiguration() {}

        private HttpConfiguration(String url, String method, String status) {
            this.url = url;
            this.method = method;
            this.status = status;
        }

        public String getUrl() {
            return url;
        }

        public String getMethod() {
            return method;
        }

        public String getStatus() {
            return status;
        }
    }
}
