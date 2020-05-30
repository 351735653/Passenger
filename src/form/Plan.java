package form;

import java.util.ArrayList;


public class Plan implements Cloneable{
	public ArrayList<Itinerary> itineraries;

	public Plan(ArrayList<Itinerary> it) {
		itineraries = new ArrayList<Itinerary>();
		
		for(int i = 0 ; i < it.size(); i++)
		{
			itineraries.add(it.get(i).clone());
		}
	}

	public Plan () {
	    itineraries = new ArrayList<Itinerary>();
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
            return clone;
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
             e.printStackTrace();
        }
        return clone;

	}
	
	/**
	 * 输出Plan详情
	 */
	public void play() {
		for(int i = 0 ;i < itineraries.size(); i++)
		{
			System.out.print("第"+i+"段行程 : " + itineraries.get(i).toString());
			System.out.println();
		}
	}
}
