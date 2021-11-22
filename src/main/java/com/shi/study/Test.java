package com.shi.study;

import org.bitcoinj.crypto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Test {

    private static Logger logger = LoggerFactory.getLogger(Test.class);

    /**
     * path路径
     */
    public final static List<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            Arrays.asList(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

    public static FileWriter writer;

    /**
     * 创建钱包
     * @throws MnemonicException.MnemonicLengthException
     */
    public static void createWallet() throws MnemonicException.MnemonicLengthException, IOException {
//        SecureRandom secureRandom = new SecureRandom();
//        byte[] entropy = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS / 8];
//        secureRandom.engineNextBytes(entropy);

        writer = new FileWriter(new File("d:\\AirDrop.txt"));
        //生成12位助记词
        List<String> str = Arrays.asList("artist","spoon","average","curtain","parrot","wage","order","give","life","blue","tube","meat");

        //使用助记词生成钱包种子
        byte[] seed = MnemonicCode.toSeed(str, "");
        DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(masterPrivateKey);
        for(int i=0;i<2000;i++){
            getAddressByLastIndex(deterministicHierarchy,i);
//            getFriendsAddressByLastIndex(deterministicHierarchy,i);
        }
        writer.close();
    }

    public static void getAddressByLastIndex(DeterministicHierarchy deterministicHierarchy,int index) throws IOException {
        DeterministicKey deterministicKey = deterministicHierarchy
                .deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(index));
        byte[] bytes = deterministicKey.getPrivKeyBytes();
        ECKeyPair keyPair = ECKeyPair.create(bytes);
        //通过公钥生成钱包地址
        String address = Keys.getAddress(keyPair.getPublicKey());
        String str;
        if(index>=200) {
            str = "\t%d%s\t:\t0x%s\n";
        }else{
            str = "%d%s\t:\t0x%s\n";
        }
        StringBuilder space = new StringBuilder();
        int loopTimes = 8-(""+index).length();
        for(int i=0;i<loopTimes;i++){
            space.append(" ");
        }
        writer.write(String.format(str,index+1,space.toString(),address));
    }

    public static void getFriendsAddressByLastIndex(DeterministicHierarchy deterministicHierarchy,int index) throws IOException {
        int startIndex = 200+index*20;
        for(int i=0;i<20;i++) {
            getAddressByLastIndex(deterministicHierarchy,startIndex+i);
        }
    }

    public static void main(String[] args) throws Exception{
        createWallet();
    }

}