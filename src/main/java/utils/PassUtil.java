package utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * ClassName:PassUtil
 * Package:utils
 * Description:
 *
 * @Author: thechen
 * @Create: 2023/11/6 - 9:57
 */
public class PassUtil {
	private PassUtil(){
	}

	public static String passEncode(String pass){
		return BCrypt.hashpw(pass, BCrypt.gensalt(12));
	}

	public static boolean checkPass(String userEnteredPassword, String hashedPassword){
		return BCrypt.checkpw(userEnteredPassword, hashedPassword);
	}
}
