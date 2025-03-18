package a01a.sol2;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LogicImpl implements Logic {

    private final int size;
    private List<Position> marks = new LinkedList<>();
    private boolean moving = false;

    public LogicImpl(int size) {
        this.size = size;

        Log.setLOGGER(Logger.getLogger(LogicImpl.class.getName()));
    }

    @Override
    public Optional<Integer> hit(Position position) {
        if (this.isOver()){
            return Optional.empty();
        }
        if (this.moving || startMoving(position)){
            this.moving = true;
            this.moveMarks();
            return Optional.empty();
        }
        Log.getLOGGER().log(Level.INFO, "Placing mark at " + position);
        this.marks.add(position);
        return Optional.of(this.marks.size());
    }

    private boolean neighbours(Position p1, Position p2){
        Log.getLOGGER().log(Level.INFO, "Calculating neighbors of " + p1 + " to see if " + p2 + " is in the range");
        return Math.abs(p1.x()-p2.x()) <= 1 && Math.abs(p1.y()-p2.y()) <= 1;
    }

    private boolean startMoving(Position position) {
        return this.marks.stream().anyMatch(p -> neighbours(p, position));
    }

    private void moveMarks() {
        Log.getLOGGER().log(Level.INFO, "Move all marks");
        this.marks = this.marks
                .stream()
                .map(p -> new Position(p.x()+1, p.y()-1))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<Integer> getMark(Position position) {
        Log.getLOGGER().log(Level.INFO, "Getting mark at position " + position + " = " + Optional.of(
                this.marks.indexOf(position)).filter(i -> i>=0).map(i -> i+1)
        );
        return Optional.of(this.marks.indexOf(position)).filter(i -> i>=0).map(i -> i+1);
    }

    @Override
    public boolean isOver() {
        if(this.marks.stream().anyMatch(p -> p.x() == this.size || p.y() == -1)) Log.getLOGGER().log(Level.INFO, "Application is over");
        return this.marks.stream().anyMatch(p -> p.x() == this.size || p.y() == -1);
    }
}
