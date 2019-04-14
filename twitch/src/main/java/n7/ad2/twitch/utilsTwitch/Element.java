package n7.ad2.twitch.utilsTwitch;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A playlist element.
 *
 * @author dkuffner
 */
public interface Element {

    String getTitle();


    int getDuration();

    /**
     * URI to media or playlist.
     *
     * @return the URI.
     */
    URI getURI();

    /**
     * Media can be encrypted.
     *
     * @return true if media encrypted.
     */
    boolean isEncrypted();

    /**
     * Element can be another playlist.
     *
     * @return true if element a playlist.
     */
    boolean isPlayList();

    /**
     * Element is a media file.
     *
     * @return true if element a media file and not a playlist.
     */
    boolean isMedia();

    /**
     * The program date.
     *
     * @return -1 in case of program date is not set.
     */
    long getProgramDate();

    String getQuality();

    class List extends ArrayList<Element> {
        public List(Collection<? extends Element> collection) {
            super(collection);
        }
    }
}
