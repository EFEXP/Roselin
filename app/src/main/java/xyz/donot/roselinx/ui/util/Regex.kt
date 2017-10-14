package xyz.donot.roselinx.ui.util

import java.util.regex.Pattern



object Regex {
    /**
     * Regular expression pattern to match all IANA top-level domains.
     * List accurate as of 2007/06/15.  List taken from:
     * http://data.iana.org/TLD/tlds-alpha-by-domain.txt
     * This pattern is auto-generated by //device/tools/make-iana-tld-pattern.py
     */
    private val TOP_LEVEL_DOMAIN_PATTERN = Pattern.compile(
            "((aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
                    + "|(biz|b[abdefghijmnorstvwyz])"
                    + "|(cat|com|coop|c[acdfghiklmnoruvxyz])"
                    + "|d[ejkmoz]"
                    + "|(edu|e[cegrstu])"
                    + "|f[ijkmor]"
                    + "|(gov|g[abdefghilmnpqrstuwy])"
                    + "|h[kmnrtu]"
                    + "|(info|int|i[delmnoqrst])"
                    + "|(jobs|j[emop])"
                    + "|k[eghimnrwyz]"
                    + "|l[abcikrstuvy]"
                    + "|(mil|mobi|museum|m[acdghklmnopqrstuvwxyz])"
                    + "|(name|net|n[acefgilopruz])"
                    + "|(org|om)"
                    + "|(pro|p[aefghklmnrstwy])"
                    + "|qa"
                    + "|r[eouw]"
                    + "|s[abcdeghijklmnortuvyz]"
                    + "|(tel|travel|tInstance[cdfghjklmnoprtvwz])"
                    + "|u[agkmsyz]"
                    + "|v[aceginu]"
                    + "|w[fs]"
                    + "|y[etu]"
                    + "|z[amw])")

    /**
     * Regular expression pattern to match RFC 1738 URLs
     * List accurate as of 2007/06/15.  List taken from:
     * http://data.iana.org/TLD/tlds-alpha-by-domain.txt
     * This pattern is auto-generated by //device/tools/make-iana-tld-pattern.py
     */
    val WEB_URL_PATTERN: Pattern = Pattern.compile(
            "((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                    + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                    + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
                    + "((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+"   // named host

                    + "(?:"   // plus top level domain

                    + "(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
                    + "|(?:biz|b[abdefghijmnorstvwyz])"
                    + "|(?:cat|com|coop|c[acdfghiklmnoruvxyz])"
                    + "|d[ejkmoz]"
                    + "|(?:edu|e[cegrstu])"
                    + "|f[ijkmor]"
                    + "|(?:gov|g[abdefghilmnpqrstuwy])"
                    + "|h[kmnrtu]"
                    + "|(?:info|int|i[delmnoqrst])"
                    + "|(?:jobs|j[emop])"
                    + "|k[eghimnrwyz]"
                    + "|l[abcikrstuvy]"
                    + "|(?:mil|mobi|museum|m[acdghklmnopqrstuvwxyz])"
                    + "|(?:name|net|n[acefgilopruz])"
                    + "|(?:org|om)"
                    + "|(?:pro|p[aefghklmnrstwy])"
                    + "|qa"
                    + "|r[eouw]"
                    + "|s[abcdeghijklmnortuvyz]"
                    + "|(?:tel|travel|tInstance[cdfghjklmnoprtvwz])"
                    + "|u[agkmsyz]"
                    + "|v[aceginu]"
                    + "|w[fs]"
                    + "|y[etu]"
                    + "|z[amw]))"
                    + "|(?:(?:25[0-5]|2[0-4]" // or ip address

                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]"
                    + "|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9])))"
                    + "|\\.\\.\\."
                    + "(?:\\:\\d{1,5})?)" // plus option port number

                    + "(\\/(?:(?:[a-zA-Z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~"  // plus option query params

