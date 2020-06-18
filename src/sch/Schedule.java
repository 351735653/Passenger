package sch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.omg.CORBA.NO_IMPLEMENT;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;


import form.Flight;
import form.Itinerary;
import form.Paras;
import form.Plan;
import form.Pnr;
import form.Solution;
import ilog.concert.IloColumn;
import ilog.concert.IloException;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.CouldNotInstallColumnException;
import ilog.cplex.IloCplex;
import tool.ColumnGeneration;
import tool.DATE;
import tool.GCtool;
import tool.GCtool.IloNumVarArray;

public class Schedule {
    
    public static ArrayList<Pnr> pnrs;
    public static ArrayList<Flight> oriflights;
    public static ArrayList<Flight> availflights;
    public Paras paras;
    public int pnrnumbers;
    public int seatnumbers;
    
    //�������
    public static ArrayList<Plan> plans;
    
    //����
    public static Multimap<Integer, Integer> ori_avail;//ԭʼ�г�����ú��������
    public static Multimap<Integer, Integer> pnr_ori;//pnr �� ԭʼ�г̵�����
    public static Multimap<Integer, Integer> long_short;//��������̺��εĶ�Ӧ����
    
    //���ļ���ַ
    public String pnrdata;
    public String flightdata;
    public String legdata;
    public String paradata;
    
    //��������
    public static ArrayList<Flight> availflight0;//���ú������ݱ���
    
    public static double RC_EPS = 1.0e-8;
    public static int longlegnum; //����������
    
    public static double bestObj=Double.MIN_VALUE;//����Ŀ��ֵ
    public static double initObj=-1; //��ʼ�����
    public static double[] bestS = null;//��õ���ѡ���
    public static double[] initS = null;
    public static ColumnGeneration bestNode;//���Ž������ڵ�
    public static ArrayList<Plan> bestPlans;
    public static ArrayList<ColumnGeneration> nodeQueue;//��֧���۽ڵ����У�������ȵķ�֧�ж�
    
