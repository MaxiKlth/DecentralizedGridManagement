package net.peak.datamodel.awbGsf;

import java.util.HashMap;

public class AwbEdgeToGsfEdgeMapping {

	private HashMap<String, String> gsfEdgeToAwbEdge;
	private HashMap<String, String> awbEdgeToGsfEdge;
	
	
	public HashMap<String, String> getGsfEdgeToAwbEdge() {
		if (gsfEdgeToAwbEdge==null) {
			gsfEdgeToAwbEdge = new HashMap<>();
		}
		return gsfEdgeToAwbEdge;
	}
	public HashMap<String, String> getAwbEdgeToGsfEdge() {
		if (awbEdgeToGsfEdge==null) {
			awbEdgeToGsfEdge = new HashMap<>();
		}
		return awbEdgeToGsfEdge;
	}
	
	public void addMapping(String gsfEdge, String awbEdgeNew) {
		
		String awbEdge = this.getGsfEdgeToAwbEdge().get(gsfEdge);
		if (awbEdge==null) {
			this.getGsfEdgeToAwbEdge().put(gsfEdge, awbEdgeNew);
			this.getAwbEdgeToGsfEdge().put(awbEdgeNew, gsfEdge);
		}
	}
	
	public String getAwbEdgeFromGsfEdge(String gsfEdge) {
		return this.getGsfEdgeToAwbEdge().get(gsfEdge);
	}
	
	public String getGsfEdgeFromAwbEdge(String awbEdge) {
		return this.getAwbEdgeToGsfEdge().get(awbEdge);
	}
}
