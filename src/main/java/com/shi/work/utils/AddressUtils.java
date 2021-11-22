package com.shi.work.utils;

public class AddressUtils {
    public static String formatAddressWithLowerCaseHexPrefix(String address){
        return formatAddressWithHexPrefix(address,"0x");
    }

    public static String formatAddressWithUpperCaseHexPrefix(String address){
        return formatAddressWithHexPrefix(address,"0X");
    }

    public static String formatAddressWithHexPrefix(String address,String prefix) {
        if (address != null && org.web3j.crypto.WalletUtils.isValidAddress(address)) {
            if (address.length() == 40) {
                address = prefix + address;
            } else {
                if(!address.startsWith(prefix)){
                    address = prefix+address.substring(2);
                }
            }
            return address;
        }
        throw new RuntimeException("The address :{} is invalid, length(address) must be 40!");
    }

    public static String formatHashLowerCaseHexPrefix(String hexStr){
        if(hexStr!=null){
            if(hexStr.startsWith("0x")) {
                return hexStr;
            }else if(hexStr.startsWith("0X")){
                hexStr = hexStr.toLowerCase();
            }else{
                hexStr = "0x" + hexStr;
            }
            return hexStr;
        }
        return null;
    }
}
