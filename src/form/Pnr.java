package form;

import java.util.ArrayList;

public class Pnr {
	public String pnrID;			// PNR���
	public int pnrNum;				//����
	public int pnrValue ;		//PNR��ֵ
	public int pnrType;				//PNR����
	public ArrayList<Plan> scheme;	//PNR�ĳ�ʼ��������������
	public int planNum;				//����������
	public Pnr(String pnrID, int pnrNum, int pnrValue, int pnrType, ArrayList<Plan> scheme, int planNum) {
		this.pnrID = pnrID;
		this.pnrNum = pnrNum;
		this.pnrValue = pnrValue;
		this.pnrType = pnrType;
		this.scheme = scheme;
		this.planNum = planNum;
	}

	/**
	 * ���pnr��������Ϣ
	 */
	public void play()
	{
		System.out.println("Pnr [pnrID=" + pnrID + ", pnrNum=" + pnrNum + ", pnrValue=" + pnrValue + ", pnrType=" + pnrType
				 + ", planNum=" + planNum + "]");
		for(int i = 0 ; i < scheme.size(); i++)
		{
			System.out.println("��"+ i +"�ױ������� : ");
			scheme.get(i).play();
		}
		
		
	}
}
