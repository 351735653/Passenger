package form;

import java.util.ArrayList;

public class Pnr {
	public String pnrID;			// PNR���
	public int pnrNum;				//����
	public int pnrValue ;		//PNR��ֵ
	public int pnrType;				//PNR����
	public Plan scheme;	//PNR�ĳ�ʼ����
	public int planNum;				//����������
	public Pnr(String pnrID, int pnrNum, int pnrValue, int pnrType, Plan scheme, int planNum) {
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

			System.out.println("ԭʼ�г� : ");
			scheme.play();
		
	}
	
	/**
         * ���pnr��Ϣ�������ԭʼ�г���Ϣ
     */
    public void playinfo()
    {
        System.out.println("Pnr [pnrID=" + pnrID + ", pnrNum=" + pnrNum + ", pnrValue=" + pnrValue + ", pnrType=" + pnrType
                 + ", planNum=" + planNum + "]");
    }
	
}
