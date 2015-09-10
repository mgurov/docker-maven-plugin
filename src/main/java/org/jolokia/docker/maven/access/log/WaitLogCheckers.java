package org.jolokia.docker.maven.access.log;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.jolokia.docker.maven.access.DockerAccess;
import org.jolokia.docker.maven.util.Logger;
import org.jolokia.docker.maven.util.Timestamp;
import org.jolokia.docker.maven.util.WaitUtil;

public class WaitLogCheckers {
    public static WaitUtil.WaitChecker getLogWaitChecker(final String logPattern, final String fail, final String containerId, final DockerAccess docker, final Logger log) {
        return new WaitUtil.WaitChecker() {

            private LogGetHandle logHandle;
            private volatile WaitUtil.WaitStatus detected = WaitUtil.WaitStatus.unknown;

            @Override
            public WaitUtil.WaitStatus check() {
                if (null == logHandle) {
                    final Predicate<CharSequence> ok = null == logPattern ? Predicates.<CharSequence>alwaysFalse() : Predicates.containsPattern(logPattern);
                    final Predicate<CharSequence> ko = null == fail ? Predicates.<CharSequence>alwaysFalse() : Predicates.containsPattern(fail);
                    logHandle = docker.getLogAsync(containerId, new LogCallback() {
                        @Override
                        public void log(int type, Timestamp timestamp, String txt) throws DoneException {
                            if (ok.apply(txt)) {
                                detected = WaitUtil.WaitStatus.positive;
                                throw new DoneException();
                            }
                            if (ko.apply(txt)) {
                                detected = WaitUtil.WaitStatus.negative;
                                throw new DoneException();
                            }
                        }

                        @Override
                        public void error(String error) {
                            log.error(error);
                        }
                    });
                }
                return detected;
            }

            @Override
            public void cleanUp() {
                if (logHandle != null) {
                    logHandle.finish();
                }
            }

            @Override
            public boolean isRequired() {
                return logPattern != null;
            }
        };
    }
}
