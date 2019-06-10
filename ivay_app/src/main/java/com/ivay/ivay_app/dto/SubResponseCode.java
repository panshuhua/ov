package com.ivay.ivay_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SubResponseCode {
   @JsonProperty("ErrorCode")
   private String errorCode;
   @JsonProperty("Message")
   private String message;
}
