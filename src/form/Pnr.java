package form;

import java.util.ArrayList;

public class Pnr {
	public String pnrID;			// PNR编号
	public int pnrNum;				//人数
	public int pnrValue ;		//PNR价值
	public int pnrType;				//PNR类型
	public Plan scheme;	//PNR的初始方案
	public int planNum;				//保护方案数
	public Pnr(String pnrID, int pnrNum, int pnrValue, int pnrType, Plan scheme, int planNum) {
		this.pnrID = pnrID;
		this.pnrNum = pnrNum;
		this.pnrValue = pnrValue;
		this.pnrType = pnrType;
		this.scheme = scheme;
		this.planNum = planNum;
	}

	/**
	 * 输出pnr的所有信息
	 */
	public void play()
	{
		System.out.println("Pnr [pnrID=" + pnrID + ", pnrNum=" + pnrNum + ", pnrValue=" + pnrValue + ", pnrType=" + pnrType
				 + ", planNum=" + planNum + "]");

			System.out.println("原始行程 : ");
			scheme.play();
		
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
