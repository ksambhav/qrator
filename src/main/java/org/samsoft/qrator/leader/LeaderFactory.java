package org.samsoft.qrator.leader;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public interface LeaderFactory {

    Candidate newCandidate(String resource);
}
