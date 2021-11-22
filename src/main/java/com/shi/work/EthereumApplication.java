package com.shi.work;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.shi.study.Test;
import com.shi.work.enums.Blockchain;
import com.shi.work.utils.AddressUtils;
import org.bitcoinj.crypto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class EthereumApplication {

    private Blockchain blockchain;

    private String constractAddress;

    private FileWriter writer;

    private Web3j web3j;

    private Credentials credentials;

    private List<String> mnemonicWorlds;

    private static Logger logger = LoggerFactory.getLogger(Test.class);

    public EthereumApplication(Blockchain blockchain, String constractAddress, String path, List<String> mnemonicWorlds) throws IOException {
        this.blockchain = blockchain;
        this.constractAddress = constractAddress;
        this.writer = new FileWriter(new File(path));
        this.mnemonicWorlds = mnemonicWorlds;
    }

    /**
     * path路径
     */
    public final static List<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            Arrays.asList(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

    public void start(int index,int length) throws Exception {
        byte[] seed = MnemonicCode.toSeed(this.mnemonicWorlds, "");
        DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(masterPrivateKey);
        for(int i=index-1;i<index+length-1;i++){
            DeterministicKey deterministicKey = deterministicHierarchy
                    .deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(i));
            byte[] bytes = deterministicKey.getPrivKeyBytes();
            ECKeyPair keyPair = ECKeyPair.create(bytes);
            //init web3j and credentials.
            this.init(keyPair);
            //write address and index into file
            String str = "%d : 0x%s\t";
            this.writeSomething(0,String.format(str,i+1,this.credentials.getAddress()));
            this.operate(i);
            this.writeSomething(0,"\n\n");
        }
        this.writer.flush();;
        this.writer.close();
    }

    public abstract void operate(int index) throws Exception;

    public void writeSomething(int tabNumber,String info) {
        String str = "%s%s\n";
        StringBuilder tabPrefix = new StringBuilder();
        for(int i = 0; i<tabNumber;i++){
            tabPrefix.append("\t");
        }
        try {
            this.writer.write(String.format(str,tabPrefix,info));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(ECKeyPair keyPair){
        this.web3j = Web3j.build(new HttpService(this.blockchain.getEndpoint()));
        this.credentials = Credentials.create(keyPair);
    }

    public BigInteger getAccountNonce() {
        BigInteger nonce = null;
        try {
            EthGetTransactionCount ethGetTransactionCount = this.web3j.ethGetTransactionCount(
                    AddressUtils.formatAddressWithLowerCaseHexPrefix(this.credentials.getAddress()), DefaultBlockParameterName.PENDING).send();
            nonce = ethGetTransactionCount.getTransactionCount();
            logger.info("The nonce of {} is : {}.",this.credentials.getAddress(),nonce);
        } catch (IOException e) {
            logger.error("An error occurred while get the nonce of account:{}, the error is :{}", credentials.getAddress(), e);
        }
        return nonce;
    }

    public BigInteger getEstimateGas(String data) {
        Transaction transaction = Transaction.createEthCallTransaction(this.credentials.getAddress(), AddressUtils.formatAddressWithLowerCaseHexPrefix(this.constractAddress), data);
        EthEstimateGas estimateGas = null;
        try {
            estimateGas = this.web3j.ethEstimateGas(transaction).send();
        } catch (IOException e) {
            logger.error("An error occurred while estimate the gas of the transaction, the error is :{}", e);
        }
        return Numeric.toBigInt(estimateGas.getResult());
    }

    public BigInteger getGasPrice() {
        EthGasPrice gasPrice = new EthGasPrice();
        try {
            gasPrice = this.web3j.ethGasPrice().send();
        } catch (IOException e) {
            logger.error("An error occurred while estimate the gas of the transaction, the error is :{}", e);
        }
        return gasPrice.getGasPrice();
    }

    public EthTransaction getTransactionByHash(String transactionHash) {
        EthTransaction response = null;
        try {
            response = this.web3j.ethGetTransactionByHash(AddressUtils.formatHashLowerCaseHexPrefix(transactionHash)).send();
        } catch (IOException e) {
            logger.error("An error occurred while get transaction,the hash of transaction is:{}, the error is :{}", transactionHash, e);
        }
        return response;
    }

    public EthGetTransactionReceipt getTransactionRecepit(String transactionHash) {
        EthGetTransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = this.web3j.ethGetTransactionReceipt(AddressUtils.formatHashLowerCaseHexPrefix(transactionHash)).send();
        } catch (IOException e) {
            logger.error("An error occurred while get the recepit of transaction,the hash of transaction is:{}, the error is :{}", transactionHash, e);
        }
        return transactionReceipt;
    }

    public void createRawTransaction(List<Type> paramList,String functionName){
        Function function = new Function(functionName, paramList, new ArrayList<>());
        String data = FunctionEncoder.encode(function);
        BigInteger nonce = this.getAccountNonce();
        BigInteger gasPrice = this.getGasPrice();
        BigInteger estimateGas = this.getEstimateGas(data);
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, estimateGas, AddressUtils.formatAddressWithLowerCaseHexPrefix(this.constractAddress), data);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,this.blockchain.getChainId(), credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        Response<String> transaction = null;
        try {
            transaction = web3j.ethSendRawTransaction(hexValue).send();
        } catch (IOException e) {
            logger.error("An error occurred while send the transaction, the error is :{}", e);
        }
        logger.info("The result is : {}.", transaction.getResult());
    }

    BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);

    public BigInteger balanceOf(String tokenAddress,String toAddress){
        //outputParameters用于接收查询结果
        List<TypeReference<?>> outputParameters = Arrays.asList(new TypeReference<Uint256>() {
        }, new TypeReference<Uint256>() {
        });
        List<Type> inputParameters = new ArrayList<Type>(Arrays.asList(new Utf8String(AddressUtils.formatAddressWithLowerCaseHexPrefix(toAddress))));
        Function function = new Function("balanceOf", inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(this.credentials.getAddress(), AddressUtils.formatAddressWithLowerCaseHexPrefix(tokenAddress), data);
        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> result = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            return new BigInteger(result.get(0).toString());
        } catch (IOException e) {
            logger.error("An error occurred, the error is :{}", e);
        }
        return BigInteger.valueOf(0);
    }

    public boolean transferToken(String tokenAddress,String toAddress,long amount){
        //outputParameters用于接收查询结果
        List<TypeReference<?>> outputParameters = Arrays.asList(new TypeReference<Uint256>() {
        }, new TypeReference<Bool>() {
        });
        List<Type> inputParameters = new ArrayList<Type>(Arrays.asList(new Utf8String(AddressUtils.formatAddressWithLowerCaseHexPrefix(toAddress)),new Uint256(amount)));
        Function function = new Function("transfer", inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(this.credentials.getAddress(), AddressUtils.formatAddressWithLowerCaseHexPrefix(tokenAddress), data);
        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> result = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            return result.get(0).toString()=="true"?true:false;
        } catch (IOException e) {
            logger.error("An error occurred, the error is :{}", e);
        }
        return false;
    }

    public void transferEth(String toAddress,long amount){
        BigInteger nonce = this.getAccountNonce();
        BigInteger gasPrice = this.getGasPrice();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, gasPrice,Convert.toWei("45000", Convert.Unit.WEI).toBigInteger(), toAddress, BigInteger.valueOf(amount));
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = null;
        try {
            ethSendTransaction = this.web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        } catch (Exception e) {
            this.writeSomething(3,String.format("%s transfer %d eth to %s error: %s",this.credentials.getAddress(),amount,toAddress,e.getMessage()));
        }
        if (ethSendTransaction.hasError()) {
            this.writeSomething(3,String.format("%s transfer %d eth to %s error: %s",this.credentials.getAddress(),amount,toAddress,ethSendTransaction.getError().getMessage()));
        }
    }

    public void transferAllEth(String toAddress){
        BigInteger nonce = this.getAccountNonce();
        BigInteger gasPrice = this.getGasPrice();
        BigInteger gasLimit = BigInteger.valueOf(45000);
        BigInteger balance = this.getEthBalance(this.credentials.getAddress());
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, gasPrice,gasLimit, toAddress, balance.subtract(gasPrice.multiply(gasLimit)));
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = null;
        try {
            ethSendTransaction = this.web3j.ethSendRawTransaction(hexValue).send();
        } catch (Exception e) {
            this.writeSomething(3,String.format("%s transfer %d eth to %s error: %s",this.credentials.getAddress(),balance,toAddress,e.getMessage()));
        }
        if (ethSendTransaction.hasError()) {
            this.writeSomething(3,String.format("%s transfer %d eth to %s error: %s",this.credentials.getAddress(),balance,toAddress,ethSendTransaction.getError().getMessage()));
        }
    }

    public BigInteger getEthBalance(String toAddress){
        EthGetBalance ethGetBalance = null;
        try {
            ethGetBalance = this.web3j.ethGetBalance(toAddress, DefaultBlockParameterName.PENDING).send();
        } catch (Exception e){
            e.printStackTrace();
        }
        return ethGetBalance == null ? BigInteger.valueOf(0) : ethGetBalance.getBalance();
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public String getConstractAddress() {
        return constractAddress;
    }

    public Web3j getWeb3j() {
        return web3j;
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
