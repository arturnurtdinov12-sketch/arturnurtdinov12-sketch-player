// Minimal stub for the auto-generated org.drinkless.tdlib.TdApi.
//
// The real TdApi.java is generated from TDLib's TL schema and contains hundreds
// of types. This stub declares ONLY the subset of types referenced by
// TGPlayer's TelegramClient implementation, with the same field names and
// constructor signatures as the real generated code, so that swapping in the
// real TdApi.java does not require any code changes here.
//
// To enable real Telegram connectivity:
//   1. Build TDLib for Android (or download a prebuilt distribution).
//   2. Replace this file and Client.java with the official versions.
//   3. Place libtdjni.so per ABI under app/src/main/jniLibs/.
//
// See README.md and scripts/setup-tdlib.sh.
package org.drinkless.tdlib;

@SuppressWarnings({"unused", "WeakerAccess"})
public class TdApi {

    public static abstract class Object {
        public native String toString();
        public abstract int getConstructor();
    }

    public static abstract class Function<R extends Object> extends Object {}

    public static class Error extends Object {
        public int code;
        public String message;
        public Error() {}
        public Error(int code, String message) { this.code = code; this.message = message; }
        @Override public int getConstructor() { return -1679978726; }
    }

    public static class Ok extends Object {
        public Ok() {}
        @Override public int getConstructor() { return -722616727; }
    }

    public static abstract class Update extends Object {}

    public static abstract class AuthorizationState extends Object {}

    public static class AuthorizationStateWaitTdlibParameters extends AuthorizationState {
        public AuthorizationStateWaitTdlibParameters() {}
        @Override public int getConstructor() { return 904720988; }
    }

    public static class AuthorizationStateWaitPhoneNumber extends AuthorizationState {
        public AuthorizationStateWaitPhoneNumber() {}
        @Override public int getConstructor() { return 306402531; }
    }

    public static class AuthorizationStateWaitCode extends AuthorizationState {
        public AuthorizationStateWaitCode() {}
        @Override public int getConstructor() { return 52643073; }
    }

    public static class AuthorizationStateWaitPassword extends AuthorizationState {
        public String passwordHint = "";
        public AuthorizationStateWaitPassword() {}
        @Override public int getConstructor() { return 187548796; }
    }

    public static class AuthorizationStateReady extends AuthorizationState {
        public AuthorizationStateReady() {}
        @Override public int getConstructor() { return -1834871737; }
    }

    public static class AuthorizationStateLoggedOut extends AuthorizationState {
        public AuthorizationStateLoggedOut() {}
        @Override public int getConstructor() { return 154449270; }
    }

    public static class AuthorizationStateClosed extends AuthorizationState {
        public AuthorizationStateClosed() {}
        @Override public int getConstructor() { return 1526047584; }
    }

    public static class UpdateAuthorizationState extends Update {
        public AuthorizationState authorizationState;
        public UpdateAuthorizationState() {}
        public UpdateAuthorizationState(AuthorizationState a) { this.authorizationState = a; }
        @Override public int getConstructor() { return 1622347490; }
    }

    public static class UpdateNewMessage extends Update {
        public Message message;
        public UpdateNewMessage() {}
        public UpdateNewMessage(Message m) { this.message = m; }
        @Override public int getConstructor() { return 238944219; }
    }

    public static class UpdateFile extends Update {
        public File file;
        public UpdateFile() {}
        public UpdateFile(File f) { this.file = f; }
        @Override public int getConstructor() { return 114132643; }
    }

    public static class UpdateNewChat extends Update {
        public Chat chat;
        public UpdateNewChat() {}
        @Override public int getConstructor() { return 2075757773; }
    }

