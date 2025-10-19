pragma solidity ^0.8.20;

contract SoundLedgerContract {
    address public owner;
    string public musicTitle;
    string public isrc;
    uint256 public totalPlays;

    struct RightsHolder{
        address wallet;
        uint256 split;
    }

    RightsHolder[] public rightsHolderInfo;

    mapping(address => uint256) public releasableRoyalties;

    event PlayRegistered(uint256 newTotalPlays);
    event RoyaltiesDeposited(address from, uint256 amount);
    event RoyaltiesWithdrawn(address to, uint256 amount);

    modifier onlyOwner() {
        require(msg.sender == owner, "Restrita apenas ao prioprietario do contrato");
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

        require(_wallets.length == _splits.length, "As listas de carteiras e porcentagens devem ter o mesmo tamanho");

        uint256 totalSplit = 0;
        for (uint i = 0; i < _wallets.length; i++){
            require(_splits[i] > 0, "a porcentagem nao pode ser zero");

            rightsHolderInfo.push(RightsHolder({
                wallet: _wallets[i],
                split: _splits[i]
            }));

            totalSplit += _splits[i];
        }
        require(totalSplit == 100, "A soma das porcentagens deve ser igual a 100");
    }

    receive() external payable {
        require(msg.value > 0, "O valor depositado deve ser maior que zero");

        for (uint i = 0; i < rightsHolderInfo.length; i++){
            RightsHolder memory holder = rightsHolderInfo[i];

            uint256 amount = (msg.value * holder.split) / 100;

            releasableRoyalties[holder.wallet] += amount;
        }
        emit RoyaltiesDeposited(msg.sender, msg.value);
    }

    function registerPlay() public onlyOwner {
        totalPlays++;
        emit PlayRegistered(totalPlays);
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

}