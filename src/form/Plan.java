package form;

import java.util.ArrayList;

import sch.Schedule;
import tool.DATE;


public class Plan implements Cloneable{
    
	public ArrayList<Itinerary> itineraries;
	public double cost;
    public int pnrindex;
    public int[] decisionvars;
	
	public Plan(ArrayList<Itinerary> it) {
		itineraries = new ArrayList<Itinerary>();
		decisionvars = new int[1];  
		for(int i = 0 ; i < it.size(); i++)
		{
			itineraries.add(it.get(i).clone());
		}
		double cost = -1;
		pnrindex = -1;
	}

	public Plan () {
	    itineraries = new ArrayList<Itinerary>();
	    decisionvars = new int[Schedule.pnrs.size() + (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size()];// + longleg
	    cost = -1;
	    pnrindex = -1;
	}
	
	public Plan clone(){
	    Plan clone = null;
        try {
            clone = (Plan)super.clone();
            clone.itineraries = new ArrayList<Itinerary>();
            for(int i = 0 ; i < this.itineraries.size(); i++)
    	    {
    	        clone.itineraries.add(this.itineraries.get(i).clone());
    	    }
            clone.cost = this.cost;
            clone.pnrindex = this.pnrindex;
            clone.decisionvars = this.decisionvars;
            return clone;
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
             e.printStackTrace();
        }
        return clone;

	}
	
	//计算Plan的变量
	public void calPlanVars() {
	    decisionvars[pnrindex] = 1;
	    for(int i = 0; i < itineraries.size(); i++)
	    {
	        int flightinid = itineraries.get(i).inid;
	        int seatid = Paras.CAB_SEQ.get(itineraries.get(i).largeCab);
	        int pnrnum = Schedule.pnrs.get(pnrindex).pnrNum;
	        decisionvars[Schedule.pnrs.size() - 1  
	                     +flightinid * Paras.CAB_SEQ.size() 
	                     +seatid + 1 ] = pnrnum;
	    }
	    
	    // + longleg
	}
	
	
    /**计算方案代价
     * @param plan
     * @param pindex
     * @return
     */
    public double calPlanCost(int pindex) {
        if(itineraries.get(0).longleg == true)
        {
            // + longleg
        }
         double result = -1;
         Plan ori = Schedule.getOriPlan(pindex);
         double passenger = 0;
         double company = 0;
         double cabchangecost = 0;//舱位变动
         double changecost = 0;//行程变动
         double timecost = 0;//总耗时变动
         double sixhrcost = 0;//接近程度(大于六小时航段)
         double mctcost = 0;//mct时间
         int cab = 0;
         int changenum = 0;
         long oritime = 0; //分钟
         long plantime = 0;
         int timediff = 0;
         int mcttime = 0;
         DATE s1 = ori.itineraries.get(0).depTime.clone();
         DATE e1 = ori.itineraries.get(1).ariTime.clone();
         oritime = e1.difftime_min(s1);
//         System.out.println("原始计划耗时: "+ oritime);
         DATE s2 = itineraries.get(0).depTime.clone();
         DATE e2 = itineraries.get(1).ariTime.clone();
         plantime = e2.difftime_min(s2);
//         System.out.println("计划总耗时: "+ plantime);
         timecost = (oritime - plantime) * Paras.TOTAL_TIME;
//         System.out.println("总耗时代价：" + timecost);
         DATE ofa = ori.itineraries.get(0).ariTime.clone();
         DATE osd = ori.itineraries.get(1).depTime.clone();
         DATE pfa = itineraries.get(0).ariTime.clone();
         DATE psd = itineraries.get(1).depTime.clone();
         for(int i = 0; i < itineraries.size(); i++)
         {
             //
             if(Math.abs(itineraries.get(i).depTime.clone().difftime_min(ori.itineraries.get(i).depTime.clone())) >= 360)
             {
                 timediff++;
             }
             //
             if(itineraries.get(i).ori == false)
             {
                 changenum++;
             }
             //
             String plancab = itineraries.get(i).largeCab;
             String oricab = ori.itineraries.get(i).largeCab;
             cab += Paras.CAB_SEQ.get(plancab) - Paras.CAB_SEQ.get(oricab);
             //
         }
         if(cab > 0)// 降舱
         {
             cabchangecost = Math.abs(cab)*Paras.CAB_DOWN;
             company = Math.abs(cab) * Paras.PROFIT_DOWN;
         }
         else//升舱或不变
         {
             cabchangecost = Math.abs(cab)*Paras.CAB_UP;
             company = Math.abs(cab) * Paras.PROFIT_UP;
         }
//         System.out.println("舱位变动代价：" + cabchangecost);
         changecost = changenum * Paras.CHANGE_NUM;
//         System.out.println("变动次数代价： " + changecost);
         sixhrcost = timediff * Paras.SIM_DEG;
//         System.out.println("接近程度代价：" + sixhrcost);
         mctcost = (osd.difftime_min(ofa) - psd.difftime_min(pfa)) * Paras.MCT_TIME;
//         System.out.println("衔接时间代价："+ mctcost);
//         System.out.println("————————————————————————————————————");
         passenger = cabchangecost +    //舱位变动
                     changecost +       //是否原航班变动
                     timecost +         //总耗时变动
                     sixhrcost +        //接近程度(大于六小时航段)
                     mctcost;           //mct时间
         //航司收益
         result = Paras.satisfaction * passenger + Paras.profit * company;
         return result;
    }
	
	
	/**
	 * 输出Plan详情
	 */
	public void play() {
	    System.out.println("COST = " + cost);
	    System.out.println("PNR索引：" + pnrindex);
	    System.out.print("变量："+"[");
	    
	    for(int i = 0; i < decisionvars.length; i++)
	    {
	        System.out.print(i+":"+decisionvars[i] +" ");
	    }
	    System.out.println("]");
		for(int i = 0 ;i < itineraries.size(); i++)
		{
			System.out.print("第"+i+"段行程 : " + itineraries.get(i).toString());
			System.out.println();
		}
	}
}
