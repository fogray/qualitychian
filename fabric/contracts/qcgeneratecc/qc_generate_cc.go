package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"bytes"
	"encoding/json"
)

type QcGenerateChaincode struct{
}

func (t *QcGenerateChaincode) Init(stub shim.ChaincodeStubInterface) peer.Response{
	return shim.Success(nil)
}

func (t *QcGenerateChaincode) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	fn, args := stub.GetFunctionAndParameters()
	if fn != "invoke" {
		return shim.Error(fmt.Sprintf("Unknown function call--fn-->%s--args[0]-->%s",fn,args[0]))
	}
	var result string
	var err error
	if args[0] == "setCodes" {
		result, err = setCodes(stub, args)
	}else if args[0] == "getCodes" {
		result, err = getCodes(stub, args)
	}else if args[0] == "queryKeys" {
		return queryKeys(stub, args)
	}else if args[0] == "deleteCodes" {
		result, err = deleteCodes(stub, args)
	}else if args[0] =="getHistory" {
		return getHistory(stub, args)
	}
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte(result))
}

func setCodes(stub shim.ChaincodeStubInterface, args []string)(string, error){
	//creatorByte,_:= stub.GetCreator()
	//certStart := bytes.IndexAny(creatorByte, "-----BEGIN")
	//if certStart == -1 {
	//	fmt.Errorf("No certificate found")
	//}
	//certText := creatorByte[certStart:]
	//bl, _ := pem.Decode(certText)
	//if bl == nil {
	//	fmt.Errorf("Could not decode the PEM structure")
	//}
	//
	//cert, err := x509.ParseCertificate(bl.Bytes)
	//if err != nil {
	//	fmt.Errorf("ParseCertificate failed")
	//}
	//uname:=cert.Subject.CommonName
	//if !strings.Contains(uname, "org1.chains.cloudchain.cn") {
	//	return fmt.Sprint("failed! 提案发起用户错误--uname-->%s",uname), fmt.Errorf("failed! 提案发起用户错误 error")
	//}
	err := stub.PutState(args[1], []byte(args[2]))
	if err != nil {
		return "failed", fmt.Errorf("setCodeInfo failed key-->%s--value-->%s",args[1],args[2])
	}
	return "success", nil
}

func getCodes(stub shim.ChaincodeStubInterface, args []string) (string,error){
	value,err := stub.GetState(args[1])
	if err != nil {
		return "failed", fmt.Errorf("getCodes failed args[1]-->%s",args[1])
	}
	if value == nil {
		return "failed", fmt.Errorf("value is nil")
	}
	return string(value), nil
}

func queryKeys(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	query := args[1]
	keysIter, err := stub.GetQueryResult(query)
	if err != nil {
		return shim.Error(fmt.Sprintf("query operation failed. Error accessing state: %s", err))
	}
	defer keysIter.Close()
	var keys []string
	for keysIter.HasNext() {
		response, iterErr := keysIter.Next()
		if iterErr != nil {
			return shim.Error(fmt.Sprintf("query operation failed. Error accessing state: %s", err))
		}
		keys = append(keys, response.Key)
	}

	jsonKeys, err := json.Marshal(keys)
	if err != nil {
		return shim.Error(fmt.Sprintf("query operation failed. Error marshaling JSON: %s", err))
	}
	return shim.Success(jsonKeys)
}

func deleteCodes(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	err := stub.DelState(args[1])
	if err != nil {
		return "failed", fmt.Errorf("deleteCodes failed args[1]-->%s",args[1])
	}
	return "success", nil
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

func main() {
	if err := shim.Start(new(QcGenerateChaincode)); err != nil {
		fmt.Printf("Error starting QualityCodeChaincode: %s", err)
	}
}