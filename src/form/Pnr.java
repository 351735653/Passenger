package form;

import java.util.ArrayList;

public class Pnr {
	public String pnrID;			// PNR编号
	public int pnrNum;				//人数
	public int pnrValue ;		//PNR价值
	public int pnrType;				//PNR类型
	public Plan oriplan;	//PNR的初始方案
	public int planNum;				//保护方案数
	public Pnr(String pnrID, int pnrNum, int pnrValue, int pnrType, Plan oriplan, int planNum) {
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
			System.out.println("第"+ 0 +"套保护方案 : ");
			oriplan.play();
	}
	/**
	 * 输出pnr信息，不输出原始行程信息
	 */
    public void playinfo()
    {
        System.out.println("Pnr [pnrID=" + pnrID + ", pnrNum=" + pnrNum + ", pnrValue=" + pnrValue + ", pnrType=" + pnrType
                 + ", planNum=" + planNum + "]");
    }
}
