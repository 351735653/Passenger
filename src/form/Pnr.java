package form;

import java.util.ArrayList;

public class Pnr {
	public String pnrID;			// PNR编号
	public int pnrNum;				//人数
	public int pnrValue ;		//PNR价值
	public int pnrType;				//PNR类型
	public ArrayList<Plan> scheme;	//PNR的初始方案及保护方案
	public int planNum;				//保护方案数
	public Pnr(String pnrID, int pnrNum, int pnrValue, int pnrType, ArrayList<Plan> scheme, int planNum) {
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
		for(int i = 0 ; i < scheme.size(); i++)
		{
			System.out.println("第"+ i +"套保护方案 : ");
			scheme.get(i).play();
		}
		
		
	}
}
