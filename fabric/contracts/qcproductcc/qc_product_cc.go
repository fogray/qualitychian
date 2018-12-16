package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"encoding/json"
	"bytes"
)

type ProductChaincode struct {
}

type ProductInfo struct {
	OrgCode string `json:"ORGANIZE_CODE"`
	ProductCode string `json:"PRODUCT_CODE"`
}

func (t *ProductChaincode)Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}

func (t *ProductChaincode)Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	fn, args := stub.GetFunctionAndParameters()
	if fn != "invoke" {
		return shim.Error(fmt.Sprintf("Unknown function call--fn-->%s--args[0]-->%s",fn,args[0]))
	}
	var result string
	var err error
	if args[0] == "setProductInfo" {
		result, err = setProductInfo(stub, args)
	}else if args[0] == "getProductInfo" {
		result, err = getProductInfo(stub, args)
	}else if args[0] == "getHistory" {
		return getHistory(stub, args)
	}else if args[0] == "deleteProductInfo" {
		result, err = deleteProductInfo(stub, args)
	}
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte(result))
}

func setProductInfo(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	pi := &ProductInfo{}
	err := json.Unmarshal([]byte(args[2]), pi)
	if err != nil {
		return "failed args error", fmt.Errorf("setBatchInfo failed key-->%s--value-->%s",args[1],args[2])
	}
//	orgCode := pi.OrgCode

//	param1 := toChaincodeArgs("invoke","getOrgInfo",orgCode)
//	response1 := stub.InvokeChaincode("qc_org_cc",param1,args[3])
//	if response1.Status != shim.OK {
//		return fmt.Sprintf("failed! orgCode %s 未备案",orgCode), fmt.Errorf(response1.Message)
//	}
//	orgInfo := string(response1.Payload)
//	if orgInfo == "" || orgInfo == "failed"{
//		return fmt.Sprint("failed! 备案信息错误--orgInfo-->%s",orgInfo), fmt.Errorf("failed! 备案信息错误 error")
//	}

	err = stub.PutState(args[1], []byte(args[2]))
	if err != nil {
		return "failed", fmt.Errorf("setProductInfo failed key-->%s--value-->%s",args[1],args[2])
	}
	return "success", nil
}

func getProductInfo(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	value,err := stub.GetState(args[1])
	if err != nil {
		return "failed", fmt.Errorf("getProductInfo failed args[1]-->%s",args[1])
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

func deleteProductInfo(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	err := stub.DelState(args[1])
	if err != nil {
		return "failed", fmt.Errorf("deleteProductInfo failed args[1]-->%s",args[1])
	}
	return "success", nil
}

func toChaincodeArgs(args ...string) [][]byte {
	bargs := make([][]byte, len(args))
	for i, arg := range args {
		bargs[i] = []byte(arg)
	}
	return bargs
}


func main() {
	if err := shim.Start(new(ProductChaincode)); err != nil {
		fmt.Printf("Error starting ProductChaincode: %s", err)
	}
}
