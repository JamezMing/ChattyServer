package global;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public final class ServerUserDataBaseManager {
	private static SqlJetDb db;
	private static final String DB_NAME = "ServerUserData.db";
	private static final String TABLE_NAME = "ServerUser";
	private static final String USER_ADDRESS_FIELD = "user_address";
	private static final String USER_NAME_FIELD = "user_name";
	private static final String USER_RECEIVEPORT_FIELD = "user_receive_port";
	private static final String USER_AVALIABLITY_FIELD = "user_avaliability";
	private static final String ALLOWED_USER_FIELD = "allowed_user";//Allowed user is splitted by character [A, B, C ...]
	private static final String USER_KEY_FIELD = "user_key";
	private static final String ADDRESSPORT_INDEX = "addressport_index";
	private static final String KEY_INDEX = "key_index";
	
	private ServerUserDataBaseManager(){}
	
	public static void init(){
		File dbFile = new File(DB_NAME);
		System.out.println(dbFile.exists());
		if(dbFile.exists()){
			try {
				db = SqlJetDb.open(dbFile, true);
				db.beginTransaction(SqlJetTransactionMode.WRITE);
				db.getOptions().setUserVersion(1);
			    db.commit();
			} catch (SqlJetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				db = SqlJetDb.open(dbFile, true);
				db.getOptions().setAutovacuum(true);
				db.beginTransaction(SqlJetTransactionMode.WRITE);
				db.getOptions().setUserVersion(1);
	            String createTableQuery = "CREATE TABLE " + TABLE_NAME + 
	            		" (" + USER_ADDRESS_FIELD + " TEXT NOT NULL, "+ USER_NAME_FIELD + " TEXT NOT NULL , " +  USER_RECEIVEPORT_FIELD + " TEXT NOT NULL , " + USER_AVALIABLITY_FIELD + "TEXT NOT NULL, " + 
	            		USER_KEY_FIELD + " PRIMARY KEY TEXT NOT NULL, " + ALLOWED_USER_FIELD + " TEXT NOT NULL, "  + ")";
	            String createNameQuery = "CREATE UNIQUE INDEX " + ADDRESSPORT_INDEX + " ON " + TABLE_NAME + "(" +  USER_ADDRESS_FIELD + ", " + USER_RECEIVEPORT_FIELD  + ")"; 
	            String createKeyQuery = "CREATE UNIQUE INDEX " + KEY_INDEX + " ON " + TABLE_NAME + "(" +  USER_KEY_FIELD + ")"; 
				db.createTable(createTableQuery);
				db.createIndex(createNameQuery);
				db.createIndex(createKeyQuery);
				db.commit();
			} catch (SqlJetException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		
	}
	
	public static void insertItem(InetAddress addr, String userName, Integer recPort, boolean isAvaliable, byte[] userKey, ArrayList<String> allowedListUser) throws SqlJetException{
		String address = addr.getHostAddress();
		String port = recPort.toString();
		db.beginTransaction(SqlJetTransactionMode.WRITE);
		ISqlJetTable table = db.getTable(TABLE_NAME);
		table.insert(address, userName, port, String.valueOf(isAvaliable), DatatypeConverter.printHexBinary(userKey), allowedListUser.toString());
	}
	
	public static ArrayList<User> recoverUserData() throws UnknownHostException{
		ArrayList<User> userList = new ArrayList<User>();
		try {
			ISqlJetCursor cursor = db.getTable(TABLE_NAME).order(db.getTable(TABLE_NAME).getPrimaryKeyIndexName());
			while(!cursor.eof()){
				InetAddress addr = InetAddress.getByName(cursor.getString(USER_ADDRESS_FIELD));
				Integer port = new Integer(cursor.getString(USER_RECEIVEPORT_FIELD));
				String name = cursor.getString(USER_NAME_FIELD);
				String avaliablity = cursor.getString(USER_AVALIABLITY_FIELD);
				String allowedUsers = cursor.getString(ALLOWED_USER_FIELD);
				String[] ulist = allowedUsers.trim().substring(1, allowedUsers.length()-1).split(",");
				for(String s: ulist){
					s = s.trim();
				}
				ArrayList<String> allowUserList = new ArrayList<String>(Arrays.asList(ulist));
				byte[] userKey = javax.xml.bind.DatatypeConverter.parseHexBinary(cursor.getString(USER_KEY_FIELD));
				User newUser = new User(name, addr, port, avaliablity ,allowUserList, userKey);
				userList.add(newUser);
			}
		} catch (SqlJetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


}
