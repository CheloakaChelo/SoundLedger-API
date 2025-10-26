package br.com.SoundLedger_API.api.blockchain.contracts;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.10.0.
 */
@SuppressWarnings("rawtypes")
public class SoundLedgerContract extends Contract {
    public static final String BINARY = "608060405234801561000f575f5ffd5b50604051610f5e380380610f5e83398101604081905261002e91610380565b5f80546001600160a01b03191633179055600161004b858261052a565b506002610058848261052a565b505f60035580518251146100b35760405162461bcd60e51b815260206004820152601760248201527f57616c6c6574732f53706c697473206d69736d6174636800000000000000000060448201526064015b60405180910390fd5b5f805b83518110156101ea575f8382815181106100d2576100d26105e4565b6020026020010151116101275760405162461bcd60e51b815260206004820152601460248201527f53706c69742063616e6e6f74206265207a65726f00000000000000000000000060448201526064016100aa565b60056040518060400160405280868481518110610146576101466105e4565b60200260200101516001600160a01b0316815260200185848151811061016e5761016e6105e4565b6020908102919091018101519091528254600180820185555f94855293829020835160029092020180546001600160a01b0319166001600160a01b0390921691909117815591015191015582518390829081106101cd576101cd6105e4565b6020026020010151826101e091906105f8565b91506001016100b6565b508060641461023b5760405162461bcd60e51b815260206004820152601360248201527f53706c697473206d7573742073756d203130300000000000000000000000000060448201526064016100aa565b505050505061061d565b634e487b7160e01b5f52604160045260245ffd5b604051601f8201601f191681016001600160401b038111828210171561028157610281610245565b604052919050565b5f82601f830112610298575f5ffd5b81516001600160401b038111156102b1576102b1610245565b6102c4601f8201601f1916602001610259565b8181528460208386010111156102d8575f5ffd5b8160208501602083015e5f918101602001919091529392505050565b5f6001600160401b0382111561030c5761030c610245565b5060051b60200190565b5f82601f830112610325575f5ffd5b8151610338610333826102f4565b610259565b8082825260208201915060208360051b860101925085831115610359575f5ffd5b602085015b8381101561037657805183526020928301920161035e565b5095945050505050565b5f5f5f5f60808587031215610393575f5ffd5b84516001600160401b038111156103a8575f5ffd5b6103b487828801610289565b602087015190955090506001600160401b038111156103d1575f5ffd5b6103dd87828801610289565b604087015190945090506001600160401b038111156103fa575f5ffd5b8501601f8101871361040a575f5ffd5b8051610418610333826102f4565b8082825260208201915060208360051b850101925089831115610439575f5ffd5b6020840193505b8284101561046f5783516001600160a01b038116811461045e575f5ffd5b825260209384019390910190610440565b6060890151909550925050506001600160401b0381111561048e575f5ffd5b61049a87828801610316565b91505092959194509250565b600181811c908216806104ba57607f821691505b6020821081036104d857634e487b7160e01b5f52602260045260245ffd5b50919050565b601f82111561052557805f5260205f20601f840160051c810160208510156105035750805b601f840160051c820191505b81811015610522575f815560010161050f565b50505b505050565b81516001600160401b0381111561054357610543610245565b6105578161055184546104a6565b846104de565b6020601f821160018114610589575f83156105725750848201515b5f19600385901b1c1916600184901b178455610522565b5f84815260208120601f198516915b828110156105b85787850151825560209485019460019092019101610598565b50848210156105d557868401515f19600387901b60f8161c191681555b50505050600190811b01905550565b634e487b7160e01b5f52603260045260245ffd5b8082018082111561061757634e487b7160e01b5f52601160045260245ffd5b92915050565b6109348061062a5f395ff3fe6080604052600436106100a8575f3560e01c80638dde3318116100625780638dde331814610229578063a23718d714610267578063c73599791461027b578063ca57cc9b1461029a578063e2dfb8b2146102ae578063fb841b46146102c3575f5ffd5b806315db71161461015557806320e66efe1461019357806321c34fcb146101a7578063318a0e29146101bd5780637a8b31e4146101de5780638da5cb5b146101f3575f5ffd5b36610151575f34116101015760405162461bcd60e51b815260206004820152601e60248201527f4465706f7369742076616c7565206d75737420626520706f736974697665000060448201526064015b60405180910390fd5b3460045f8282546101129190610748565b9091555050604080513381523460208201527f904cd7c4dd50dd37b41bafc036d9eb1876b6cc3fa8a39a350b80fc6ab6a47a04910160405180910390a1005b5f5ffd5b348015610160575f5ffd5b5061018061016f366004610761565b60066020525f908152604090205481565b6040519081526020015b60405180910390f35b34801561019e575f5ffd5b50600354610180565b3480156101b2575f5ffd5b506101bb6102e4565b005b3480156101c8575f5ffd5b506101d1610434565b60405161018a919061078e565b3480156101e9575f5ffd5b5061018060045481565b3480156101fe575f5ffd5b505f54610211906001600160a01b031681565b6040516001600160a01b03909116815260200161018a565b348015610234575f5ffd5b506102486102433660046107c3565b6104c0565b604080516001600160a01b03909316835260208301919091520161018a565b348015610272575f5ffd5b506101d16104f6565b348015610286575f5ffd5b506101bb6102953660046107c3565b610503565b3480156102a5575f5ffd5b506101bb610568565b3480156102b9575f5ffd5b5061018060035481565b3480156102ce575f5ffd5b506102d76106c1565b60405161018a91906107da565b335f81815260066020526040902054806103405760405162461bcd60e51b815260206004820152601d60248201527f566f6365206e616f2074656d2073616c646f207061726120736163617200000060448201526064016100f8565b6001600160a01b0382165f818152600660205260408082208290555190919083908381818185875af1925050503d805f8114610397576040519150601f19603f3d011682016040523d82523d5f602084013e61039c565b606091505b50509050806103ed5760405162461bcd60e51b815260206004820152601960248201527f46616c686120616f20656e76696172206f732066756e646f730000000000000060448201526064016100f8565b604080516001600160a01b0385168152602081018490527f312b70e5726648ad7ab64374bb491934c2732cfb4f8c285ea2c1bbe4fa07f3db910160405180910390a1505050565b6001805461044190610831565b80601f016020809104026020016040519081016040528092919081815260200182805461046d90610831565b80156104b85780601f1061048f576101008083540402835291602001916104b8565b820191905f5260205f20905b81548152906001019060200180831161049b57829003601f168201915b505050505081565b600581815481106104cf575f80fd5b5f918252602090912060029091020180546001909101546001600160a01b03909116915082565b6002805461044190610831565b5f546001600160a01b0316331461052c5760405162461bcd60e51b81526004016100f890610869565b60038190556040518181527f89fbeade86ef86773f0077acd0783f24f331b82366f9107e2a30db80381bf1e7906020015b60405180910390a150565b5f546001600160a01b031633146105915760405162461bcd60e51b81526004016100f890610869565b600454806105e15760405162461bcd60e51b815260206004820152601860248201527f4e6f2062616c616e636520746f2064697374726962757465000000000000000060448201526064016100f8565b5f60048190555b600554811015610690575f60058281548110610606576106066108b4565b5f91825260208083206040805180820190915260029093020180546001600160a01b031683526001015490820181905290925060649061064690866108c8565b61065091906108df565b905080156106865781516001600160a01b03165f9081526006602052604081208054839290610680908490610748565b90915550505b50506001016105e8565b506040518181527f1e6812624aa5f0975bc161996447b115443437d70e53e15143998a4558fa24459060200161055d565b60606005805480602002602001604051908101604052809291908181526020015f905b8282101561072b575f848152602090819020604080518082019091526002850290910180546001600160a01b031682526001908101548284015290835290920191016106e4565b50505050905090565b634e487b7160e01b5f52601160045260245ffd5b8082018082111561075b5761075b610734565b92915050565b5f60208284031215610771575f5ffd5b81356001600160a01b0381168114610787575f5ffd5b9392505050565b602081525f82518060208401528060208501604085015e5f604082850101526040601f19601f83011684010191505092915050565b5f602082840312156107d3575f5ffd5b5035919050565b602080825282518282018190525f918401906040840190835b8181101561082657835180516001600160a01b0316845260209081015181850152909301926040909201916001016107f3565b509095945050505050565b600181811c9082168061084557607f821691505b60208210810361086357634e487b7160e01b5f52602260045260245ffd5b50919050565b6020808252602b908201527f5265737472697461206170656e617320616f2070726f70726965746172696f2060408201526a646f20636f6e747261746f60a81b606082015260800190565b634e487b7160e01b5f52603260045260245ffd5b808202811582820484141761075b5761075b610734565b5f826108f957634e487b7160e01b5f52601260045260245ffd5b50049056fea264697066735822122012077f6530c2fd9d13b80a3445be476afe269e85bd75d3ec02cc232c987fce4364736f6c634300081d0033";

