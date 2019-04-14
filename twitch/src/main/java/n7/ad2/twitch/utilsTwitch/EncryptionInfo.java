package n7.ad2.twitch.utilsTwitch;

import java.net.URI;

/**
 * Contains information about media encryption.
 */
public interface EncryptionInfo {
    URI getURI();

    String getMethod();
}
