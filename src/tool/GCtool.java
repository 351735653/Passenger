 package tool;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

/**
 * @author whz
 * @date 2020/05/31
 */
public class GCtool {

    public static class IloNumVarArray 
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
}
