package com.hubis.acs.common.adapter.plc.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class PLCDataHandler {

    @Value("${plc.data.format}")
    private String dataFormat;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * PLC 데이터를 변환하여 처리
     */
    public Optional<String> parseData(String rawData) {
        return switch (dataFormat.toLowerCase()) {
            case "xml" -> parseXML(rawData);
            case "json" -> parseJSON(rawData);
            case "modbus" -> parseModbus(rawData);
            default -> Optional.empty();
        };
    }

    private Optional<String> parseXML(String xmlData) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new org.xml.sax.InputSource(new StringReader(xmlData)));
            String status = document.getElementsByTagName("status").item(0).getTextContent();
            return Optional.of(status);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<String> parseJSON(String jsonData) {
        try {
            Map<String, Object> data = objectMapper.readValue(jsonData, HashMap.class);
            String status = (String) data.get("status");
            return Optional.of(status);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private Optional<String> parseModbus(String modbusData) {
        if ("0x01".equalsIgnoreCase(modbusData)) {
            return Optional.of("ON");
        } else if ("0x00".equalsIgnoreCase(modbusData)) {
            return Optional.of("OFF");
        } else {
            return Optional.empty();
        }
    }

    /**
     * 응답 데이터를 원래 형식(XML, JSON, Modbus)으로 변환
     */
    public String formatResponse(String status) {
        switch (dataFormat.toLowerCase()) {
            case "xml":
                return "<plc><status>" + status + "</status></plc>";
            case "json":
                Map<String, String> jsonMap = new HashMap<>();
                jsonMap.put("status", status);
                try {
                    return objectMapper.writeValueAsString(jsonMap);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "{}";
                }
            case "modbus":
                return "ON".equalsIgnoreCase(status) ? "0x01" : "0x00";
            default:
                return status;
        }
    }
}
