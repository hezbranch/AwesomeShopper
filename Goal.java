

public class Goal {
    public Goal(String nme, double[] pos){
        super();
        name = nme;
        position = pos;
    }
    String name;
    double[] position;

    @Override
    public String toString(){
        return String.format("%1$s %2$d %3$d", name, position[0], position[1]);
    }
}
