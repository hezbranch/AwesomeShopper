/*Multi Agent Implementation*/
/*In use for CheckoutAtRegister class*/

import com.diarc.DiarcComponent;
public class runMultiplayer {
    public static class AgentThread extends Thread {
        private int index;
        private int numPlayers;
        private Class<? extends DiarcComponent> agentClass;
        public AgentThread(Class<? extends com.diarc.DiarcComponent> agentClass, 
int index, int numPlayers) {
            this.index = index;
            this.numPlayers = numPlayers;
            this.agentClass = agentClass;
        }
        public void run() {
            DiarcComponent.createInstance(this.agentClass, new String[]{
                    "-agentName", "agent" + index,
                    "-agentIndex", Integer.toString(index),
                    "-numPlayers", Integer.toString(numPlayers)});
        }
    }
    public static void main(String[] args) {
        //AgentThread a1 = new AgentThread(/*Your class name here*/.class, 1, 2);
        AgentThread a1 = new AgentThread(AgentG4.class, 1, 2);
        AgentThread a2 = new AgentThread(TestAgent.class, 0, 2);
        a1.start();
        a2.start();
    }
}
