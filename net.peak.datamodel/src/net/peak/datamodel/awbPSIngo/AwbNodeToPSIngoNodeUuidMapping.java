package net.peak.datamodel.awbPSIngo;

import java.util.HashMap;

public class AwbNodeToPSIngoNodeUuidMapping {

	private HashMap<String, String> psingoNodeUuidToAwbNode;
	private HashMap<String, String> awbNodeToPsingoNodeUuid;
	
	
	public HashMap<String, String> getPsingoNodeUuidToAwbNode() {
		if (psingoNodeUuidToAwbNode==null) {
			psingoNodeUuidToAwbNode = new HashMap<>();
		}
		return psingoNodeUuidToAwbNode;
	}
	public HashMap<String, String> getAwbNodeToPsingoNodeUuid() {
		if (awbNodeToPsingoNodeUuid==null) {
			awbNodeToPsingoNodeUuid = new HashMap<>();
		}
		return awbNodeToPsingoNodeUuid;
	}
	
	public void addMapping(String psiNodeUuid, String awbNodeNew) {
		
		String awbNode = this.getPsingoNodeUuidToAwbNode().get(psiNodeUuid);
		if (awbNode==null) {
			this.getPsingoNodeUuidToAwbNode().put(psiNodeUuid, awbNodeNew);
			this.getAwbNodeToPsingoNodeUuid().put(awbNodeNew, psiNodeUuid);
		}
	}
	
	public String getAwbNodeFromPsiNodeUuid(String psiNodeUuid) {
		return this.getPsingoNodeUuidToAwbNode().get(psiNodeUuid);
	}
	
	public String getPsiNodeUuidFromAwbNode(String awbNode) {
		return this.getAwbNodeToPsingoNodeUuid().get(awbNode);
	}
	
}
