/**
 * User: Joern Buitink
 * Date: 17.06.14
 * Time: 08:25
 * http://www.steinchenwelt.de
 */
public enum Languages {

    ARABIC("ar", ""),
    BULGARIAN("bg", ""),
    CATALAN("ca", ""),

    CZECH("cs", ""),
    DANISH("da", ""),
    DUTCH("nl", ""),
    ENGLISH("en",""),
    ESTONIAN("et", ""),
    FINNISH("fi", ""),
    FRENCH("fr", ""),
    //GERMAN("de", ""),
    GREEK("el", ""),
    HUNGARIAN("hu", ""),
    INDONESIAN("id", ""),
    ITALIAN("it", ""),
    LATVIAN("lv", ""),
    LITHUANIAN("lt", ""),
    MALAY("ms", ""),
    MALTESE("mt", ""),
    NORWEGIAN("no", ""),
    POLISH("pl", ""),
    PORTUGUESE("pt", ""),
    ROMANIAN("ro", ""),
    RUSSIAN("ru", ""),
    SLOVAK("sk", ""),
    SLOVENIAN("sl", ""),
    SPANISH("es", ""),
    SWEDISH("sv", ""),
    TURKISH("tr", ""),
    UKRAINIAN("uk", "");

    private String msName;
    private String googleName;

    Languages(String msName, String googleName) {
        this.msName = msName;
        if ("".equals(googleName)) {
            this.googleName = msName;
        } else {
            this.googleName = googleName;
        }
    }

    public String getMsName() {
        return msName;
    }

    public String getGoogleName() {
        return googleName;
    }
}
