// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract SoundLedgerContract {
    address public owner;
    string public musicTitle;
    string public isrc;
    uint256 public totalPlaysReported;

    struct RightsHolder {
        address wallet;
        uint256 split;
    }

    RightsHolder[] public rightsHoldersInfo;

    mapping(address => uint256) public releasableRoyalties;

    event RoyaltiesDeposited(address from, uint256 amount);
    event RoyaltiesWithdrawn(address to, uint256 amount);
    event PlayCountUpdated(uint256 newTotalPlays);

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

        require(_wallets.length == _splits.length, "As listas de carteiras e porcentagens devem ter o mesmo tamanho");

        uint256 totalSplit = 0;
        for (uint i = 0; i < _wallets.length; i++) {
            require(_splits[i] > 0, "a porcentagem nao pode ser zero");

            rightsHoldersInfo.push(RightsHolder({
                wallet: _wallets[i],
                split: _splits[i]
            }));

            totalSplit += _splits[i];
        }
        require(totalSplit == 100, "A soma das porcentagens deve ser igual a 100");
    }

    receive() external payable {
        require(msg.value > 0, "O valor depositado deve ser maior que zero");

        for (uint i = 0; i < rightsHoldersInfo.length; i++) {
            RightsHolder memory holder = rightsHoldersInfo[i];

            uint256 amount = (msg.value * holder.split) / 100;

            releasableRoyalties[holder.wallet] += amount;
        }
        emit RoyaltiesDeposited(msg.sender, msg.value);
    }


    function updatePlayCount(uint256 _newPlayCount) public onlyOwner {
        // Validação opcional para garantir que a contagem não diminua
        // require(_newPlayCount >= totalPlaysReported, "A nova contagem nao pode ser menor que a atual");

        totalPlaysReported = _newPlayCount;
        emit PlayCountUpdated(_newPlayCount);
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

    function getTotalPlays() public view returns (uint256) {
        return totalPlaysReported;
    }

    function getRightsHoldersInfo() public view returns (RightsHolder[] memory) {
        return rightsHoldersInfo;
    }
}