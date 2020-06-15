package form;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
	    decisionvars = new int[Schedule.pnrs.size() + (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size()
	                           + Schedule.longlegnum * 2 * Paras.CAB_SEQ.size()];// + longleg 613
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
	
	//����Plan�ı���
	public void calPlanVars() {
	    decisionvars[pnrindex] = 1;
	    
        /*	    for(int i = 0; i < itineraries.size(); i++)
        {
            int flightinid = itineraries.get(i).inid;
            int seatid = Paras.CAB_SEQ.get(itineraries.get(i).largeCab);
            int pnrnum = Schedule.pnrs.get(pnrindex).pnrNum;
            decisionvars[Schedule.pnrs.size() - 1  
                         +flightinid * Paras.CAB_SEQ.size() 
                         +seatid + 1 ] = pnrnum;
        }
        */
	    if(itineraries.get(0).longleg == true)//longleg613
	    {
	        
	            int flightinid = itineraries.get(0).inid;
	            int longindex = flightinid + 1 - (Schedule.availflights.size() - Schedule.longlegnum);
	            int seatid = Paras.CAB_SEQ.get(itineraries.get(0).largeCab);
	            int pnrnum = Schedule.pnrs.get(pnrindex).pnrNum;
	            decisionvars[Schedule.pnrs.size() + 
	                         (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size() +
	                         Paras.CAB_SEQ.size() * 2 * (longindex - 1)
	                         +seatid] = pnrnum;
	            decisionvars[Schedule.pnrs.size() + 
                             (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size() +
                             Paras.CAB_SEQ.size() * 2 * (longindex - 1)
                             + seatid
                             + Paras.CAB_SEQ.size()] = pnrnum;
	    }
	    else {
	        for(int i = 0; i < itineraries.size(); i++)
	        {
	            int flightinid = itineraries.get(i).inid;
	            int seatid = Paras.CAB_SEQ.get(itineraries.get(i).largeCab);
	            int pnrnum = Schedule.pnrs.get(pnrindex).pnrNum;
	            decisionvars[Schedule.pnrs.size() - 1  
	                         +flightinid * Paras.CAB_SEQ.size() 
	                         +seatid + 1 ] = pnrnum;
	            if(Schedule.long_short.containsValue(flightinid))
	            {
	                Set<Integer> lindex = Schedule.long_short.keySet();
	                for(int longindex : lindex)
	                {
	                    List<Integer> shortindex = (List<Integer>)Schedule.long_short.get(longindex);
	                    for(int j = 0; j < shortindex.size(); j++)
	                    {
	                        if(flightinid == shortindex.get(j))
	                        {
	                            int longloc = longindex + 1 - (Schedule.availflights.size() - Schedule.longlegnum);
	                            int shortloc =  j;
	                            decisionvars[Schedule.pnrs.size() + 
	                                         (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size() +
	                                         Paras.CAB_SEQ.size() * 2 * (longloc - 1)
	                                         + Paras.CAB_SEQ.size() * shortloc
	                                         + seatid] = pnrnum;
	                        }
	                    }
	                }
	            }
	        }
	        
        }
	    
	}
	
	
    /**���㷽������
     * @param plan
     * @param pindex
     * @return
     * @throws Exception 
     */
    public double calPlanCost(int pindex) throws Exception {
        if(itineraries.get(0).longleg == true)//�������ÿ�
        {
            // + longleg613
            Plan t = new Plan();
            
            List<Integer> flightsId = (List<Integer>)Schedule.long_short.get(itineraries.get(0).inid);
            for(int i = 0; i < flightsId.size(); i++)
            {
                Flight ff = Schedule.availflights.get(flightsId.get(i));
                Itinerary ii = new Itinerary(ff.inid, ff.flightID, itineraries.get(0).largeCab, "a", ff.depAirport, ff.depTime.toDateTime()
                    , ff.AriAirport, ff.AriTime.toDateTime(), ff.ori, ff.longleg);
                t.itineraries.add(ii);
            }
            return calPlanCost_Longleg(t, pindex);
        }
        else if(itineraries.size() == 1)//�����ÿ�
        {
            double result = -1;
            Plan ori = Schedule.getOriPlan(pindex);
            double passenger = 0;
            double company = 0;
            double cabchangecost = 0;//��λ�䶯
            double changecost = 0;//�г̱䶯
            double timecost = 0;//�ܺ�ʱ�䶯
            double sixhrcost = 0;//�ӽ��̶�(������Сʱ����)
            double mctcost = 0;//mctʱ��
            int cab = 0;
            int changenum = 0;
            long oritime = 0; //����
            long plantime = 0;
            int timediff = 0;
            int mcttime = 0;
            DATE s1 = ori.itineraries.get(0).depTime.clone();
            DATE e1 = ori.itineraries.get(0).ariTime.clone();
            oritime = e1.difftime_min(s1);
            DATE s2 = itineraries.get(0).depTime.clone();
            DATE e2 = itineraries.get(0).ariTime.clone();
            plantime = e2.difftime_min(s2);
            timecost = (oritime - plantime) * Paras.TOTAL_TIME;
            
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
            if(cab > 0)// ����
            {
                cabchangecost = Math.abs(cab)*Paras.CAB_DOWN;
                company = Math.abs(cab) * Paras.PROFIT_DOWN;
            }
            else//���ջ򲻱�
            {
                cabchangecost = Math.abs(cab)*Paras.CAB_UP;
                company = Math.abs(cab) * Paras.PROFIT_UP;
            }
   //         System.out.println("��λ�䶯���ۣ�" + cabchangecost);
            changecost = changenum * Paras.CHANGE_NUM;
   //         System.out.println("�䶯�������ۣ� " + changecost);
            sixhrcost = timediff * Paras.SIM_DEG;
   //         System.out.println("�ӽ��̶ȴ��ۣ�" + sixhrcost);
            mctcost = 0;
   //         System.out.println("�ν�ʱ����ۣ�"+ mctcost);
   //         System.out.println("������������������������������������������������������������������������");
            passenger = cabchangecost +    //��λ�䶯
                        changecost +       //�Ƿ�ԭ����䶯
                        timecost +         //�ܺ�ʱ�䶯
                        sixhrcost +        //�ӽ��̶�(������Сʱ����)
                        mctcost;           //mctʱ��
            //��˾����
            result = Paras.satisfaction * passenger + Paras.profit * company;
            return result;
            
        }
        else {//�����ÿ�
             double result = -1;
             Plan ori = Schedule.getOriPlan(pindex);
             double passenger = 0;
             double company = 0;
             double cabchangecost = 0;//��λ�䶯
             double changecost = 0;//�г̱䶯
             double timecost = 0;//�ܺ�ʱ�䶯
             double sixhrcost = 0;//�ӽ��̶�(������Сʱ����)
             double mctcost = 0;//mctʱ��
             int cab = 0;
             int changenum = 0;
             long oritime = 0; //����
             long plantime = 0;
             int timediff = 0;
             int mcttime = 0;
             DATE s1 = ori.itineraries.get(0).depTime.clone();
             DATE e1 = ori.itineraries.get(1).ariTime.clone();
             oritime = e1.difftime_min(s1);
    //         System.out.println("ԭʼ�ƻ���ʱ: "+ oritime);
             DATE s2 = itineraries.get(0).depTime.clone();
             DATE e2 = itineraries.get(1).ariTime.clone();
             plantime = e2.difftime_min(s2);
    //         System.out.println("�ƻ��ܺ�ʱ: "+ plantime);
             timecost = (oritime - plantime) * Paras.TOTAL_TIME;
    //         System.out.println("�ܺ�ʱ���ۣ�" + timecost);
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
             if(cab > 0)// ����
             {
                 cabchangecost = Math.abs(cab)*Paras.CAB_DOWN;
                 company = Math.abs(cab) * Paras.PROFIT_DOWN;
             }
             else//���ջ򲻱�
             {
                 cabchangecost = Math.abs(cab)*Paras.CAB_UP;
                 company = Math.abs(cab) * Paras.PROFIT_UP;
             }
    //         System.out.println("��λ�䶯���ۣ�" + cabchangecost);
             changecost = changenum * Paras.CHANGE_NUM;
    //         System.out.println("�䶯�������ۣ� " + changecost);
             sixhrcost = timediff * Paras.SIM_DEG;
    //         System.out.println("�ӽ��̶ȴ��ۣ�" + sixhrcost);
             mctcost = (osd.difftime_min(ofa) - psd.difftime_min(pfa)) * Paras.MCT_TIME;
    //         System.out.println("�ν�ʱ����ۣ�"+ mctcost);
    //         System.out.println("������������������������������������������������������������������������");
             passenger = cabchangecost +    //��λ�䶯
                         changecost +       //�Ƿ�ԭ����䶯
                         timecost +         //�ܺ�ʱ�䶯
                         sixhrcost +        //�ӽ��̶�(������Сʱ����)
                         mctcost;           //mctʱ��
             //��˾����
             result = Paras.satisfaction * passenger + Paras.profit * company;
             return result;
        }
         
    }
	/**
	 * ���㳤���η�������
	 * @param pindex
	 * @return
	 */
    public double calPlanCost_Longleg(Plan plan,int pindex) {
        double result = 0;
        Plan ori = Schedule.getOriPlan(pindex);
        double passenger = 0;
        double company = 0;
        double cabchangecost = 0;//��λ�䶯
        double changecost = 0;//�г̱䶯
        double timecost = 0;//�ܺ�ʱ�䶯
        double sixhrcost = 0;//�ӽ��̶�(������Сʱ����)
        double mctcost = 0;//mctʱ��
        int cab = 0;
        int changenum = 0;
        long oritime = 0; //����
        long plantime = 0;
        int timediff = 0;
        int mcttime = 0;
        DATE s1 = ori.itineraries.get(0).depTime.clone();
        DATE e1 = ori.itineraries.get(1).ariTime.clone();
        oritime = e1.difftime_min(s1);
//        System.out.println("ԭʼ�ƻ���ʱ: "+ oritime);
        DATE s2 = plan.itineraries.get(0).depTime.clone();
        DATE e2 = plan.itineraries.get(1).ariTime.clone();
        plantime = e2.difftime_min(s2);
//        System.out.println("�ƻ��ܺ�ʱ: "+ plantime);
        timecost = (oritime - plantime) * Paras.TOTAL_TIME;
//        System.out.println("�ܺ�ʱ���ۣ�" + timecost);
        DATE ofa = ori.itineraries.get(0).ariTime.clone();
        DATE osd = ori.itineraries.get(1).depTime.clone();
        DATE pfa = plan.itineraries.get(0).ariTime.clone();
        DATE psd = plan.itineraries.get(1).depTime.clone();
        for(int i = 0; i < plan.itineraries.size(); i++)
        {
            //
            if(Math.abs(plan.itineraries.get(i).depTime.clone().difftime_min(ori.itineraries.get(i).depTime.clone())) >= 360)
            {
                timediff++;
            }
            //
            if(plan.itineraries.get(i).ori == false)
            {
                changenum++;
            }
            //
            String plancab = plan.itineraries.get(i).largeCab;
            String oricab = ori.itineraries.get(i).largeCab;
            cab += Paras.CAB_SEQ.get(plancab) - Paras.CAB_SEQ.get(oricab);
            //
        }
        if(cab > 0)// ����
        {
            cabchangecost = Math.abs(cab)*Paras.CAB_DOWN;
            company = Math.abs(cab) * Paras.PROFIT_DOWN;
        }
        else//���ջ򲻱�
        {
            cabchangecost = Math.abs(cab)*Paras.CAB_UP;
            company = Math.abs(cab) * Paras.PROFIT_UP;
        }
//        System.out.println("��λ�䶯���ۣ�" + cabchangecost);
        changecost = changenum * Paras.CHANGE_NUM;
//        System.out.println("�䶯�������ۣ� " + changecost);
        sixhrcost = timediff * Paras.SIM_DEG;
//        System.out.println("�ӽ��̶ȴ��ۣ�" + sixhrcost);
        mctcost = (osd.difftime_min(ofa) - psd.difftime_min(pfa)) * Paras.MCT_TIME;
//        System.out.println("�ν�ʱ����ۣ�"+ mctcost);
//        System.out.println("������������������������������������������������������������������������");
        passenger = cabchangecost +    //��λ�䶯
                    changecost +       //�Ƿ�ԭ����䶯
                    timecost +         //�ܺ�ʱ�䶯
                    sixhrcost +        //�ӽ��̶�(������Сʱ����)
                    mctcost;           //mctʱ��
        //��˾����
        result = Paras.satisfaction * passenger + Paras.profit * company;

        return result;
    }
    
    
    
	/**
	 * ���Plan����
	 */
	public void play() {
	    System.out.println("COST = " + cost);
	    System.out.println("PNR������" + pnrindex);
	    System.out.print("������"+"[");
	    
	    for(int i = 0; i < decisionvars.length; i++)
	    {
	        System.out.print(i+":"+decisionvars[i] +" ");
	    }
	    System.out.println("]");
		for(int i = 0 ;i < itineraries.size(); i++)
		{
			System.out.print("��"+i+"���г� : " + itineraries.get(i).toString());
			System.out.println();
		}
	}
}
