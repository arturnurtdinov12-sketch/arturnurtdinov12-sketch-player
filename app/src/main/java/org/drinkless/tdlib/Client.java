// Stub for TDLib org.drinkless.tdlib.Client.
//
// This compiles the project without requiring the native TDLib library to be
// present. For real Telegram connectivity, replace this file (and TdApi.java)
// with the official files from the TDLib distribution and place the native
// libtdjni.so binaries into app/src/main/jniLibs/<abi>/.
//
// Run scripts/setup-tdlib.sh for an automated setup once you have the build
// outputs available locally (see README.md).
package org.drinkless.tdlib;

public class Client {

    public interface ResultHandler {
        void onResult(TdApi.Object object);
    }

    public interface ExceptionHandler {
        void onException(Throwable e);
    }

    public interface LogMessageHandler {
        void onLogMessage(int verbosityLevel, String message);
    }

    private final ResultHandler updatesHandler;

    private Client(ResultHandler updatesHandler,
                   ExceptionHandler updateExceptionHandler,
                   ExceptionHandler defaultExceptionHandler) {
        this.updatesHandler = updatesHandler;
    }

    public static Client create(ResultHandler updatesHandler,
                                ExceptionHandler updateExceptionHandler,
                                ExceptionHandler defaultExceptionHandler) {
        return new Client(updatesHandler, updateExceptionHandler, defaultExceptionHandler);
    }

    public void send(TdApi.Function<? extends TdApi.Object> query, ResultHandler handler) {
        // Stub — returns an Error so callers can fail gracefully.
        if (handler != null) {
            handler.onResult(new TdApi.Error(-1, "TDLib stub: link the real org.drinkless.tdlib.Client"));
        }
    }

    public void send(TdApi.Function<? extends TdApi.Object> query,
                     ResultHandler handler,
                     ExceptionHandler exceptionHandler) {
        send(query, handler);
    }

    public static TdApi.Object execute(TdApi.Function<? extends TdApi.Object> query) {
        return new TdApi.Error(-1, "TDLib stub: link the real org.drinkless.tdlib.Client");
    }

    public static void setLogMessageHandler(int maxVerbosityLevel, LogMessageHandler handler) {
        // no-op stub
    }

    public void close() {
        // no-op stub
    }

    /** Notifies test consumers of an update; not used in the stub. */
    @SuppressWarnings("unused")
    public void deliverUpdateForTesting(TdApi.Object update) {
        if (updatesHandler != null) updatesHandler.onResult(update);
    }
}
