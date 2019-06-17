package fi.jonix.hkmyynti.util;

public class XmlUtils {

	public static String getXmlTag(String tagName, String value) {
		return "<" + tagName + ">" + value + "</" + tagName + ">\n";
	}
	
}
