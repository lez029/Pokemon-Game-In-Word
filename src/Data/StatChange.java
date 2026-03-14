package Data;

import Entity.Move.Stat;

public record StatChange(
        Stat stat_change,
        int modifier
) {
}
