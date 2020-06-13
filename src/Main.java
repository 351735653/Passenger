import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import sch.Schedule;


public class Main {
	    
	public static void main(String[] args) throws Exception {
		
		Schedule sch = new Schedule();
		int dataindex = 1;
		sch.run(dataindex);

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
//		System.out.println(sch.longlegnum);
//		System.out.println(sch.paras.CAB_SEQ.size());
		
	}

}