                    + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
                    + "(?:\\b|$)") // and finally, a word boundary or end of
    // input.  This is to stop foo.sure from
    // matching as foo.su

    private val IP_ADDRESS_PATTERN = Pattern.compile(
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
                    + "|[1-9][0-9]|[0-9]))")


    val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+")

    /**
     * This pattern is intended for searching for things that look like they
     * might be phone numbers in arbitrary text, not for validating whether
     * something is in fact a phone number.  It will miss many things that
     * are legitimate phone numbers.
     *
     *
     *
     *  The pattern matches the following:
     *
     *  * Optionally, a + sign followed immediately by one or more digits. Spaces, dots, or dashes
     * may follow.
     *  * Optionally, sets of digits in parentheses, separated by spaces, dots, or dashes.
     *  * A string starting and ending with a digit, containing digits, spaces, dots, and/or dashes.
     *
     */
    val PHONE_PATTERN: Pattern = Pattern.compile(// sdd = space, dot, or dash
            "(\\+[0-9]+[\\- \\.]*)?"                    // +<digits><sdd>*

                    + "(\\([0-9]+\\)[\\- \\.]*)?"               // (<digits>)<sdd>*

                    + "([0-9][0-9\\- \\.][0-9\\- \\.]+[0-9])") // <digit><digit|sdd>+<digit>



    // Twitters own regex patterns
    private val UNICODE_SPACES = "[" +
            "\\u0009-\\u000d" + //  # White_Space # Cc   [5] <control-0009>..<control-000D>

            "\\u0020" + // White_Space # Zs       SPACE

            "\\u0085" + // White_Space # Cc       <control-0085>

            "\\u00a0" + // White_Space # Zs       NO-BREAK SPACE

            "\\u1680" + // White_Space # Zs       OGHAM SPACE MARK

            "\\u180E" + // White_Space # Zs       MONGOLIAN VOWEL SEPARATOR

            "\\u2000-\\u200a" + // # White_Space # Zs  [11] EN QUAD..HAIR SPACE

            "\\u2028" + // White_Space # Zl       LINE SEPARATOR

            "\\u2029" + // White_Space # Zp       PARAGRAPH SEPARATOR

            "\\u202F" + // White_Space # Zs       NARROW NO-BREAK SPACE

            "\\u205F" + // White_Space # Zs       MEDIUM MATHEMATICAL SPACE

            "\\u3000" + // White_Space # Zs       IDEOGRAPHIC SPACE

            "]"

    private val LATIN_ACCENTS_CHARS = "\\u00c0-\\u00d6\\u00d8-\\u00f6\\u00f8-\\u00ff" + // Latin-1

            "\\u0100-\\u024f" + // Latin Extended A and B

            "\\u0253\\u0254\\u0256\\u0257\\u0259\\u025b\\u0263\\u0268\\u026f\\u0272\\u0289\\u028b" + // IPA Extensions

            "\\u02bb" + // Hawaiian

            "\\u0300-\\u036f" + // Combining diacritics

            "\\u1e00-\\u1eff" // Latin Extended Additional (mostly for Vietnamese)
    private val HASHTAG_ALPHA_CHARS = "a-z$LATIN_ACCENTS_CHARS\\u0400-\\u04ff\\u0500-\\u0527\\u2de0-\\u2dff\\ua640-\\ua69f\\u0591-\\u05bf\\u05c1-\\u05c2\\u05c4-\\u05c5\\u05c7\\u05d0-\\u05ea\\u05f0-\\u05f4\\ufb1d-\\ufb28\\ufb2a-\\ufb36\\ufb38-\\ufb3c\\ufb3e\\ufb40-\\ufb41\\ufb43-\\ufb44\\ufb46-\\ufb4f\\u0610-\\u061a\\u0620-\\u065f\\u066e-\\u06d3\\u06d5-\\u06dc\\u06de-\\u06e8\\u06ea-\\u06ef\\u06fa-\\u06fc\\u06ff\\u0750-\\u077f\\u08a0\\u08a2-\\u08ac\\u08e4-\\u08fe\\ufb50-\\ufbb1\\ufbd3-\\ufd3d\\ufd50-\\ufd8f\\ufd92-\\ufdc7\\ufdf0-\\ufdfb\\ufe70-\\ufe74\\ufe76-\\ufefc\\u200c\\u0e01-\\u0e3a\\u0e40-\\u0e4e\\u1100-\\u11ff\\u3130-\\u3185\\uA960-\\uA97F\\uAC00-\\uD7AF\\uD7B0-\\uD7FF\\p{InHiragana}\\p{InKatakana}\\p{InCJKUnifiedIdeographs}\\u3003\\u3005\\u303b\\uff21-\\uff3a\\uff41-\\uff5a\\uff66-\\uff9f\\uffa1-\\uffdc"                  // half width Hangul (Korean)
    private val HASHTAG_ALPHA_NUMERIC_CHARS = "0-9\\uff10-\\uff19_$HASHTAG_ALPHA_CHARS"
    private val HASHTAG_ALPHA = "[$HASHTAG_ALPHA_CHARS]"
    private val HASHTAG_ALPHA_NUMERIC = "[$HASHTAG_ALPHA_NUMERIC_CHARS]"

    /* URL related hash regex collection */
    private val URL_VALID_PRECEEDING_CHARS = "(?:[^A-Z0-9@＠$#＃\u202A-\u202E]|^)"

    private val URL_VALID_CHARS = "[\\p{Alnum}$LATIN_ACCENTS_CHARS]"
    private val URL_VALID_SUBDOMAIN = "(?>(?:$URL_VALID_CHARS[$URL_VALID_CHARS\\-_]*)?$URL_VALID_CHARS\\.)"
    private val URL_VALID_DOMAIN_NAME = "(?:(?:$URL_VALID_CHARS[$URL_VALID_CHARS\\-]*)?$URL_VALID_CHARS\\.)"
    /* Any non-space, non-punctuation characters. \p{Z} = any kind of whitespace or invisible separator. */
    private val URL_VALID_UNICODE_CHARS = "[.[^\\p{Punct}\\s\\p{Z}\\p{InGeneralPunctuation}]]"

    private val URL_VALID_GTLD =
            "(?:(?:" +
                    "academy|accountants|active|actor|aero|agency|airforce|archi|army|arpa|asia|associates|attorney|audio|autos|" +
                    "axa|bar|bargains|bayern|beer|berlin|best|bid|bike|bio|biz|black|blackfriday|blue|bmw|boutique|brussels|build|" +
                    "builders|buzz|bzh|cab|camera|camp|cancerresearch|capetown|capital|cards|care|career|careers|cash|cat|catering|" +
                    "center|ceo|cheap|christmas|church|citic|claims|cleaning|clinic|clothing|club|codes|coffee|college|cologne|com|" +
                    "community|company|computer|condos|construction|consulting|contractors|cooking|cool|coop|country|credit|" +
                    "creditcard|cruises|cuisinella|dance|dating|degree|democrat|dental|dentist|desi|diamonds|digital|direct|" +
                    "directory|discount|dnp|domains|durban|edu|education|email|engineer|engineering|enterprises|equipment|estate|" +
                    "eus|events|exchange|expert|exposed|fail|farm|feedback|finance|financial|fish|fishing|fitness|flights|florist|" +
                    "foo|foundation|frogans|fund|furniture|futbol|gal|gallery|gift|gives|glass|global|globo|gmo|gop|gov|graphics|" +
                    "gratis|green|gripe|guide|guitars|guru|hamburg|haus|hiphop|hiv|holdings|holiday|homes|horse|host|house|" +
                    "immobilien|industries|info|ink|institute|insure|int|international|investments|jetzt|jobs|joburg|juegos|kaufen|" +
                    "kim|kitchen|kiwi|koeln|kred|land|lawyer|lease|lgbt|life|lighting|limited|limo|link|loans|london|lotto|luxe|" +
                    "luxury|maison|management|mango|market|marketing|media|meet|menu|miami|mil|mini|mobi|moda|moe|monash|mortgage|" +
                    "moscow|motorcycles|museum|nagoya|name|navy|net|neustar|nhk|ninja|nyc|okinawa|onl|org|organic|ovh|paris|" +
                    "partners|parts|photo|photography|photos|physio|pics|pictures|pink|place|plumbing|post|press|pro|productions|" +
                    "properties|pub|qpon|quebec|recipes|red|rehab|reise|reisen|ren|rentals|repair|report|republican|rest|reviews|" +
                    "rich|rio|rocks|rodeo|ruhr|ryukyu|saarland|schmidt|schule|scot|services|sexy|shiksha|shoes|singles|social|" +
                    "software|sohu|solar|solutions|soy|space|spiegel|supplies|supply|support|surf|surgery|suzuki|systems|tattoo|" +
                    "tax|technology|tel|tienda|tips|tirol|today|tokyo|tools|town|toys|trade|training|travel|university|uno|" +
                    "vacations|vegas|ventures|versicherung|vet|viajes|villas|vision|vlaanderen|vodka|vote|voting|voto|voyage|wang|" +
                    "watch|webcam|website|wed|wien|wiki|works|wtc|wtf|xxx|xyz|yachts|yokohama|zone|дети|москва|онлайн|орг|сайт|" +
                    "بازار|شبكة|موقع|संगठन|みんな|世界|中信|中文网|公司|公益|商城|商标|在线|我爱你|政务|机构|游戏|移动|组织机构|网址|网络|集团|삼성" +
                    ")(?=[^\\p{Alnum}@]|$))"
    private val URL_VALID_CCTLD =
            "(?:(?:" +
                    "ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|ax|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bl|bm|bn|bo|bq|br|bs|bt|bv|" +
                    "bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cw|cx|cy|cz|de|dj|dk|dm|do|dz|ec|ee|eg|eh|er|es|et|eu|" +
                    "fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|tweetId|ie|il|im|in|" +
                    "io|iq|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mf|" +
                    "mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|" +
                    "pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|rs|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|ss|st|su|sv|sx|sy|" +
                    "sz|tc|td|tf|tg|th|tj|tk|tl|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|um|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|" +
                    "za|zm|zw|мкд|мон|рф|срб|укр|қаз|الاردن|الجزائر|السعودية|المغرب|امارات|ایران|بھارت|تونس|سودان|سورية|عمان|" +
                    "فلسطين|قطر|مصر|مليسيا|پاکستان|भारत|বাংলা|ভারত|ਭਾਰਤ|ભારત|இந்தியா|இலங்கை|சிங்கப்பூர்|భారత్|ලංකා|ไทย|გე|中国|中國|台湾|" +
                    "台灣|新加坡|香港|한국" +
                    ")(?=[^\\p{Alnum}@]|$))"
    private val URL_PUNYCODE = "(?:xn--[0-9a-z]+)"
    private val SPECIAL_URL_VALID_CCTLD = "(?:(?:" + "co|tv" + ")(?=[^\\p{Alnum}@]|$))"

    private val URL_VALID_DOMAIN =
            "(?:$URL_VALID_SUBDOMAIN+$URL_VALID_DOMAIN_NAME(?:$URL_VALID_GTLD|$URL_VALID_CCTLD|$URL_PUNYCODE))|(?:$URL_VALID_DOMAIN_NAME(?:$URL_VALID_GTLD|$URL_PUNYCODE|$SPECIAL_URL_VALID_CCTLD))|(?:(?<=https?://)(?:(?:$URL_VALID_DOMAIN_NAME$URL_VALID_CCTLD)|(?:$URL_VALID_UNICODE_CHARS+\\.(?:$URL_VALID_GTLD|$URL_VALID_CCTLD))))|(?:$URL_VALID_DOMAIN_NAME$URL_VALID_CCTLD(?=/))"

    private val URL_VALID_PORT_NUMBER = "[0-9]++"

    private val URL_VALID_GENERAL_PATH_CHARS = "[a-z0-9!\\*';:=\\+,.\\$/%#\\[\\]\\-_~\\|&@$LATIN_ACCENTS_CHARS]"
    /** Allow URL paths to contain up to two nested levels of balanced parens
     * 1. Used in Wikipedia URLs like /Primer_(film)
     * 2. Used in IIS sessions like /S(dfd346)/
     * 3. Used in Rdio URLs like /track/We_Up_(Album_Version_(Edited))/
     */
    private val URL_BALANCED_PARENS = "\\((?:$URL_VALID_GENERAL_PATH_CHARS+|(?:$URL_VALID_GENERAL_PATH_CHARS*\\($URL_VALID_GENERAL_PATH_CHARS+\\)$URL_VALID_GENERAL_PATH_CHARS*))\\)"

    /** Valid end-of-path characters (so /foo. does not gobble the period).
     * 2. Allow =&# for empty URL parameters and other URL-join artifacts
     */
    private val URL_VALID_PATH_ENDING_CHARS = "[a-z0-9=_#/\\-\\+$LATIN_ACCENTS_CHARS]|(?:$URL_BALANCED_PARENS)"

    private val URL_VALID_PATH = "(?:(?:$URL_VALID_GENERAL_PATH_CHARS*(?:$URL_BALANCED_PARENS$URL_VALID_GENERAL_PATH_CHARS*)*$URL_VALID_PATH_ENDING_CHARS)|(?:@$URL_VALID_GENERAL_PATH_CHARS+/))"

    private val URL_VALID_URL_QUERY_CHARS = "[a-z0-9!?\\*'\\(\\);:&=\\+\\$/%#\\[\\]\\-_\\.,~\\|@]"
    private val URL_VALID_URL_QUERY_ENDING_CHARS = "[a-z0-9_&=#/]"
    private val VALID_URL_PATTERN_STRING =
            "(((https?://)?($URL_VALID_DOMAIN)(?::($URL_VALID_PORT_NUMBER))?(/${URL_VALID_PATH}*+)?(\\?$URL_VALID_URL_QUERY_CHARS*$URL_VALID_URL_QUERY_ENDING_CHARS)?))"

    private val AT_SIGNS_CHARS = "@\uFF20"

    private val DOLLAR_SIGN_CHAR = "\\$"
    private val CASHTAG = "[a-z]{1,6}(?:[._][a-z]{1,2})?"

    /* Begin public constants */

    val HASHTAG_PATTERN: Pattern = Pattern.compile("(#)($HASHTAG_ALPHA_NUMERIC*$HASHTAG_ALPHA$HASHTAG_ALPHA_NUMERIC*)", Pattern.CASE_INSENSITIVE)
    val VALID_HASHTAG_GROUP_BEFORE = 1
    val VALID_HASHTAG_GROUP_HASH = 2
    val VALID_HASHTAG_GROUP_TAG = 3
    val INVALID_HASHTAG_MATCH_END: Pattern = Pattern.compile("^(?:[#＃]|://)")
    val RTL_CHARACTERS: Pattern = Pattern.compile("[\u0600-\u06FF\u0750-\u077F\u0590-\u05FF\uFE70-\uFEFF]")

    private val AT_SIGNS: Pattern = Pattern.compile("[$AT_SIGNS_CHARS]")
    val MENTION_PATTERN: Pattern = Pattern.compile("($AT_SIGNS+)([a-z0-9_]{1,20})(/[a-z][a-z0-9_\\-]{0,24})?", Pattern.CASE_INSENSITIVE)
    val VALID_MENTION_OR_LIST_GROUP_BEFORE = 1
    val VALID_MENTION_OR_LIST_GROUP_AT = 2
    val VALID_MENTION_OR_LIST_GROUP_USERNAME = 3
    val VALID_MENTION_OR_LIST_GROUP_LIST = 4

    val VALID_REPLY: Pattern = Pattern.compile("^(?:$UNICODE_SPACES)*$AT_SIGNS([a-z0-9_]{1,20})", Pattern.CASE_INSENSITIVE)
    val VALID_REPLY_GROUP_USERNAME = 1

    val INVALID_MENTION_MATCH_END: Pattern = Pattern.compile("^(?:[$AT_SIGNS_CHARS$LATIN_ACCENTS_CHARS]|://)")

    val VALID_URL: Pattern = Pattern.compile(VALID_URL_PATTERN_STRING, Pattern.CASE_INSENSITIVE)
    val VALID_URL_GROUP_ALL = 1
    val VALID_URL_GROUP_BEFORE = 2
    val VALID_URL_GROUP_URL = 3
    val VALID_URL_GROUP_PROTOCOL = 4
    val VALID_URL_GROUP_DOMAIN = 5
    val VALID_URL_GROUP_PORT = 6
    val VALID_URL_GROUP_PATH = 7
    val VALID_URL_GROUP_QUERY_STRING = 8



}
