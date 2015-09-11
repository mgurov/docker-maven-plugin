package org.jolokia.docker.maven.access.log;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.jolokia.docker.maven.access.DockerAccess;
import org.jolokia.docker.maven.util.Logger;
import org.jolokia.docker.maven.util.Timestamp;
import org.jolokia.docker.maven.util.WaitUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class WaitLogCheckers {
    public static Collection<? extends WaitUtil.WaitChecker> getLogWaitChecker(final String logPattern,
                                                                               final String fail,
                                                                               final String containerId,
                                                                               final DockerAccess docker,
                                                                               final Logger log) {

        final List<RegexpWaitLogChecker> checkers = new ArrayList<>();

        if (null != logPattern) {
            checkers.add(new RegexpWaitLogChecker(Pattern.compile(logPattern), WaitLogCheckerType.ok));
        }

        if (null != fail) {
            checkers.add(new RegexpWaitLogChecker(Pattern.compile(fail), WaitLogCheckerType.fail));
        }

        if (checkers.isEmpty()) {
            return checkers;
        }

        final Supplier<LogGetHandle> logHandleSupplier = new Supplier<LogGetHandle>() {
            @Override
            public LogGetHandle get() {
                return docker.getLogAsync(containerId, new LogCallback() {
                    @Override
                    public void log(int type, Timestamp timestamp, String txt) throws DoneException {
                        for (RegexpWaitLogChecker checker : checkers) {
                            checker.onLogLine(txt);
                        }
                    }

                    @Override
                    public void error(String error) {
                        log.error(error);
                    }
                });
            }
        };

        final Supplier<LogGetHandle> cachedLogHandleSupplier = Suppliers.memoize(logHandleSupplier);

        for (RegexpWaitLogChecker checker : checkers) {
            checker.setLogHandleSupplier(cachedLogHandleSupplier);
        }

        return Collections.unmodifiableCollection(checkers);
    }

    private enum WaitLogCheckerType {
        ok {
            @Override
            public WaitUtil.WaitStatus toStatus() {
                return WaitUtil.WaitStatus.positive;
            }
        },
        fail {
            @Override
            public WaitUtil.WaitStatus toStatus() {
                return WaitUtil.WaitStatus.negative;
            }
        };

        public abstract WaitUtil.WaitStatus toStatus();
    }

    private static class RegexpWaitLogChecker implements WaitUtil.WaitChecker {

        private final Pattern pattern;

        private final WaitLogCheckerType whenDetected;

        private volatile WaitUtil.WaitStatus detected = WaitUtil.WaitStatus.unknown;

        private Supplier<LogGetHandle> logHandleSupplier;

        private boolean tracing = false;

        public RegexpWaitLogChecker(Pattern pattern, WaitLogCheckerType whenDetected) {
            this.pattern = pattern;
            this.whenDetected = whenDetected;
        }

        public void onLogLine(String txt) throws LogCallback.DoneException {
            if (pattern.matcher(txt).find()) {
                detected = whenDetected.toStatus();
                throw new LogCallback.DoneException();
            }
        }

        @Override
        public WaitUtil.WaitStatus check() {
            if (!tracing) {
                tracing = null != logHandleSupplier.get();
            }
            return detected;
        }

        @Override
        public void cleanUp() {
            if (tracing) {
                logHandleSupplier.get().finish(); //TODO: protect against double reset? Might not be needed.
            }
        }

        @Override
        public boolean isRequired() {
            return WaitLogCheckerType.ok == whenDetected;
        }

        public void setLogHandleSupplier(Supplier<LogGetHandle> logHandleSupplier) {
            this.logHandleSupplier = logHandleSupplier;
        }

        @Override
        public String toString() {
            return String.format("%s on log '%s'", whenDetected, pattern);
        }
    }
}
