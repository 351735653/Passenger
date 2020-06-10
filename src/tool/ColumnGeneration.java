 package tool;
 
import tool.GCtool.IloNumVarArray;

import java.util.List;

import form.Paras;
import form.Plan;
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
    
    public ColumnGeneration() throws IloException
    {
        mainSolver = new IloCplex();
        obj = mainSolver.addMaximize();//目标函数
        fill = new IloRange[Schedule.pnrs.size() + (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size()];//longleg
        mainSolver.setOut(null);
        vars= new IloNumVarArray();
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
                List<Integer> tmp = Schedule.availflight0.get(i).availSeatNum;
                for(int k = 0; k < tmp.size(); k++)
                {
                    fill[j] = mainSolver.addRange(0, tmp.get(k));
                    j++;
                }
            }
        }
        //添加列变量
        for(int i = 0; i < Schedule.plans.size(); i++)
        {
            addColumn(Schedule.plans.get(i));
            vars.getElement(i).setName("X"+i);
        }
        GCtool.reportModel(mainSolver, fill);
    }
    /**
     * 添加一列，并添加变量
     * @param plan
     * @throws IloException
     */
    public void addColumn(Plan plan) throws IloException
    {
        IloColumn c = generateColumn(plan);
        vars.add(mainSolver.numVar(c, 0, 1));
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
        while( i < Schedule.pnrs.size())
        {
            c1 = c1.and(mainSolver.column(fill[i],plan.decisionvars[i]));
            i++;
        }
        while(i < (Schedule.pnrs.size() + 
                (Schedule.availflights.size() - Schedule.longlegnum) * Paras.CAB_SEQ.size()-1) )
        {
            c1 = c1.and(mainSolver.column(fill[i],plan.decisionvars[i]));
            i++;
        }
        // + longleg
        return c1;
        
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
    
}
