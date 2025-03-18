package a01a.sol2;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUI extends JFrame {
    
    private static final long serialVersionUID = -6218820567019985015L;
    private final Map<JButton, Position> cells = new HashMap<>();
    private Logic logic;
    
    public GUI(int size) {
        Log.setLOGGER(Logger.getLogger(LogicImpl.class.getName()));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(70*size, 70*size);
        this.logic = new LogicImpl(size);
        
        JPanel panel = new JPanel(new GridLayout(size,size));
        this.getContentPane().add(panel);
        
        ActionListener al = e -> {
            Log.getLOGGER().log(Level.INFO, "Button clicked");
            var jb = (JButton)e.getSource();
            this.logic.hit(this.cells.get(jb));
            for (var entry: this.cells.entrySet()){
                entry.getKey().setText(
                    this.logic
                        .getMark(entry.getValue())
                        .map(String::valueOf)
                        .orElse(" "));
            }
            if (this.logic.isOver()){
                System.exit(0);
            }
        };
                
        for (int i=0; i<size; i++){
            for (int j=0; j<size; j++){
            	final JButton jb = new JButton();
                this.cells.put(jb, new Position(j,i));
                jb.addActionListener(al);
                panel.add(jb);
            }
        }
        this.setVisible(true);
    }


    // Below are the methods created ad-hoc for testing
    // Setter method
    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    // Getter method for the cells for the logic
    public Logic getLogic() {
        return this.logic;
    }

    // Getter method for the cells
    public Map<JButton, Position> getCells() {
        return this.cells;
    }

    // Method to check if the game is finished
    public boolean isGameFinished() {
        return this.logic.isOver();
    }

    // New method to handle button clicks
    public void handleButtonClick(JButton button) {
        Position position = this.cells.get(button);
        this.logic.hit(position);

        for (var entry : this.cells.entrySet()) {
            entry.getKey().setText(
                    this.logic
                            .getMark(entry.getValue())
                            .map(String::valueOf)
                            .orElse(" "));
        }
    }
    
}
