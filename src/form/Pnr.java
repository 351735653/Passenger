package form;

import java.util.ArrayList;

public class Pnr {
	public String pnrID;			// PNR编号
	public int pnrNum;				//人数
	public int pnrValue ;		//PNR价值
	public int pnrType;				//PNR类型
<<<<<<< HEAD
	public Plan scheme;	//PNR的初始方案及保护方案
	public int planNum;				//保护方案数
	public Pnr(String pnrID, int pnrNum, int pnrValue, int pnrType, Plan scheme, int planNum) {
=======
	public Plan oriplan;	//PNR的初始方案
	public int planNum;				//保护方案数
	public Pnr(String pnrID, int pnrNum, int pnrValue, int pnrType, Plan oriplan, int planNum) {
>>>>>>> 589de7df336608d60781c6b4bbeb4e20d99c1a30
		this.pnrID = pnrID;
		this.pnrNum = pnrNum;
		this.pnrValue = pnrValue;
		this.pnrType = pnrType;
		this.oriplan = oriplan;
		this.planNum = planNum;
	}

	/**
	 * 输出pnr的信息及原始行程信息
	 */
	public void play()
	{
		System.out.println("Pnr [pnrID=" + pnrID + ", pnrNum=" + pnrNum + ", pnrValue=" + pnrValue + ", pnrType=" + pnrType
				 + ", planNum=" + planNum + "]");
<<<<<<< HEAD

			System.out.println("原始行程 : ");
			scheme.play();
		
	}
	
	/**
         * 输出pnr信息，不输出原始行程信息
     */
=======
			System.out.println("第"+ 0 +"套保护方案 : ");
			oriplan.play();
	}
	/**
	 * 输出pnr信息，不输出原始行程信息
	 */
>>>>>>> 589de7df336608d60781c6b4bbeb4e20d99c1a30
    public void playinfo()
    {
        System.out.println("Pnr [pnrID=" + pnrID + ", pnrNum=" + pnrNum + ", pnrValue=" + pnrValue + ", pnrType=" + pnrType
                 + ", planNum=" + planNum + "]");
    }
<<<<<<< HEAD
	
=======
>>>>>>> 589de7df336608d60781c6b4bbeb4e20d99c1a30
}
