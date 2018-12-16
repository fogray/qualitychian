package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"bytes"
	"encoding/json"
)

type OrgChaincode struct {
}

func (t *OrgChaincode)Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}

func (t *OrgChaincode)Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	fn, args := stub.GetFunctionAndParameters()
	if fn != "invoke" {
		return shim.Error(fmt.Sprintf("Unknown function call--fn-->%s--args[0]-->%s",fn,args[0]))
	}
	var result string
	var err error
	if args[0] == "setOrgInfo" {
		result, err = setOrgInfo(stub, args)
	}else if args[0] == "getOrgInfo" {
		result, err = getOrgInfo(stub, args)
	}else if args[0] == "getHistory" {
		return getHistory(stub, args)
	}else if args[0] == "deleteOrgInfo" {
		result, err = deleteOrgInfo(stub, args)
	}
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte(result))
}

func setOrgInfo(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	err := stub.PutState(args[1], []byte(args[2]))
	if err != nil {
		return "failed", fmt.Errorf("setOrgInfo failed key-->%s--value-->%s",args[1],args[2])
	}
	return "success", nil
}

func getOrgInfo(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	value,err := stub.GetState(args[1])
	if err != nil {
		return "failed", fmt.Errorf("getOrgInfo failed args[1]-->%s",args[1])
	}
	if value == nil {
		return "failed", fmt.Errorf("value is nil")
	}
	return string(value), nil
}

func getHistory(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	it,err:= stub.GetHistoryForKey(args[1])
	if err!=nil{
		return shim.Error(err.Error())
	}
	var result,_= getHistoryListResult(it)
	return shim.Success(result)
}

func getHistoryListResult(resultsIterator shim.HistoryQueryIteratorInterface) ([]byte,error){
	defer resultsIterator.Close()
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		item,_:= json.Marshal( queryResponse)
		buffer.Write(item)
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")
	return buffer.Bytes(), nil
}

func deleteOrgInfo(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	err := stub.DelState(args[1])
	if err != nil {
		return "failed", fmt.Errorf("deleteOrgInfo failed args[1]-->%s",args[1])
	}
	return "success", nil
}


func main() {
	if err := shim.Start(new(OrgChaincode)); err != nil {
		fmt.Printf("Error starting ProductChaincode: %s", err)
	}
}
