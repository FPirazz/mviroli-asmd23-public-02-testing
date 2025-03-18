package a01a.sol2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GUITest {

    private GUI gui;
    private Logic spy;

    @BeforeEach
    void setUpGUI() {
        this.gui  = new GUI(10);
        this.spy = spy(new LogicImpl(10));
        this.gui.setLogic(this.spy);
    }

    @Test
    @DisplayName("Check whether the GUI has been initialized correctly")
    void testInitialization() {
        assertEquals(100, this.gui.getCells().size());
    }

    @Test
    @DisplayName("Check whether clicking a button produces the correct result")
    void testCorrectButtonClick() {
        JButton buttonClicked = this.gui.getCells().entrySet().stream()
                .filter(a -> a.getValue().equals(new Position(4, 4)))
                .findFirst().get().getKey();

        verifyNoInteractions(this.gui.getLogic());
        this.gui.handleButtonClick(buttonClicked);

        assertEquals("1", buttonClicked.getText());
        verify(this.gui.getLogic(), times(1)).getMark(new Position(4, 4));
    }


    @Test
    @DisplayName("Check whether clicking a neighboring button moves the existing buttons correctly")
    void testCorrectMovement() {
        JButton markPlaced = this.gui.getCells().entrySet().stream()
                .filter(a -> a.getValue().equals(new Position(4, 4)))
                .findFirst().get().getKey();

        JButton neighborHit = this.gui.getCells().entrySet().stream()
                .filter(a -> a.getValue().equals(new Position(5, 5)))
                .findFirst().get().getKey();

        verifyNoInteractions(this.gui.getLogic());

        this.gui.handleButtonClick(markPlaced);
        verify(this.gui.getLogic(), times(1)).getMark(new Position(4, 4));
        this.gui.handleButtonClick(neighborHit);
        verify(this.gui.getLogic(), times(2)).getMark(new Position(4, 4));
    }

    @Test
    @DisplayName("Check whether the application terminates correctly under the right conditions")
    void testCorrectExiting() {
        JButton markPlaced = this.gui.getCells().entrySet().stream()
                .filter(a -> a.getValue().equals(new Position(9, 0)))
                .findFirst().get().getKey();

        JButton neighborHit = this.gui.getCells().entrySet().stream()
                .filter(a -> a.getValue().equals(new Position(9, 1)))
                .findFirst().get().getKey();

        verifyNoInteractions(this.gui.getLogic());

        this.gui.handleButtonClick(markPlaced);
        verify(this.gui.getLogic(), times(1)).getMark(new Position(4, 4));
        this.gui.handleButtonClick(neighborHit);
        verify(this.gui.getLogic(), times(2)).getMark(new Position(4, 4));

        verify(this.gui.getLogic(), times(2)).isOver();
        assertTrue(this.gui.getLogic().isOver());
    }




}
