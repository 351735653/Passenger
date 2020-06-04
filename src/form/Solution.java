 package form;

import java.util.ArrayList;

import sch.Schedule;



public class Solution implements Cloneable{
    
    public ArrayList<Plan> plans;//侭嗤圭宛
    public ArrayList<Integer> pnrindex; //斤哘plans嶄Plan沫哈議pnr沫哈

    public Solution clone()
    {
        Solution sol = new Solution();
        ArrayList<Plan> plan0 = new ArrayList<Plan>();
        for(int i = 0; i < plans.size(); i++)
            plan0.add(plans.get(i).clone());
        ArrayList<Integer> index0 = new ArrayList<Integer>();
        for(int i = 0; i < pnrindex.size(); i++)
        {
            index0.add(new Integer(pnrindex.get(i).intValue()));
        }
        return sol;
    }
    
    public Solution()
    {
        plans = new ArrayList<Plan>();
        pnrindex = new ArrayList<Integer>();
    }
    public void play()
    {
        System.out.println("！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
        for(int i = 0; i < plans.size(); i++)
        {
            Schedule.pnrs.get(pnrindex.get(i)).playinfo();
            plans.get(i).play();
        }
    }

}
