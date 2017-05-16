package org.samsoft.qrator.leader.impl;

import org.apache.curator.framework.CuratorFramework;
import org.samsoft.qrator.leader.Candidate;
import org.samsoft.qrator.leader.LeaderFactory;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public class LeaderFactoryImpl implements LeaderFactory {

    private final CuratorFramework curatorFrameWork;
    private final String rootLeaderPath;

    public LeaderFactoryImpl(CuratorFramework curatorFrameWork, String rootLeaderPath) {
        this.curatorFrameWork = curatorFrameWork;
        this.rootLeaderPath = rootLeaderPath;
    }

    @Override
    public Candidate newCandidate(String resource) {
        return new CandidateImpl(curatorFrameWork, rootLeaderPath, resource);
    }
}
