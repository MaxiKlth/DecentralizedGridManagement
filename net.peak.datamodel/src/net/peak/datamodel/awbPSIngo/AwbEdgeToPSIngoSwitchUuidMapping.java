package net.peak.datamodel.awbPSIngo;

import java.util.HashMap;

public class AwbEdgeToPSIngoSwitchUuidMapping {

	private HashMap<String, String> psingoSwitchUuidToAwbEdge;
	private HashMap<String, String> awbEdgeToPsingoSwitchUuid;
	
	
	public HashMap<String, String> getPsingoSwitchUuidToAwbEdge() {
		if (psingoSwitchUuidToAwbEdge==null) {
			psingoSwitchUuidToAwbEdge = new HashMap<>();
		}
		return psingoSwitchUuidToAwbEdge;
	}
	public HashMap<String, String> getAwbEdgeToPsingoSwitchUuid() {
		if (awbEdgeToPsingoSwitchUuid==null) {
			awbEdgeToPsingoSwitchUuid = new HashMap<>();
		}
		return awbEdgeToPsingoSwitchUuid;
	}
	
	public void addMapping(String psiSwitchUuid, String awbEdgeNew) {
		
		String awbEdge = this.getPsingoSwitchUuidToAwbEdge().get(psiSwitchUuid);
		if (awbEdge==null) {
			this.getPsingoSwitchUuidToAwbEdge().put(psiSwitchUuid, awbEdgeNew);
			this.getAwbEdgeToPsingoSwitchUuid().put(awbEdgeNew, psiSwitchUuid);
		}
	}
	
	public String getAwbEdgeFromPsiNodeUuid(String psiSwitchUuid) {
		return this.getPsingoSwitchUuidToAwbEdge().get(psiSwitchUuid);
	}
	
	public String getPsiSwitchUuidFromAwbEdge(String awbEdge) {
		return this.getAwbEdgeToPsingoSwitchUuid().get(awbEdge);
	}
	
}
