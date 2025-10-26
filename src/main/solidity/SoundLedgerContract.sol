// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract SoundLedgerContract {
    address public owner;
    string public musicTitle;
    string public isrc;
    uint256 public totalPlaysReported;
    uint256 public totalContractBalanceReceived;

    struct RightsHolder {
        address wallet;
        uint256 split;
    }

    RightsHolder[] public rightsHoldersInfo;
    mapping(address => uint256) public releasableRoyalties;

    event RoyaltiesDeposited(address from, uint256 amount);
    event RoyaltiesWithdrawn(address to, uint256 amount);
    event PlayCountUpdated(uint256 newTotalPlays);
    event DistributionComplete(uint256 totalDistributed);

    modifier onlyOwner() {
        require(msg.sender == owner, "Restrita apenas ao proprietario do contrato");
        _;
    }

    constructor(
        string memory _musicTitle,
        string memory _isrc,
        address[] memory _wallets,
        uint256[] memory _splits
    ) {
        owner = msg.sender;
        musicTitle = _musicTitle;
        isrc = _isrc;
        totalPlaysReported = 0;
        require(_wallets.length == _splits.length, "Wallets/Splits mismatch");
        uint256 totalSplit = 0;
        for (uint i = 0; i < _wallets.length; i++) {
            require(_splits[i] > 0, "Split cannot be zero");
            rightsHoldersInfo.push(RightsHolder({ wallet: _wallets[i], split: _splits[i] }));
            totalSplit += _splits[i];
        }
        require(totalSplit == 100, "Splits must sum 100");
    }

    receive() external payable {
        require(msg.value > 0, "Deposit value must be positive");
        totalContractBalanceReceived += msg.value;
        emit RoyaltiesDeposited(msg.sender, msg.value);
    }

    function updatePlayCount(uint256 _newPlayCount) public onlyOwner {
        totalPlaysReported = _newPlayCount;
        emit PlayCountUpdated(_newPlayCount);
    }

    function distributeAccumulatedBalance() public onlyOwner {
        uint256 balanceToDistribute = totalContractBalanceReceived;
        require(balanceToDistribute > 0, "No balance to distribute");
        totalContractBalanceReceived = 0;
        for (uint i = 0; i < rightsHoldersInfo.length; i++) {
            RightsHolder memory holder = rightsHoldersInfo[i];
            uint256 amount = (balanceToDistribute * holder.split) / 100;
            if (amount > 0) {
                releasableRoyalties[holder.wallet] += amount;
            }
        }
        emit DistributionComplete(balanceToDistribute);
    }

    function withdrawRoyalties() public {
        address payable beneficiary = payable(msg.sender);
        uint256 amount = releasableRoyalties[beneficiary];

        require(amount > 0, "Voce nao tem saldo para sacar");

        releasableRoyalties[beneficiary] = 0;

        (bool sent, ) = beneficiary.call{value: amount}("");
        require(sent, "Falha ao enviar os fundos");

        emit RoyaltiesWithdrawn(beneficiary, amount);
    }

    function getTotalPlays() public view returns (uint256) { return totalPlaysReported; }
    function getRightsHoldersInfo() public view returns (RightsHolder[] memory) { return rightsHoldersInfo; }
}