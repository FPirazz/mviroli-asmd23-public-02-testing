package a01a.sol2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LogicTest {

    private Logic logic;

    @BeforeEach
    void setUpLogic() {
        this.logic  = new LogicImpl(10);
    }

    @Test
    @DisplayName("Check correct mark placement and retrieval")
    void testMarkPlacementAndRetrieval() {
        assertEquals(Optional.empty(), logic.getMark(new Position(4, 4)));

        Optional<Integer> placedMark = logic.hit(new Position(4, 4));

        assertEquals(placedMark, logic.getMark(new Position(4, 4)));
    }

    @Test
    @DisplayName("Check whether the application ends correctly")
    void testApplicationEnds() {
        logic.hit(new Position(9, 9));
        assertFalse(logic.isOver());

        logic.hit(new Position(9, 8));

        assertEquals(Optional.empty(), logic.hit(new Position(0, 0)));
        assertTrue(logic.isOver());
    }

    @Test
    @DisplayName("Check remaining coverage actions")
    void testCoverageCompletely() {

        logic.hit(new Position(7, 7));
        logic.hit(new Position(5, 7));
        logic.hit(new Position(7, 9));


        logic.hit(new Position(4, 4));
        logic.hit(new Position(3, 5));
        assertEquals(Optional.of(4), logic.getMark(new Position(5, 3)));

        logic.hit(new Position(4, 4));
        assertEquals(Optional.of(4), logic.getMark(new Position(6, 2)));
    }

}
