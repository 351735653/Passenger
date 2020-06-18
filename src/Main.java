import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sch.Schedule;


public class Main {
	    
	public static void main(String[] args) throws Exception {
		
		Schedule sch = new Schedule();
		int dataindex = 3;
		sch.run(dataindex);
		System.out.println("旅客总数："+sch.pnrnumbers);
		System.out.println("可用座位数："+sch.seatnumbers);
//		String t1 = "1,1,9,1,1,CA1519|P|a|PEK|2020-01-01 06:30:00|SHA|2020-01-01 08:25:00,CA8425|F|a|SHA|2020-01-01 16:15:00|HKG|2020-01-01 18:55:00";
//		String t2 = "2,3,9,1,1,CA1519|A|a|PEK|2020-01-01 06:30:00|SHA|2020-01-01 08:25:00";
//		String[] i1 = t1.split(",");
//		String[] i2 = t2.split(",");
//		System.out.println("t1: "+ i1.length);
//		System.out.println("t2: "+ i2.length);
		    
//		for(int i = 0; i < sch.pnrs.size(); i++)
//		{
//		    sch.pnrs.get(i).play();
//		}
//		
//		for(int i = 0; i < sch.plans.size(); i++)
//		{
//		    sch.pnrs.get(sch.plans.get(i).pnrindex).playinfo();
//		    sch.plans.get(i).play();
//		}
		
		
//		List<Integer> tmp = new ArrayList<Integer>();
//		tmp.add(1);
//		tmp.add(2);
//		System.out.println(tmp.size());
//		tmp.clear();
//		System.out.println(tmp.size());

		
		
//		System.out.println(sch.paras.CAB_SEQ);
//		//end
//		
//
//		//测试Paras读数据情况
//		System.out.println(sch.paras.toString());
//		//end
//		

//		
//		for(int i = 0; i < sch.availflights.size(); i++)
//        {
//            System.out.println(sch.availflights.get(i).toString());
//        }
//		System.out.println(sch.pnr_ori);
//		System.out.println(sch.long_short);
//		sch.long_short.put(16, 2);
//		sch.long_short.put(16, 13);
//		Set<Integer> t = sch.long_short.keySet();
//		System.out.println(sch.long_short.keySet());
//		System.out.println(t.size());
		
//		if(sch.long_short.containsValue(3))
//		{
//		    System.out.println("yes");
//		}
		
//		System.out.println(sch.longlegnum);
//		System.out.println(sch.paras.CAB_SEQ.size());
		
	}

}
