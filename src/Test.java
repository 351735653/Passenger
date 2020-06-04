import ilog.concert.IloColumn;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

/**
 * @author whz
 * @date 2020/06/03
 */
public class Test {
    static class IloNumVarArray 
    {
      int _num           = 0;
      IloNumVar[] _array = new IloNumVar[32];

      void add(IloNumVar ivar) {
         if ( _num >= _array.length ) {
            IloNumVar[] array = new IloNumVar[2 * _array.length];
            System.arraycopy(_array, 0, array, 0, _num);
            _array = array;
         }
         _array[_num++] = ivar;
      }

      IloNumVar getElement(int i) { return _array[i]; }
      int       getSize()         { return _num; }
    }
    
    public static void reportModel(IloCplex solver,IloRange[] fill) throws IloException
    {
        System.out.println("Objective:");
        System.out.println(solver.getObjective());
        System.out.println("Constraints:");
        for(int i=0;i<fill.length;i++)
        {
                System.out.println(fill[i].getLB()+"<="+fill[i].getExpr()+"<="+fill[i].getUB());
        }  
    }
    
    public static void main(String[] args) throws IloException
    {
        IloCplex mainSolver = new IloCplex();
        IloObjective obj = mainSolver.addMaximize();
        IloNumVarArray vars = new IloNumVarArray();
        IloRange[] fill = new IloRange[2];
        fill[0] = mainSolver.addRange(-Double.MAX_VALUE, 6);
        fill[1] = mainSolver.addRange(-Double.MAX_VALUE, 4);
        IloColumn c1 = mainSolver.column(obj,2);
        c1 = c1.and(mainSolver.column(fill[0],1));
        c1 = c1.and(mainSolver.column(fill[1],1));
        vars.add(mainSolver.numVar(c1, 0,Double.MAX_VALUE));
        vars.getElement(0).setName("X"+0);
        
        IloColumn c2 = mainSolver.column(obj,1);
        c2 = c2.and(mainSolver.column(fill[0],1));
        c2 = c2.and(mainSolver.column(fill[1],4));
        vars.add(mainSolver.numVar(c2, 0,Double.MAX_VALUE));
        vars.getElement(1).setName("X"+1);
        
        IloColumn c3 = mainSolver.column(obj,-1);
        c3 = c3.and(mainSolver.column(fill[0],2));
        c3 = c3.and(mainSolver.column(fill[1],-1));
        vars.add(mainSolver.numVar(c3, 0,Double.MAX_VALUE));
        vars.getElement(2).setName("X"+2);
        
        mainSolver.setOut(null);
        
        reportModel(mainSolver, fill);
        
        mainSolver.solve();
        System.out.println("Solution value = " + mainSolver.getObjValue());
        for (int  j = 0; j < vars.getSize(); j++) 
        {
            System.out.println("X"+ j +"="+ mainSolver.getValue(vars.getElement(j)));
        }
          
        
    }
}
