import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
 
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.MimeMessage.RecipientType;
 
/**
* メールの送信サンプル
*
* @author javajapan
*
*/
public class MailUtil {
 
public static void main(String[] args) throws NoSuchProviderException, UnsupportedEncodingException, MessagingException {
 
String mailserver = "smtp.gmail.com11";
String mailport = "587";
String mailloginuser = "ログインID";
String mailloginpassword = "ログインパスワード";
String fromAddress = "fromaddress@gmail.com";
String fromName = "山田太郎";
String subject = "件名です";
String text = "本文です";
String encode = "UTF-8";
String contentType = "text/plain";
 
List<String[]> toList = new ArrayList<String[]>();
toList.add(new String[] { "toaddress@gmail.com" });
 
List<String[]> ccList = new ArrayList<String[]>();
ccList.add(new String[] { "ccaddress1@gmail.com" });
ccList.add(new String[] { "ccaddress22@gmail.com" });
ccList.add(new String[] { "ccaddress333@gmail.com" });

// 送りたい名前とファイルパスをセットで２つのファイルを添付
String[][] attachmentFiles = new String[][] { { "添付ファイル1.txt", "C:/test1.txt" }, { "添付ファイル2.txt", "C:/test2.txt" } };
 
// メール送信
Session session = createSession(mailserver, mailport, mailloginuser, mailloginpassword, false, true, true);
sendMail(fromAddress, fromName, toList, null, null, subject, text, attachmentFiles, new Date(), encode, contentType, session);
 
}
 
/**
* @param from
* fromのメールアドレス
* @param fromName
* 　FROMの表示名,設定しなくてもよい
* @param toList
* TOのアドレス一覧 {"メールアドレス","表示名","文字コード"}のListで渡すこと 表示名、文字コードは省略化
* null可
* @param ccList
* CCのアドレス一覧 null可
* @param bccList
* BCCのアドレス一覧 null可
* @param subject
* 件名文字列
* @param text
* 本文文字列
* @param attachmentFiles
* 添付ファイル {"名称","添付したファイルのパス"}のリストで渡すこと null可
* @param sendDate
* 送信日時
* @param charaset
* 文字コード
* @param contentType
* 　コンテントタイプ text/plainなど
*
* @param session
* @throws NoSuchProviderException
* @throws MessagingException
* @throws UnsupportedEncodingException
*/
private static void sendMail(String from, String fromName, List<String[]> toList, List<String[]> ccList, List<String[]> bccList, String subject, String text, String[][] attachmentFiles, Date sendDate, String charaset, String contentHeader, Session session) throws NoSuchProviderException, MessagingException, UnsupportedEncodingException {
 
MimeMessage mimeMessage = createMimeMessage(from, fromName, toList, ccList, bccList, subject, text, attachmentFiles, sendDate, charaset, contentHeader, session);
 
Transport.send(mimeMessage);
}
 
/**
* @param from
* @param fromName
* @param toList
* @param ccList
* @param bccList
* @param subject
* @param text
* @param attachmentFiles
* @param sendDate
* @param charaset
* @param contentType
* @param session
* @return
* @throws MessagingException
* @throws UnsupportedEncodingException
*/
private static MimeMessage createMimeMessage(String from, String fromName, List<String[]> toList, List<String[]> ccList, List<String[]> bccList, String subject, String text, String[][] attachmentFiles, Date sendDate, String charaset, String contentType, Session session) throws MessagingException, UnsupportedEncodingException {
 
MimeMessage mimeMessage = new MimeMessage(session);
 
mimeMessage.setFrom(new InternetAddress(from, fromName, charaset));
 
mimeMessage.setSubject(subject, charaset);
 
mimeMessage.setSentDate(sendDate);
 
if (toList != null) {
List<InternetAddress> toAddressList = convertAddressList(toList);
mimeMessage.setRecipients(RecipientType.TO, toAddressList.toArray(new InternetAddress[] {}));
}
 
if (ccList != null) {
List<InternetAddress> toAddressList = convertAddressList(ccList);
mimeMessage.setRecipients(RecipientType.CC, toAddressList.toArray(new InternetAddress[] {}));
}
 
if (bccList != null) {
List<InternetAddress> toAddressList = convertAddressList(bccList);
mimeMessage.setRecipients(RecipientType.BCC, toAddressList.toArray(new InternetAddress[] {}));
}
 
Multipart multiPart = new MimeMultipart();
MimeBodyPart textBodyPart = new MimeBodyPart();
textBodyPart.setText(text, charaset);
textBodyPart.setHeader("Content-Type", contentType + " charset=\"" + charaset + "\"");
multiPart.addBodyPart(textBodyPart);
 
if (attachmentFiles != null) {
for (int i = 0; i < attachmentFiles.length; i++) {
MimeBodyPart fileBodyPart = new MimeBodyPart();
String[] fileData = attachmentFiles[i];
fileBodyPart.setDataHandler(new DataHandler(new FileDataSource(fileData[1])));
fileBodyPart.setFileName(MimeUtility.encodeText((fileData[0]), charaset, "B"));
multiPart.addBodyPart(fileBodyPart);
}
}
 
mimeMessage.setContent(multiPart);
 
return mimeMessage;
}
 
/**
* @param toList
* @return
* @throws UnsupportedEncodingException
*/
private static List<InternetAddress> convertAddressList(List<String[]> toList) throws UnsupportedEncodingException {
 
List<InternetAddress> toAddressList = new ArrayList<InternetAddress>();
 
for (String[] to : toList) {
 
int todatasize = to.length;
InternetAddress toaddressdata = new InternetAddress();
toaddressdata.setAddress(to[0]);
 
if (todatasize == 2) {
toaddressdata.setAddress(to[0]);
toaddressdata.setPersonal(to[1]);
} else if (todatasize == 3) {
toaddressdata.setPersonal(to[1], to[2]);
}
toAddressList.add(toaddressdata);
 
}
return toAddressList;
}
 
/**
* @param authUser
* ログインユーザID
* @param authPassword
* 　ログインパスワード
* @param debugMode
* デバッグ有無
* @param isAuth
* 　認証の有無
* @param isSSL
* 　SSL有無
* @return
* @throws NoSuchProviderException
*/
private static Session createSession(String mailserver, String mailport, final String authUser, final String authPassword, boolean debugMode, boolean isAuth, boolean isSSL) throws NoSuchProviderException {
 
Properties props = new Properties();
 
props.put("mail.smtp.host", mailserver);
props.put("mail.smtp.port", mailport);
 
// このあたり、よくわかっていない..
//if (isSSL) {
//props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//props.put("mail.smtp.socketFactory.fallback", "false");
//}
 
Session session = null;
 
if (isAuth) {
props.put("mail.smtp.auth", "true");
session = Session.getInstance(props, new Authenticator() {
protected PasswordAuthentication getPasswordAuthentication() {
return new PasswordAuthentication(authUser, authPassword);
}
});
 
session.getTransport("smtp");
} else {
props.put("mail.smtp.auth", "false");
session = Session.getDefaultInstance(props, null);
}
 
session.setDebug(debugMode);
 
return session;
}
 
}