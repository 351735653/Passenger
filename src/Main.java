import sch.Schedule;


public class Main {

	public static void main(String[] args) throws Exception {
		
		Schedule sch = new Schedule();
		int dataindex = 2;
		sch.run(dataindex);
		
//		//测试pnr读数据情况
//		sch.readPnr("./test1/pnr.csv");
		for(int i = 0; i < Schedule.pnrs.size(); i++)
		{
		    Schedule.pnrs.get(i).play();
		}
<<<<<<< HEAD
		
		for(int i = 0; i < sch.solution.plans.size(); i++)
		{
		    sch.pnrs.get(sch.solution.pnrindex.get(i)).playinfo();;
		    sch.solution.plans.get(i).play();
		}
=======

		sch.solutions.get(0).play();
		
		System.out.println(sch.long_short);
>>>>>>> 589de7df336608d60781c6b4bbeb4e20d99c1a30
//		List<Integer> tmp = new ArrayList<Integer>();
//		tmp.add(1);
//		tmp.add(2);
//		System.out.println(tmp.size());
		
		
//		System.out.println(sch.paras.CAB_SEQ);
//		//end
//		
//
//		//测试Paras读数据情况
		System.out.println(sch.paras.toString());
//		//end
//		

//		
//		for(int i = 0; i < sch.availflights.size(); i++)
//        {
//            System.out.println(sch.availflights.get(i).toString());
//        }
//		System.out.println(sch.pnr_ori);
		
	}

}
