import Data.MoveData;
import Data.StatChange;
import Entity.Move.DamageClass;
import Entity.Move.Stat;
import Entity.Move.Target;
import Entity.Type.Type;
import Tools.Writer.JSONDataWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JSONDataWriterTest {
    @TempDir
    Path directory;

    @Test
    void moveDataRoundTripPreservesNullsEnumsAndStatChanges() throws IOException {
        MoveData move = new MoveData(null, DamageClass.STATUS, null,
                "Raises attack.", "Raises attack.", "Test move.", 1, "test-boost",
                0, 20, 0, new ArrayList<>(List.of(new StatChange(Stat.ATTACK, 2))),
                Target.USER, Type.NORMAL);
        Path file = directory.resolve("move.json");

        new JSONDataWriter<MoveData>().write(file.toString(), move);

        MoveData restored = new ObjectMapper().readValue(file.toFile(), MoveData.class);
        assertEquals(move, restored);
    }
}