    public static class SetTdlibParameters extends Function<Ok> {
        public boolean useTestDc;
        public String databaseDirectory = "";
        public String filesDirectory = "";
        public byte[] databaseEncryptionKey = new byte[0];
        public boolean useFileDatabase = true;
        public boolean useChatInfoDatabase = true;
        public boolean useMessageDatabase = true;
        public boolean useSecretChats = false;
        public int apiId;
        public String apiHash = "";
        public String systemLanguageCode = "en";
        public String deviceModel = "Android";
        public String systemVersion = "Android";
        public String applicationVersion = "1.0";
        public boolean enableStorageOptimizer = true;
        public boolean ignoreFileNames = false;
        public SetTdlibParameters() {}
        @Override public int getConstructor() { return -775883218; }
    }

    public static class SetAuthenticationPhoneNumber extends Function<Ok> {
        public String phoneNumber;
        public PhoneNumberAuthenticationSettings settings;
        public SetAuthenticationPhoneNumber() {}
        public SetAuthenticationPhoneNumber(String p, PhoneNumberAuthenticationSettings s) {
            this.phoneNumber = p; this.settings = s;
        }
        @Override public int getConstructor() { return 868276259; }
    }

    public static class PhoneNumberAuthenticationSettings extends Object {
        public boolean allowFlashCall;
        public boolean allowMissedCall;
        public boolean isCurrentPhoneNumber;
        public boolean hasUnknownPhoneNumber;
        public boolean allowSmsRetrieverApi;
        public String[] authenticationTokens = new String[0];
        public PhoneNumberAuthenticationSettings() {}
        @Override public int getConstructor() { return 1126509380; }
    }

    public static class CheckAuthenticationCode extends Function<Ok> {
        public String code;
        public CheckAuthenticationCode() {}
        public CheckAuthenticationCode(String code) { this.code = code; }
        @Override public int getConstructor() { return -302103382; }
    }

    public static class CheckAuthenticationPassword extends Function<Ok> {
        public String password;
        public CheckAuthenticationPassword() {}
        public CheckAuthenticationPassword(String password) { this.password = password; }
        @Override public int getConstructor() { return -2025698400; }
    }

    public static class LogOut extends Function<Ok> {
        public LogOut() {}
        @Override public int getConstructor() { return -1581923301; }
    }

    public static class Close extends Function<Ok> {
        public Close() {}
        @Override public int getConstructor() { return -1187782273; }
    }

    public static abstract class ChatList extends Object {}
    public static class ChatListMain extends ChatList {
        public ChatListMain() {}
        @Override public int getConstructor() { return -400991316; }
    }

    public static class GetChats extends Function<Chats> {
        public ChatList chatList;
        public int limit;
        public GetChats() {}
        public GetChats(ChatList c, int l) { this.chatList = c; this.limit = l; }
        @Override public int getConstructor() { return 1847060757; }
    }

    public static class LoadChats extends Function<Ok> {
        public ChatList chatList;
        public int limit;
        public LoadChats() {}
        public LoadChats(ChatList c, int l) { this.chatList = c; this.limit = l; }
        @Override public int getConstructor() { return -1117103146; }
    }

    public static class Chats extends Object {
        public int totalCount;
        public long[] chatIds = new long[0];
        public Chats() {}
        @Override public int getConstructor() { return 1809654812; }
    }

    public static class GetChat extends Function<Chat> {
        public long chatId;
        public GetChat() {}
        public GetChat(long id) { this.chatId = id; }
        @Override public int getConstructor() { return 1866601536; }
    }

    public static abstract class ChatType extends Object {}

    public static class ChatTypePrivate extends ChatType {
        public long userId;
        public ChatTypePrivate() {}
        @Override public int getConstructor() { return 1700720838; }
    }

    public static class ChatTypeBasicGroup extends ChatType {
        public long basicGroupId;
        public ChatTypeBasicGroup() {}
        @Override public int getConstructor() { return 21815278; }
    }

    public static class ChatTypeSupergroup extends ChatType {
        public long supergroupId;
        public boolean isChannel;
        public ChatTypeSupergroup() {}
        @Override public int getConstructor() { return 955152366; }
    }

