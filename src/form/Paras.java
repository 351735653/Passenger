package form;

import java.util.HashMap;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Paras {
	public static double satisfaction;						//满意度权重
	public static double profit;							//收益权重
	public static HashMap<String, Integer> CAB_SEQ;		//舱位序列
	public static Multimap<String,String>CAB_LIMIT;	//舱位限制
	public static String ORI_CAB;			//原舱位保护的等级
	public static double CAB_UP;			//升舱
	public static double CAB_DOWN;			//降舱
	public static double CHANGE_NUM;		//变动次数
	public static double TOTAL_TIME;		//总耗时
	public static double SIM_DEG;			//接近程度
	public static double MCT_TIME;			//衔接时间
	public static double PROFIT_UP;			//升舱收益
	public static double PROFIT_DOWN;		//降舱收益
	public Paras(
	    String sa,
	    String pro,
	    String cabseq,
	    String cablimit,
	    String oricab,
	    String cabup,
	    String cabdown,
	    String changenum,
	    String totaltime,
	    String simdeg,
	    String mctime,
	    String proup,
	    String prodown)
	{
	    Paras.satisfaction = Double.parseDouble(sa);
	    Paras.profit = Double.parseDouble(pro);
	    Paras.CAB_SEQ = getCabSeq(cabseq);
	    Paras.CAB_LIMIT = getCabLimit(cablimit);
	    Paras.ORI_CAB = oricab;
	    Paras.CAB_UP =Double.parseDouble(cabup);
	    Paras.CAB_DOWN = Double.parseDouble(cabdown);
	    Paras.CHANGE_NUM = Double.parseDouble(changenum);
	    Paras.TOTAL_TIME = Double.parseDouble(totaltime);
	    Paras.SIM_DEG = Double.parseDouble(simdeg);
	    Paras.MCT_TIME = Double.parseDouble(mctime);
	    Paras.PROFIT_UP = Double.parseDouble(proup);
	    Paras.PROFIT_DOWN = Double.parseDouble(prodown);
	}
	
	@Override
    public String toString() {
        return "Paras [satisfaction=" + satisfaction + ", profit=" + profit + ", CAB_SEQ=" + CAB_SEQ + ", CAB_LIMIT="
            + CAB_LIMIT + ", ORI_CAB=" + ORI_CAB + ", CAB_UP=" + CAB_UP + ", CAB_DOWN=" + CAB_DOWN + ", CHANGE_NUM="
            + CHANGE_NUM + ", TOTAL_TIME=" + TOTAL_TIME + ", SIM_DEG=" + SIM_DEG + ", MCT_TIME=" + MCT_TIME
            + ", PROFIT_UP=" + PROFIT_UP + ", PROFIT_DOWN=" + PROFIT_DOWN + "]";
    }

    /**
     * 缺省构造方法
     */
    public Paras() {
         // TODO Auto-generated constructor stub
    }

    /**
	 * 处理字符串，获得舱位等级序列
	 * @param tmp
	 * @return
	 */
	public static HashMap<String, Integer> getCabSeq(String tmp)
	{
	    String item[] = tmp.split("\\|");
	    HashMap<String, Integer> result = new HashMap<String, Integer>();
	    for(int j = 0; j < item.length; j++)
	    {
	        result.put(item[j], j);
	    }
	    return result;
	}
	
	/**
	 * 处理字符串，获得舱位等级对应关系
	 * @param tmp
	 * @return
	 */
	public static Multimap<String, String> getCabLimit(String tmp)
	{
	    Multimap<String, String> result = ArrayListMultimap.create();
	    String item[] = tmp.split("\\|");
	    for(int i = 0; i < item.length; i++)
	    {
	        String sub1[] = item[i].split("#");
	        String key = sub1[0];
	        String sub2[] = sub1[1].split("\\+");
	        for(int j = 0 ; j < sub2.length ; j++)
	        {
	            result.put(key, sub2[j]);
	        }
	    }
	    return result;
	}
}
