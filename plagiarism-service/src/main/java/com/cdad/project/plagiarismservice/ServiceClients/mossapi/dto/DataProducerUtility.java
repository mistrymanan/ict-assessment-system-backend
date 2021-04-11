package com.cdad.project.plagiarismservice.ServiceClients.mossapi.dto;

public class DataProducerUtility {
    static public String getName(String str){
        String [] strData=str.split("/");
        return strData[strData.length-1];
    }
    static public GetProcessedData processData(GetProcessedData data){
        data.getLinks().stream().forEach(link -> {
            link.setFirst(getName(link.getFirst()));
            link.setSecond(getName(link.getSecond()));
        });
        data.getNodes().stream().forEach(node -> {
            node.setName(getName(node.getName()));
        });
     return data;
    }
}
