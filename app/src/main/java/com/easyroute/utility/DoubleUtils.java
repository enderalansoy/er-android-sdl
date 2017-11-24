package com.easyroute.utility;

import java.text.DecimalFormat;

/**
 * Kayan noktalı sayılarla ilgili fonksiyonların bulunduğu utility sınıfı
 *
 * @author imeneksetmob
 */
public class DoubleUtils {

    /**
     * Varsayılan olarak virgülden sonra yuvarlanacak hane sayısı
     */
    public static final int DEFAULT_DECIMAL_PLACES = 2;

    /**
     * Kayan noktalı sayının virgülden sonrasını formatlayıp verir
     *
     * @param input
     * @return
     */
    public static String getFormattedDecimalPart(String input) {
        if (input.contains(",")) input = input.replace(",", "");
        if (input.length() != 0) {
            Double d = Double.valueOf(input);
            DecimalFormat df = new DecimalFormat("#,###");
            input = df.format(d);
            input = input.replace(",", "z");
            input = input.replace(".", ",");
            input = input.replace("z", ".");
        }
        return input;
    }

    /**
     * Gönderilen double sayının virgülden sonrasını yuvarlar
     *
     * @param val
     * @return
     */
    public static String roundAndFormat(double val) {
        return format(round(val));
    }

    /**
     * String şeklinde gönderilen kayan noktalı sayıyı formatlar
     *
     * @param input
     * @return
     */
    public static String format(String input) {
        if (input.contains(",")) input = input.replace(",", "");
        Double d = Double.valueOf(input);
        DecimalFormat df = new DecimalFormat("#,###.##");
        input = df.format(d);
        input = input.replace(",", "z");
        input = input.replace(".", ",");
        input = input.replace("z", ".");
        if (!input.contains(".")) {
            input += ".00";
        } else if (input.lastIndexOf(".") == input.length() - 2) {
            input = input + "0";
        }
        return input;
    }

    /**
     * Double'ı format'lar ve String'e çevirip döndürür
     *
     * @param val
     * @return
     */
    public static String format(double val) {
        return format(Double.toString(val));
    }

    public static double roundDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    /**
     * Parametre ile gönderilen double'ın virgülden sonrasını yuvarlayarak 2
     * haneye düşürür.
     *
     * @param val
     * @return
     */
    public static double round(double val) {
        String _d = nosci(val);
        if (!_d.contains(".")) return val;
        String _int = _d.substring(0, _d.indexOf("."));
        String _decimal = _d.substring(_d.indexOf(".") + 1);
        if (_decimal.length() > DEFAULT_DECIMAL_PLACES) {
            _decimal = _decimal.substring(0, DEFAULT_DECIMAL_PLACES);
        }
        return Double.parseDouble(_int + "." + _decimal);
    }

    public static String roundToString(double val) {
        String _d = nosci(val);
        if (!_d.contains(".")) return _d;
        String _int = _d.substring(0, _d.indexOf("."));
        String _decimal = _d.substring(_d.indexOf(".") + 1);
        long _decimalInt = Long.parseLong(_decimal);
        if (_decimalInt == 0) {
            return _int;
        } else {
            if (_decimal.length() > DEFAULT_DECIMAL_PLACES) {
                _decimal = _decimal.substring(0, DEFAULT_DECIMAL_PLACES);
            } else {
                for (int i = 0; i < DEFAULT_DECIMAL_PLACES - _decimal.length(); i++) {
                    _decimal += "0";
                }
            }
        }
        return _int + "." + _decimal;
    }

    /**
     * Büyük double sayısının virgülden sonrasını 2 haneye yuvarlar.
     *
     * @param d
     * @return
     */
    public static String nosci(double d) {
        if (d < 0) {
            return "-" + nosci(-d);
        }
        String javaString = String.valueOf(d);
        int indexOfE = javaString.indexOf("E");
        if (indexOfE == -1) {
            return javaString;
        }
        StringBuffer sb = new StringBuffer();
        if (d > 1) {// big number
            int exp = Integer.parseInt(javaString.substring(indexOfE + 1));
            String sciDecimal = javaString.substring(2, indexOfE);
            int sciDecimalLength = sciDecimal.length();
            if (exp == sciDecimalLength) {
                sb.append(javaString.charAt(0));
                sb.append(sciDecimal);
            } else if (exp > sciDecimalLength) {
                sb.append(javaString.charAt(0));
                sb.append(sciDecimal);
                for (int i = 0; i < exp - sciDecimalLength; i++) {
                    sb.append('0');
                }
            } else if (exp < sciDecimalLength) {
                sb.append(javaString.charAt(0));
                sb.append(sciDecimal.substring(0, exp));
                sb.append('.');
                for (int i = exp; i < sciDecimalLength; i++) {
                    sb.append(sciDecimal.charAt(i));
                }
            }
            return sb.toString();
        } else {
            return javaString;
        }
    }
}
