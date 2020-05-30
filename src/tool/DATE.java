package tool;

import java.util.Calendar;

public class DATE implements Cloneable {
	public DATE() 
	{
		_tm = Calendar.getInstance();
		_tm.set(Calendar.MILLISECOND, 0);
		toDateTime();
	}
	
	public DATE(final String s) throws Exception
	{
		_tm = Calendar.getInstance();
		setDate(s);
	}
	
	public DATE clone()
	{
		try{
			DATE cloned = (DATE)super.clone();
			cloned._buf = (String) _buf;
			cloned._tm = (Calendar) _tm.clone();
			return cloned;		
		}
		catch(CloneNotSupportedException e)
		{
			return null;
		}	
	}
	
	//转换类似于2017-5-5 16:25:00的字符串为日期结构
	/**
	 * 使用字符串设置日期
	 * @param str 格式：2017-5-5 16:25:00
	 * @return 
	 * @throws Exception
	 */
	public boolean setDate(final String str) throws Exception
	{
		int y = 0, m = 0, d = 0, h = 0, M = 0, s = 0;
		int i = 0;
		while (str.charAt(i) != '-' && Character.isDigit(str.charAt(i)))	 //年
		{
			y = y * 10 + str.charAt(i) - '0';
			i++;
		}
		if (str.charAt(i) != '-')
			throw new Exception("str格式错误，格式应为YYYY-MM-DD hh:mm:ss");

		i++;
		while (str.charAt(i) != '-' && Character.isDigit(str.charAt(i)))	//月
		{
			m = m * 10 + str.charAt(i) - '0';
			i++;
		}
		m -= 1;  
		if (str.charAt(i) != '-')
			throw new Exception("str格式错误，格式应为YYYY-MM-DD hh:mm:ss");

		i++;
		while (i < str.length() && str.charAt(i) != ' ' && Character.isDigit(str.charAt(i)))    //日
		{
			d = d * 10 + str.charAt(i) - '0';
			i++;
		}
		if(str.length() > i)
		{
			if(str.charAt(i) != ' ')
				throw new Exception("str格式错误，格式应为YYYY-MM-DD hh:mm:ss");
			else
			{
				i++;
				while (str.charAt(i) != ':' && Character.isDigit(str.charAt(i)))    //时
				{
					h = h * 10 + str.charAt(i) - '0';
					i++;
				}
				if (str.charAt(i) != ':')
					throw new Exception("str格式错误，格式应为YYYY-MM-DD hh:mm:ss");

				i++;
				while (str.length() > i && str.charAt(i) != ':' && Character.isDigit(str.charAt(i)))    //分
				{
					M = M * 10 + str.charAt(i) - '0';
					i++;
				}
				if (str.length() <= i)
				{
					s = 0;
				}
				else
				{
					if (str.charAt(i) != ':')
						throw new Exception("str格式错误，格式应为YYYY-MM-DD hh:mm:ss");

					i++;
					while (str.length() > i && Character.isDigit(str.charAt(i)))
					{
						s = s * 10 + str.charAt(i) - '0';
						i++;
					}
					if (str.length() > i)
						throw new Exception("str格式错误，格式应为YYYY-MM-DD hh:mm:ss");
				}
			}
		}
	
		_tm.set(y, m, d, h, M, s);
		_tm.set(Calendar.MILLISECOND, 0);

		toDateTime();
		return true;
	}

	public Calendar getCalendar()
	{
		return _tm;
	}

	/**
	 * 计算两时间的差，dt1-dt2
	 * @param dt1
	 * @param dt2
	 * @return
	 */
	public static long difftime_min(final DATE dt1, final DATE dt2)  
	{
		return (dt1.getCalendar().getTimeInMillis() - dt2.getCalendar().getTimeInMillis()) / 1000 / 60;
	}


	/**
	 * 计算两时间的差
	 * 当前对象的时间减dt
	 * @param dt
	 * @return
	 */
	public long difftime_min(final DATE dt)
	{
		return (_tm.getTimeInMillis() - dt.getCalendar().getTimeInMillis()) / 1000 / 60;
	}

	/**
	 * 将_tm转换为YYYY-MM-DD HH:mm:ss的字符串格式
	 * @return
	 */
	public String toDateTime()       
	{
		_buf = String.format("%04d-%02d-%02d %02d:%02d:%02d", _tm.get(Calendar.YEAR), _tm.get(Calendar.MONTH) + 1, _tm.get(Calendar.DATE),
			_tm.get(Calendar.HOUR_OF_DAY), _tm.get(Calendar.MINUTE), _tm.get(Calendar.SECOND));
		
		return _buf;
	}

	/**
	 * 将_tm转换为YYYY-MM-DD的字符串格式
	 * @return
	 */
	public String toDate()       
	{
		String str = _buf.substring(0, 10);
		return str;
	}
	
	/**
	 * 将_tm转换为HH:mm:ss的字符串格式
	 * @return
	 */
	public String toTime()       
	{
		return _buf.substring(11, 19);
	}
	
	public boolean equal(final DATE other)
	{
		return _tm.equals(other._tm);
	}


	/**
	 * 当前时间加上给定时间，改变当前值
	 * @param minValue 给定的时间 单位：分钟
	 * @return
	 */
	public DATE add(int minValue)     
	{
		_tm.add(Calendar.MINUTE, minValue);
		toDateTime();
		return this;
	}
	
	/**
	 * 当前时间减去给定时间，改变当前值
	 * @param minValue 给定的时间 单位：分钟
	 * @return
	 */
	public DATE substract(int minValue)  
	{
		_tm.add(Calendar.MINUTE, -minValue);
		toDateTime();
		return this;
	}

	/**
	 * 与参数中的日期dt比较
	 * @param dt
	 * @return 早于dt返回负值，晚于dt返回正值，相等返回0
	 */
	public int compareTo(final DATE dt)
	{
		return this._tm.compareTo(dt._tm);
	}
	
	/**
	 * 获取最大时间9999.12.31 00:00:00
	 * @return
	 */
	public static DATE maxDate()
	{
		DATE dt = new DATE();
		
	    try {
	    	dt.setDate("9999-12-31 00:00:00");
		} catch (Exception e) {
			System.out.println(e.toString());
		} 
	    return dt;
	}
	
	/**
	 * 获取最小时间1970.01.01 00:00:00
	 * @return DATE类型时间
	 */
	public static DATE minDate()
	{
		DATE dt = new DATE();
		
	    try {
	    	dt.setDate("1970-1-1 00:00:00");
		} catch (Exception e) {
			System.out.println(e.toString());
		} 
	    return dt;
	}
	
	private Calendar _tm = null;
	String  _buf = null;
}
