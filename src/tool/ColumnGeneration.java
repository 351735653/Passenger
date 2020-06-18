 package tool;
 
import tool.GCtool.IloNumVarArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


import form.Flight;
import form.Itinerary;
import form.Paras;
import form.Plan;
import form.Pnr;
import ilog.concert.IloColumn;
import ilog.concert.IloException;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import sch.Schedule;

/**
 * @author whz
 * @date 2020/06/05
 */
public class ColumnGeneration 
{
    public IloCplex mainSolver;
    public IloObjective obj;
    public IloRange[]   fill;
    public IloNumVarArray vars;
    
    public double[] curSol;
    public double curObj;
    public ArrayList<Plan> plans;
    public ArrayList<Integer> fixedVars;
    public int varnum;
    
    public ColumnGeneration() throws IloException
    {
        mainSolver = new IloCplex();
        obj = mainSolver.addMaximize();//目标函数
        fill = new IloRange[Schedule.pnrs.size() + (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size()
                            + Schedule.longlegnum * 2 * Paras.CAB_SEQ.size()];//longleg613
        mainSolver.setOut(null);
        vars= new IloNumVarArray();
        plans = new ArrayList<Plan>();
        fixedVars = new ArrayList<Integer>();
        varnum = 0;
    }
    
    @SuppressWarnings("unchecked")
    public ColumnGeneration(ColumnGeneration node, ArrayList<Integer> fixedVars) throws IloException
    {
        this.fixedVars=(ArrayList<Integer>)fixedVars.clone();
        mainSolver = new IloCplex();
        obj = mainSolver.addMaximize();//目标函数
        fill = new IloRange[Schedule.pnrs.size() + (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size()
                            + Schedule.longlegnum * 2 * Paras.CAB_SEQ.size()];//longleg613
        mainSolver.setOut(null);
        vars= new IloNumVarArray();
        plans = new ArrayList<Plan>();
        int j = 0;
        while( j < Schedule.pnrs.size())
        {
            fill[j] = mainSolver.addRange(Schedule.pnrs.get(j).planNum, Schedule.pnrs.get(j).planNum);
            j++;
        }
        while(j < (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size())
        {
            for(int i = 0; i < (Schedule.availflights.size() - Schedule.longlegnum); i++)
            {
                List<Integer> tmp = Schedule.availflights.get(i).availSeatNum;
                for(int k = 0; k < tmp.size(); k++)
                {
                    fill[j] = mainSolver.addRange(0, tmp.get(k));
                    j++;
                }
            }
        }
        while(j < Schedule.pnrs.size() + (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size()
                               + Schedule.longlegnum * 2 * Paras.CAB_SEQ.size())
        {
            for(int i = 0; i < Schedule.longlegnum; i++)
            {
                List<Integer> tmp =(List<Integer>)Schedule.long_short.get(Schedule.availflights.size() - Schedule.longlegnum + i);
                for(int k = 0; k < tmp.size(); k++)
                {
                    List<Integer> as = Schedule.availflights.get(tmp.get(k)).availSeatNum;
                    for(int l = 0; l < as.size(); l++)
                    {
                        fill[j] = mainSolver.addRange(0, as.get(l));
                        j++;
                    }
                }
            }
        }
        for(int i = 0; i < node.plans.size(); i++)
        {
            addColumn(node.plans.get(i), this.fixedVars.get(i));
        }
        
//        GCtool.reportModel(mainSolver, fill);
    }
    
    /**
     * 初始化模型，添加初始解
     * @throws IloException
     */
    public void initProblem() throws IloException
    {
        int j = 0;
        while( j < Schedule.pnrs.size())
        {
            fill[j] = mainSolver.addRange(Schedule.pnrs.get(j).planNum, Schedule.pnrs.get(j).planNum);
            j++;
        }
        while(j < (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size())
        {
            for(int i = 0; i < (Schedule.availflights.size() - Schedule.longlegnum); i++)
            {
                List<Integer> tmp = Schedule.availflights.get(i).availSeatNum;
                for(int k = 0; k < tmp.size(); k++)
                {
                    fill[j] = mainSolver.addRange(0, tmp.get(k));
                    j++;
                }
            }
        }
        while(j < Schedule.pnrs.size() + (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size()
                               + Schedule.longlegnum * 2 * Paras.CAB_SEQ.size())
        {
            for(int i = 0; i < Schedule.longlegnum; i++)
            {
                List<Integer> tmp =(List<Integer>)Schedule.long_short.get(Schedule.availflights.size() - Schedule.longlegnum + i);
                for(int k = 0; k < tmp.size(); k++)
                {
                    List<Integer> as = Schedule.availflights.get(tmp.get(k)).availSeatNum;
                    for(int l = 0; l < as.size(); l++)
                    {
                        fill[j] = mainSolver.addRange(0, as.get(l));
                        j++;
                    }
                }
            }
        }
        //添加列变量
        for(int i = 0; i < Schedule.plans.size(); i++)
        {
            addColumn(Schedule.plans.get(i));
        }
//        GCtool.reportModel(mainSolver, fill);
    }
    /**
     * 添加一列，并添加变量
     * @param plan
     * @throws IloException
     */
    public void addColumn(Plan plan) throws IloException
    {
//        plan.play();
        IloColumn c = generateColumn(plan);
        vars.add(mainSolver.numVar(c, 0, 1));
        vars.getElement(varnum).setName("X"+varnum);
        varnum++;
        plans.add(plan);
        fixedVars.add(-1);
    }
    /**
     * 添加修正变量的一列
     * @param plan
     * @param fixed
     * @throws IloException
     */
    public void addColumn(Plan plan, int fixed) throws IloException
    {
//        plan.play();
        IloColumn c=generateColumn(plan);
        if(fixed == -1)
            vars.add(mainSolver.numVar(c,0,1));
        else
            vars.add(mainSolver.numVar(c,fixed,fixed));
        vars.getElement(varnum).setName("X"+varnum);
        varnum++;
        fixedVars.add(fixed);
        plans.add(plan);
    }
    
    
    /**根据一个plan 生成一列
     * @param plan
     * @return
     * @throws IloException
     */
    public IloColumn generateColumn(Plan plan) throws IloException
    {
        IloColumn c1= mainSolver.column(obj,plan.cost);
        int i = 0;
//        while( i < Schedule.pnrs.size())
//        {
//            c1 = c1.and(mainSolver.column(fill[i],plan.decisionvars[i]));
//            i++;
//        }
//        while(i < (Schedule.pnrs.size() + 
//                (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size()-1) )
//        {
//            c1 = c1.and(mainSolver.column(fill[i],plan.decisionvars[i]));
//            i++;
//        }
//        // + longleg613
        while(i < Schedule.pnrs.size() + (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size()
                               + Schedule.longlegnum * 2 * Paras.CAB_SEQ.size())
        {
            c1 = c1.and(mainSolver.column(fill[i],plan.decisionvars[i]));
            i++;
        }
        return c1;
        
    }

    
    public Plan findSolution_Heuristic() throws Exception
    {
        //
        Plan bestplan = new Plan();
        double subobj = 0;
        double[] price = mainSolver.getDuals(fill);
        
        System.out.println();
        for(double ttt: price)
        {
            System.out.print(ttt+" ");
        }
        System.out.println();
        
        String smallCab = "a";
        ArrayList<Itinerary> it1 = new ArrayList<Itinerary>();
        ArrayList<Itinerary> it2 = new ArrayList<Itinerary>();
        //
        for(int i = 0; i < Schedule.pnrs.size(); i++)
        {
            int pindex = i;//第i个pnr的索引
            Pnr p = Schedule.pnrs.get(pindex);
            Plan plan = new Plan();
            List<Integer> orindexset = (List<Integer>)Schedule.pnr_ori.get(pindex);//pnr原始行程索引集合
            
            if(orindexset.size() == 1)//一段行程
            {
                DATE orideptime = Schedule.oriflights.get(orindexset.get(0)).depTime.clone();//第i个pnr第一段原始行程的出发时间
                
                //第一段
                int oindex1 = orindexset.get(0);
                List<String> availseatset1 = Schedule.getAvailSeatClass(pindex, oindex1);
                List<Integer> afindexset1 = (List<Integer>)Schedule.ori_avail.get(oindex1); //第i个pnr第j段原始行程可用航班集合
                for(int f = 0; f < afindexset1.size(); f++)
                {
                    int aindex = afindexset1.get(f);//第i个pnr第1段原始行程第f个可用航班索引
                    Flight flight = Schedule.availflights.get(aindex); //第i个pnr第1段原始行程第f个可用航班
                    if(Schedule.availflights.get(aindex).depTime.clone().compareTo(orideptime) >= 0)
                    {
                        for(int j = 0; j < availseatset1.size(); j++)
                        {
                            String seat = availseatset1.get(j);
                            if(flight.availSeatNum.get(Paras.CAB_SEQ.get(seat)) >= p.pnrNum)
                            {
                                Itinerary it = new Itinerary(flight.inid, flight.flightID, seat, smallCab, flight.depAirport, 
                                                            flight.depTime.toDateTime(), flight.AriAirport, flight.AriTime.toDateTime(), 
                                                            flight.ori,flight.longleg);
                                it1.add(it);
                            }
                        }
                    }
                    else {
                        continue;
                    }
                    
                }
                //第一段end
                
                for(int f = 0; f < it1.size(); f++)
                {
                    generatePlan(plan, it1.get(f), null, pindex);
                    double tmp = calSubObjective(price, plan);
                    if((tmp < 0)&&(tmp < subobj))
                    {
                        bestplan = plan.clone();
                        subobj = tmp;
                    }
                    plan = new Plan();
                }
    
                it1.clear();
                it2.clear();
                
                
            }
            else //两段行程
            {
                DATE orideptime = Schedule.oriflights.get(orindexset.get(0)).depTime.clone();//第i个pnr第一段原始行程的出发时间
                
                //第一段
                int oindex1 = orindexset.get(0);
                List<String> availseatset1 = Schedule.getAvailSeatClass(pindex, oindex1);
                List<Integer> afindexset1 = (List<Integer>)Schedule.ori_avail.get(oindex1); //第i个pnr第j段原始行程可用航班集合
                for(int f = 0; f < afindexset1.size(); f++)
                {
                    int aindex = afindexset1.get(f);//第i个pnr第1段原始行程第f个可用航班索引
                    Flight flight = Schedule.availflights.get(aindex); //第i个pnr第1段原始行程第f个可用航班
                    if(Schedule.availflights.get(aindex).depTime.clone().compareTo(orideptime) >= 0)
                    {
                        for(int j = 0; j < availseatset1.size(); j++)
                        {
                            String seat = availseatset1.get(j);
                            if(flight.availSeatNum.get(Paras.CAB_SEQ.get(seat)) >= p.pnrNum)
                            {
                                Itinerary it = new Itinerary(flight.inid, flight.flightID, seat, smallCab, flight.depAirport, 
                                                            flight.depTime.toDateTime(), flight.AriAirport, flight.AriTime.toDateTime(), 
                                                            flight.ori,flight.longleg);
                                it1.add(it);
                            }
                        }
                    }
                    else {
                        continue;
                    }
                    
                }
                //第一段end
                
                //第二段
                int oindex2 = orindexset.get(1);
                List<String> availseatset = Schedule.getAvailSeatClass(pindex, oindex2);
                List<Integer> afindexset = (List<Integer>)Schedule.ori_avail.get(oindex2); //第i个pnr第j段原始行程可用航班集合
                for(int f = 0; f < afindexset.size(); f++)
                {
                    int aindex = afindexset.get(f);//第i个pnr第2段原始行程第f个可用航班索引
                    Flight flight = Schedule.availflights.get(aindex); //第i个pnr第1段原始行程第f个可用航班
                    if(Schedule.availflights.get(aindex).depTime.clone().compareTo(orideptime) >= 0)
                    {
                        for(int j = 0; j < availseatset.size(); j++)
                        {
                            String seat = availseatset.get(j);
                            if(flight.availSeatNum.get(Paras.CAB_SEQ.get(seat)) >= p.pnrNum)
                            {
                                Itinerary it = new Itinerary(flight.inid, flight.flightID, seat, smallCab, flight.depAirport, 
                                                            flight.depTime.toDateTime(), flight.AriAirport, flight.AriTime.toDateTime(), 
                                                            flight.ori,flight.longleg);
                                it2.add(it);
                            }
                        }
                    }
                    else {
                        continue;
                    }
                }
                //第二段end
                
                
                for(int f = 0; f < it1.size(); f++)
                {
                    for(int j = 0; j < it2.size(); j++)
                    {
                        DATE t1 = it1.get(f).ariTime.clone();
                        DATE t2 = it2.get(j).depTime.clone();
                        int mct = Schedule.availflights.get(it2.get(j).inid).MCT;
                        if((t1.add(mct)).compareTo(t2) <= 0)
                        {
                            generatePlan(plan, it1.get(f), it2.get(j), pindex);
                            double tmp = calSubObjective(price, plan);
                            if((tmp < 0)&&(tmp < subobj))
                            {
                                bestplan = plan.clone();
                                subobj = tmp;
                            }
                            plan = new Plan();
                        }
                        else {
                            plan = new Plan();
                            continue;
                        }
                    }
                }
    
                it1.clear();
                it2.clear();
            }
            
            
        }
        
        
        if(subobj >= -Schedule.RC_EPS)
            bestplan = null;
        
        return bestplan;
    }
    /**
     * 根据两个行程、pnr索引、一个空plan对象，生成一个完整的plan对象
     * @param plan
     * @param it1
     * @param it2
     * @param pindex
     * @throws Exception 
     */
    public void generatePlan(Plan plan,Itinerary it1, Itinerary it2, int pindex) throws Exception
    {
        plan.itineraries.add(it1);
        if(it2 != null)
        {
            plan.itineraries.add(it2);
        }
        plan.cost = plan.calPlanCost(pindex);
        plan.pnrindex = pindex;
        plan.calPlanVars();
    }

    /**
     * 根据对偶价格和当前plan计算子问题目标函数值
     * @param price
     * @param plan
     * @return
     */
    public double calSubObjective(double[] price, Plan plan)
    {
        double result = 0;
        int i = 0;
        for(i = 0; i < Schedule.pnrs.size(); i++)
        {
            result += price[i] * plan.decisionvars[i];
        }
        while(i < (Schedule.pnrs.size() + (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size()
            + Schedule.longlegnum * 2 * Paras.CAB_SEQ.size()) )//longleg613
        {
            result += price[i] * plan.decisionvars[i];
            i++;
        }
        return (result - plan.cost);
    }
    /*
     * 函数求解方法，存储当前解及当前目标值
     */
    public boolean solve() throws IloException
    {
        if(!mainSolver.solve())
            return false;
        curSol=new double[vars.getSize()];
        for (int  j = 0; j < vars.getSize(); j++) 
            curSol[j]=mainSolver.getValue(vars.getElement(j));
        curObj=mainSolver.getObjValue();
        return true;
    }

    /**
     * @param string
     * @throws FileNotFoundException 
     */
    public void outPutResult(String string) throws FileNotFoundException {
        // TODO Auto-generated method stub
        
        String fileName=string;
        File fileTest = new File(fileName);//准备输出的文件
        PrintStream fileStream = new PrintStream(new FileOutputStream(fileTest));
        fileStream.println("//"+curObj);
        int num = 0;
        for(double i : curSol)
        {
            if(i == 1)
            {
                num++;
            }
            fileStream.print(i+" ");
        }
        fileStream.println();
        fileStream.println(num);
    
    }
    
}
