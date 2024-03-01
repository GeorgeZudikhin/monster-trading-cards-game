package gameManager;

import java.util.concurrent.Semaphore;

public class BattleLockManager {
    private static final Semaphore battleLock = new Semaphore(1);

    public static boolean tryAcquireBattle() {
        return battleLock.tryAcquire();
    }

    public static void releaseBattle() {
        battleLock.release();
    }
}
