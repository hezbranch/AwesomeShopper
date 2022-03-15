

public class Goal implements Comparable<Goal>{
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

    @Override
    public int compareTo(Goal g){
        System.out.println("Comparing this" + this.name 
                            + " " + position[1]
                            + " g " + g.name 
                            + " " + g.position[1]);
        if(name == "cart_return"){
            return -1;
        }
        if(g.name == "cart_return"){
            return 1;
        }

        int y_diff = (int)(g.position[1] - position[1]);
        System.out.println("y_diff" + y_diff + "  xs: " + position[0] + " , " + g.position[0]);

        // This done as a stopgap, ideally the notion of aisle could be considered
        // without it needing to be just written down. 
        double a1 = 1.5;
        double a2 = 5.5; 
        double a3 = 9.5;
        double a4 = 13.5;
        double a5 = 17.5;
        double a6 = 21.5;

        if (y_diff == 0){
            // For even aisle we set the goals in one direction 
            if(position[1] == a1 || position[1] == a5 || position[1] == a5){
                 return (int)(g.position[0] - position[0]);
            }
            // for odd aisle we set the 
            return (int)(position[0] - g.position[0]);
            // System.out.println("y_diff" + y_diff + "  xs: " + position[0] + " , " + g.position[0]);
        }
        return y_diff;
    }
}
