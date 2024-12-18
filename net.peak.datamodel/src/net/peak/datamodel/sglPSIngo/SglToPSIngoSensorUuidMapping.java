package net.peak.datamodel.sglPSIngo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Class SglToPSIngoSensorUuidMapping.
 */
public class SglToPSIngoSensorUuidMapping {
	
	private HashMap<String, List<String>> psingoToSgl;
	private HashMap<String, String> sglToPsingo;
	
	
	public HashMap<String, List<String>> getPsingoToSgl() {
		if (psingoToSgl==null) {
			psingoToSgl = new HashMap<>();
		}
		return psingoToSgl;
	}
	public HashMap<String, String> getSglToPsingo() {
		if (sglToPsingo==null) {
			sglToPsingo = new HashMap<>();
		}
		return sglToPsingo;
	}
	
	
	public void addMapping(String psiUUID, String sglID) {
		
		// --- check psi mapping
		List<String> sglList = this.getPsingoToSgl().get(psiUUID);
		if (sglList==null) {
			sglList = new ArrayList<>();
			sglList.add(sglID);
			this.getPsingoToSgl().put(psiUUID, sglList);
		} else {
			if (sglList.contains(sglID)==false) {
				sglList.add(sglID);
			}
		}
		
		// --- Check SGL --------
		this.getSglToPsingo().put(sglID, psiUUID);
	}
	

	public List<String> getSGLidsFromPSIuuid(String psiUUID) {
		return this.getPsingoToSgl().get(psiUUID);
	}
	
	public String getPSIuuidFromSGLid(String sglID) {	
		return this.getPSIuuidFromSGLid(sglID);
	}
	
}