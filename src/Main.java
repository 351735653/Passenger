import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import form.Itinerary;
import form.Paras;
import sch.Schedule;


public class Main {

	public static void main(String[] args) throws Exception {
		
		Schedule sch = new Schedule();
		int dataindex = 2;
		sch.run(dataindex);
		
//		//����pnr���������
//		sch.readPnr("./test1/pnr.csv");
		for(int i = 0; i < sch.pnrs.size(); i++)
		{
		    sch.pnrs.get(i).play();
		}

//		List<Integer> tmp = new ArrayList<Integer>();
//		tmp.add(1);
//		tmp.add(2);
//		System.out.println(tmp.size());
		
		
//		System.out.println(sch.paras.CAB_SEQ);
//		//end
//		
//		//����flight���������
//		sch.readFlight("./test1/flight.csv");
//		//end
//
//		//����Paras���������
//		sch.readPara("./test1/para.csv");
//		System.out.println(sch.paras.toString());
//		//end
//		
//		sch.readLeg("./test1/leg.csv");
//		
//		for(int i = 0; i < sch.availflights.size(); i++)
//        {
//            System.out.println(sch.availflights.get(i).toString());
//        }
//		System.out.println(sch.pnr_ori);
		

		
	}

}
