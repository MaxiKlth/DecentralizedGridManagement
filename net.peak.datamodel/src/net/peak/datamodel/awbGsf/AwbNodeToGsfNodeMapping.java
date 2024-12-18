package net.peak.datamodel.awbGsf;

import java.util.HashMap;

public class AwbNodeToGsfNodeMapping {

	private HashMap<String, String> gsfNodeToAwbNode;
	private HashMap<String, String> awbNodeToGsfNode;
	
	
	public HashMap<String, String> getGsfNodeToAwbNode() {
		if (gsfNodeToAwbNode==null) {
			gsfNodeToAwbNode = new HashMap<>();
		}
		return gsfNodeToAwbNode;
	}
	public HashMap<String, String> getAwbNodeToGsfNode() {
		if (awbNodeToGsfNode==null) {
			awbNodeToGsfNode = new HashMap<>();
		}
		return awbNodeToGsfNode;
	}
	
	public void addMapping(String gsfNode, String awbNodeNew) {
		
		String awbNode = this.getGsfNodeToAwbNode().get(gsfNode);
		if (awbNode==null) {
			this.getGsfNodeToAwbNode().put(gsfNode, awbNodeNew);
			this.getAwbNodeToGsfNode().put(awbNodeNew, gsfNode);
		}
	}
	
	public String getAwbNodeFromGsfNode(String gsfNode) {
		return this.getGsfNodeToAwbNode().get(gsfNode);
	}
	
	public String getGsfNodeFromAwbNode(String awbNode) {
		return this.getAwbNodeToGsfNode().get(awbNode);
	}
}
