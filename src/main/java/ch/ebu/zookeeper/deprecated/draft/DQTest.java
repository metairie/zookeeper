package ch.ebu.zookeeper.deprecated.draft;

import ch.ebu.zookeeper.fwk.CountdownWatcher;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class DQTest {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("USAGE: DQTest hostPort znode ");
            System.exit(2);
        }
        String hostPort = args[0];
        String znode = args[1];

        // instantiate
        List<ZooKeeper> lz = new ArrayList<ZooKeeper>();
        DQTest dqTest = new DQTest(lz);
        dqTest.testOffer1();
    }

    private static final Logger LOG = LoggerFactory.getLogger(DQTest.class);

    private List<ZooKeeper> allClients;

    public DQTest(List<ZooKeeper> allClients) {
        this.allClients = allClients;
    }

    private static String getHexSessionId(long sessionId) {
        return "0x" + Long.toHexString(sessionId);
    }

    private ZooKeeper createClient() throws IOException, InterruptedException {
        CountdownWatcher watcher = new CountdownWatcher();
        return createClient(watcher, "109.74.206.103:2181", 3000);
    }

    private ZooKeeper createClient(CountdownWatcher watcher, String hp, int timeout) throws IOException, InterruptedException {
        watcher.reset();
        ZooKeeper zk = new ZooKeeper(hp, timeout, watcher);
        if (!watcher.clientConnected.await(timeout, TimeUnit.MILLISECONDS)) {
            Assert.fail("Unable to connect to server");
        }
        synchronized (this) {
/*            boolean allClientsSetup = false;
            if (!allClientsSetup) {
                LOG.error("allClients never setup");
                Assert.fail("allClients never setup");
            }*/
            if (allClients != null) {
                allClients.add(zk);

            } else {
                // test done - close the zk, not needed
                zk.close();
            }
        }
        return zk;
    }

    @After
    public void tearDown() throws Exception {
        LOG.info("FINISHED ");
    }

    @Test
    public void testOffer1() throws Exception {
        String dir = "/testOffer1";
        String testString = "Hello World";
        final int num_clients = 1;
        ZooKeeper clients[] = new ZooKeeper[num_clients];
        DistributedQueue queueHandles[] = new DistributedQueue[num_clients];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = createClient();
            queueHandles[i] = new DistributedQueue(clients[i], dir, null);
        }

        queueHandles[0].offer(testString.getBytes());

        /*byte dequeuedBytes[] = queueHandles[0].remove();
        Assert.assertEquals(new String(dequeuedBytes), testString);*/
    }

    @Test
    public void testOffer2() throws Exception {
        String dir = "/testOffer2";
        String testString = "Hello World";
        final int num_clients = 2;
        ZooKeeper clients[] = new ZooKeeper[num_clients];
        DistributedQueue queueHandles[] = new DistributedQueue[num_clients];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = createClient();
            queueHandles[i] = new DistributedQueue(clients[i], dir, null);
        }

        queueHandles[0].offer(testString.getBytes());

       /* byte dequeuedBytes[] = queueHandles[1].remove();
        Assert.assertEquals(new String(dequeuedBytes), testString);*/
    }

    @Test
    public void testTake1() throws Exception {
        String dir = "/testTake1";
        String testString = "Hello World";
        final int num_clients = 1;
        ZooKeeper clients[] = new ZooKeeper[num_clients];
        DistributedQueue queueHandles[] = new DistributedQueue[num_clients];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = createClient();
            queueHandles[i] = new DistributedQueue(clients[i], dir, null);
        }

        queueHandles[0].offer(testString.getBytes());

        byte dequeuedBytes[] = queueHandles[0].take();
        Assert.assertEquals(new String(dequeuedBytes), testString);
    }


    @Test
    public void testRemove1() throws Exception {
        String dir = "/testRemove1";
        String testString = "Hello World";
        final int num_clients = 1;
        ZooKeeper clients[] = new ZooKeeper[num_clients];
        DistributedQueue queueHandles[] = new DistributedQueue[num_clients];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = createClient();
            queueHandles[i] = new DistributedQueue(clients[i], dir, null);
        }

        try {
            queueHandles[0].remove();
        } catch (NoSuchElementException e) {
            return;
        }
        Assert.fail();
    }

    private void createNremoveMtest(String dir, int n, int m) throws Exception {
        String testString = "Hello World";
        final int num_clients = 2;
        ZooKeeper clients[] = new ZooKeeper[num_clients];
        DistributedQueue queueHandles[] = new DistributedQueue[num_clients];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = createClient();
            queueHandles[i] = new DistributedQueue(clients[i], dir, null);
        }

        for (int i = 0; i < n; i++) {
            String offerString = testString + i;
            queueHandles[0].offer(offerString.getBytes());
        }

        byte data[] = null;
        for (int i = 0; i < m; i++) {
            data = queueHandles[1].remove();
        }
        Assert.assertNotNull(data);
        Assert.assertEquals(new String(data), testString + (m - 1));
    }

    @Test
    public void testRemove2() throws Exception {
        createNremoveMtest("/testRemove2", 10, 2);
    }

    @Test
    public void testRemove3() throws Exception {
        createNremoveMtest("/testRemove3", 1000, 1000);
    }

    private void createNremoveMelementTest(String dir, int n, int m) throws Exception {
        String testString = "Hello World";
        final int num_clients = 2;
        ZooKeeper clients[] = new ZooKeeper[num_clients];
        DistributedQueue queueHandles[] = new DistributedQueue[num_clients];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = createClient();
            queueHandles[i] = new DistributedQueue(clients[i], dir, null);
        }

        for (int i = 0; i < n; i++) {
            String offerString = testString + i;
            queueHandles[0].offer(offerString.getBytes());
        }

        byte data[] = null;
        for (int i = 0; i < m; i++) {
            data = queueHandles[1].remove();
        }
        Assert.assertNotNull(data);
        Assert.assertEquals(new String(queueHandles[1].element()), testString + m);
    }

    @Test
    public void testElement1() throws Exception {
        createNremoveMelementTest("/testElement1", 1, 0);
    }

    @Test
    public void testElement2() throws Exception {
        createNremoveMelementTest("/testElement2", 10, 2);
    }

    @Test
    public void testElement3() throws Exception {
        createNremoveMelementTest("/testElement3", 1000, 500);
    }

    @Test
    public void testElement4() throws Exception {
        createNremoveMelementTest("/testElement4", 1000, 1000 - 1);
    }

    @Test
    public void testTakeWait1() throws Exception {
        String dir = "/testTakeWait1";
        final String testString = "Hello World";
        final int num_clients = 1;
        final ZooKeeper clients[] = new ZooKeeper[num_clients];
        final DistributedQueue queueHandles[] = new DistributedQueue[num_clients];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = createClient();
            queueHandles[i] = new DistributedQueue(clients[i], dir, null);
        }

        final byte[] takeResult[] = new byte[1][];
        Thread takeThread = new Thread() {
            public void run() {
                try {
                    takeResult[0] = queueHandles[0].take();
                } catch (KeeperException | InterruptedException e) {
                    LOG.error(e.getLocalizedMessage());
                }
            }
        };
        takeThread.start();

        Thread.sleep(1000);
        Thread offerThread = new Thread() {
            public void run() {
                try {
                    queueHandles[0].offer(testString.getBytes());
                } catch (KeeperException | InterruptedException e) {
                    LOG.error(e.getLocalizedMessage());
                }
            }
        };
        offerThread.start();
        offerThread.join();

        takeThread.join();

        Assert.assertNotNull(takeResult[0]);
        Assert.assertEquals(new String(takeResult[0]), testString);
    }

    @Test
    public void testTakeWait2() throws Exception {
        String dir = "/testTakeWait2";
        final String testString = "Hello World";
        final int num_clients = 1;
        final ZooKeeper clients[] = new ZooKeeper[num_clients];
        final DistributedQueue queueHandles[] = new DistributedQueue[num_clients];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = createClient();
            queueHandles[i] = new DistributedQueue(clients[i], dir, null);
        }
        int num_attempts = 2;
        for (int i = 0; i < num_attempts; i++) {
            final byte[] takeResult[] = new byte[1][];
            final String threadTestString = testString + i;
            Thread takeThread = new Thread() {
                public void run() {
                    try {
                        takeResult[0] = queueHandles[0].take();
                    } catch (KeeperException | InterruptedException e) {
                        LOG.error(e.getLocalizedMessage());
                    }
                }
            };
            takeThread.start();

            Thread.sleep(1000);
            Thread offerThread = new Thread() {
                public void run() {
                    try {
                        queueHandles[0].offer(threadTestString.getBytes());
                    } catch (KeeperException | InterruptedException e) {
                        LOG.error(e.getLocalizedMessage());
                    }
                }
            };
            offerThread.start();
            offerThread.join();

            takeThread.join();

            Assert.assertNotNull(takeResult[0]);
            Assert.assertEquals(new String(takeResult[0]), threadTestString);
        }
    }
}
