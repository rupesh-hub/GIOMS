//package com.gerp.attendance.config.converter;
//
//import com.gerp.shared.utils.UtilityService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.lang.NonNull;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//
//@Component
//public class StringtoLocalDateConverter implements Converter<String, LocalDate> {
//
//	@Autowired
//	private UtilityService utilityService;
//
//	@Override
//	public LocalDate convert(@NonNull String textDate) {
//		try {
////			System.out.println("-----------------------");
////			System.out.println(date);
////			System.out.println("-----------------------");
////			String textDate = date.trim();
//			if (textDate.equals(""))
//				return null;
//			else {
//				return utilityService.stringToLocalDate(textDate);
//			}
//		}catch (Exception e){
//			return null;
//		}
//	}
//}
