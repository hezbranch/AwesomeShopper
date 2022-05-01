/*Multi Agent Implementation (New Version)*/
/*In use for CheckoutAtRegister class*/
import com.diarc.DiarcComponent;

public class runMultiplayer {


    public static class AgentThread extends Thread {
        private int index;
        private int numPlayers;
        private Class<? extends DiarcComponent> agentClass;

        public AgentThread(Class<? extends com.diarc.DiarcComponent> agentClass, int index, int numPlayers) {
            this.index = index;
            this.numPlayers = numPlayers;
            this.agentClass = agentClass;
        }

        public void run() {
            DiarcComponent.createInstance(this.agentClass, new String[]{
                    "-agentName", "agent" + index, 
                    "-agentIndex", Integer.toString(index)});
	    //                    "-numPlayers", Integer.toString(numPlayers)});
        }
    }


    public static void main(String[] args) {
        AgentThread a1 = new AgentThread(AgentG4.class, 0, 2); //For Some reason our current agent works the best in multiplayer mode if it can be the first agent in the list order
        AgentThread a2 = new AgentThread(TestAgent.class, 1, 2);
        //AgentThread a3 = new AgentThread(TestAgent.class, 1, 2);
    a1.start();
        System.out.println("G4's Agent is starting!");
    a2.start();
    //a3.start();
    }
}

/*
    public static void main(String[] args) {
	Thread[] threads;
	if (args.length > 0) {
	    threads = new Thread[args.length];
	    for(int i=0; i<args.length; i++) {
		try {
		    threads[i] = new AgentThread((Class<? extends com.diarc.DiarcComponent>)java.lang.Class.forName(args[i]),i,args.length);
		}
		catch(ClassNotFoundException cnfe) {
		    System.err.println("Could not find " + args[i]);
		}
	    }
	    for(Thread t:threads) {
		t.start();
	    }
	}
    }
}
*/
