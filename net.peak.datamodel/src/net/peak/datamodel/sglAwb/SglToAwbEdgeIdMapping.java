package net.peak.datamodel.sglAwb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Class SglToAwbEdgeIdMapping.
 */
public class SglToAwbEdgeIdMapping {

	private HashMap<String, List<String>> awbEdgeIdToSgl;
	private HashMap<String, String> sglToAwbEdgeId;
	
	
	public HashMap<String, List<String>> getAwbEdgeIdToSgl() {
		if (this.awbEdgeIdToSgl==null) {
			this.awbEdgeIdToSgl = new HashMap<>();
		}
		return this.awbEdgeIdToSgl;
	}
	public HashMap<String, String> getSglToAwbEdgeId() {
		if (this.sglToAwbEdgeId==null) {
			this.sglToAwbEdgeId = new HashMap<>();
		}
		return this.sglToAwbEdgeId;
	}
	
	public void addMapping(String awbEdgeId, String sglID) {
		
		// --- check awb mapping
		List<String> sglList = this.getAwbEdgeIdToSgl().get(awbEdgeId);
		if (sglList==null) {
			sglList = new ArrayList<>();
			sglList.add(sglID);
			this.getAwbEdgeIdToSgl().put(awbEdgeId, sglList);
		} else {
			if (sglList.contains(sglID)==false) {
				sglList.add(sglID);
			}
		}
		
		// --- Check SGL --------
		this.getSglToAwbEdgeId().put(sglID, awbEdgeId);
	}
	
	
	public List<String> getSglIdsFromAwbId(String awbId) {
		return this.getAwbEdgeIdToSgl().get(awbId);
	}
	
	public String getAwbIdFromSglId(String sglID) {	
		return this.getAwbIdFromSglId(sglID);
	}
	
}
