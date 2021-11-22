package com.shi.work.app;

import com.shi.work.EthereumApplication;
import com.shi.work.enums.Blockchain;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XpollinateFantomAndMatic extends EthereumApplication {


    public XpollinateFantomAndMatic(Blockchain blockchain, String constractAddress, String path, List<String> mnemonicWorlds) throws IOException {
        super(blockchain, constractAddress, path, mnemonicWorlds);
    }

    @Override
    public void operate(int index) throws IOException, InterruptedException {
        //The operation
        super.writeSomething(5,"Just for test!");
        BigInteger balance = this.balanceOf("0x04068da6c83afcfa0e13ba15a6696662335d5b75",super.getCredentials().getAddress());
        int a = 0;
    }

    public static void main(String[] args) throws Exception{
        XpollinateFantomAndMatic app = new XpollinateFantomAndMatic(
                Blockchain.FantomMainnet,
                "0xce761d788df608bd21bdd59d6f4b54b2e27f25bb",
                "d:\\rarity.txt",
                Arrays.asList("solve","spice","dolphin","treat","pelican","solve","pottery","afford","elevator","future","divide","trouble")
        );
        app.start(1,1000);
    }
}
