package net.peak.datamodel.sglAwb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Class SglToAwbNodeIdMapping.
 */
public class SglToAwbNodeIdMapping {

	private HashMap<String, List<String>> awbNodeIdToSgl;
	private HashMap<String, String> sglToAwbNodeId;
	
	
	public HashMap<String, List<String>> getAwbNodeIdToSgl() {
		if (this.awbNodeIdToSgl==null) {
			this.awbNodeIdToSgl = new HashMap<>();
		}
		return this.awbNodeIdToSgl;
	}
	public HashMap<String, String> getSglToAwbNodeId() {
		if (this.sglToAwbNodeId==null) {
			this.sglToAwbNodeId = new HashMap<>();
		}
		return this.sglToAwbNodeId;
	}
	
	public void addMapping(String awbNodeId, String sglID) {
		
		// --- check awb mapping
		List<String> sglList = this.getAwbNodeIdToSgl().get(awbNodeId);
		if (sglList==null) {
			sglList = new ArrayList<>();
			sglList.add(sglID);
			this.getAwbNodeIdToSgl().put(awbNodeId, sglList);
		} else {
			if (sglList.contains(sglID)==false) {
				sglList.add(sglID);
			}
		}
		
		// --- Check SGL --------
		this.getSglToAwbNodeId().put(sglID, awbNodeId);
	}
	
	
	public List<String> getSglIdsFromAwbId(String awbId) {
		return this.getAwbNodeIdToSgl().get(awbId);
	}
	
	public String getAwbIdFromSglId(String sglID) {	
		return this.getAwbIdFromSglId(sglID);
	}
	
}
