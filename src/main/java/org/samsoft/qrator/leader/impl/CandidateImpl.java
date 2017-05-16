package org.samsoft.qrator.leader.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.samsoft.qrator.leader.Candidate;
import org.samsoft.qrator.leader.LeaderElectionListener;

import java.io.IOException;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public class CandidateImpl extends LeaderSelectorListenerAdapter implements Candidate {

    private LeaderSelector leaderSelector;
    private final CuratorFramework curatorFrameWork;
    private final String leaderRoot;
    private LeaderElectionListener listener;
    private final String resource;

    public CandidateImpl(CuratorFramework curatorFrameWork, String leaderRoot, String resource) {
        this.curatorFrameWork = curatorFrameWork;
        this.leaderRoot = leaderRoot;
        this.resource = resource;
    }

    @Override
    public void applyForLeadership(boolean repeatedly, LeaderElectionListener listener) {
        leaderSelector = new LeaderSelector(curatorFrameWork, leaderRoot + "/" + this.resource, this);
        if (repeatedly) {
            leaderSelector.autoRequeue();
        }
        this.listener = listener;
        leaderSelector.start();
    }

    @Override
    public void close() throws IOException {
        this.listener.onElected();
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        this.listener.onElected();
    }
}
