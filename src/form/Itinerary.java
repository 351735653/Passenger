package form;

import tool.DATE;

public class Itinerary implements Cloneable{
	public String flightID;			//�����
	public String largeCab;			//��յȼ�
	public String smallCab;			//С�յȼ�
	public String depAirport;		//����վ
	public DATE depTime;			//����ʱ��
	public String ariAirport;		//����վ
	public DATE ariTime;			//����ʱ��
	public boolean ori;             //�Ƿ�ԭ�����־��true�ǣ�false��
	public Itinerary(String flightID, String largeCab, String smallCab, String depAirport, String depTime,
			String ariAirport, String ariTime,boolean ori) throws Exception {
		this.flightID = flightID;
		this.largeCab = largeCab;
		this.smallCab = smallCab;
		this.depAirport = depAirport;
		this.depTime = new DATE(depTime);
		this.ariAirport = ariAirport;
		this.ariTime = new DATE(ariTime);
		this.ori = ori;
	}
	public Itinerary clone()
	{
		
		try {
		    Itinerary clone=(Itinerary)super.clone();
		    clone.flightID = flightID;
		    clone.largeCab = largeCab;
		    clone.smallCab = smallCab;
		    clone.depAirport = depAirport;
		    clone.depTime = depTime.clone();
		    clone.ariAirport = ariAirport;
		    clone.ariTime = ariTime.clone();
		    clone.ori = ori;
		    return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	@Override
	public String toString() {
		return "Itinerary [flightID=" + flightID + ", largeCab=" + largeCab + ", smallCab=" + smallCab + ", depAirport="
				+ depAirport + ", depTime=" + depTime.toDateTime() + ", ariAirport=" + ariAirport + ", ariTime=" + ariTime.toDateTime() +  ", ori=" + ori+"]";
	}
	
}
