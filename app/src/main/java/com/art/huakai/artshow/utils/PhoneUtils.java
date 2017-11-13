package com.art.huakai.artshow.utils;

import android.text.TextUtils;

/**
 * 手机号工具类
 *
 * @author win7
 */
public class PhoneUtils {
    /**
     * @param phone 获取的完整的手机号，可能带+86等信息
     * @return 截取的11位187XXXXXXXX手机号或者“”空的手机号
     */
    public static String get11PhoneNum(String phone) {
        int numLength = phone.length();
        if (numLength >= 11) {
            phone = phone.substring(numLength - 11, numLength);
            return phone.startsWith("1") ? phone : "";
        } else {
            return "";
        }

    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNumber(String mobiles) {
        /*
        移动：134、135、136、137、138、139、147、150、151、157(TD)、158、159、187、188
		联通：130、131、132、152、155、156、185、186
		电信：133、153、180、189、（1349卫通）
		总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		*/
        String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }

}
