package demo;

import java.util.*;

import common.Location;
import common.Machine;

public class Machine_0099 extends Machine
{
    private final int LEFT = 0;
    private final int RIGHT = 1;

    private ArrayList<Machine> Machines;
    private int Number_Machines;
    private int Number_Faulty_Machines;

    private int Step_Size;
    private Location position = new Location(0, 0);
    private Location direction = new Location(1, 0);
    private int ID;

    private int current_Phase = -1;
    private boolean State;
    private boolean isLeader = false;
    private Random Random_Number = new Random();

    private int Round_0_Decision;
    private int Round_1_Decision;
    private int Round_2_Decision;

    private boolean Round_0_Taken;
    private boolean Round_1_Taken;
    private boolean Round_2_Taken;

    private int Round_1_LEFT;
    private int Round_2_LEFT;

    private int Round_1_RIGHT;
    private int Round_2_RIGHT;

    @Override
    public void setStepSize(int stepSize)
    {
        Step_Size = stepSize;
    }

    @Override

    public void setState(boolean state)
    {
        State = state;
    }

    @Override
    public String name()
    {
        return "0099" + ID;
    }

    @Override
    public Location getPosition()
    {
        return new Location(position.getX(), position.getY());
    }

    @Override
    public void setMachines(ArrayList<Machine> machines)
    {
        Machines = new ArrayList<Machine>(machines);
        //System.out.println("Machines Set: " + this.machines);
        Number_Machines = machines.size();

        Number_Faulty_Machines = (Number_Machines - 1)/3;

        ID = Machines.indexOf(this);
    }

    @Override
    public void setLeader()
    {
        isLeader = true;
        
        if(Random_Number.nextInt(2) == 0)
        {
            Round_0_Decision = LEFT;
        }
        else
        {
            Round_0_Decision = RIGHT;
        }
        
        System.out.println("Leader's Decision: " + Round_0_Decision);
        
        current_Phase++;

        Circulate(State, Round_0_Decision, 0);
    }

    @Override
    public void move()
    {
        position.setLoc(position.getX()+direction.getX()*Step_Size, position.getY()+direction.getY()*Step_Size);
        Round_1_LEFT = 0;
        Round_2_LEFT = 0;

        Round_1_RIGHT = 0;
        Round_2_RIGHT = 0;

        Round_0_Taken = false;
        Round_1_Taken = false;
        Round_2_Taken = false;

		State = true;
    }

    @Override
    public void sendMessage(int sourceID, int phaseNum, int roundNum, int decision)
    {
        if(phaseNum > current_Phase)
        {
            current_Phase = phaseNum;
            isLeader = false;
        }

        if(phaseNum==current_Phase)
        {
            if(roundNum==0 && !Round_0_Taken)
            {
                Round_0_Taken=true;
                Round_0_Decision = decision;
                Circulate(State, Round_0_Decision, 1);
            }

            if(roundNum==1 && !Round_1_Taken)
            {
                if(decision == RIGHT) 
                {
                    Round_1_RIGHT++;
                }
                else 
                {
                    Round_1_LEFT++;
                }
                
                if(Round_1_LEFT<Round_1_RIGHT)
                {
                    Round_1_Decision = RIGHT;
                }
                else
                {
                    Round_1_Decision = LEFT;
                }

                if(Math.max(Round_1_LEFT,Round_1_RIGHT) > Number_Faulty_Machines)
                {
                    Round_1_Taken = true;
                    Circulate(State, Round_1_Decision, 2);
                }
            }

            if(roundNum==2 && !Round_2_Taken)
            {
                if(decision == RIGHT) 
                {
                    Round_2_RIGHT++;
                }
                else 
                {
                    Round_2_LEFT++;
                }
            
                if((Round_2_LEFT >= 2*Number_Faulty_Machines + 1) || (Round_2_RIGHT >= 2*Number_Faulty_Machines + 1)) 
                {
                    Round_2_Taken = true;
                    if(State) 
                    {
                        if(Round_2_LEFT<Round_2_RIGHT)
                        {
                        Round_2_Decision = RIGHT;
                        }
                        else
                        {
                           Round_2_Decision = LEFT;
                        }
                    }
                    else 
                    {
                        Round_2_Decision = Random_Number.nextInt(2);
                    }
                    

                    int x = direction.getX();
                    int y = direction.getY();

                    if(x == -1 || x == 1) 
                    {
                        x = 0;
                    }
                    else if(x == 0) 
                    {
                        if(Round_2_Decision != LEFT)
                        {
                            x = 1;
                        }
                        else
                        {
                            x = -1;
                        }
                    }

                    if(y == -1 || y == 1) 
                    {
                        y = 0;
                    }

		            else if(y == 0) 
                    {
                        if(Round_2_Decision != LEFT)
                        {
                            y = 1;
                        }
                        else
                        {
                            y = -1;
                        }
                    }

                    direction.setLoc(x, y);

                    System.out.println("Phase Number: " + phaseNum);
                    System.out.println("Machine " + ID +" Final Decison: " + Round_2_Decision);
                    System.out.println("R2L " + Round_2_LEFT + ", R2R " + Round_2_RIGHT);
                }
            }
        }
    }


    private void Circulate(boolean State, int Decision, int Round)
    {
        ArrayList<Machine> List = new ArrayList<Machine>(Machines);
        if(Round!=0)
        {
            if(State)
            {
                for(Machine X: List)
                {
                    X.sendMessage(ID, current_Phase, Round, Decision);
                }
            }
            else
            {
                int A = Random_Number.nextInt(2);
                int B = Random_Number.nextInt(2);
                if(A==0)
                {
                    for(Machine X: List)
                    {
                        X.sendMessage(ID, current_Phase, Round, B);
                    }
                }
            }
        }
        else
        {
            if(State)
            {
                for(Machine X: List)
                {
                    X.sendMessage(ID, current_Phase, Round, Decision);
                }
            }
            else
            {
                int decision = Random_Number.nextInt(2);

                Collections.shuffle(List);
                int Messages = Number_Machines - Random_Number.nextInt(Number_Faulty_Machines);
                //System.out.println("Leader's Faulte Decision: " + decision);
                int cnt = 0;
                boolean state = false;
                for(Machine X: List)
                {
                    if(cnt>Messages)
                    {
                        break;
                    }
                    cnt++;
                    if(this==X)
                    {
                        state = true;
                    }
                }
                cnt = 0;
                if(state)
                {
                    for(Machine X: List)
                    {
                        if(cnt>Messages)
                        {
                            break;
                        }
                        cnt++;
                        X.sendMessage(ID, current_Phase, Round, decision);
                    }
                }
                else
                {
                    this.sendMessage(ID, current_Phase, Round, decision);
                    for(Machine X: List)
                    {
                        X.sendMessage(ID, current_Phase, Round, decision);
                        if(cnt>Messages-1)
                        {
                            break;
                        }
                        cnt++;
                    }
                }
            }
        }
    }
}