package cloud.qasino.games.pattern.strategy.algorithm;

import cloud.qasino.games.pattern.strategy.Drive;

public class NormalDrive implements Drive
{
    @Override
    public void drive() {
        System.out.println("normal dirve capability");
    }
}
