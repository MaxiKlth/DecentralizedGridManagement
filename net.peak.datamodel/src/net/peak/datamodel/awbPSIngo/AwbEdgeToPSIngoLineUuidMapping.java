package net.peak.datamodel.awbPSIngo;

import java.util.HashMap;

public class AwbEdgeToPSIngoLineUuidMapping {

	private HashMap<String, String> psingoLineUuidToAwbEdge;
	private HashMap<String, String> awbEdgeToPsingoLineUuid;
	
	
	public HashMap<String, String> getPsingoLineUuidToAwbEdge() {
		if (psingoLineUuidToAwbEdge==null) {
			psingoLineUuidToAwbEdge = new HashMap<>();
		}
		return psingoLineUuidToAwbEdge;
	}
	public HashMap<String, String> getAwbEdgeToPsingoLineUuid() {
		if (awbEdgeToPsingoLineUuid==null) {
			awbEdgeToPsingoLineUuid = new HashMap<>();
		}
		return awbEdgeToPsingoLineUuid;
	}
	
	public void addMapping(String psiLineUuid, String awbEdgeNew) {
		
		String awbEdge = this.getPsingoLineUuidToAwbEdge().get(psiLineUuid);
		if (awbEdge==null) {
			this.getPsingoLineUuidToAwbEdge().put(psiLineUuid, awbEdgeNew);
			this.getAwbEdgeToPsingoLineUuid().put(awbEdgeNew, psiLineUuid);
		}
	}
	
	public String getAwbEdgeFromPsiLineUuid(String psiLineUuid) {
		return this.getPsingoLineUuidToAwbEdge().get(psiLineUuid);
	}
	
	public String getPsiLineUuidFromAwbEdge(String awbEdge) {
		return this.getAwbEdgeToPsingoLineUuid().get(awbEdge);
	}
	
}
