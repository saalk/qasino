package cloud.qasino.games.pattern.strategy.algorithm;

import cloud.qasino.games.pattern.strategy.Drive;

public class LuxuryDrive implements Drive
{
    @Override
    public void drive() {
        System.out.println("luxury drive capability");
    }
}
