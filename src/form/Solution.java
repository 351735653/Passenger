 package form;

import java.util.ArrayList;
<<<<<<< HEAD
=======
import java.util.Arrays;
>>>>>>> 589de7df336608d60781c6b4bbeb4e20d99c1a30

import sch.Schedule;



public class Solution implements Cloneable{
    
<<<<<<< HEAD
    public ArrayList<Plan> plans;//���з���
    public ArrayList<Integer> pnrindex; //��Ӧplans��Plan������pnr����

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
=======
    public ArrayList<Plan> plans;//һ�������ķ���
    public int[] pnrindex; //��Ӧplans��Plan������pnr����

    public Solution clone()
    {
        Solution sol=new Solution();
        ArrayList<Plan> plan0=new ArrayList<Plan>();
        for(int i=0;i<plans.size();i++)
            plan0.add(plans.get(i).clone());
        sol.pnrindex = pnrindex.clone();
>>>>>>> 589de7df336608d60781c6b4bbeb4e20d99c1a30
        return sol;
    }
    
    public Solution()
    {
        plans = new ArrayList<Plan>();
<<<<<<< HEAD
        pnrindex = new ArrayList<Integer>();
=======
        pnrindex = new int[Schedule.pnrs.size()];
>>>>>>> 589de7df336608d60781c6b4bbeb4e20d99c1a30
    }
    public void play()
    {
        System.out.println("��������������������������������������������������������������������������������������������������������������������");
        for(int i = 0; i < plans.size(); i++)
        {
<<<<<<< HEAD
            Schedule.pnrs.get(pnrindex.get(i)).playinfo();
=======
            Schedule.pnrs.get(pnrindex[i]).playinfo();
>>>>>>> 589de7df336608d60781c6b4bbeb4e20d99c1a30
            plans.get(i).play();
        }
    }

}
