

public class Node implements Comparable<Node>{
    public Node(double[] pos, double fscr){
        super();
        position = pos;
        gScore = Double.POSITIVE_INFINITY;
        fScore = fscr;
        parentToThisDir = null;
    }

    public Node(double[] pos){
        super();
        position = pos;
        gScore = Double.POSITIVE_INFINITY;
        fScore = Double.POSITIVE_INFINITY;
        parent = null;
        parentToThisDir = null;
    }

    double[] position;
    double gScore;
    double fScore;
    Node parent;
    String parentToThisDir;

    
    @Override
    public int compareTo(Node n){
        if(fScore < n.fScore){
            return -1;
        } 
        if(fScore > n.fScore){
            return 1;
        }
        return 0;
    }
}
