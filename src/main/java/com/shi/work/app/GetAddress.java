package com.shi.work.app;

import com.shi.work.EthereumApplication;
import com.shi.work.enums.Blockchain;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GetAddress extends EthereumApplication {


    public GetAddress(Blockchain blockchain, String constractAddress, String path, List<String> mnemonicWorlds) throws IOException {
        super(blockchain, constractAddress, path, mnemonicWorlds);
    }

    public void operate(int index) throws IOException, InterruptedException {
        //The operation
        super.writeSomething(5,"Just for test!");
    }

    public static void main(String[] args) throws Exception{
        GetAddress app = new GetAddress(
                Blockchain.FantomMainnet,
                "0xce761d788df608bd21bdd59d6f4b54b2e27f25bb",
                "d:\\address.txt",
                Arrays.asList("artist","spoon","average","curtain","parrot","wage","order","give","life","blue","tube","meat")
        );
        app.start(1,1000);
    }
}
