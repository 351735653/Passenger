package form;

import java.util.ArrayList;

import sch.Schedule;
import tool.DATE;


public class Plan implements Cloneable{
	public ArrayList<Itinerary> itineraries;
	public double cost;
	
	public Plan(ArrayList<Itinerary> it) {
		itineraries = new ArrayList<Itinerary>();
		
		for(int i = 0 ; i < it.size(); i++)
		{
			itineraries.add(it.get(i).clone());
		}
		double cost = -1;
	}

	public Plan () {
	    itineraries = new ArrayList<Itinerary>();
	    cost = -1;
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
            return clone;
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
             e.printStackTrace();
        }
        return clone;

	}
	
    /**���㷽������
     * @param plan
     * @param pindex
     * @return
     */
    public double calPlanCost(int pindex) {
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
         System.out.println("��λ�䶯���ۣ�" + cabchangecost);
         changecost = changenum * Paras.CHANGE_NUM;
         System.out.println("�䶯�������ۣ� " + changecost);
         sixhrcost = timediff * Paras.SIM_DEG;
         System.out.println("�ӽ��̶ȴ��ۣ�" + sixhrcost);
         mctcost = (osd.difftime_min(ofa) - psd.difftime_min(pfa)) * Paras.MCT_TIME;
         System.out.println("�ν�ʱ����ۣ�"+ mctcost);
         System.out.println("������������������������������������������������������������������������");
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
		for(int i = 0 ;i < itineraries.size(); i++)
		{
			System.out.print("��"+i+"���г� : " + itineraries.get(i).toString());
			System.out.println();
		}
	}
}
