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

public class FantomRarity extends EthereumApplication {


    public FantomRarity(Blockchain blockchain, String constractAddress, String path, List<String> mnemonicWorlds) throws IOException {
        super(blockchain, constractAddress, path, mnemonicWorlds);
    }

    @Override
    public void operate(int index) throws IOException, InterruptedException {
        //The operation
        super.writeSomething(5,"Just for test!");
        for(long i = 9;i<=11;i++) {
            this.summon(BigInteger.valueOf(i));
            Thread.sleep(6000);
        }
    }

    //智能合约：0xce761d788df608bd21bdd59d6f4b54b2e27f25bb 中召唤师召唤英雄的方法
    public void summon(BigInteger type){
        List<Type> paramList = new ArrayList<Type>(Arrays.asList(
                new Uint256(type)));
        String functionName = "summon";
        super.createRawTransaction(paramList,functionName);
    }

/*    public void summon(BigInteger type){
        List<Type> paramList = new ArrayList<Type>(Arrays.asList(
                new Uint256(type)));
        Function function = new Function("summon", paramList, new ArrayList<>());
        String data = FunctionEncoder.encode(function);
        BigInteger nonce = super.getAccountNonce();
        BigInteger gasPrice = super.getGasPrice();
        BigInteger estimateGas = super.getEstimateGas(data);
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, estimateGas, AddressUtils.formatAddressWithLowerCaseHexPrefix(super.constractAddress), data);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,super.blockchain.getChainId(), credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        Response<String> transaction = null;
        try {
            transaction = web3j.ethSendRawTransaction(hexValue).send();
        } catch (IOException e) {
            logger.error("An error occurred while send the transaction, the error is :{}", e);
        }
        logger.info("The result is : {}.", transaction.getResult());
    }*/

    public static void main(String[] args) throws Exception{
        FantomRarity app = new FantomRarity(
                Blockchain.FantomMainnet,
                "0xce761d788df608bd21bdd59d6f4b54b2e27f25bb",
                "d:\\rarity.txt",
                Arrays.asList("artist","spoon","average","curtain","parrot","wage","order","give","life","blue","tube","meat")
        );
        app.start(1,1000);
    }
}
