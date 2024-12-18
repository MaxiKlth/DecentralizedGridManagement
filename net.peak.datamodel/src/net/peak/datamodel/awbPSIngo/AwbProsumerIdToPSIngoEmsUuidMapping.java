package net.peak.datamodel.awbPSIngo;

import java.util.HashMap;

public class AwbProsumerIdToPSIngoEmsUuidMapping {

	private HashMap<String, String> psingoEmsUuidToAwbProsumerId;
	private HashMap<String, String> awbProsumerIdToPsingoEmsUuid;
	
	
	public HashMap<String, String> getPsingoEmsUuidToProsumerId() {
		if (psingoEmsUuidToAwbProsumerId==null) {
			psingoEmsUuidToAwbProsumerId = new HashMap<>();
		}
		return psingoEmsUuidToAwbProsumerId;
	}
	public HashMap<String, String> getAwbProsumerIdToPsingoEmsUuid() {
		if (awbProsumerIdToPsingoEmsUuid==null) {
			awbProsumerIdToPsingoEmsUuid = new HashMap<>();
		}
		return awbProsumerIdToPsingoEmsUuid;
	}
	
	public void addMapping(String psiEmsUuid, String awbProsumerIdNew) {
		
		String prosumerId = this.getPsingoEmsUuidToProsumerId().get(psiEmsUuid);
		if (prosumerId==null) {
			this.getPsingoEmsUuidToProsumerId().put(psiEmsUuid, awbProsumerIdNew);
			this.getAwbProsumerIdToPsingoEmsUuid().put(awbProsumerIdNew, psiEmsUuid);
		}
	}
	
	public String getAwbProsumerIdFromPsiEmsUuid(String psiLineUuid) {
		return this.getPsingoEmsUuidToProsumerId().get(psiLineUuid);
	}
	
	public String getPsiEmsUuidFromAwbProsumerId(String prosumerId) {
		return this.getAwbProsumerIdToPsingoEmsUuid().get(prosumerId);
	}
	
}
