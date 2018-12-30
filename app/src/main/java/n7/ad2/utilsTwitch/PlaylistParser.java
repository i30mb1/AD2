package n7.ad2.utilsTwitch;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation based on http://tools.ietf.org/html/draft-pantos-http-live-streaming-02#section-3.1.
 *
 * @author dkuffner
 */
@SuppressWarnings("StringBufferReplaceableByString")
final class PlaylistParser {
    private final Logger log = Logger.getLogger(getClass().getName());
    private PlaylistType type;

    private PlaylistParser(PlaylistType type) {
        if (type == null) {
            throw new NullPointerException("type"); //NonNls
        }
        this.type = type;
    }

    static PlaylistParser create(PlaylistType type) {
        return new PlaylistParser(type);
    }

    public Playlist parse(Readable source) throws ParseException {

        final Scanner scanner = new Scanner(source);

        boolean firstLine = true;

        int lineNumber = 0;

        final List<Element> elements = new ArrayList<>(10);
        final ElementBuilder builder = new ElementBuilder();
        boolean endListSet = false;
        int targetDuration = -1;
        int mediaSequenceNumber = -1;

        EncryptionInfo currentEncryption = null;
        String quality = null;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.length() > 0) {
                if (line.startsWith(M3uConstants.EX_PREFIX)) {
                    if (firstLine) {
                        checkFirstLine(lineNumber, line);
                        firstLine = false;
                    } else if (line.startsWith(M3uConstants.EXTINF)) {
                        parseExtInf(line, lineNumber, builder);
                    } else if (line.startsWith(M3uConstants.EXT_X_ENDLIST)) {
                        endListSet = true;
                    } else if (line.startsWith(M3uConstants.EXT_X_TARGET_DURATION)) {
                        if (targetDuration != -1) {
                            throw new ParseException(line, lineNumber, M3uConstants.EXT_X_TARGET_DURATION + " duplicated");
                        }
                        targetDuration = parseTargetDuration(line, lineNumber);
                    } else if (line.startsWith(M3uConstants.EXT_X_MEDIA_SEQUENCE)) {
                        if (mediaSequenceNumber != -1) {
                            throw new ParseException(line, lineNumber, M3uConstants.EXT_X_MEDIA_SEQUENCE + " duplicated");
                        }
                        mediaSequenceNumber = parseMediaSequence(line, lineNumber);
                    } else if (line.startsWith(M3uConstants.EXT_X_PROGRAM_DATE_TIME)) {
                        long programDateTime = parseProgramDateTime(line, lineNumber);
                        builder.programDate(programDateTime);
                    } else if (line.startsWith(M3uConstants.EXT_X_KEY)) {
                        currentEncryption = parseEncryption(line, lineNumber);
                    } else if (line.startsWith(M3uConstants.EXT_X_STREAM_INF)) {
                        parseExtStreamInf(line, lineNumber, builder);
                    } else {
                        log.log(Level.FINE, new StringBuilder().append("Unknown: '").append(line).append("'").toString());
                    }
                } else if (line.startsWith(M3uConstants.COMMENT_PREFIX)) {
                    // no first line check because comments will be ignored.
                    // comment do nothing
                    if (log.isLoggable(Level.FINEST)) {
                        log.log(Level.FINEST, "----- Comment: " + line);
                    }
                } else {
                    if (firstLine) {
                        checkFirstLine(lineNumber, line);
                    }

                    // No prefix: must be the media uri.
                    builder.encrypted(currentEncryption);

                    builder.uri(toURI(line));
                    elements.add(builder.create());

                    // a new element begins.
                    builder.reset();
                }
            }

            lineNumber++;
        }

        return new Playlist(Collections.unmodifiableList(elements), endListSet, targetDuration, mediaSequenceNumber);
    }

    private URI toURI(String line) {
        try {
            return (URI.create(line));
        } catch (IllegalArgumentException e) {
            return new File(line).toURI();
        }
    }

    private long parseProgramDateTime(String line, int lineNumber) throws ParseException {
        return M3uConstants.Patterns.toDate(line, lineNumber);
    }

    private int parseTargetDuration(String line, int lineNumber) throws ParseException {
        return (int) parseNumberTag(line, lineNumber, M3uConstants.Patterns.EXT_X_TARGET_DURATION, M3uConstants.EXT_X_TARGET_DURATION);
    }

    private int parseMediaSequence(String line, int lineNumber) throws ParseException {
        return (int) parseNumberTag(line, lineNumber, M3uConstants.Patterns.EXT_X_MEDIA_SEQUENCE, M3uConstants.EXT_X_MEDIA_SEQUENCE);
    }

    private long parseNumberTag(String line, int lineNumber, Pattern patter, String property) throws ParseException {
        Matcher matcher = patter.matcher(line);
        if (!matcher.find() && !matcher.matches() && matcher.groupCount() < 1) {
            throw new ParseException(line, lineNumber, property + " must specify duration");
        }

        try {
            return Long.valueOf(matcher.group(1));
        } catch (NumberFormatException e) {
            // should not happen because of
            throw new ParseException(line, lineNumber, e);
        }
    }

    private void checkFirstLine(int lineNumber, String line) throws ParseException {
        if (type == PlaylistType.M3U8 && !line.startsWith(M3uConstants.EXTM3U)) {
            throw new ParseException(line, lineNumber, "Playlist type '" + PlaylistType.M3U8 + "' must start with " + M3uConstants.EXTM3U);
        }
    }

    private void parseExtInf(String line, int lineNumber, ElementBuilder builder) throws ParseException {
        // EXTINF:200,Title
        final Matcher matcher = M3uConstants.Patterns.EXTINF.matcher(line);

        if (!matcher.find() && !matcher.matches() && matcher.groupCount() < 1) {
            throw new ParseException(line, lineNumber, "EXTINF must specify at least the duration");
        }

        String duration = matcher.group(1);
        String title = matcher.groupCount() > 1 ? matcher.group(2) : "";

        try {
            builder.duration(Integer.valueOf(duration)).title(title);
        } catch (NumberFormatException e) {
            // should not happen because of 
            throw new ParseException(line, lineNumber, e);
        }
    }

    private void parseExtStreamInf(String line, int lineNumber, ElementBuilder builder) throws ParseException {
        // EXTINF:200,Title
        final Matcher matcher = Pattern.compile("VIDEO=\"(.+?)\"").matcher(line);// Patterns.EXTSTREAMINF.matcher(line);
        //VIDEO="mobile"

        if (!matcher.find() && !matcher.matches() && matcher.groupCount() < 1) {
            throw new ParseException(line, lineNumber, "EXTINF must specify at least the duration");
        }

        String quality = matcher.group(1);

        try {
            builder.quality(quality);
        } catch (NumberFormatException e) {
            // should not happen because of
            throw new ParseException(line, lineNumber, e);
        }
    }

    private EncryptionInfo parseEncryption(String line, int lineNumber) throws ParseException {
        Matcher matcher = M3uConstants.Patterns.EXT_X_KEY.matcher(line);

        if (!matcher.find() || !matcher.matches() || matcher.groupCount() < 1) {
            throw new ParseException(line, lineNumber, "illegal input: " + line);
        }

        String method = matcher.group(1);
        String uri = matcher.group(3);

        if (method.equalsIgnoreCase("none")) {
            return null;
        }

        return new ElementImpl.EncryptionInfoImpl(uri != null ? toURI(uri) : null, method);
    }

}
