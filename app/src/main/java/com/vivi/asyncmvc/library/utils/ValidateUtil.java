package com.vivi.asyncmvc.library.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.vivi.asyncmvc.comm.AppConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据验证Util
 *
 * @author gongwei 2018.12.25
 */
public class ValidateUtil {
    /**
     * 身份证验证
     *
     * @return boolean
     */
    public static boolean validateIdCard(String idCard) {
        return IDCardValidateUtil.isCardNo(idCard);
    }

    /**
     * 手机号验证
     *
     * @param mobile
     * @return
     */
    public static boolean validateMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return false;
        }
        String phoneRegex = "[1]\\d{10}";
        if (!mobile.matches(phoneRegex)) {
            return false;
        }
        return true;
    }

    /**
     * 邮箱验证
     *
     * @param mail
     * @return
     */
    public static boolean validateMail(String mail) {
        String RULE_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        Pattern p = Pattern.compile(RULE_EMAIL);
        Matcher m = p.matcher(mail);
        return m.matches();
    }

    /**
     * 验证密码规则:
     * ① 相同的字符不行
     * ② 顺子不行
     *
     * @param password
     * @return
     */
    public static boolean validatePasswordRule(String password) {
        if (TextUtils.isEmpty(password) || password.length() < AppConfig.PASSWORD_MIN_LENGTH || password.length() > AppConfig.PASSWORD_MAX_LENGTH) {
            UI.showToast("请输入8-16位密码，不含空格");
            return false;
        } else if (isSameCharacters(password) || isContinuousDigit(password)) {
            UI.showToast("密码安全等级过低，请重新设置密码");
            return false;
        }
        return true;
    }

    /**
     * 判断字符串是否为相同的字符构成
     *
     * @param text
     * @return
     */
    private static boolean isSameCharacters(String text) {
        if (TextUtils.isEmpty(text)) return false;
        if (text.length() == 1) return true;
        char first = text.charAt(0);
        for (int i = 1; i < text.length(); i++) {
            if (first != text.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否连续数字
     * 纯数字(数字0-9,对应ASCII为48-57)
     *
     * @param text
     * @return true：是连续数字
     */
    public static boolean isContinuousDigit(String text) {
        for (int i = 0; i < text.length() - 1; i++) {
            //当前ascii值
            int currentAscii = Integer.valueOf(text.charAt(i));
            //下一个ascii值
            int nextAscii = Integer.valueOf(text.charAt(i + 1));
            //满足区间进行判断
            if (rangeInDefined(currentAscii, 48, 57) && rangeInDefined(nextAscii, 48, 57)) {
                //计算两数之间差一位则为连续
                if (Math.abs((nextAscii - currentAscii)) != 1) {
                    return false;
                } else {
                    continue;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断一个数字是否在某个区间
     *
     * @param current 当前比对值
     * @param min     最小范围值
     * @param max     最大范围值
     * @return
     */
    private static boolean rangeInDefined(int current, int min, int max) {
        return Math.max(min, current) == Math.min(current, max);
    }
}
