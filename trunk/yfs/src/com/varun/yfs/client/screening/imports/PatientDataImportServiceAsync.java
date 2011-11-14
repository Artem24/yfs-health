package com.varun.yfs.client.screening.imports;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.varun.yfs.dto.ProgressDTO;

public interface PatientDataImportServiceAsync
{

	void startProcessing(ImportType type, String path, boolean readId, AsyncCallback<String> callback);

	void getProgress(AsyncCallback<ProgressDTO> callback);

	void getProcessedRecords(AsyncCallback<List<? extends BaseModelData>> callback);

	void getErrorRecords(AsyncCallback<List<String>> callback);

}
