

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class DateFormatUtil {

	@SuppressWarnings("finally")
	public static String FormatDate(String dateStr) {

		HashMap<String, String> dateRegFormat = new HashMap<String, String>();
		dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D+\\d{1,2}\\D*$", "yyyy-MM-dd-HH-mm-ss");// 2014年3月12日 13时5分34秒，2014-03-12 12:05:34，2014/3/12 12:5:34
		dateRegFormat.put("^\\d{4}\\D+\\d{2}\\D+\\d{2}\\D+\\d{2}\\D+\\d{2}$", "yyyy-MM-dd-HH-mm");// 2014-03-12 12:05
		dateRegFormat.put("^\\d{4}\\D+\\d{2}\\D+\\d{2}\\D+\\d{2}$", "yyyy-MM-dd-HH");// 2014-03-12 12
		dateRegFormat.put("^\\d{4}\\D+\\d{1,2}\\D+\\d{1,2}\\D$", "yyyy-MM-dd");// 2014-03-12
		dateRegFormat.put("^\\d{4}\\D+\\d{2}\\D*$", "yyyy-MM");// 2014-03
		dateRegFormat.put("^\\d{4}\\D$", "yyyy");// 2014
		dateRegFormat.put("^\\d{14}$", "yyyyMMddHHmmss");// 20140312120534
		dateRegFormat.put("^\\d{12}$", "yyyyMMddHHmm");// 201403121205
		dateRegFormat.put("^\\d{10}$", "yyyyMMddHH");// 2014031212
		dateRegFormat.put("^\\d{8}$", "yyyyMMdd");// 20140312
		dateRegFormat.put("^\\d{6}$", "yyyyMM");// 201403
		dateRegFormat.put("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm-ss");// 13:05:34
																							// 拼接当前日期
		dateRegFormat.put("^\\d{2}\\s*:\\s*\\d{2}$", "yyyy-MM-dd-HH-mm");// 13:05
																			// 拼接当前日期
		dateRegFormat.put("^\\d{2}\\D+\\d{1,2}\\D+\\d{1,2}$", "yy-MM-dd");// 14.10.18(年.月.日)
		dateRegFormat.put("^\\d{1,2}\\D+\\d{1,2}$", "yyyy-dd-MM");// 30.12(日.月)
																	// 拼接当前年份
		dateRegFormat.put("^\\d{1,2}\\D+\\d{1,2}\\D+\\d{4}$", "dd-MM-yyyy");// 12.21.2013(日.月.年)

		String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat formatter2;
		String dateReplace;
		String strSuccess = "";
		try {
			for (String key : dateRegFormat.keySet()) {
				if (Pattern.compile(key).matcher(dateStr).matches()) {
					formatter2 = new SimpleDateFormat(dateRegFormat.get(key));
					if (key.equals("^\\d{2}\\s*:\\s*\\d{2}\\s*:\\s*\\d{2}$") || key.equals("^\\d{2}\\s*:\\s*\\d{2}$")) {// 13:05:34
																														// 或
																														// 13:05
																														// 拼接当前日期
						dateStr = curDate + "-" + dateStr;
					} else if (key.equals("^\\d{1,2}\\D+\\d{1,2}$")) {// 21.1
																		// (日.月)
																		// 拼接当前年份
						dateStr = curDate.substring(0, 4) + "-" + dateStr;
					}
					dateReplace = dateStr.replaceAll("\\D+", "-");
					// System.out.println(dateRegExpArr[i]);
					strSuccess = formatter1.format(formatter2.parse(dateReplace));
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("-----------------日期格式无效:" + dateStr);
			throw new Exception("日期格式无效");
		} finally {
			return strSuccess;
		}
	}

	public static void main(String[] args) {
	    String[] dateStrArray=new String[]{
	        "2014-03-12 12:05:34",
	        "2014-03-12 12:05",
	        "2014-03-12 12",
	        "2014-03-12",
	        "2014-03",
	        "2014",
	        "20140312120534",
	        "2014/03/12 12:05:34",
	        "2014/3/12 12:5:34",
	        "2014年3月12日 13时5分34秒",
	        "201403121205",
	        "1234567890",
	        "20140312",
	        "201403",
	        "2000 13 33 13 13 13",
	        "30.12.2013",
	        "12.21.2013",
	        "21.1",
	        "13:05:34",
	        "12:05",
	        "14.1.8",
	        "14.10.18",
	        "2015年6月12日",
	        "2015年6月",
	        "2015年"
	    };
	    
		for (int i = 0; i < dateStrArray.length; i++) {
			System.out.println("----"+FormatDate(dateStrArray[i]));
		}
	}
}