    public Schedule()
    {
        pnrs = new ArrayList<Pnr>();
        oriflights = new ArrayList<Flight>();
        availflights = new ArrayList<Flight>();
        paras = new Paras();
        
        plans = new ArrayList<Plan>();
        
        ori_avail = ArrayListMultimap.create();
        long_short = ArrayListMultimap.create();
        pnr_ori = ArrayListMultimap.create();
        
        availflight0 = new ArrayList<Flight>();
        longlegnum = 0;
        bestS = null;
        initS = null;
        pnrnumbers = 0;
        seatnumbers = 0;
    }
    
    
    
    
    /**
     * ��������ģ�Ͳ��������
     * @throws Exception 
     */
    public void buildModel(int t) throws Exception {
        nodeQueue = new ArrayList<ColumnGeneration>();
        ColumnGeneration node = new ColumnGeneration();
        node.initProblem();
        nodeQueue.add(node);
        long begintime = 0;
        long endtime = 0;
        begintime = System.currentTimeMillis();
        int k = 0;
        while(nodeQueue.size() > 0)
        {
            ColumnGeneration problem = nodeQueue.get(nodeQueue.size()-1);
            boolean next=false;
            
            while(true)
            {
                double[] s = null;
                if(problem.solve())
                {
//                    //
//                    GCtool.reportModel(problem.mainSolver, problem.fill);
//                    //
                    s=problem.curSol;
                }
                else
                {
                    next=true;
                    break;
                }
                double tempObj = problem.curObj;
                System.out.println("obj:: "+tempObj);
//                if(tempObj == 13559.300000000003)
//                    System.out.println("test");
                if(isIntSol(s) && tempObj > bestObj)
                {
                    k++;
                    problem.outPutResult("./test" + t + "/GC_result_"+k+".csv");
                    bestObj = tempObj;
                    bestS = s.clone();
                    bestNode = problem;
                }
                boolean breakFlag=true;
                Plan plan = problem.findSolution_Heuristic();
                if(plan != null)
                {
                    problem.addColumn(plan);
                    breakFlag=false;
                    System.out.println("cloumnNum:::"+problem.vars.getSize());
                }
                if(breakFlag)
                    break;
//                endtime=System.currentTimeMillis();
//                if(endtime-begintime>300000)
//                    break;
            }
            if(next)//���ڲ���������ѭ���� ��ֹ�ڵ�
            {   
                problem.mainSolver.end();
                nodeQueue.remove(nodeQueue.size()-1);
                continue;
            }
            else //���ڲ��ɼ�����������ѭ��
            {
//                if(problem.curObj <= bestObj || isIntSol(problem.curSol))
                if(Math.abs(problem.curObj - bestObj) / bestObj <= 0.05|| isIntSol(problem.curSol))
                {
                    problem.mainSolver.end();
                    nodeQueue.remove(nodeQueue.size()-1);
                    continue;
                }
                else 
                {

                    //System.out.println("��֧");
                    //��֧�������ڵ㣬ɾ����ǰ�ڵ�
                    @SuppressWarnings("unchecked")
                    ArrayList<Integer> fixedVars=(ArrayList<Integer>) problem.fixedVars.clone();
                    double min = 1;
                    int index = 0;
                    for(int i = 0; i < problem.curSol.length; i++)
                    {
//                        double m1 = Math.ceil(problem.curSol[i]);//����ȡ��
//                        double m2 = Math.floor(problem.curSol[i]);//����ȡ��
//                        double m = Math.min(problem.curSol[i]-m2, m1-problem.curSol[i]);
                        double m=Math.rint(problem.curSol[i]);
                        if(Math.abs(m-problem.curSol[i])>0)
                        {
                            index=i;
                            break;
                        }
//                        if(m > 0)
//                        {
//                            if(m < min)
//                            {
//                                index = i;
//                                min = m;
//                                break;
//                            }
//                        }   
                        else
                            continue;
                    }
                    //  System.out.println("index:::"+index+"  "+problem.curSol[index]);

                    //stop
//                    System.out.println("__________________________________");
                    System.err.println("_________________________________��֧____________________________________________");
//                    System.out.println("���д�С��" + nodeQueue.size());
//                    int num = 0;
//                    for(int i = 0; i < problem.curSol.length; i++)
//                    { 
//                        if(problem.curSol[i] == 1)
//                        {
//                            num++;
//                        }
//                        System.out.printf(i+": "+"%-6.2f"+" ",problem.curSol[i]);
//                    }
//                    System.out.println();
//                    System.out.println("����ȡֵΪ1�ĸ���Ϊ��"+num);
//                    System.out.println("__________________________________");
                    //
                    fixedVars.set(index, 0);
                    ColumnGeneration node1=new ColumnGeneration(problem,fixedVars);
                    fixedVars.set(index, 1);
                    ColumnGeneration node2=new ColumnGeneration(problem,fixedVars);
                    problem.mainSolver.end();                   
                    nodeQueue.remove(nodeQueue.size()-1);
                    nodeQueue.add(node1);
                    nodeQueue.add(node2);
                }
            }
            endtime = System.currentTimeMillis();
//            if(endtime-begintime > 3600000)
//                break;
        }
        endtime = System.currentTimeMillis();
        System.out.println("ʱ�����ģ�"+(endtime - begintime)/1000.0+"s");
        bestPlans=new ArrayList<>();
        for(int i=0;i<bestS.length;i++)
        {
            if(bestS[i]==1)
            {
                bestPlans.add(bestNode.plans.get(i));
            }
        }
//        while(true)
//        {
//            if(cg.solve())
//            {
//                
//                Plan plan = cg.findSolution_Heuristic();
//                if( plan != null)
//                {
//                    
//                    plan.play();//
//                    
//                    cg.addColumn(plan);
//                    plans.add(plan);
//                    System.out.println("cloumnNum:::"+plans.size());
//                }
//                else {
//                    GCtool.reportResult(cg.mainSolver, cg.vars, plans);
//                    System.out.println("bestobj:" + cg.curObj);
//                    System.out.println("initobj:" + initObj);
//                    break;
//                }
//            }
//            else {
//                System.out.println("δ�ɹ����");
//                break;
//                
//            }
//            
//        }
//        cg.mainSolver.end();
//   
        
        
    }
    /**
     * ������
     * @param t
     * @throws Exception
     */
    public void run(int t) throws Exception
    {
        setDataIndex(t);//ȷ��Ҫ�������������
        readData(pnrdata, flightdata, legdata, paradata);//������
        copyData();//����һ�ݿ��ú�������
        feasibleSolution();//���ɳ�ʼ���н�
        buildModel(t);//���ݳ�ʼ���н⽨ģ��������������е���
        report();
        
//        String fileName = "./test" + t + "/afterSchFlight.csv";
//        File fileTest = new File(fileName);//׼��������ļ�
//        PrintStream fileStream = new PrintStream(new FileOutputStream(fileTest));
//        for(int i = 0; i < availflights.size(); i++)
//        {
//            fileStream.println(availflights.get(i).toString());
//        }
//        fileStream.close();
    }
    public void report() throws IOException
    {
        int num = 0;
        String init="";
        System.out.println();
        init="initObj:"+new BigDecimal(initObj).toString() +";";
        System.out.println(init);
        
        String best="";
        System.out.println();
        best="bestObj:"+new BigDecimal(bestObj).toString() +";";
        System.out.println(best);
        
        for(int i = 0; i < bestPlans.size(); i++)
        {
            bestPlans.get(i).play();
        }
        for(int i = 0; i < bestS.length; i++)
        { 
            if(bestS[i] == 1)
            {
                num++;
            }
            System.out.printf("%-6.2f",bestS[i]);
        }
        System.out.println();
        System.out.println("ѡ�з���������"+num);
    }
    
    
    public static boolean isIntSol(double[] sol)
    {
        for(int i=0;i<sol.length;i++)
        {
            if((int)sol[i]!=sol[i])
                return false;
        }
        return true;
    }
    
