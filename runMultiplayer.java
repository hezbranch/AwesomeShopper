/*Multi Agent Implementation*/
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

