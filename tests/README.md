# 离线回归验证

需要 JDK 17 和本机已有的 `org.json` JAR，在项目根目录运行：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File tests/verify.ps1
```

自定义 JSON 依赖位置：在命令末尾增加 `-JsonJar "完整路径/json.jar"`。
ExecutionPolicy 参数仅作用于本次进程，不修改系统策略。测试不访问网络。

脚本编译核心代码并开启 Java 断言，验证队伍、换人、长回合、背包、
普通招式完整对战、伤害边界、属性免疫、PP、能力等级和复制隔离。
Jackson JSON 导出及依赖它的 MoveParser 不在此脚本的编译范围内。

伤害计算目前是单目标基础实现：等级、威力、物理或特殊攻防、能力等级、
85–100% 随机浮动、本系加成和属性克制。尚未支持暴击、天气、特性、
异常状态、复杂目标类型、特殊招式效果和 PP 耗尽后的挣扎。
`applyMove(Pokemon)` 保留为招式扩展钩子；普通调用通过
`Pokemon.attack` / `Move.use` 统一执行命中检查和 PP 消耗。

规则参考：[伤害公式](https://bulbapedia.bulbagarden.net/wiki/Damage)，
[Pokémon Showdown 属性数据](https://github.com/smogon/pokemon-showdown/blob/master/data/typechart.ts)。
本实现尚未追求与特定世代逐步取整完全一致。
