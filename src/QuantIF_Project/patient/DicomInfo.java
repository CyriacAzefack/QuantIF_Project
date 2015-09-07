package Quantif_project.patient;
/**
 * 
 * @author Cyriac
 *
 */
public class DicomInfo {
	/**
	 * Etiquette de l'info formé de 2 nombre de 4 chiffres : 0028, 1051 
	 */
	private String tag[];
	
	/**
	 * Clé de l'info
	 */
	private String key;
	
	/**
	 * Valeure de l'infos
	 */
	private String value;
	
	/**
	 * Taille minimale d'une info
	 * '0028,1051  Window Width: 13228'
	 */
	private static final int TAILLE_MIN_STRING = 10; // les 8 premiers chiffres, la ',' et le ':' au moins
	
	/**
	 * Crée une instance de DicomInfo
	 * @param tag
	 * @param key
	 * @param value
	 * @throws Exception
	 * 		Exception levée quand l'étiquette de l'info n'a pas exactement 2 valeurs
	 */
	public DicomInfo (String tag[], String key, String value) throws Exception {
		if (tag.length != 2) {
			throw new Exception("L'étiquette doit être composé de deux valeurs!!");
		}
		
		this.tag = tag;
		
		this.key = key;
		
		this.value = value;
		
	}
	
	/**
	 * Crée une instance de DicomInfo à l'aide d'un string de la forme
	 * info = '0028,1051  Window Width: 13228'
	 * @param info
	 * @throws Exception 
	 * 		Exeption levée quand la taille du string d'info est inférieur à la taille minimale
	 */
	public DicomInfo(String info) throws Exception {
		if (info.length() < TAILLE_MIN_STRING) { //Si la chaîne n'est pas assez longue
			throw new Exception("Le string pour créer une DicomInfo est trop court : "
					+ "Au moins " + TAILLE_MIN_STRING + " caractères!!");
		}
		String splitStr[] = splitInfo(info);
		this.tag = new String[2];
		this.tag[0] = splitStr[0];
		this.tag[1] = splitStr[1];
		this.key = splitStr[2];
		this.value = splitStr[3];
	}
	
	public String toString() {
		String str = "Etiquette : ("+tag[0]+","+tag[1]+")";
		str += " Clé : "+key;
		str += " Valeur : "+value;
		
		return str;
		
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	/**
	 * Découpe le long string d'information en 4 paramètres et les range dans un
	 * tableau de string comme suit :
	 * - str[0] = tag[0]
	 * - str[1] = tag[1]
	 * - str[2] = key
	 * - str[3] = value
	 * @param infoString
	 * @return
	 */
	private String[] splitInfo(String infoString) {
		String[] str = new String[4];
		
		//On récupère les octets 0:4 de infoString
		str[0] = infoString.substring(0,4);
		
		//On récupère les octets 5:9 de infoString
		str[1] = infoString.substring(5,9);
		
		//On récupère ce qui reste
		String rest = infoString.substring(11);
		
		//On split par rapport au ":"
		String[] rest2 = rest.split(":");
		
		//On nettoie les 2 parties qui restent
		str[2] = rest2[0].replaceAll(" ", "").replaceAll("'", "");
		str[3] = rest2[1].replaceAll(" ", "");
		
		return str;
	}

}
