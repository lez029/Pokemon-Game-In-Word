package Data;

import Entity.Move.DamageClass;
import Entity.Move.Target;
import Entity.Type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public record MoveData(
        int accuracy,
        DamageClass damageClass,
        int statusChance,
        String effect,
        String shortEffect,
        String flavorText,
        int id,
        String name,
        int power,
        int pp,
        int priority,
        ArrayList<StatChange> statChangeArrayList,
        Target target,
        Type type
) {}
