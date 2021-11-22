package com.shi.work.enums;

import java.math.BigInteger;

public enum Blockchain {

    EthereumMainnet("Ethereum Mainnet","https://mainnet.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161",1,"ETH","https://etherscan.io"),
    EthereumTestnetRopsten("Ethereum Testnet Ropsten","https://ropsten.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161",3,"ETH","https://ropsten.etherscan.io"),
    EthereumTestnetRinkeby("Ethereum Testnet Rinkeby","https://rinkeby.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161",4,"ETH","https://rinkeby.etherscan.io"),
    EthereumTestnetGoerli("Ethereum Testnet Goerli","https://goerli.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161",5,"ETH","https://goerli.etherscan.io"),
    EthereumTestnetKovan("Ethereum Testnet Kovan","https://kovan.infura.io/v3/9aa3d95b3bc440fa88ea12eaa4456161",42,"ETH","https://kovan.etherscan.io"),
    EthereumLayer2Optimistic("Ethereum Layer2 Optimistic","https://mainnet.optimism.io",10,"ETH","https://optimistic.etherscan.io"),
    EthereumLayer2Arbitrum("Ethereum Layer2 Arbitrum","https://arb1.arbitrum.io/rpc",42161,"ETH","https://arbiscan.io"),
    BSCMainnet("Binance Smart Chain Mainnet","https://bsc-dataseed1.ninicoin.io",56,"BNB","https://bscscan.com/"),
    MaticMainnet("Matic Mainnet","https://rpc-mainnet.maticvigil.com",137,"MATIC","https://explorer-mainnet.maticvigil.com"),
    FantomMainnet("Fantom Mainnet","https://rpcapi.fantom.network",250,"FTM","https://ftmscan.com"),
    FantomTestnet("Fantom Testnet","https://rpc.testnet.fantom.network/",4002,"FTM","https://rpcapi.fantom.network"),
    xDaiMainnet("xDai Mainnet","https://rpc.xdaichain.com/curve",100,"xDai","https://blockscout.com/xdai/mainnet/");


    private String network;
    private String endpoint;
    private long chainId;
    private String currency;
    private String explorerUrl;

    Blockchain(String network,String endpoint,long chainId,String currency,String explorerUrl){
        this.network=network;
        this.endpoint=endpoint;
        this.chainId=chainId;
        this.currency=currency;
        this.explorerUrl=explorerUrl;
    }

    public String getNetwork(){
        return this.network;
    }

    public String getEndpoint(){
        return this.endpoint;
    }

    public long getChainId(){
        return this.chainId;
    }

    public String getCurrency(){
        return this.currency;
    }

    public String getExplorerUrl(){
        return this.explorerUrl;
    }

}