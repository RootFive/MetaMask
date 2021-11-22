package com.shi.work.app;

import com.shi.work.EthereumApplication;
import com.shi.work.enums.Blockchain;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TransferEth extends EthereumApplication {


    public TransferEth(Blockchain blockchain, String constractAddress, String path, List<String> mnemonicWorlds) throws IOException {
        super(blockchain, constractAddress, path, mnemonicWorlds);
    }

    public void operate(int index) throws IOException, InterruptedException {
        this.transferAllEth("0x659402C1A8B4C67125BefC8f67Ae7eD7eBE319E2");
    }

    public static void main(String[] args) throws Exception{
        TransferEth app = new TransferEth(
                Blockchain.EthereumTestnetRopsten,
                "",
                "d:\\transfer-eth.txt",
                Arrays.asList("solve","spice","fish","treat","pelican","kernel","pottery","apple","elevator","influx","divide","trouble")
        );
        app.start(1,1);
    }
}
