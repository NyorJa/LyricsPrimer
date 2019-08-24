
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Traitement {
	private static final char Separateur = '/';
	private static final char NoBreakSpace = "\u00A0".charAt(0);

	private static Map<String, String> replacements = null;
	private static Map<String, String> optimizations = null;

	public static void init() {
		replacements = new HashMap<String, String>();
		replacements.put("[āâ]", "aa");
		replacements.put("[ēê]", "ei");
		replacements.put("[īî]", "ii");
		replacements.put("[ōô]", "ou");
		replacements.put("[ūû]", "uu");
		replacements.put("　", " ");

		optimizations = new HashMap<String, String>();
		// Voyelles
//		optimizations.put("a/a",  "aa");
		optimizations.put("a/i", "ai");
//		optimizations.put("i/i",  "ii");
//		optimizations.put("u/u",  "uu");
		optimizations.put("e/i", "ei");
		optimizations.put("o/u", "ou");
		optimizations.put("([aiueo])/\\1", "$1$1");
		// Consonnes
		optimizations.put("/([zrtpmskhgdcb])/\\1", "$1/$1"); // Enlever une sync du sokuon
		optimizations.put(" t/t", " tt"); // Cas spécial du tte isolé
		optimizations.put("[/ ]n([^aeiou])", "n$1"); // Si un mot commence par un n isolé on l'attache au mot d'avant
														// pour raboter une sync
	}

	public static String optimize(String in) {
		String lcStr = in;
		for (String k : optimizations.keySet()) {
			String tcReg = ("" + k.charAt(0)).toUpperCase() + k.substring(1);
			String tcTarget = ("" + optimizations.get(k).charAt(0)).toUpperCase() + optimizations.get(k).substring((1));

			lcStr = lcStr.replaceAll(k.toLowerCase(), optimizations.get(k).toLowerCase());
			lcStr = lcStr.replaceAll(k.toUpperCase(), optimizations.get(k).toUpperCase());
			lcStr = lcStr.replaceAll(tcReg, tcTarget);
		}
		return lcStr;

		/*
		 * // Consts de commodité final Integer SLASH = 0; final Integer LOWER = 1;
		 * final Integer UPPER = 2; final Integer OTHER = -1;
		 * 
		 * // On mappe la casse pour reconstruire apres List<Integer> casseMap =
		 * in.chars().sequential().boxed().map(c -> { if (c == "/".codePointAt(0)) {
		 * return SLASH; } else if (Character.isLowerCase(c)) { return LOWER; } else if
		 * (Character.isUpperCase(c)) { return UPPER; } else { return OTHER; }
		 * }).collect(Collectors.toList());
		 * 
		 * String lcStr = in.toLowerCase();
		 * 
		 * for (String k : optimizations.keySet()) { Pattern pat = Pattern.compile(k);
		 * Matcher mat = pat.matcher(lcStr); while (mat.find()) { lcStr =
		 * lcStr.replaceFirst(k, optimizations.get(k)); if (k.length() > k.replace("/",
		 * "").length()) { casseMap.remove(mat.start() + casseMap.subList(mat.start(),
		 * mat.end()).indexOf(SLASH)); } } lcStr = lcStr.replaceAll(k.toLowerCase(),
		 * optimizations.get(k).toLowerCase()); }
		 * 
		 */

	}

	private static String applyReplacements(String str) {
		String res = str;
		for (String k : replacements.keySet()) {
			res = res.replaceAll(k, replacements.get(k));
		}
		return res;
	}

	public enum CharType {
		ROMAJI_VOWEL, ROMAJI_CONSONANT, KANA, KANJI, SOKUON, SMALL_KANA, LINK, WHITESPACE, BRACKET, PONCTUATION, NUMBER,
		OTHER;
	}

	public static final String traiter(String lyrics) {
		if (lyrics == null) {
			return "";
		}
		lyrics = applyReplacements(lyrics);
		// lyrics = Traitement.cleanWaO(lyrics);
		StringBuffer currentSyllable = new StringBuffer();
		StringBuffer result = new StringBuffer();
		char previousLCChar = ' ';
		char nextLCChar = 'a';
		int i = 0;
		while (i < lyrics.length()) {
			char character = lyrics.charAt(i);
			if (i + 1 < lyrics.length()) {
				nextLCChar = Character.toLowerCase(lyrics.charAt(i + 1));
			} else {
				nextLCChar = 'a';
			}
			char LCcharacter = Character.toLowerCase(character);
			System.out.println("reading kind of " + character + " as " + Traitement.analyze(LCcharacter));
			block0: switch (Traitement.analyze(LCcharacter)) {
			case ROMAJI_VOWEL:
				switch (Traitement.analyze(previousLCChar)) {
				case ROMAJI_CONSONANT:
					currentSyllable.append(character);
					break block0;
				case LINK:
				case ROMAJI_VOWEL:
				case KANA:
				case KANJI:
				case SOKUON:
				case SMALL_KANA:
				case PONCTUATION:
				case NUMBER:
				case OTHER:
					result.append(currentSyllable.toString());
					currentSyllable = new StringBuffer();
					currentSyllable.append(Separateur).append(character);
					break block0;
				case WHITESPACE:
				case BRACKET:
					if (previousLCChar == '\n' || i == 0) {
//						currentSyllable.append(Character.toUpperCase(character));
						currentSyllable.append(character);
						break block0;
					}
					currentSyllable.append(character);
				}
				break;
			case ROMAJI_CONSONANT:
				switch (Traitement.analyze(previousLCChar)) {
				case ROMAJI_CONSONANT:
					// Switch la comparaison a n si on veut que les nya soient nya ou n/ya
					// if (/* previousLCChar != 'n' && */ LCcharacter != previousLCChar) {
					if ((previousLCChar != 'n' && LCcharacter != previousLCChar)
							|| (previousLCChar == 'n' && LCcharacter == 'y')) { // Je crois que ce truc là sert juste a
																				// écrite les tsu shi chi
						currentSyllable.append(character);
						break block0;
					}
				case ROMAJI_VOWEL:
				case KANA:
				case KANJI:
				case SOKUON:
				case SMALL_KANA:
				case LINK:
				case PONCTUATION:
				case NUMBER:
				case OTHER:
					result.append(currentSyllable.toString());
					currentSyllable = new StringBuffer();
					currentSyllable.append(Separateur).append(character);
					break block0;
				case WHITESPACE:
				case BRACKET:
					if (previousLCChar == '\n' || i == 0) {
//						currentSyllable.append(Character.toUpperCase(character));
						currentSyllable.append(character);
						break block0;
					}
					currentSyllable.append(character);
				}
				break;
			case KANA:
				switch (Traitement.analyze(previousLCChar)) {
				case SOKUON: {
					currentSyllable.append(character);
					break block0;
				}
				case SMALL_KANA: {
					result.append(currentSyllable.toString());
					currentSyllable = new StringBuffer();
					currentSyllable.append(Separateur).append(character);
					break block0;
				}
				case ROMAJI_VOWEL:
				case ROMAJI_CONSONANT:
				case KANA:
				case KANJI:
				case LINK:
				case PONCTUATION:
				case NUMBER:
				case OTHER: {
					result.append(currentSyllable.toString());
					currentSyllable = new StringBuffer();
					currentSyllable.append(Separateur).append(character);
					break block0;
				}
				case WHITESPACE:
				case BRACKET:
					if (previousLCChar == '\n' || i == 0) {
						currentSyllable.append(character);
						break block0;
					}
					currentSyllable.append(character);
				}
				break;
			case KANJI:
				switch (Traitement.analyze(previousLCChar)) {
				case ROMAJI_VOWEL:
				case ROMAJI_CONSONANT:
				case KANA:
				case KANJI:
				case SOKUON:
				case SMALL_KANA:
				case LINK:
				case PONCTUATION:
				case NUMBER:
				case OTHER:
					result.append(currentSyllable.toString());
					currentSyllable = new StringBuffer();
					currentSyllable.append(Separateur).append(character);
					break block0;
				case WHITESPACE:
				case BRACKET:
					if (previousLCChar == '\n' || i == 0) {
						currentSyllable.append(character);
						break block0;
					}
					currentSyllable.append(character);
				}
				break;
			case SOKUON:
				switch (Traitement.analyze(previousLCChar)) {
				case ROMAJI_VOWEL:
				case ROMAJI_CONSONANT:
				case KANA:
				case KANJI:
				case SOKUON:
				case SMALL_KANA:
				case LINK:
				case PONCTUATION:
				case NUMBER:
				case OTHER:
					result.append(currentSyllable.toString());
					currentSyllable = new StringBuffer();
					currentSyllable.append(Separateur).append(character);
					break block0;
				case WHITESPACE:
				case BRACKET:
					if (previousLCChar == '\n' || i == 0) {
						currentSyllable.append(character);
						break block0;
					}
					currentSyllable.append(character);
				}
				break;
			case SMALL_KANA:
				switch (Traitement.analyze(previousLCChar)) {
				case ROMAJI_VOWEL:
				case ROMAJI_CONSONANT:
				case KANA:
				case KANJI:
				case SOKUON:
				case SMALL_KANA:
				case LINK:
				case PONCTUATION:
				case NUMBER:
				case OTHER:
					currentSyllable.append(character);
					break block0;
				case WHITESPACE:
				case BRACKET:
					if (previousLCChar == '\n' || i == 0) {
						currentSyllable.append(character);
						break block0;
					}
					currentSyllable.append(character);
				}
				break;

			case LINK:
				switch (Traitement.analyze(previousLCChar)) {

				case ROMAJI_VOWEL:
				case ROMAJI_CONSONANT:
				case KANA:
				case KANJI:
				case SOKUON:
				case SMALL_KANA:
				case PONCTUATION:
				case NUMBER:
				case OTHER:
					currentSyllable.append(character);
					break;
				case LINK:
					result.append(currentSyllable.toString());
					currentSyllable = new StringBuffer();
					currentSyllable.append(Separateur).append(character);
					break block0;
				case WHITESPACE:
				case BRACKET:
					currentSyllable.append(character);
				}
				break;
			case WHITESPACE:
				switch (Traitement.analyze(nextLCChar)) {
				// case WHITESPACE:
				case PONCTUATION:
					if (character == ' ') {
						character = NoBreakSpace;
					}
				default:

				}
				result.append(currentSyllable.toString());
				currentSyllable = new StringBuffer();
				result.append(Character.toChars(character));

				break;
			case PONCTUATION:
				switch (Traitement.analyze(previousLCChar)) {
				case ROMAJI_VOWEL:
				case ROMAJI_CONSONANT:
				case KANA:
				case KANJI:
				case SOKUON:
				case SMALL_KANA:
				case LINK:
				case PONCTUATION:
				case NUMBER:
				case OTHER:
					result.append(currentSyllable.toString());
					currentSyllable = new StringBuffer();
					currentSyllable.append(character);
					break block0;

				case WHITESPACE:
				case BRACKET:
					if (previousLCChar == '\n' || i == 0) {
						currentSyllable.append(character);
						break block0;
					}
					currentSyllable.append(character);
				}
				break;
			case OTHER:
				result.append(currentSyllable.toString());
				currentSyllable = new StringBuffer();
				if (previousLCChar != '\n' && i != 0) {
					currentSyllable.append(Separateur);
				}
				currentSyllable.append(character);
				break block0;
			case BRACKET:
				result.append(currentSyllable.toString());
				currentSyllable = new StringBuffer();
				result.append(Character.toChars(character));
				break;
			case NUMBER:
				switch (Traitement.analyze(previousLCChar)) {
				case NUMBER:
					// On groupe les nombres ensemble
					currentSyllable.append(character);
					break;
				case KANA:
				case KANJI:
				case LINK:
				case OTHER:
				case PONCTUATION:
				case ROMAJI_CONSONANT:
				case ROMAJI_VOWEL:
				case SMALL_KANA:
				case SOKUON:
				case WHITESPACE:
				case BRACKET:
					// Le reste du temps on romp la syllabe et on part sur une nouvelle
					result.append(currentSyllable.toString());
					currentSyllable = new StringBuffer();
					currentSyllable.append(character);

				}
			}
			previousLCChar = LCcharacter;
			++i;
		}
		result.append(currentSyllable.toString());
		return result.toString();
	}

	private static CharType analyze(char character) {
		if (Character.isWhitespace(character)) {
			return CharType.WHITESPACE;
		}

		switch (Character.toLowerCase(character)) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return CharType.NUMBER;

		case 'a':
		case 'ā':
		case 'e':
		case 'ē':
		case 'i':
		case 'ī':
		case 'o':
		case 'ō':
		case 'u':
		case 'ū':
			return CharType.ROMAJI_VOWEL;

		case 'b':
		case 'c':
		case 'd':
		case 'f':
		case 'g':
		case 'h':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			return CharType.ROMAJI_CONSONANT;

		case '-':
//			case '~':
		case ':':
		case '\'':
		case ';':
//		case '一':
		case 'ー':
			return CharType.LINK;

		case '(':
		case '「':
		case '」':
		case '｢':
		case '｣':
		case '"':
		case ')':
		case '`':
		case '“':
		case '”':
		case '（':
		case '〉':
		case '【':
		case '】':
			return CharType.BRACKET;

		case ' ':
			return CharType.WHITESPACE;

		case '！':
		case '!':
		case '?':
		case '？':
		case '.':
		case '。':
		case '…':
		case '、':
		case ',':
		case '♡':

			return CharType.PONCTUATION;

		case 'ッ':
		case 'っ':
			return CharType.SOKUON;

		case 'ゃ':
		case 'ゅ':
		case 'ょ':
		case 'ャ':
		case 'ュ':
		case 'ョ':
			return CharType.SMALL_KANA;

		default:
			if (Character.UnicodeBlock.of(character) == UnicodeBlock.HIRAGANA)
				return CharType.KANA;
			if (Character.UnicodeBlock.of(character) == UnicodeBlock.KATAKANA)
				return CharType.KANA;
			if (Character.UnicodeBlock.of(character) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
				return CharType.KANJI;
		}
		return CharType.OTHER;
	}

	public static String splitThatShit(String text) {
		if (text == null)
			return "";
		int cursor = -1;
		int cursor2 = text.indexOf('(', cursor);
		String main = "";
		String chorus = "";
		String newLine;
		while (cursor2 >= 0) {
			main += text.substring(cursor + 1, cursor2);
			cursor = text.indexOf(')', cursor2);
			if (cursor < 0)
				return ("INVALID CHORUS");
			newLine = text.substring(cursor2 + 1, cursor);
			if (!newLine.isEmpty()) {
				chorus += "\n" + String.valueOf(newLine.charAt(0)) + newLine.substring(1);
			}
			cursor2 = text.indexOf('(', cursor);
		}
		main += text.substring(cursor + 1);

		if (chorus.isEmpty())
			return text;
		return (main + "\n\n***CHORUS START***\n" + chorus).replaceAll("  +", " ");
	}
}
