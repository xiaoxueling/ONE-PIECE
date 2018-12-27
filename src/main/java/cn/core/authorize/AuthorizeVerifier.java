package cn.core.authorize;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import cn.core.authorize.Authorize.AuthorizeType;
import cn.util.DataConvert;

@Service
public class AuthorizeVerifier {
	public static boolean has(HttpSession session, Authorize authorize) {
		return has(session, authorize.setting(), authorize.type());
	}

	@SuppressWarnings("unchecked")
	public static boolean has(HttpSession session, String setting, AuthorizeType type) {
		
		int userType=DataConvert.ToInteger(session.getAttribute("userType"),1);
		if(userType==0){
			return true;
		}
		List<String> aList = (List<String>) session.getAttribute("AuthorityID");
		if(aList==null||aList.isEmpty()){
			return false;
		}
		String[] arr = setting.split(",");
		int index = 0;
		for (int i = 0; i < arr.length; i++) {
			String sett = arr[i];
			if (type == AuthorizeType.ONE) {
				for (String mm : aList) {
					if (mm.trim().equals(sett.trim())) {
						return true;
					} else {
						continue;
					}
				}
			} else {
				for (String mm : aList) {
					if (mm.trim().equals(sett.trim())) {
						index++;
						if (index == arr.length) {
							return true;
						}
					} else {
						continue;
					}
				}
			}
		}
		return false;
	}
}
