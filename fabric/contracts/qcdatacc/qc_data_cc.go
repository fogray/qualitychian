package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
	"encoding/json"
	"bytes"
)

type DataChaincode struct {
}

func (t *DataChaincode)Init(stub shim.ChaincodeStubInterface) peer.Response{
	return shim.Success(nil)
}

func (t *DataChaincode)Invoke(stub shim.ChaincodeStubInterface) peer.Response {
    fn, args := stub.GetFunctionAndParameters()
    if fn != "invoke" {
        return shim.Error(fmt.Sprintf("DataChaincode Unknown function call--fn-->%s--args[0]-->%s",fn,args[0]))
    }
    var result string
    var err error
    if args[0] == "setData" {
        result, err = setData(stub, args)
    }else if args[0] == "getData" {
        result, err = getData(stub, args)
    }else if args[0] == "queryData" {
        result, err = queryData(stub, args)
    }else if args[0] == "deleteData" {
        result, err = deleteData(stub, args)
    }else if args[0] == "getHistory" {
        return getHistory(stub, args)
    }
    if err != nil {
        return shim.Error(err.Error())
    }
    return shim.Success([]byte(result))
}

func setData(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	err := stub.PutState(args[1], []byte(args[2]))
	if err != nil {
		return fmt.Sprintf("{\"success\": \"false\",\"msg\":\"DataChaincode setData failed key:%s--value:%s\"}", args[1], args[2]) , nil
	}
	return "{\"success\": \"true\", \"msg\":\"上链成功\"}", nil
}

func getData(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	value,err := stub.GetState(args[1])
	if err != nil {
		return fmt.Sprintf("{\"success\":\"false\",\"msg\":\"DataChaincode getData failed key:%s\"}", args[1]), nil
	}
	if value == nil {
		return "{\"success\": \"false\", \"msg\":\"获取失败，该记录不存在\"}", nil
	}
	return fmt.Sprintf("{\"success\": \"true\", \"msg\":\"获取成功\", \"data\": %s}", string(value)), nil
}

func queryData(stub shim.ChaincodeStubInterface, args []string) (string, error){
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

func deleteData(stub shim.ChaincodeStubInterface, args []string)(string, error) {
	err := stub.DelState(args[1])
	if err != nil {
		return fmt.Sprintf("{\"success\":\"false\",\"msg\":\"DataChaincode deleteData failed key:%s\"}", args[1]), nil
	}
	return "{\"success\": \"true\", \"msg\":\"删除成功\"}", nil
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
	buffer.WriteString("{\"success\":\"true\", \"msg\":\"获取成功\",\"data\":{\"history_list\":[")

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
		item,_ := json.Marshal(queryResponse)
		buffer.Write(item)
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]}}")
	return buffer.Bytes(), nil
}

func main() {
	if err := shim.Start(new(DataChaincode)); err != nil {
		fmt.Printf("Error starting DataChaincode: %s", err)
	}
}