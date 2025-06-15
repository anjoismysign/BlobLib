package io.github.anjoismysign.bloblib.entities.proxy;

import io.github.anjoismysign.bloblib.entities.IFileManager;
import io.github.anjoismysign.bloblib.managers.IManagerDirector;

/**
 * There's no issue with proxying IFileManager and IManagerDirector
 * since their usage is minimal, efficient and unless you call
 * the Proxy's methods hundreds of thousands of times in the same tick,
 * you won't notice any performance issues...
 * Just to clarify, if providing a proxied ManagerDirector
 * as the ManagerDirector inside the BlobPlugin class,
 * you won't notice any performance issues.
 */
public class BlobProxifier {
    /**
     * Will proxy the given IFileManager.
     *
     * @param fileManager The IFileManager to proxy.
     * @return The proxied IFileManager.
     */
    public static IFileManager PROXY(IFileManager fileManager) {
        return new IFileManagerProxy(fileManager);
    }

    /**
     * Will proxy the given IManagerDirector.
     *
     * @param managerDirector The IManagerDirector to proxy.
     * @return The proxied IManagerDirector.
     */
    public static IManagerDirector PROXY(IManagerDirector managerDirector) {
        return new IManagerDirectorProxy(managerDirector);
    }
}
