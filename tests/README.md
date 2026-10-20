# 回归验证

需要 JDK 17，在项目根目录运行：

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean verify
```

Maven Wrapper 固定 Maven 版本，`pom.xml` 统一依赖与插件版本。首次运行需要联网下载依赖，测试自身不访问网络。
macOS / Linux 使用 `sh ./mvnw test` 或 `sh ./mvnw clean verify`。

也可以继续使用原 PowerShell 入口；它现在调用 Maven 的 `clean verify`：

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File tests/verify.ps1
```

不再需要 `-JsonJar` 参数或本地 JAR 路径。
ExecutionPolicy 参数仅作用于本次进程，不修改系统策略。

Maven 编译完整应用源码，并通过 JUnit Jupiter 执行三组测试。
Surefire 显式开启 Java 断言，原有两组回归测试也会检查断言是否生效，验证队伍、换人、长回合、背包、
普通招式完整对战、伤害边界、属性免疫、PP、能力等级和复制隔离。
`JSONDataWriterTest` 验证招式 record 的 JSON 导出和读取，覆盖空值、枚举及能力变化列表。
`MoveParser` 和 JSON 导出工具均纳入编译，但此处尚未验证网络导入、解析规则、经验成长和所有战斗边界。
测试报告位于 `target/surefire-reports/`；未发现测试时构建会失败。

缓存过完整构建依赖后可运行 `.\mvnw.cmd -o clean verify`；首次构建不能离线进行。

伤害计算目前是单目标基础实现：等级、威力、物理或特殊攻防、能力等级、
85–100% 随机浮动、本系加成和属性克制。尚未支持暴击、天气、特性、
异常状态、复杂目标类型、特殊招式效果和 PP 耗尽后的挣扎。
`applyMove(Pokemon)` 保留为招式扩展钩子；普通调用通过
`Pokemon.attack` / `Move.use` 统一执行命中检查和 PP 消耗。

规则参考：[伤害公式](https://bulbapedia.bulbagarden.net/wiki/Damage)，
[Pokémon Showdown 属性数据](https://github.com/smogon/pokemon-showdown/blob/master/data/typechart.ts)。
本实现尚未追求与特定世代逐步取整完全一致。
