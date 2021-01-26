package n7.ad2.ui.streams.utilsTwitch;

import java.net.URI;

/**
 * Contains information about media encryption.
 */
public interface EncryptionInfo {
    URI getURI();

    String getMethod();
}
