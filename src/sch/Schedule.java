package sch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
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
    
    //方案相关
    public static ArrayList<Plan> plans;
    
    //索引
    public static Multimap<Integer, Integer> ori_avail;//原始行程与可用航班的索引
    public static Multimap<Integer, Integer> pnr_ori;//pnr 与 原始行程的索引
    public static Multimap<Integer, Integer> long_short;//长航段与短航段的对应索引
    
    //读文件地址
    public String pnrdata;
    public String flightdata;
    public String legdata;
    public String paradata;
    
    //备份数据
    public static ArrayList<Flight> availflight0;//可用航班数据备份
    
    public static double RC_EPS = 1.0e-8;
    public static int longlegnum; //长航段数量
    public static double initCost; //初始解代价
    
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
        
        availflight0= new ArrayList<Flight>();
        longlegnum = 0;
    }
    
    public void copyData()
    {
        for(int i = 0 ; i < availflights.size(); i++)
        {
            availflight0.add(availflights.get(i).clone());
        }
    }

    
    /**
         * 可行解
     * @throws Exception 
     */
    public void feasibleSolution() throws Exception {
        ArrayList<Integer> pnrindexset = getPnrByNum();
        for(int i = 0 ; i < pnrindexset.size(); i++)
        {
            int pindex = pnrindexset.get(i);//第i个pnr的索引
            Plan plan = new Plan();
            List<Integer> orindexset = (List<Integer>)pnr_ori.get(pindex);//pnr原始行程索引集合
            
            DATE orideptime = oriflights.get(orindexset.get(0)).depTime.clone();//第i个pnr第一段原始行程的出发时间
            DATE prearitime = null;
            
            for(int j = 0 ; j < orindexset.size(); j++)
            {
                int oindex = orindexset.get(j);//第i个pnr第j段原始行程的索引 
                List<Integer> afindexset = (List<Integer>)ori_avail.get(oindex); //第i个pnr第j段原始行程可用航班集合
                for(int f = 0 ; f < afindexset.size(); f++)
                {
                    int aindex = afindexset.get(f);//第i个pnr第j段原始行程第f个可用航班索引
                    Itinerary t = assignPnrToFlight(pindex, oindex, aindex, orideptime, prearitime);
                    if(t != null)
                    {
                        plan.itineraries.add(t);
                        prearitime = t.ariTime.clone();
                        break;
                    }
                    if(f == afindexset.size()-1)
                    {
                        throw new Exception("PNRID为"+pnrs.get(pindex).pnrID+"的pnr    航班ID为"+oriflights.get(oindex).flightID+"的原始行程未找到可行方案feasibleSolution");
                    }
                }
            }
//            pnrs.get(pindex).scheme.add(plan);
            plan.cost = plan.calPlanCost(pindex);
            plan.pnrindex = pindex;
            plan.calPlanVars();
            plans.add(plan);
        }
        initCost = calInitCost();
        resetData();
    }
    /**
     * 计算初始解的总代价
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
    /**计算方案代价
     * @param plan
     * @param pindex
     * @return
     */
    public double calPlanCost(Plan plan, int pindex) {
         double result = -1;
         Plan ori = getOriPlan(pindex);
         double passenger = 0;
         double company = 0;
         double cabchangecost = 0;//舱位变动
         double changecost = 0;//行程变动
         double timecost = 0;//总耗时变动
         double sixhrcost = 0;//接近程度(大于六小时航段)
         double mctcost = 0;//mct时间
         int cab = 0;
         int changenum = 0;
         long oritime = 0; //分钟
         long plantime = 0;
         int timediff = 0;
         int mcttime = 0;
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
         if(cab > 0)// 降舱
         {
             cabchangecost = Math.abs(cab)*Paras.CAB_DOWN;
             company = Math.abs(cab) * Paras.PROFIT_DOWN;
         }
         else//升舱或不变
         {
             cabchangecost = Math.abs(cab)*Paras.CAB_UP;
             company = Math.abs(cab) * Paras.PROFIT_UP;
         }
         changecost = changenum * Paras.CHANGE_NUM;
         sixhrcost = timediff * Paras.SIM_DEG;
         mctcost = (osd.difftime_min(ofa) - psd.difftime_min(pfa)) * Paras.MCT_TIME;
         passenger = cabchangecost +    //舱位变动
                     changecost +       //是否原航班变动
                     timecost +         //总耗时变动
                     sixhrcost +        //接近程度(大于六小时航段)
                     mctcost;           //mct时间
         //航司收益
         result = Paras.satisfaction * passenger + Paras.profit * company;
         return result;
    }

    /**
     * 按照人数排序pnr
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
                    if(pnrs.get(i1).pnrNum >= pnrs.get(i2).pnrNum)
                        return -1;
                    else
                        return 1;
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
     * 顺序读取pnr 航班 长航班 参数 数据
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
     * 控制数据版本，在readData之前调用
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
     * 根据pnr索引获得pnr原始行程信息
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
     * 根据pnr索引，原始行程索引，获得原始方案的舱位等级
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
            System.err.println("pnr索引为"+pnrindex+" 原始行程索引为"+itindex+"未找到舱位等级getSeatClass");
        }
        return result;
    }
    /**
     * 根据pnr索引，原始行程索引 获得  可用舱位等级  集合
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
    /**根据pnr索引  可用航班索引判断航班是否可用
     * @param pindex
     * @param aindex
     * @return
     */
    private boolean flightAvailForPnr(int pindex, int aindex) {

         return false;
    }
    /**
     * 根据pnr索引 pnr第i个行程索引，第i个行程可用航班索引，判断是否可为pnr安排方案，如果可以，则核减人数，并生成方案。
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
            int seatindex = paras.CAB_SEQ.get(availseatset.get(i)); //i等级座位索引
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
     * 读pnr数据  添加oriflights及内部编号
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
            String pnrID;// PNR编号
            int pnrNum;// 人数
            int pnrValue; // PNR价值
            int pnrType;// PNR类型
            int planNum; // 保护方案数
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
                //添加oriflights
                
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
            line = reader.readLine();
            if (line == null)
                break;
        }
        reader.close();
    }

    /**
     * 读取可用航班数据
     * @param flightData
     * @throws Exception
     */
    public void readFlight(String flightData) throws Exception
    {
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
                    throw new Exception("未找到 "+id+" 原始行程");
                }
                line = reader.readLine();
            }
            else
            {
                String flightID = item[0];         //航班号
                String depAirport = item[1];       //出发机场
                String ariAirport = item[2];       //到达机场
                DATE depTime = new DATE(item[3]);            //出发时间
                DATE ariTime = new DATE(item[4]);            //到达时间
                int MCT = Integer.parseInt(item[5]);                 //中转衔接时间
                
                List<String> availSeatNum = Splitter.on("|").splitToList(item[6]); //各等级可用座位数
                
                boolean ori = (Integer.parseInt(item[7]) == 1) ? true : false;             //是否原航班标志；true是，false否
                boolean longleg = false;                //是否长航班
                Flight f = new Flight(j, flightID, depAirport, ariAirport, depTime, ariTime, MCT, availSeatNum, ori,longleg);
                //添加ori_avail索引
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
     * 读取参数数据
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
     * 读取长航班数据
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
            
            int inid = availflights.size();//内部编号
            String flightID;         //航班号
            String depAirport;       //出发机场
            String ariAirport;       //到达机场
            DATE depTime;            //出发时间
            DATE ariTime;            //到达时间
            int MCT;                 //中转衔接时间
            List<String> availSeatNum = new ArrayList<String>(); //各等级可用座位数
            boolean ori =false;             //是否原航班标志；true是，false否
            boolean longleg = true;                //是否长航班
            
            
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
     * 根据航班号，出发时间，到达时间 获得 原始行程的内部编号
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
     * 根据航班号，出发时间，到达时间获得可用航班的内部编号
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
        throw new Exception("（长航段）未找到航班号为"+id+" 出发时间为"+deptime.toDateTime()+" 到达时间为"+aritime.toDateTime()+"的航班");
    }
    
    /**
     * 添加初始航班时
     * 根据航班号，出发时间，到达时间 判断是否包含重复航班
     * 已存在则true
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
     * 复原可用航班数据
     */
    public void resetData()
    {
        availflights.clear();
        for(int i = 0 ; i < availflight0.size(); i++)
        {
            availflights.add(availflight0.get(i).clone());
        }
    }
    
    /**
     * 建立问题模型并迭代求解
     * @throws Exception 
     */
    public void buildModel() throws Exception {
        ColumnGeneration cg = new ColumnGeneration();
        cg.initProblem();
        while(true)
        {
            if(cg.solve())
            {
                
                Plan plan = cg.findSolution_Heuristic();
                if( plan != null)
                {
                    
                    plan.play();//
                    
                    cg.addColumn(plan);
                    plans.add(plan);
                    System.out.println("cloumnNum:::"+plans.size());
                }
                else {
                    GCtool.reportResult(cg.mainSolver, cg.vars, plans);
                    System.out.println("bestobj:" + cg.curObj);
                    System.out.println("initobj:" + initCost);
                    break;
                }
            }
            else {
                System.out.println("未成功求解");
                break;
                
            }
            
        }
        cg.mainSolver.end();
//        if(cg.solve())
//        {
//            System.out.println("当前目标值为："+ cg.curObj);
//            System.out.println("当前解为：" );
//            for(int i = 0; i < cg.curSol.length; i++)
//            {
//                System.out.print(" X" + i + ":"+ cg.curSol[i]);
//            }
//            System.out.println();
//            System.out.println("Dual Price");
//            double[] price = cg.mainSolver.getDuals(cg.fill);
//            int tmp = Schedule.pnrs.size() + (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size();
//            for(int i = 0; i < tmp; i++)
//            {
//                System.out.println(" "+ i +":" +price[i]);
//            }
//        }
        
        
    }
    /**
     * 主函数
     * @param t
     * @throws Exception
     */
    public void run(int t) throws Exception
    {
        setDataIndex(t);//确定要进行试验的数据
        readData(pnrdata, flightdata, legdata, paradata);//读数据
        copyData();//备份一份可用航班数据
        feasibleSolution();//生成初始可行解
        buildModel();//根据初始可行解建模，并解子问题进行迭代
        
        
//        String fileName = "./test" + t + "/afterSchFlight.csv";
//        File fileTest = new File(fileName);//准备输出的文件
//        PrintStream fileStream = new PrintStream(new FileOutputStream(fileTest));
//        for(int i = 0; i < availflights.size(); i++)
//        {
//            fileStream.println(availflights.get(i).toString());
//        }
//        fileStream.close();
    }
    
}
