package form;

import java.util.ArrayList;

public class Pnr {
	public String pnrID;			// PNR���
	public int pnrNum;				//����
	public int pnrValue ;		//PNR��ֵ
	public int pnrType;				//PNR����
	public Plan oriplan;	//PNR�ĳ�ʼ����
	public int planNum;				//����������
	public Pnr(String pnrID, int pnrNum, int pnrValue, int pnrType, Plan oriplan, int planNum) {
		this.pnrID = pnrID;
		this.pnrNum = pnrNum;
		this.pnrValue = pnrValue;
		this.pnrType = pnrType;
		this.oriplan = oriplan;
		this.planNum = planNum;
	}

	/**
	 * ���pnr����Ϣ��ԭʼ�г���Ϣ
	 */
	public void play()
	{
		System.out.println("Pnr [pnrID=" + pnrID + ", pnrNum=" + pnrNum + ", pnrValue=" + pnrValue + ", pnrType=" + pnrType
				 + ", planNum=" + planNum + "]");
			System.out.println("��"+ 0 +"�ױ������� : ");
			oriplan.play();
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
