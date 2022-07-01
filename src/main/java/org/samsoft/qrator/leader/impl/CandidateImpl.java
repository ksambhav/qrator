package org.samsoft.qrator.leader.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.samsoft.qrator.leader.Candidate;
import org.samsoft.qrator.leader.LeaderElectionListener;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
class CandidateImpl extends LeaderSelectorListenerAdapter implements Candidate {

    private final CuratorFramework curatorFrameWork;
    private final String leaderRoot;
    private final String resource;
    private LeaderElectionListener listener;

    public CandidateImpl(CuratorFramework curatorFrameWork, String leaderRoot, String resource) {
        this.curatorFrameWork = curatorFrameWork;
        this.leaderRoot = leaderRoot;
        this.resource = resource;
    }

    @Override
    public void applyForLeadership(boolean repeatedly, LeaderElectionListener listener) {
        LeaderSelector leaderSelector = new LeaderSelector(curatorFrameWork, leaderRoot + "/" + this.resource, this);
        if (repeatedly) {
            leaderSelector.autoRequeue();
        }
        this.listener = listener;
        leaderSelector.start();
    }

    @Override
    public void close() {
        this.listener.onElected();
    }

    @Override
    public void takeLeadership(CuratorFramework client) {
        this.listener.onElected();
    }
}
