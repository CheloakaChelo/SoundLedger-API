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
    public static final String BINARY = "608060405234801561000f575f5ffd5b50604051610ea1380380610ea183398101604081905261002e916103bb565b5f80546001600160a01b03191633179055600161004b8582610565565b5060026100588482610565565b505f60035580518251146100d95760405162461bcd60e51b815260206004820152603f60248201527f4173206c697374617320646520636172746569726173206520706f7263656e7460448201527f6167656e7320646576656d20746572206f206d65736d6f2074616d616e686f0060648201526084015b60405180910390fd5b5f805b8351811015610210575f8382815181106100f8576100f861061f565b60200260200101511161014d5760405162461bcd60e51b815260206004820152601f60248201527f6120706f7263656e746167656d206e616f20706f646520736572207a65726f0060448201526064016100d0565b6004604051806040016040528086848151811061016c5761016c61061f565b60200260200101516001600160a01b031681526020018584815181106101945761019461061f565b6020908102919091018101519091528254600180820185555f94855293829020835160029092020180546001600160a01b0319166001600160a01b0390921691909117815591015191015582518390829081106101f3576101f361061f565b6020026020010151826102069190610633565b91506001016100dc565b50806064146102765760405162461bcd60e51b815260206004820152602c60248201527f4120736f6d612064617320706f7263656e746167656e7320646576652073657260448201526b020696775616c2061203130360a41b60648201526084016100d0565b5050505050610658565b634e487b7160e01b5f52604160045260245ffd5b604051601f8201601f191681016001600160401b03811182821017156102bc576102bc610280565b604052919050565b5f82601f8301126102d3575f5ffd5b81516001600160401b038111156102ec576102ec610280565b6102ff601f8201601f1916602001610294565b818152846020838601011115610313575f5ffd5b8160208501602083015e5f918101602001919091529392505050565b5f6001600160401b0382111561034757610347610280565b5060051b60200190565b5f82601f830112610360575f5ffd5b815161037361036e8261032f565b610294565b8082825260208201915060208360051b860101925085831115610394575f5ffd5b602085015b838110156103b1578051835260209283019201610399565b5095945050505050565b5f5f5f5f608085870312156103ce575f5ffd5b84516001600160401b038111156103e3575f5ffd5b6103ef878288016102c4565b602087015190955090506001600160401b0381111561040c575f5ffd5b610418878288016102c4565b604087015190945090506001600160401b03811115610435575f5ffd5b8501601f81018713610445575f5ffd5b805161045361036e8261032f565b8082825260208201915060208360051b850101925089831115610474575f5ffd5b6020840193505b828410156104aa5783516001600160a01b0381168114610499575f5ffd5b82526020938401939091019061047b565b6060890151909550925050506001600160401b038111156104c9575f5ffd5b6104d587828801610351565b91505092959194509250565b600181811c908216806104f557607f821691505b60208210810361051357634e487b7160e01b5f52602260045260245ffd5b50919050565b601f82111561056057805f5260205f20601f840160051c8101602085101561053e5750805b601f840160051c820191505b8181101561055d575f815560010161054a565b50505b505050565b81516001600160401b0381111561057e5761057e610280565b6105928161058c84546104e1565b84610519565b6020601f8211600181146105c4575f83156105ad5750848201515b5f19600385901b1c1916600184901b17845561055d565b5f84815260208120601f198516915b828110156105f357878501518255602094850194600190920191016105d3565b508482101561061057868401515f19600387901b60f8161c191681555b50505050600190811b01905550565b634e487b7160e01b5f52603260045260245ffd5b8082018082111561065257634e487b7160e01b5f52601160045260245ffd5b92915050565b61083c806106655f395ff3fe608060405260043610610092575f3560e01c80638dde3318116100575780638dde3318146102a6578063a23718d7146102e4578063c7359979146102f8578063e2dfb8b214610317578063fb841b461461032c575f5ffd5b806315db7116146101e757806320e66efe1461022557806321c34fcb14610239578063318a0e291461024f5780638da5cb5b14610270575f5ffd5b366101e3575f34116100fe5760405162461bcd60e51b815260206004820152602a60248201527f4f2076616c6f72206465706f73697461646f206465766520736572206d61696f6044820152697220717565207a65726f60b01b60648201526084015b60405180910390fd5b5f5b6004548110156101a8575f6004828154811061011e5761011e610687565b5f91825260208083206040805180820190915260029093020180546001600160a01b031683526001015490820181905290925060649061015e90346106af565b61016891906106cc565b82516001600160a01b03165f908152600560205260408120805492935083929091906101959084906106eb565b9091555050600190920191506101009050565b50604080513381523460208201527f904cd7c4dd50dd37b41bafc036d9eb1876b6cc3fa8a39a350b80fc6ab6a47a04910160405180910390a1005b5f5ffd5b3480156101f2575f5ffd5b506102126102013660046106fe565b60056020525f908152604090205481565b6040519081526020015b60405180910390f35b348015610230575f5ffd5b50600354610212565b348015610244575f5ffd5b5061024d61034d565b005b34801561025a575f5ffd5b5061026361049d565b60405161021c919061072b565b34801561027b575f5ffd5b505f5461028e906001600160a01b031681565b6040516001600160a01b03909116815260200161021c565b3480156102b1575f5ffd5b506102c56102c0366004610760565b610529565b604080516001600160a01b03909316835260208301919091520161021c565b3480156102ef575f5ffd5b5061026361055f565b348015610303575f5ffd5b5061024d610312366004610760565b61056c565b348015610322575f5ffd5b5061021260035481565b348015610337575f5ffd5b50610340610614565b60405161021c9190610777565b335f81815260056020526040902054806103a95760405162461bcd60e51b815260206004820152601d60248201527f566f6365206e616f2074656d2073616c646f207061726120736163617200000060448201526064016100f5565b6001600160a01b0382165f818152600560205260408082208290555190919083908381818185875af1925050503d805f8114610400576040519150601f19603f3d011682016040523d82523d5f602084013e610405565b606091505b50509050806104565760405162461bcd60e51b815260206004820152601960248201527f46616c686120616f20656e76696172206f732066756e646f730000000000000060448201526064016100f5565b604080516001600160a01b0385168152602081018490527f312b70e5726648ad7ab64374bb491934c2732cfb4f8c285ea2c1bbe4fa07f3db910160405180910390a1505050565b600180546104aa906107ce565b80601f01602080910402602001604051908101604052809291908181526020018280546104d6906107ce565b80156105215780601f106104f857610100808354040283529160200191610521565b820191905f5260205f20905b81548152906001019060200180831161050457829003601f168201915b505050505081565b60048181548110610538575f80fd5b5f918252602090912060029091020180546001909101546001600160a01b03909116915082565b600280546104aa906107ce565b5f546001600160a01b031633146105d95760405162461bcd60e51b815260206004820152602b60248201527f5265737472697461206170656e617320616f2070726f70726965746172696f2060448201526a646f20636f6e747261746f60a81b60648201526084016100f5565b60038190556040518181527f89fbeade86ef86773f0077acd0783f24f331b82366f9107e2a30db80381bf1e79060200160405180910390a150565b60606004805480602002602001604051908101604052809291908181526020015f905b8282101561067e575f848152602090819020604080518082019091526002850290910180546001600160a01b03168252600190810154828401529083529092019101610637565b50505050905090565b634e487b7160e01b5f52603260045260245ffd5b634e487b7160e01b5f52601160045260245ffd5b80820281158282048414176106c6576106c661069b565b92915050565b5f826106e657634e487b7160e01b5f52601260045260245ffd5b500490565b808201808211156106c6576106c661069b565b5f6020828403121561070e575f5ffd5b81356001600160a01b0381168114610724575f5ffd5b9392505050565b602081525f82518060208401528060208501604085015e5f604082850101526040601f19601f83011684010191505092915050565b5f60208284031215610770575f5ffd5b5035919050565b602080825282518282018190525f918401906040840190835b818110156107c357835180516001600160a01b031684526020908101518185015290930192604090920191600101610790565b509095945050505050565b600181811c908216806107e257607f821691505b60208210810361080057634e487b7160e01b5f52602260045260245ffd5b5091905056fea264697066735822122057bac04308b63338b87e59a21a9561876db805d5ab783a7644ebe5a75d39a82164736f6c634300081d0033";

    public static final String FUNC_GETRIGHTSHOLDERSINFO = "getRightsHoldersInfo";

    public static final String FUNC_GETTOTALPLAYS = "getTotalPlays";

    public static final String FUNC_ISRC = "isrc";

    public static final String FUNC_MUSICTITLE = "musicTitle";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RELEASABLEROYALTIES = "releasableRoyalties";

    public static final String FUNC_RIGHTSHOLDERSINFO = "rightsHoldersInfo";

    public static final String FUNC_TOTALPLAYSREPORTED = "totalPlaysReported";

    public static final String FUNC_UPDATEPLAYCOUNT = "updatePlayCount";

    public static final String FUNC_WITHDRAWROYALTIES = "withdrawRoyalties";

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
