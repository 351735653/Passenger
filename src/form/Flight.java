package form;

import java.util.ArrayList;
import java.util.List;

import tool.DATE;

public class Flight {
    
    public int inid;                //�ڲ����
    
	public String flightID;			//�����
	public String depAirport;		//��������
	public String AriAirport;		//�������
	public DATE depTime;			//����ʱ��
	public DATE AriTime;			//����ʱ��
	public int MCT;					//��ת�ν�ʱ��
	public List<Integer> availSeatNum;		//���ȼ�������λ��
	public boolean ori;				//�Ƿ�ԭ�����־��true�ǣ�false��
	public boolean longleg;            //�Ƿ��ǳ�����
	
	//ԭʼ�г̣�ȱʡ���췽��
	public Flight(  
	                int inid,
	                String flightID,                
	                String depAirport,
	                String depTime,
	                String ariAirport,
	                String ariTime) throws Exception
	{
	    this.inid = inid;
	    this.flightID = flightID;
	    this.depAirport = depAirport;
	    this.AriAirport = ariAirport;
	    this.depTime = new DATE(depTime);
	    this.AriTime = new DATE(ariTime);
	    this.MCT = 0;
	    this.availSeatNum = new ArrayList<Integer>();
	    this.ori = false;
	    this.longleg = false;
	}
	public Flight(int inid,String flightID,String depAirport,String ariAirport,DATE depTime,DATE ariTime,int MCT,List<String> availSeatNum,boolean ori,boolean longleg)
	{
	    this.inid = inid;
	    this.flightID = flightID;
	    this.depAirport = depAirport;
	    this.AriAirport = ariAirport;
	    this.depTime = depTime;
	    this.AriTime = ariTime;
	    this.MCT = MCT;
	    this.availSeatNum = new ArrayList<Integer>();
	    for(int i = 0 ; i < availSeatNum.size(); i++)
	    {
	        this.availSeatNum.add(Integer.parseInt(availSeatNum.get(i)));
	    }
	    this.ori = ori;
	    this.longleg = longleg;
	}
	

	public Flight clone()
	{
	    try {
            Flight clone = (Flight)super.clone();
            clone.inid = inid;
            clone.flightID = flightID;
            clone.depAirport = depAirport;
            clone.AriAirport = AriAirport;
            clone.depTime = depTime;
            clone.AriTime = AriTime;
            clone.MCT = MCT;
            clone.availSeatNum = new ArrayList<Integer>();
            for (int i = 0 ; i < availSeatNum.size(); i++)
            {
                clone.availSeatNum.add(availSeatNum.get(i));
            }
            clone.ori = ori;
            clone.longleg = longleg;
            return clone;
            
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
             e.printStackTrace();
             return null;
        }

	}
    @Override
    public String toString() {
                    return "Flight [inid=" + inid + ", flightID=" + flightID + ", depAirport=" + depAirport + ", AriAirport="
            + AriAirport + ", depTime=" + depTime.toDateTime() + ", AriTime=" + AriTime.toDateTime() + ", MCT=" + MCT +", availSeatNum=" +
        availSeatNum+ ", ori=" + ori + ", longleg=" + longleg+ "]";



    }
}
