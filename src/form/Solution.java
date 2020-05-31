 package form;

import java.util.ArrayList;
import java.util.Arrays;

import sch.Schedule;



public class Solution implements Cloneable{
    
    public ArrayList<Plan> plans;//匯耗頼屁議圭宛
    public int[] pnrindex; //斤哘plans嶄Plan沫哈議pnr沫哈

    public Solution clone()
    {
        Solution sol=new Solution();
        ArrayList<Plan> plan0=new ArrayList<Plan>();
        for(int i=0;i<plans.size();i++)
            plan0.add(plans.get(i).clone());
        sol.pnrindex = pnrindex.clone();
        return sol;
    }
    
    public Solution()
    {
        plans = new ArrayList<Plan>();
        pnrindex = new int[Schedule.pnrs.size()];
    }
    public void play()
    {
        System.out.println("！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
        for(int i = 0; i < plans.size(); i++)
        {
            Schedule.pnrs.get(pnrindex[i]).playinfo();
            plans.get(i).play();
        }
    }

}
