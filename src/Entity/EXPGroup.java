package Entity;

public interface EXPGroup {
    int calculateEXPNeed(int level);
}

class SlowestGroup implements EXPGroup {
    @Override
    public int calculateEXPNeed(int level) {
        if (level <= 15)
            return (int)(0.02 * Math.pow(level, 3) * (level + 73) / 3);
        if (level <= 36)
            return (int)(0.02 * Math.pow(level, 4) + 0.28 * Math.pow(level, 3));
        if (level <= 100)
            return (int)(0.02 * Math.pow(level, 3) + (double) (level + 64) / 2);
        else return 0;
    }
}

class MediumSlowGroup implements EXPGroup {
    @Override
    public int calculateEXPNeed(int level) {
        return (int)(1.2 * Math.pow(level, 3)
                - 15 * Math.pow(level, 2)
                + 100 * level - 140);
    }
}

class SlowGroup implements EXPGroup {
    @Override
    public int calculateEXPNeed(int level) {
        return (int)(1.25 * Math.pow(level, 3));
    }
}

class FastGroup implements EXPGroup {
    @Override
    public int calculateEXPNeed(int level) {
        return (int)(0.8 * Math.pow(level, 3));
    }
}

class MediumFastGroup implements EXPGroup {
    @Override
    public int calculateEXPNeed(int level) {
        return (int)Math.pow(level, 3);
    }
}

class FastestGroup implements  EXPGroup {
    @Override
    public int calculateEXPNeed(int level) {
        if (level <= 50)
            return (int)(-0.02 * Math.pow(level, 4) + 2 * Math.pow(level, 3));
        if (level <= 68)
            return (int)(-0.01 * Math.pow(level, 4) + 1.5 * Math.pow(level, 3));
        if (level <= 98)
            return (int)(-0.002 * Math.pow(level, 3) * (1911 - 10 * level) / 3);
        if (level <= 100)
            return (int)(-0.01 * Math.pow(level, 4) + 1.6 * Math.pow(level, 3));
        else return 0;
    }
}