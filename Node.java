

public class Node implements Comparable<Node>{
    public Node(double[] pos, double fscr){
        super();
        position = pos;
        g = Double.POSITIVE_INFINITY;
        double fScore = fscr;
        parentToThisDir = null;
    }

    public Node(double[] pos){
        super();
        position = pos;
        g = Double.POSITIVE_INFINITY;
        fScore = Double.POSITIVE_INFINITY;
        parent = null;
        parentToThisDir = null;
    }

    @Override
    public int compareTo(Node n){{
        if(fScore < n.fscore){
            return -1;
        } 
        if(fscore > n.fscore){
            return 1;
        }
        return 0;
    }

    double[] position;
    double gScore;
    double fScore;
    Node parent;
    String parentToThisDir;
}