    public void copyData()
    {
        for(int i = 0 ; i < availflights.size(); i++)
        {
            availflight0.add(availflights.get(i).clone());
        }
    }

    
    /**
     * ���н�
     * @throws Exception 
     */
    public void feasibleSolution() throws Exception {
        ArrayList<Integer> pnrindexset = getPnrByNum();
        for(int i = 0 ; i < pnrindexset.size(); i++)
        {
            int pindex = pnrindexset.get(i);//��i��pnr������
            Plan plan = new Plan();
            List<Integer> orindexset = (List<Integer>)pnr_ori.get(pindex);//pnrԭʼ�г���������
            
            DATE orideptime = oriflights.get(orindexset.get(0)).depTime.clone();//��i��pnr��һ��ԭʼ�г̵ĳ���ʱ��
            DATE prearitime = null;
            
            for(int j = 0 ; j < orindexset.size(); j++)
            {
                int oindex = orindexset.get(j);//��i��pnr��j��ԭʼ�г̵����� 
                List<Integer> afindexset = getAvailFlightByTime((List<Integer>)ori_avail.get(oindex));//��i��pnr��j��ԭʼ�г̿��ú��༯��
//                List<Integer> afindexset = (List<Integer>)ori_avail.get(oindex); 
                for(int f = 0 ; f < afindexset.size(); f++)
                {
                    int aindex = afindexset.get(f);//��i��pnr��j��ԭʼ�г̵�f�����ú�������
                    Itinerary t = assignPnrToFlight(pindex, oindex, aindex, orideptime, prearitime);
                    if(t != null)
                    {
                        plan.itineraries.add(t);
                        prearitime = t.ariTime.clone();
                        break;
                    }
                    if(f == afindexset.size()-1)
                    {
                        throw new Exception("PNRIDΪ"+pnrs.get(pindex).pnrID+"��pnr    ����IDΪ"+oriflights.get(oindex).flightID+"��ԭʼ�г�δ�ҵ����з���feasibleSolution");
                    }
                }
            }
//            pnrs.get(pindex).scheme.add(plan);
            plan.cost = plan.calPlanCost(pindex);
            plan.pnrindex = pindex;
            plan.calPlanVars();
            plans.add(plan);
        }
        initObj = calInitCost();
        resetData();
    }
    /**
     * �����ʼ����ܴ���
     * @return
     */
    public double calInitCost() {
        double result = 0;
        for(int i = 0; i < plans.size(); i ++)
        {
//            System.out.println(" cost"+i+":"+plans.get(i).cost);
            result += plans.get(i).cost;
        }
        return result;
    }
    /**���㷽������
     * @param plan
     * @param pindex
     * @return
     */
    public double calPlanCost(Plan plan, int pindex) {
         double result = -1;
         Plan ori = getOriPlan(pindex);
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
         DATE s1 = ori.itineraries.get(0).depTime.clone();
         DATE e1 = ori.itineraries.get(1).ariTime.clone();
         oritime = e1.difftime_min(s1);
         DATE s2 = plan.itineraries.get(0).depTime.clone();
         DATE e2 = plan.itineraries.get(1).ariTime.clone();
         plantime = e2.difftime_min(s2);
         timecost = (oritime - plantime) * Paras.TOTAL_TIME;
         DATE ofa = ori.itineraries.get(0).ariTime.clone();
         DATE osd = ori.itineraries.get(1).depTime.clone();
         DATE pfa = ori.itineraries.get(0).ariTime.clone();
         DATE psd = ori.itineraries.get(1).depTime.clone();
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
         changecost = changenum * Paras.CHANGE_NUM;
         sixhrcost = timediff * Paras.SIM_DEG;
         mctcost = (osd.difftime_min(ofa) - psd.difftime_min(pfa)) * Paras.MCT_TIME;
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
     * ������������pnr
     */
    public ArrayList<Integer> getPnrByNum()
    {
        ArrayList<Integer> tmpList = new ArrayList<Integer>();
        for(int i = 0 ; i < pnrs.size(); i++)
        {
            tmpList.add(i);
        }
        
        Collections.sort(tmpList, new Comparator<Integer> () {
            public int compare(Integer i1, Integer i2)
            {
                try {
                    if(pnrs.get(i1).pnrNum > pnrs.get(i2).pnrNum)
                        return -1;
                    else if(pnrs.get(i1).pnrNum == pnrs.get(i2).pnrNum)
                        return 0;
                    else {
                        return 1;
                    }
                        
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        }
    );
        return tmpList;
    }
    
    public ArrayList<Integer> getAvailFlightByTime(List<Integer> findexset)
    {
        ArrayList<Integer> tmpList = new ArrayList<Integer>();
        for(int i = 0 ; i < findexset.size(); i++)
        {
            tmpList.add(findexset.get(i));
        }
        
        Collections.sort(tmpList, new Comparator<Integer> () {
            public int compare(Integer i1, Integer i2)
            {
                try {
//                    if(pnrs.get(i1).pnrNum > pnrs.get(i2).pnrNum)
//                        return -1;
//                    else if(pnrs.get(i1).pnrNum == pnrs.get(i2).pnrNum)
//                        return 0;
//                    else {
//                        return 1;
                    if(availflights.get(i1).depTime.clone().compareTo(availflights.get(i2).depTime.clone()) < 0)
                        return -1;
                    else if (availflights.get(i1).depTime.clone().compareTo(availflights.get(i2).depTime.clone()) == 0)
                        return 0;
                    else {
                        return 1;
                    }
                        
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        }
    );
        return tmpList;
    }
    /**
     * ˳���ȡpnr ���� ������ ���� ����
     * @param pnr
     * @param flight
     * @param leg
     * @param para
     * @throws Exception
     */
    public void readData(String pnr, String flight,String leg,String para) throws Exception
    {
        readPnr(pnr);
        readFlight(flight);
        readLeg(leg);
        readPara(para);
    }
    /**
     * �������ݰ汾����readData֮ǰ����
     * @param t
     */
    public void setDataIndex(int t)
    {
        pnrdata = "./test" + t + "/pnr.csv";
        flightdata = "./test" + t + "/flight.csv";
        legdata = "./test" + t + "/leg.csv";
        paradata = "./test" + t + "/para.csv";

    }
    
    /**
     * ����pnr�������pnrԭʼ�г���Ϣ
     * @param pnrindex
     * @return
     */
    public static Plan getOriPlan(int pnrindex)
    {
        Plan result = pnrs.get(pnrindex).scheme.clone();
//        result.play();
        return result;
    }
    /**
     * ����pnr������ԭʼ�г����������ԭʼ�����Ĳ�λ�ȼ�
     * @param pnrindex
     * @param itindex
     * @return
     */
    public static String getSeatClass(int pnrindex,int itindex)
    {
        Plan tmp = getOriPlan(pnrindex);
        Flight ori = oriflights.get(itindex);
        String result = null;
        for(int i = 0; i < tmp.itineraries.size(); i++)
        {
            if(ori.flightID.equals(tmp.itineraries.get(i).flightID)
                &&(ori.depTime.compareTo(tmp.itineraries.get(i).depTime)==0)
                &&(ori.AriTime.compareTo(tmp.itineraries.get(i).ariTime)==0))
            {
                result = tmp.itineraries.get(i).largeCab;
            }
        }
        if(result == null)
        {
            System.err.println("pnr����Ϊ"+pnrindex+" ԭʼ�г�����Ϊ"+itindex+"δ�ҵ���λ�ȼ�getSeatClass");
        }
        return result;
    }
    /**
     * ����pnr������ԭʼ�г����� ���  ���ò�λ�ȼ�  ����
     * @param pnrindex
     * @param itindex
     * @return
     */
    public static List<String> getAvailSeatClass(int pnrindex,int itindex)
    {
        String tmp = getSeatClass(pnrindex, itindex);
        List<String> result = new ArrayList<String>(Paras.CAB_LIMIT.get(tmp));
        
        return result;
    }

    /**
     * ����pnr���� pnr��i���г���������i���г̿��ú����������ж��Ƿ��Ϊpnr���ŷ�����������ԣ���˼������������ɷ�����
     * @param pnrindex
     * @param itindex
     * @param afindex
     * @param prearitime 
     * @param orideptime 
     * @return
     * @throws Exception
     */
    public Itinerary assignPnrToFlight(int pnrindex, int itindex, int afindex, DATE orideptime, DATE prearitime) throws Exception
    {
        List<String> availseatset = getAvailSeatClass(pnrindex, itindex);
        Flight f = availflights.get(afindex);
        if(f.depTime.compareTo(orideptime) < 0)
        {
            return null;
        }
        if(prearitime != null)
        {
            DATE fdep = f.depTime.clone();
            DATE avail = prearitime.clone().add(f.MCT);
            if(fdep.compareTo(avail) <= 0)
            {
                return null;
            }
        }
        Pnr p = pnrs.get(pnrindex);
        String largeCab = null;
        String smallCab = "a";
        for(int i = 0; i < availseatset.size(); i++)
        {
            int seatindex = Paras.CAB_SEQ.get(availseatset.get(i)); //i�ȼ���λ����
            int aseatnum = f.availSeatNum.get(seatindex);
            if(aseatnum >= p.pnrNum )
            {
                largeCab = availseatset.get(i);
                f.availSeatNum.set(seatindex, aseatnum-p.pnrNum);
                Itinerary it = new Itinerary(f.inid, f.flightID, largeCab, smallCab, f.depAirport, f.depTime.toDateTime(), f.AriAirport, f.AriTime.toDateTime(), f.ori,f.longleg);
                return it;
            }
        }
        return null;
        
    }
    
    /**
     * ��pnr����  ���oriflights���ڲ����
     * @param pnrData
     * @throws Exception
     */
    public void readPnr(String pnrData) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader(pnrData));
        String line = reader.readLine();
        if (line.trim().substring(0, 2).compareTo("//") == 0)
            line = reader.readLine();
        int j = 0;
        int p = 0;
        while (line != null) {
            String pnrID;// PNR���
            int pnrNum;// ����
            int pnrValue; // PNR��ֵ
            int pnrType;// PNR����
            int planNum; // ����������
            String[] item = line.split(",");
            pnrID = item[0];
            pnrNum = Integer.parseInt(item[1]);
            pnrValue = Integer.parseInt(item[2]);
            pnrType = Integer.parseInt(item[3]);
            planNum = Integer.parseInt(item[4]);
            ArrayList<Itinerary> itineraries = new ArrayList<Itinerary>();
            int i = 5;

            while (i <= (item.length-1)) //one614 
            {
                String[] subitem = item[i].split("\\|");
                String flightID = subitem[0];
                String largeCab = subitem[1];
                String smallCab = subitem[2];
                String depAirport = subitem[3];
                String depTime = subitem[4];
                String ariAirport = subitem[5];
                String ariTime = subitem[6];
                //���oriflights
                
                if(whetherContainRepeat(flightID, depTime, ariTime))
                {
                    pnr_ori.put(p,getOriInidByFidDtimeAtime(flightID,new DATE(depTime),new DATE(ariTime)));
                }
                else {
                    Flight f = new Flight(j,flightID,depAirport,depTime,ariAirport,ariTime);
                    oriflights.add(f);
                    pnr_ori.put(p, j);
                    j++;
                }
                
                //end
                Itinerary it = new Itinerary(-1,flightID, largeCab, smallCab, depAirport, depTime, ariAirport, ariTime, true, false);
                itineraries.add(it);
                i++;
            }
            Plan plan = new Plan(itineraries);
            Pnr pnr = new Pnr(pnrID, pnrNum, pnrValue, pnrType, plan, planNum);
            pnrs.add(pnr);
            p++;
            //
            pnrnumbers += pnrNum;
            //
            line = reader.readLine();
            if (line == null)
                break;
        }
        reader.close();
    }

    /**
     * ��ȡ���ú�������
     * @param flightData
     * @throws Exception
     */
    public void readFlight(String flightData) throws Exception
    {

        @SuppressWarnings("resource")
        BufferedReader reader = new BufferedReader(new FileReader(flightData));
        String line = reader.readLine();
        if (line.trim().substring(0, 2).compareTo("//") == 0)
            line = reader.readLine();
        int oriid = -1;
        int j = 0;
        while (line != null)
        {
            String[] item = line.split(",");
            if(item.length == 3)
            {
                String id = item[0];
                DATE deptime = new DATE(item[1]);
                DATE aritime = new DATE(item[2]);
                oriid = getOriInidByFidDtimeAtime(id,deptime,aritime);
                if(oriid == -1)
                {
                    throw new Exception("δ�ҵ� "+id+" ԭʼ�г�");
                }
                line = reader.readLine();
                if (line == null)
                    break;
            }
            else
            {
                String flightID = item[0];         //�����
                String depAirport = item[1];       //��������
                String ariAirport = item[2];       //�������
                DATE depTime = new DATE(item[3]);            //����ʱ��
                DATE ariTime = new DATE(item[4]);            //����ʱ��
                int MCT = Integer.parseInt(item[5]);                 //��ת�ν�ʱ��
                
                List<String> availSeatNum = Splitter.on("|").splitToList(item[6]); //���ȼ�������λ��
                //
                for(String s : availSeatNum)
                {
                    seatnumbers += Integer.parseInt(s);
                }
                //
                boolean ori = (Integer.parseInt(item[7]) == 1) ? true : false;             //�Ƿ�ԭ�����־��true�ǣ�false��
                boolean longleg = false;                //�Ƿ񳤺���
                Flight f = new Flight(j, flightID, depAirport, ariAirport, depTime, ariTime, MCT, availSeatNum, ori,longleg);
                //���ori_avail����
                ori_avail.put(oriid,j); 
                j++;
                availflights.add(f);
                line = reader.readLine();
                if (line == null)
                    break;
            }
        }
        reader.close();
        
    }
    
    /**
     * ��ȡ��������
     * @param paradata
     * @throws IOException
     */
    public void readPara(String paradata) throws Exception
    {
        BufferedReader reader = new BufferedReader(new FileReader(paradata));
        String line = reader.readLine();
        if (line.trim().substring(0, 2).compareTo("//") == 0)
            line = reader.readLine();
        String sa = null;
        String pro = null;
        String cabseq = null;
        String cablimit = null;
        String oricab = null;
        String cabup = null;
        String cabdown = null;
        String changenum = null;
        String totaltime = null;
        String simdeg = null;
        String mctime = null;
        String proup = null;
        String prodown = null;
        while (line != null)
        {
            String item[] = line.split(",");
            switch (item[0]) {
                case "TARGET":
                    sa = item[1];
                    pro = item[2];
                    break;
                case "CAB_SEQ":
                    cabseq = item[1];
                    break;
                case "CAB_LIMIT":
                    cablimit = item[1];
                    break;
                case "ORI_CAB":
                    oricab = item[1];
                    break;
                case "CAB_UP":
                    cabup = item[1];
                    break;
                case "CAB_DOWN":
                    cabdown = item[1];
                    break;
                case "CHANGE_NUM":
                    changenum = item[1];
                    break;
                case "TOTAL_TIME":
                    totaltime = item[1];
                    break;
                case "SIM_DEG":
                    simdeg = item[1];
                    break;
                case "MCT_TIME":
                    mctime = item[1];
                    break;
                case "PROFIT_UP":
                    proup = item[1];
                    break;
                case "PROFIT_DOWN":
                    prodown = item[1];
                    break;
                default:
                    break;
            }
            line = reader.readLine();
            if (line == null)
                break;
        }
        paras = new Paras(sa, pro, cabseq, cablimit, oricab, cabup, cabdown, changenum, totaltime, simdeg, mctime, proup, prodown);
        
        reader.close();
    }
    /**
     * ��ȡ����������
     * @param legdata
     * @throws Exception
     */
    public void readLeg(String legdata) throws Exception
    {
        BufferedReader reader = new BufferedReader(new FileReader(legdata));
        String line = reader.readLine();
        if (line.trim().substring(0, 2).compareTo("//") == 0)
            line = reader.readLine();
        
        while (line != null)
        {
            
            int inid = availflights.size();//�ڲ����
            String flightID;         //�����
            String depAirport;       //��������
            String ariAirport;       //�������
            DATE depTime;            //����ʱ��
            DATE ariTime;            //����ʱ��
            int MCT;                 //��ת�ν�ʱ��
            List<String> availSeatNum = new ArrayList<String>(); //���ȼ�������λ��
            boolean ori =false;             //�Ƿ�ԭ�����־��true�ǣ�false��
            boolean longleg = true;                //�Ƿ񳤺���
            
            
            String[] item = line.split(",");
            flightID = item[0];
            String[] subitem1 = item[1].split("\\|");
            String[] subitem2 = item[2].split("\\|");
            int inid1 = getAvailInidByFidDtimeAtime(subitem1[0], new DATE(subitem1[1]), new DATE(subitem1[2]));
            int inid2 = getAvailInidByFidDtimeAtime(subitem2[0], new DATE(subitem2[1]), new DATE(subitem2[2]));
            Flight tmp1 = availflights.get(inid1);
            Flight tmp2 = availflights.get(inid2);
            depAirport = tmp1.depAirport;
            depTime = tmp1.depTime.clone();
            ariAirport = tmp2.AriAirport;
            ariTime = tmp2.AriTime.clone();
            MCT = tmp1.MCT + tmp2.MCT;
            for(int i = 0 ; i < tmp1.availSeatNum.size(); i++)
            {
                int t1 = tmp1.availSeatNum.get(i);
                int t2 = tmp2.availSeatNum.get(i);
                if(t1 < t2)
                {
                    availSeatNum.add(tmp1.availSeatNum.get(i).toString());
                }
                else {
                    availSeatNum.add(tmp2.availSeatNum.get(i).toString());
                }
            }
            Flight f = new Flight(inid, flightID, depAirport, ariAirport, depTime, ariTime, MCT, availSeatNum, ori,longleg);
            availflights.add(f);
            long_short.put(inid, tmp1.inid);
            long_short.put(inid, tmp2.inid);
            longlegnum++;
            line = reader.readLine();
            if (line == null)
                break;
        }
        reader.close();
    }
    
    
    /**
     * ���ݺ���ţ�����ʱ�䣬����ʱ�� ��� ԭʼ�г̵��ڲ����
     * @return
     */
    public int getOriInidByFidDtimeAtime(String id, DATE deptime, DATE aritime) {
        for(int i = 0 ; i < oriflights.size(); i++)
        {
            if(oriflights.get(i).flightID.equals(id)
                &&(oriflights.get(i).depTime.compareTo(deptime)==0)
                &&(oriflights.get(i).AriTime.compareTo(aritime)==0))
                return oriflights.get(i).inid;
        }
         return -1;
    }
    /**
     * ���ݺ���ţ�����ʱ�䣬����ʱ���ÿ��ú�����ڲ����
     * @param id
     * @param deptime
     * @param aritime
     * @return
     * @throws Exception 
     */
    public int getAvailInidByFidDtimeAtime(String id, DATE deptime, DATE aritime) throws Exception {
        for(int i = 0 ; i < availflights.size(); i++)
        {
            if(availflights.get(i).flightID.equals(id)
                &&(availflights.get(i).depTime.compareTo(deptime)==0)
                &&(availflights.get(i).AriTime.compareTo(aritime)==0))
                return availflights.get(i).inid;
        }
        throw new Exception("�������Σ�δ�ҵ������Ϊ"+id+" ����ʱ��Ϊ"+deptime.toDateTime()+" ����ʱ��Ϊ"+aritime.toDateTime()+"�ĺ���");
    }
    
    /**
     * ��ӳ�ʼ����ʱ
     * ���ݺ���ţ�����ʱ�䣬����ʱ�� �ж��Ƿ�����ظ�����
     * �Ѵ�����true
     * @param id
     * @param deptime
     * @param aritime
     * @throws Exception
     */
    public boolean whetherContainRepeat(String id, String deptime, String aritime) throws Exception
    {
        DATE dep = new DATE(deptime);
        DATE ari = new DATE(aritime);
        for(int i = 0 ; i < oriflights.size(); i++)
        {
            if(oriflights.get(i).flightID.equals(id)
                &&(oriflights.get(i).depTime.compareTo(dep)==0)
                &&(oriflights.get(i).AriTime.compareTo(ari)==0))
                return true;
        }
        return false;
    }
    /**
     * ��ԭ���ú�������
     */
    public void resetData()
    {
        availflights.clear();
        for(int i = 0 ; i < availflight0.size(); i++)
        {
            availflights.add(availflight0.get(i).clone());
        }
    }
    

    
}
