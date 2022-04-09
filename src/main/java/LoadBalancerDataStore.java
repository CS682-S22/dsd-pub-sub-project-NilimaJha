import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class that holds all the info.
 * @author nilimajha
 */
public class Info {
    private LeaderInfo leaderInfo = new LeaderInfo();
    private volatile int id = 0;
    private final ReentrantReadWriteLock leaderInfoLock = new ReentrantReadWriteLock();

    /**
     * increment id and return incremented value.
     * @return id
     */
    public synchronized int getId() {
        id += 1;
        return id;
    }

    /**
     * method updates the leader Info.
     * @param leaderName
     * @param leaderId
     * @param leaderIP
     * @param leaderPort
     * @return true
     */
    public boolean updateLeaderInfo(String leaderName, int leaderId, String leaderIP, int leaderPort) {
        leaderInfoLock.writeLock().lock();
        leaderInfo.setLeaderName(leaderName);
        leaderInfo.setLeaderId(leaderId);
        leaderInfo.setLeaderIP(leaderIP);
        leaderInfo.setLeaderPort(leaderPort);
        leaderInfoLock.writeLock().unlock();
        return true;
    }

    /**
     * returns the leaderInfo
     * @return currentLeaderInfo
     */
    public LeaderInfo getLeaderInfo() {
        leaderInfoLock.readLock().lock();
        System.out.println("#######Creating leaderInfo Instance.#######");
        LeaderInfo currentLeaderInfo = new LeaderInfo();
        System.out.println("#######Getting leaderInfo.#######");
        currentLeaderInfo.setLeaderName(leaderInfo.getLeaderName());
        currentLeaderInfo.setLeaderId(leaderInfo.getLeaderId());
        currentLeaderInfo.setLeaderIP(leaderInfo.getLeaderIP());
        currentLeaderInfo.setLeaderPort(leaderInfo.getLeaderPort());
        System.out.println(" LeaderName : " + leaderInfo.getLeaderName() +
                " LeaderID : " + leaderInfo.getLeaderId() +
                " LeaderIP : " + leaderInfo.getLeaderIP() +
                " LeaderPort : " + leaderInfo.getLeaderPort());

        System.out.println(" CurrentLeaderName : " + currentLeaderInfo.getLeaderName() +
                " CurrentLeaderID : " + currentLeaderInfo.getLeaderId() +
                " CurrentLeaderIP : " + currentLeaderInfo.getLeaderIP() +
                " CurrentLeaderPort : " + currentLeaderInfo.getLeaderPort());
        leaderInfoLock.readLock().unlock();
        return currentLeaderInfo;
    }

}
