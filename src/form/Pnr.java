package form;

import java.util.ArrayList;

public class Pnr {
	public String pnrID;			// PNR���
	public int pnrNum;				//����
	public int pnrValue ;		//PNR��ֵ
	public int pnrType;				//PNR����
<<<<<<< HEAD
	public Plan scheme;	//PNR�ĳ�ʼ��������������
	public int planNum;				//����������
	public Pnr(String pnrID, int pnrNum, int pnrValue, int pnrType, Plan scheme, int planNum) {
=======
	public Plan oriplan;	//PNR�ĳ�ʼ����
	public int planNum;				//����������
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
	 * ���pnr����Ϣ��ԭʼ�г���Ϣ
	 */
	public void play()
	{
		System.out.println("Pnr [pnrID=" + pnrID + ", pnrNum=" + pnrNum + ", pnrValue=" + pnrValue + ", pnrType=" + pnrType
				 + ", planNum=" + planNum + "]");
<<<<<<< HEAD

			System.out.println("ԭʼ�г� : ");
			scheme.play();
		
	}
	
	/**
         * ���pnr��Ϣ�������ԭʼ�г���Ϣ
     */
=======
			System.out.println("��"+ 0 +"�ױ������� : ");
			oriplan.play();
	}
	/**
	 * ���pnr��Ϣ�������ԭʼ�г���Ϣ
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