    public static final String FUNC_DISTRIBUTEACCUMULATEDBALANCE = "distributeAccumulatedBalance";

    public static final String FUNC_GETRIGHTSHOLDERSINFO = "getRightsHoldersInfo";

    public static final String FUNC_GETTOTALPLAYS = "getTotalPlays";

    public static final String FUNC_ISRC = "isrc";

    public static final String FUNC_MUSICTITLE = "musicTitle";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RELEASABLEROYALTIES = "releasableRoyalties";

    public static final String FUNC_RIGHTSHOLDERSINFO = "rightsHoldersInfo";

    public static final String FUNC_TOTALCONTRACTBALANCERECEIVED = "totalContractBalanceReceived";

    public static final String FUNC_TOTALPLAYSREPORTED = "totalPlaysReported";

    public static final String FUNC_UPDATEPLAYCOUNT = "updatePlayCount";

    public static final String FUNC_WITHDRAWROYALTIES = "withdrawRoyalties";

    public static final Event DISTRIBUTIONCOMPLETE_EVENT = new Event("DistributionComplete", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event PLAYCOUNTUPDATED_EVENT = new Event("PlayCountUpdated", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
    ;

    public static final Event ROYALTIESDEPOSITED_EVENT = new Event("RoyaltiesDeposited", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event ROYALTIESWITHDRAWN_EVENT = new Event("RoyaltiesWithdrawn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected SoundLedgerContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SoundLedgerContract(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SoundLedgerContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SoundLedgerContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<DistributionCompleteEventResponse> getDistributionCompleteEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(DISTRIBUTIONCOMPLETE_EVENT, transactionReceipt);
        ArrayList<DistributionCompleteEventResponse> responses = new ArrayList<DistributionCompleteEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DistributionCompleteEventResponse typedResponse = new DistributionCompleteEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.totalDistributed = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static DistributionCompleteEventResponse getDistributionCompleteEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(DISTRIBUTIONCOMPLETE_EVENT, log);
        DistributionCompleteEventResponse typedResponse = new DistributionCompleteEventResponse();
        typedResponse.log = log;
        typedResponse.totalDistributed = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<DistributionCompleteEventResponse> distributionCompleteEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getDistributionCompleteEventFromLog(log));
    }

    public Flowable<DistributionCompleteEventResponse> distributionCompleteEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DISTRIBUTIONCOMPLETE_EVENT));
        return distributionCompleteEventFlowable(filter);
    }

    public static List<PlayCountUpdatedEventResponse> getPlayCountUpdatedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PLAYCOUNTUPDATED_EVENT, transactionReceipt);
        ArrayList<PlayCountUpdatedEventResponse> responses = new ArrayList<PlayCountUpdatedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PlayCountUpdatedEventResponse typedResponse = new PlayCountUpdatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.newTotalPlays = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static PlayCountUpdatedEventResponse getPlayCountUpdatedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PLAYCOUNTUPDATED_EVENT, log);
        PlayCountUpdatedEventResponse typedResponse = new PlayCountUpdatedEventResponse();
        typedResponse.log = log;
        typedResponse.newTotalPlays = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<PlayCountUpdatedEventResponse> playCountUpdatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getPlayCountUpdatedEventFromLog(log));
    }

    public Flowable<PlayCountUpdatedEventResponse> playCountUpdatedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PLAYCOUNTUPDATED_EVENT));
        return playCountUpdatedEventFlowable(filter);
    }

    public static List<RoyaltiesDepositedEventResponse> getRoyaltiesDepositedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ROYALTIESDEPOSITED_EVENT, transactionReceipt);
        ArrayList<RoyaltiesDepositedEventResponse> responses = new ArrayList<RoyaltiesDepositedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoyaltiesDepositedEventResponse typedResponse = new RoyaltiesDepositedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RoyaltiesDepositedEventResponse getRoyaltiesDepositedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ROYALTIESDEPOSITED_EVENT, log);
        RoyaltiesDepositedEventResponse typedResponse = new RoyaltiesDepositedEventResponse();
        typedResponse.log = log;
        typedResponse.from = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<RoyaltiesDepositedEventResponse> royaltiesDepositedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRoyaltiesDepositedEventFromLog(log));
    }

    public Flowable<RoyaltiesDepositedEventResponse> royaltiesDepositedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROYALTIESDEPOSITED_EVENT));
        return royaltiesDepositedEventFlowable(filter);
    }

    public static List<RoyaltiesWithdrawnEventResponse> getRoyaltiesWithdrawnEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ROYALTIESWITHDRAWN_EVENT, transactionReceipt);
        ArrayList<RoyaltiesWithdrawnEventResponse> responses = new ArrayList<RoyaltiesWithdrawnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RoyaltiesWithdrawnEventResponse typedResponse = new RoyaltiesWithdrawnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static RoyaltiesWithdrawnEventResponse getRoyaltiesWithdrawnEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ROYALTIESWITHDRAWN_EVENT, log);
        RoyaltiesWithdrawnEventResponse typedResponse = new RoyaltiesWithdrawnEventResponse();
        typedResponse.log = log;
        typedResponse.to = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<RoyaltiesWithdrawnEventResponse> royaltiesWithdrawnEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getRoyaltiesWithdrawnEventFromLog(log));
    }

    public Flowable<RoyaltiesWithdrawnEventResponse> royaltiesWithdrawnEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ROYALTIESWITHDRAWN_EVENT));
        return royaltiesWithdrawnEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> distributeAccumulatedBalance() {
        final Function function = new Function(
                FUNC_DISTRIBUTEACCUMULATEDBALANCE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<List> getRightsHoldersInfo() {
        final Function function = new Function(FUNC_GETRIGHTSHOLDERSINFO, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<RightsHolder>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getTotalPlays() {
        final Function function = new Function(FUNC_GETTOTALPLAYS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> isrc() {
        final Function function = new Function(FUNC_ISRC, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> musicTitle() {
        final Function function = new Function(FUNC_MUSICTITLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> releasableRoyalties(String param0) {
        final Function function = new Function(FUNC_RELEASABLEROYALTIES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple2<String, BigInteger>> rightsHoldersInfo(BigInteger param0) {
        final Function function = new Function(FUNC_RIGHTSHOLDERSINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<String, BigInteger>>(function,
                new Callable<Tuple2<String, BigInteger>>() {
                    @Override
                    public Tuple2<String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> totalContractBalanceReceived() {
        final Function function = new Function(FUNC_TOTALCONTRACTBALANCERECEIVED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> totalPlaysReported() {
        final Function function = new Function(FUNC_TOTALPLAYSREPORTED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> updatePlayCount(BigInteger _newPlayCount) {
        final Function function = new Function(
                FUNC_UPDATEPLAYCOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_newPlayCount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> withdrawRoyalties() {
        final Function function = new Function(
                FUNC_WITHDRAWROYALTIES, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static SoundLedgerContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SoundLedgerContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SoundLedgerContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SoundLedgerContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SoundLedgerContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SoundLedgerContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SoundLedgerContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SoundLedgerContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<SoundLedgerContract> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _musicTitle, String _isrc, List<String> _wallets, List<BigInteger> _splits) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_musicTitle), 
                new org.web3j.abi.datatypes.Utf8String(_isrc), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_wallets, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(_splits, org.web3j.abi.datatypes.generated.Uint256.class))));
        return deployRemoteCall(SoundLedgerContract.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<SoundLedgerContract> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _musicTitle, String _isrc, List<String> _wallets, List<BigInteger> _splits) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_musicTitle), 
                new org.web3j.abi.datatypes.Utf8String(_isrc), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_wallets, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(_splits, org.web3j.abi.datatypes.generated.Uint256.class))));
        return deployRemoteCall(SoundLedgerContract.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SoundLedgerContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _musicTitle, String _isrc, List<String> _wallets, List<BigInteger> _splits) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_musicTitle), 
                new org.web3j.abi.datatypes.Utf8String(_isrc), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_wallets, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(_splits, org.web3j.abi.datatypes.generated.Uint256.class))));
        return deployRemoteCall(SoundLedgerContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<SoundLedgerContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _musicTitle, String _isrc, List<String> _wallets, List<BigInteger> _splits) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_musicTitle), 
                new org.web3j.abi.datatypes.Utf8String(_isrc), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_wallets, org.web3j.abi.datatypes.Address.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.datatypes.generated.Uint256.class,
                        org.web3j.abi.Utils.typeMap(_splits, org.web3j.abi.datatypes.generated.Uint256.class))));
        return deployRemoteCall(SoundLedgerContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class RightsHolder extends StaticStruct {
        public String wallet;

        public BigInteger split;

        public RightsHolder(String wallet, BigInteger split) {
            super(new org.web3j.abi.datatypes.Address(160, wallet), 
                    new org.web3j.abi.datatypes.generated.Uint256(split));
            this.wallet = wallet;
            this.split = split;
        }

        public RightsHolder(Address wallet, Uint256 split) {
            super(wallet, split);
            this.wallet = wallet.getValue();
            this.split = split.getValue();
        }
    }

    public static class DistributionCompleteEventResponse extends BaseEventResponse {
        public BigInteger totalDistributed;
    }

    public static class PlayCountUpdatedEventResponse extends BaseEventResponse {
        public BigInteger newTotalPlays;
    }

    public static class RoyaltiesDepositedEventResponse extends BaseEventResponse {
        public String from;

        public BigInteger amount;
    }

    public static class RoyaltiesWithdrawnEventResponse extends BaseEventResponse {
        public String to;

        public BigInteger amount;
    }
}