    public static class ChatTypeSecret extends ChatType {
        public int secretChatId;
        public long userId;
        public ChatTypeSecret() {}
        @Override public int getConstructor() { return 136722563; }
    }

    public static class ChatPhotoInfo extends Object {
        public File small;
        public File big;
        public ChatPhotoInfo() {}
        @Override public int getConstructor() { return -1383417141; }
    }

    public static class Chat extends Object {
        public long id;
        public ChatType type;
        public String title = "";
        public ChatPhotoInfo photo;
        public Chat() {}
        @Override public int getConstructor() { return -1971028916; }
    }

    public static class LocalFile extends Object {
        public String path = "";
        public boolean canBeDownloaded;
        public boolean canBeDeleted;
        public boolean isDownloadingActive;
        public boolean isDownloadingCompleted;
        public int downloadOffset;
        public int downloadedPrefixSize;
        public int downloadedSize;
        public LocalFile() {}
        @Override public int getConstructor() { return -1166400317; }
    }

    public static class RemoteFile extends Object {
        public String id = "";
        public String uniqueId = "";
        public boolean isUploadingActive;
        public boolean isUploadingCompleted;
        public int uploadedSize;
        public RemoteFile() {}
        @Override public int getConstructor() { return 1761289748; }
    }

    public static class File extends Object {
        public int id;
        public long size;
        public long expectedSize;
        public LocalFile local;
        public RemoteFile remote;
        public File() {}
        @Override public int getConstructor() { return 1263291956; }
    }

    public static class DownloadFile extends Function<File> {
        public int fileId;
        public int priority;
        public long offset;
        public long limit;
        public boolean synchronous;
        public DownloadFile() {}
        public DownloadFile(int id, int p, long o, long l, boolean s) {
            this.fileId = id; this.priority = p; this.offset = o; this.limit = l; this.synchronous = s;
        }
        @Override public int getConstructor() { return 1059402292; }
    }

    public static class GetChatHistory extends Function<Messages> {
        public long chatId;
        public long fromMessageId;
        public int offset;
        public int limit;
        public boolean onlyLocal;
        public GetChatHistory() {}
        public GetChatHistory(long chatId, long fromMessageId, int offset, int limit, boolean onlyLocal) {
            this.chatId = chatId; this.fromMessageId = fromMessageId;
            this.offset = offset; this.limit = limit; this.onlyLocal = onlyLocal;
        }
        @Override public int getConstructor() { return -799960465; }
    }

    public static class Messages extends Object {
        public int totalCount;
        public Message[] messages = new Message[0];
        public Messages() {}
        @Override public int getConstructor() { return -16498159; }
    }

    public static class Message extends Object {
        public long id;
        public long chatId;
        public int date;
        public MessageContent content;
        public Message() {}
        @Override public int getConstructor() { return 661301989; }
    }

    public static abstract class MessageContent extends Object {}

    public static class MessageAudio extends MessageContent {
        public Audio audio;
        public FormattedText caption;
        public MessageAudio() {}
        @Override public int getConstructor() { return 276722716; }
    }

    public static class MessageDocument extends MessageContent {
        public Document document;
        public FormattedText caption;
        public MessageDocument() {}
        @Override public int getConstructor() { return 596945783; }
    }

    public static class FormattedText extends Object {
        public String text = "";
        public FormattedText() {}
        @Override public int getConstructor() { return -252624564; }
    }

    public static class Audio extends Object {
        public int duration;
        public String title = "";
        public String performer = "";
        public String fileName = "";
        public String mimeType = "";
        public File audio;
        public Audio() {}
        @Override public int getConstructor() { return -557915893; }
    }

    public static class Document extends Object {
        public String fileName = "";
        public String mimeType = "";
        public File document;
        public Document() {}
        @Override public int getConstructor() { return -1357271080; }
    }
}
