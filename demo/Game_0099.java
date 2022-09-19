package demo;

import java.util.*;

import common.Game;
import common.Machine;

public class Game_0099 extends Game
{
    private ArrayList<Machine> machines;
    private int numFaulty;
    
    public void addMachines(ArrayList<Machine> _machines, int _numFaulty)
    {
        machines = new ArrayList<Machine>(_machines);
        this.numFaulty = _numFaulty;
        //System.out.println("HELLLLO");
    }

    public void startPhase()
    {
        ArrayList<Machine> shuffled = new ArrayList<Machine>(machines);
        Collections.shuffle(shuffled);

        //randomly assigning faulty machines
        int cnt = 0;
        for(Machine X: shuffled)
        {
            X.setMachines(machines);
            if(cnt>=numFaulty)
            {
                X.setState(true);
            }
            else
            {
                cnt++;
                X.setState(false);
                System.out.println("Faulty: " + machines.indexOf(X));
            }
        }
        
        //randomly setting Leader from shuffled 
        Random Random_Number = new Random();
        int Leader_Index = Random_Number.nextInt(shuffled.size());
        System.out.println("Leader's Index: " + Leader_Index);
        machines.get(Leader_Index).setLeader();
    }
}