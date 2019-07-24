package com.ivay.ivay_app.service;

import java.io.IOException;

import com.ivay.ivay_app.dto.VTPTranInfo;
import com.ivay.ivay_app.dto.VTPTranOutputData;
import com.ivay.ivay_app.dto.VTPTranProcessInput;
import com.ivay.ivay_app.dto.VTPTranProcessOutput;
import com.ivay.ivay_app.dto.VTPUpdateTranStatusResponse;

public interface VTPRepayApiService {

    VTPTranInfo getTransInfo(String id, String xToken, String xSigned);

    VTPTranProcessOutput transactionProcess(VTPTranProcessInput input, String xToken, String xSigned);

    VTPTranProcessOutput trackingStatus(String tid, String xToken, String xSigned);

    VTPUpdateTranStatusResponse updateTransactionStatus(VTPTranOutputData data) throws IOException;
}
