package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"encoding/json"
	"bytes"
	"encoding/pem"
	"crypto/x509"
)

type CirculationChaincode struct {
}

func (t *CirculationChaincode)Init(stub shim.ChaincodeStubInterface) peer.Response{
	return shim.Success(nil)
}

func (t *CirculationChaincode)Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	fn, args := stub.GetFunctionAndParameters()
	if fn != "invoke" {
		return shim.Error(fmt.Sprintf("Unknown function call--fn-->%s--args[0]-->%s",fn,args[0]))
	}
	var result string
	var err error
	if args[0] == "setTraceInfo" {
		result, err = setTraceInfo(stub, args)
	}else if args[0] == "getTraceInfo" {
		result, err = getTraceInfo(stub, args)
	}else if args[0] == "queryTraceKeys" {
		result, err = queryTraceKeys(stub, args)
	}else if args[0] == "deleteTraceInfo" {
		result, err = deleteTraceInfo(stub, args)
	}else if args[0] == "getHistory" {
		return getHistory(stub, args)
	}
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success([]byte(result))
}

type BatchInfo1 struct{
	Start string `json:"START"`
	End string `json:"END"`
	OrgCode string `json:"ORGANIZE_CODE"`
	ProductCode string `json:"PRODUCT_CODE"`
}

type BatchInfo2 struct{
	OrgCode string `json:"ORGANIZE_CODE"`
	ProductCode string `json:"PRODUCT_CODE"`
}

type CodeInfo1 struct {
	Start string `json:"START"`
	End string `json:"END"`
	SecretKey string `json:"SECRETKEY"`
}

func setTraceInfo(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	//获取交易提交者的身份（证书中的用户名）
	creatorByte,_:= stub.GetCreator()
	certStart := bytes.IndexAny(creatorByte, "-----BEGIN")
	if certStart == -1 {
		return "failed", fmt.Errorf("No certificate found")
	}
	certText := creatorByte[certStart:]
	bl, _ := pem.Decode(certText)
	if bl == nil {
		return "failed", fmt.Errorf("Could not decode the PEM structure")
	}
	_, err := x509.ParseCertificate(bl.Bytes)
	if err != nil {
		return "failed", fmt.Errorf("ParseCertificate failed")
	}
	
	traceType, res := args[4], "failed"
	if traceType == "1" {
		res, err = setTraceInfo1(stub, args[1], args[2], args[3])
	} else if traceType == "2" {
		res, err = setTraceInfo2(args[1], args[2])
	} else if traceType == "3" {
		res, err = setTraceInfo2(args[1], args[2])
	} else if traceType == "4" {
		res, err = setTraceInfo2(args[1], args[2])
	} else {
		return "failed", fmt.Errorf("trace code type is invalid. valid are 1,2,3,4.")
	}
	
	err = stub.PutState(args[1], []byte(res))
	if err != nil {
		return "failed!!", fmt.Errorf("setTraceInfo failed key-->%s--value-->%s",args[1],args[2])
	}
	return "success", nil
}

func setTraceInfo1( stub shim.ChaincodeStubInterface, key string, value string, channel string ) (string, error) {
	//从json参数中获取START END ORGANIZE_CODE PRODUCT_CODE
	bi := &BatchInfo1{}
	err := json.Unmarshal([]byte(value), bi)
	if err != nil {
		return "failed", fmt.Errorf("setBatchInfo failed key-->%s--value-->%s", key, value)
	}
	end := bi.End

	//判断START END在qc_cc下发码段范围内
	param1 := toChaincodeArgs("invoke","queryKeys",fmt.Sprintf("{\"selector\":{\"$and\": [{\"END\": {\"$gte\": \"%s\"}}]}}",end))
	response1 := stub.InvokeChaincode("qc_generate_cc",param1, channel)
	if response1.Status != shim.OK {
		return "failed", fmt.Errorf("failed! 码段不存在")
	}
	keys := string(response1.Payload)
	if keys=="null" {
		return "failed", fmt.Errorf("failed! 码段不存在")
	}
	
	return value, nil
}

func setTraceInfo2( key string, value string) (string, error) {
	
	//从json参数中获取ORGANIZE_CODE PRODUCT_CODE
	bi := &BatchInfo2{}
	err := json.Unmarshal([]byte(value), bi)
	if err != nil {
		return "failed", fmt.Errorf("setBatchInfo failed key-->%s--value-->%s", key, value)
	}
	
	return value, nil
}

func getTraceInfo(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	value,err := stub.GetState(args[1])
	if err != nil {
		return "failed", fmt.Errorf("getBatchInfo failed args[1]-->%s",args[1])
	}
	if value == nil {
		return "failed", fmt.Errorf("value is nil")
	}
	return string(value), nil
}

func queryTraceKeys(stub shim.ChaincodeStubInterface, args []string) (string, error){
	query := args[1]
	keysIter, err := stub.GetQueryResult(query)
	if err != nil {
		return "failed",err
	}
	defer keysIter.Close()
	var keys []string
	for keysIter.HasNext() {
		response, iterErr := keysIter.Next()
		if iterErr != nil {
			return "failed",err
		}
		keys = append(keys, response.Key)
	}

	jsonKeys, err := json.Marshal(keys)
	if err != nil {
		return "failed",err
	}
	if jsonKeys == nil {
		return "failed",err
	}
	return string(jsonKeys),nil
}

func deleteTraceInfo(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	err := stub.DelState(args[1])
	if err != nil {
		return "failed", fmt.Errorf("deleteTraceInfo failed args[1]-->%s",args[1])
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

func toChaincodeArgs(args ...string) [][]byte {
	bargs := make([][]byte, len(args))
	for i, arg := range args {
		bargs[i] = []byte(arg)
	}
	return bargs
}

func main() {
	if err := shim.Start(new(CirculationChaincode)); err != nil {
		fmt.Printf("Error starting CirculationChaincode: %s", err)
	}
}