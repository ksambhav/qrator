package org.samsoft.qrator.leader;

import java.io.Closeable;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public interface Candidate extends Closeable {

    void applyForLeadership(boolean repeatedly, LeaderElectionListener listener);
}
