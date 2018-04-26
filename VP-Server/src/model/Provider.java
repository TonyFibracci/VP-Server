package model;

public enum Provider {
	
	BLOOMBERG, MARKIT, ICE;
	
	public static Provider getProvider(String name) {
		switch(name) {
		case "Bloomberg":
			return BLOOMBERG;
		case "Markit":
			return MARKIT;
		case "ICE":
			return ICE;
		default:
			return null;
		}
	}

}
